package fr.univ_tours.li.mdjedaini.ideb.olap.query;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.olap4j.CellSet;
import org.olap4j.layout.RectangularCellSetFormatter;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryOperationDifferential;
import fr.univ_tours.li.mdjedaini.ideb.ext.falseto.FalsetoQueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.ResultStructure;

/**
 * This class represents a formal model of an OLAP query.
 * It is composed of three components. The measure, the group by set (GBS) and
 * the selection predicates...
 * @author mahfoud
 */
public class QueryTriplet extends Query implements java.io.Serializable {

    //

    /**
     *
     */
    protected Set<MeasureFragment> measureList;

    /**
     *
     */
    protected Set<ProjectionFragment> projectionList;

    /**
     *
     */
    protected Set<SelectionFragment> selectionList;
    
    Query originalQuery;    // original query, before query conversion (often a mdx query)
    
    /**
     * 
     * @param arg_cube 
     */
    public QueryTriplet(EAB_Cube arg_cube) {
        super(arg_cube);
        this.cube           = arg_cube;
        this.measureList    = new HashSet<>();
        this.projectionList = new HashSet<>();
        this.selectionList  = new HashSet<>();
        
        for(EAB_Hierarchy h_tmp : arg_cube.getHierarchyList()) {
            ProjectionFragment pf   = new ProjectionFragment(this, h_tmp.getAllLevel());
            this.projectionList.add(pf);
        }

    }
    
    /**
     * Copy constructor for the query triplet.
     * 
     * @param arg_qt 
     */
    public QueryTriplet(QueryTriplet arg_qt) {
        this(arg_qt.getCube());
        
        this.measureList    = new HashSet<>(arg_qt.getMeasures());
        this.projectionList = new HashSet<>(arg_qt.getProjectionFragments());
        this.selectionList  = new HashSet<>(arg_qt.getSelectionFragments());
        
    }
    
    /**
     * 
     * @param arg_measureList
     * @param arg_projectionList
     * @param arg_selectionList
     */
//    public QueryTriplet(Set<MeasureFragment> arg_measureList, Set<ProjectionFragment> arg_projectionList, Set<SelectionFragment> arg_selectionList) {
//        this.measureList    = arg_measureList;
//        this.projectionList = arg_projectionList;
//        this.selectionList  = arg_selectionList;
//    }
 
    /**
     * Returns the measures in this query.
     * If the query has not any measure set, we return the cube default measure.
     * @return measure of the query
     */
    public HashSet<MeasureFragment> getMeasures() {
        if(!this.measureList.isEmpty()) {
            return (HashSet<MeasureFragment>) this.measureList;
        } else {
            MeasureFragment mf_tmp          = new MeasureFragment(this, this.getCube().getDefaultMeasure());
            HashSet<MeasureFragment> result = new HashSet<>();
            result.add(mf_tmp);
            return result;
        }
    }

    /**
     *
     * @return
     */
    public Query getOriginalQuery() {
        return originalQuery;
    }

    /**
     *
     * @param originalQuery
     */
    public void setOriginalQuery(Query originalQuery) {
        this.originalQuery = originalQuery;
    }

    
    
    /**
     *
     * @return the list of projection fragments.
     */
    public HashSet<ProjectionFragment> getProjectionFragments() {
        return (HashSet<ProjectionFragment>) this.projectionList;
    }

    /**
     *
     * @return the list of selection fragments.
     */
    public HashSet<SelectionFragment> getSelectionFragments() {
        return (HashSet<SelectionFragment>) this.selectionList;
    }

    /**
     * Adds a projection to the query and replaces the old projection only if the
     * given one is more detailed than the existing one.
     * This can be used for example for drill down.
     * It is also used for parsing MDX queries and converting them to QueryTriplet
     *
     * @param arg_p the projection fragment.
     */
    public void addProjectionIfMoreDetailed(ProjectionFragment arg_p) {
        
        EAB_Level currentLevel  = this.getProjectionFragmentByHierarchy(arg_p.getLevel().getHierarchy()).getLevel();
        EAB_Level newLevel      = arg_p.getLevel();

        // add the projection only if the new level is child of current level
        if(newLevel.isChildOf(currentLevel)) {
            this.addProjection(arg_p);
        }
        
    }
    
    /**
     * Add a projection fragment according to a projection fragment already
     * created.
     *
     * @param arg_p the projection fragment.
     */
    public void addProjection(ProjectionFragment arg_p) {
        // we remove the previous GBS for this hierarchy
        this.removeProjectionByHierarchy(arg_p.getLevel().getHierarchy());
        
        if (!this.projectionList.contains(arg_p)) {
            this.projectionList.add(arg_p);
        }
    }

    /**
     * Add a selection fragment to the query.
     * If the selection criteria is on level All, we do not add it.
     *
     * @param s the selection fragment.
     */
    public void addSelection(SelectionFragment s) {
        // we do nothing is the selection criteria is on the level all
        if(s.getLevel().isAllLevel()) {
            return;
        }
        
        if (!this.selectionList.contains(s)) {
            this.selectionList.add(s);
        }
    }

    /**
     * 
     * @param arg_sf 
     */
    public void removeSelection(SelectionFragment arg_sf) {
        this.selectionList.remove(arg_sf);
    }
    
    /**
     * 
     * @param arg_h 
     */
    public void removeSelectionByHierarchy(EAB_Hierarchy arg_h) {
        for(SelectionFragment sf_tmp : this.getSelectionFragmentByHierarchy(arg_h)) {
            this.removeSelection(sf_tmp);
        }
    }
    
    /**
     * Projects a query on the most detailed level.
     * Projection is only performed for hierarchies that are not All...
     * @return 
     */
    public QueryTriplet project() {
        QueryTriplet qt = new QueryTriplet(this);
        
        for(EAB_Hierarchy h_tmp : qt.getHierarchyWithProjectionOtherThanAll()) {
            
            // old pf, just for debug purposes
            // ProjectionFragment pf_tmp   = qt.getProjectionFragmentByHierarchy(h_tmp);
            
            ProjectionFragment new_pf   = new ProjectionFragment(qt, h_tmp.getMostDetailedLevel());
            qt.addProjection(new_pf);
            
        }
        
        return qt;
    }
    
    /**
     * 
     * @param arg_qt
     * @return 
     */
    public Boolean isNeighborOf(QueryTriplet arg_qt) {

        //we need the results for the comparison
        Result r1   = this.getResult();
        Result r2   = arg_qt.getResult();
        
        r1.computeMemberListByHierarchy();
        r2.computeMemberListByHierarchy();
        
        // je n'inclu pas les mesures dans le differentiel pour le moment...
        Integer differential    = 0;
        
        for(EAB_Hierarchy h_tmp : this.getCube().getHierarchyList()) {
            
            EAB_Level l1    = this.getProjectionFragmentByHierarchy(h_tmp).getLevel();
            EAB_Level l2    = arg_qt.getProjectionFragmentByHierarchy(h_tmp).getLevel();
            
            // if both levels are not linked together
            if(!l1.equals(l2) && !l1.isDirectChildOf(l2) && !l2.isDirectChildOf(l1)) {
                return false;
            }
            
            if(l1.isDirectChildOf(l2) || l2.isDirectChildOf(l1)) {
                differential++;
            }
            
        }

        // now we check the differential
        if(differential > 1) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     * @param arg_qt
     * @return 
     */
    public Boolean isRefineOf(QueryTriplet arg_qt) {

        Boolean result  = true;
    
        QueryOperationDifferential qod  = new QueryOperationDifferential(this.getCube().getBencharkEngine());
        
        // if measures have changed, return false
        if(!qod.addedMeasures(arg_qt, this).isEmpty() || !qod.removedMeasures(arg_qt, this).isEmpty()) {
            return false;
        }
        
        // if a filter has been removed, return false
        if(!qod.removedFilters(arg_qt, this).isEmpty()) {
            return false;
        }
        
        // if a roolup has been performed, return false
        if(!qod.rollUp(arg_qt, this).isEmpty()) {
            return false;
        }

        // to return true, the query must have at least one added filter OR one drill down
        // otherwise it would be the same query
        if(qod.addedFilters(arg_qt, this).isEmpty() && qod.drillDown(arg_qt, this).isEmpty()) {
            return false;
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_qt
     * @return 
     */
    public Boolean isRelaxOf(QueryTriplet arg_qt) {

        return arg_qt.isRefineOf(this);
        
    }
    
    /**
     * Add a measure fragment according to a measure fragment already created.
     *
     * @param m the measure fragment.
     */
    public void addMeasure(MeasureFragment m) {
        if (!this.measureList.contains(m)) {
            this.measureList.add(m);
        }
    }
    
    /**
     * 
     * @param arg_pf
     * @return 
     */
    private boolean isASelection(ProjectionFragment arg_pf) {
        for(SelectionFragment sf : this.selectionList) {
            if(sf.getHierarchyName().equals(arg_pf.getHierarchyName())) {
                return true;
            }
        }
        return false;
    }
    
    private void removeProjectionByHierarchy(EAB_Hierarchy arg_h) {
        Set<ProjectionFragment> toRemove    = new HashSet<>();
        
        // we record the fragments to remove
        for(ProjectionFragment pf_tmp : this.getProjectionFragments()) {
            if(pf_tmp.getLevel().getHierarchy().equals(arg_h)) {
                toRemove.add(pf_tmp);
            }
        }

        // now we actually remove them
        for(ProjectionFragment pf_tmp : toRemove) {
            this.projectionList.remove(pf_tmp);
        }
        
    }
    
    /**
     * 
     * @param arg_pf
     * @return 
     */
    private ArrayList<SelectionFragment> getMatchingSelectionList(ProjectionFragment arg_pf) {
        ArrayList<SelectionFragment> result = new ArrayList<SelectionFragment>();
        
        for(SelectionFragment sf : this.selectionList) {
            if(sf.getHierarchyName().equals(arg_pf.getHierarchyName())) {
                result.add(sf);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_hierarchy
     * @return 
     */
    public ProjectionFragment getProjectionFragmentByHierarchy(EAB_Hierarchy arg_hierarchy) {
        for(ProjectionFragment pf : this.projectionList) {
            if(pf.getLevel().getHierarchy().equals(arg_hierarchy)) {
                return pf;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param arg_hierarchy
     * @return 
     */
    public Set<SelectionFragment> getSelectionFragmentByHierarchy(EAB_Hierarchy arg_hierarchy) {
        return this.getSelectionFragmentByHierarchyName(arg_hierarchy.getUniqueName());
    }
    
    /**
     * 
     * @param arg_hierarchyName
     * @return 
     */
    public Set<SelectionFragment> getSelectionFragmentByHierarchyName(String arg_hierarchyName) {
        Set<SelectionFragment> result   = new HashSet<>();
        
        for(SelectionFragment sf : this.selectionList) {
            if(sf.getHierarchyName().equals(arg_hierarchyName)) {
                result.add(sf);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Set<EAB_Hierarchy> getHierarchyWithProjectionToAll() {
        Set<EAB_Hierarchy> result   = new HashSet<>();
        
        for(ProjectionFragment pf_tmp : this.projectionList) {
            if(pf_tmp.getLevel().isAllLevel()) {
                result.add(pf_tmp.getLevel().getHierarchy());
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Set<EAB_Hierarchy> getHierarchyWithProjectionOtherThanAll() {
        Set<EAB_Hierarchy> r    = new HashSet<>();
        
        for(ProjectionFragment pf_tmp : this.projectionList) {
            if(!pf_tmp.getLevel().isAllLevel()) {
                r.add(pf_tmp.getLevel().getHierarchy());
            }
        }
        
        return r;
    }
    
    /**
     * Return hierarchies for which there is at least one selection on a projection level.
     * For instance, if we have projection [CUSTOMER][COUNTRY] and selection [CUSTOMER][FRANCE],
     * the hierarchy CUSTOMER will be returned.
     * @return 
     */
    public Set<EAB_Hierarchy> getHierarchyWithSelection() {
        Set<EAB_Hierarchy> r    = new HashSet<>();
        
        for(EAB_Hierarchy h_tmp : this.getCube().getHierarchyList()) {
            if(!this.getSelectionFragmentByHierarchy(h_tmp).isEmpty()) {
                r.add(h_tmp);
            }
        }
        
        return r;
    }
    
    /**
     * Return hierarchies for which there is at least one selection on a projection level.
     * For instance, if we have projection [CUSTOMER][COUNTRY] and selection [CUSTOMER][FRANCE],
     * the hierarchy CUSTOMER will be returned.
     * @return 
     */
    public Set<EAB_Hierarchy> getHierarchyWithSelectionOnProjection() {
        Set<EAB_Hierarchy> r    = new HashSet<>();
        
        for(EAB_Hierarchy h_tmp : this.getHierarchyWithProjectionOtherThanAll()) {
            ProjectionFragment pf_tmp   = this.getProjectionFragmentByHierarchy(h_tmp);
            EAB_Level eab_l = pf_tmp.getLevel();
            for(SelectionFragment sf_tmp : this.getSelectionFragmentByHierarchy(eab_l.getHierarchy())) {
                if(sf_tmp.getLevel().equals(eab_l)) {
                    r.add(eab_l.getHierarchy());
                }
            }
        }
        
        return r;
    }
    
    /**
     * 
     * @param arg_hierarchy
     * @return 
     */
    public Set<EAB_Member> computeMemberListByHierarchy(EAB_Hierarchy arg_hierarchy) {

        // get members for each hierarchy
        Set<EAB_Member> memberList  = new HashSet<>();
        
        // ################################
        // FROM HERE, THIS IS NOT A MEASURE
        // ################################
        
        // iterator over hierarchy list
        ProjectionFragment pf   = this.getProjectionFragmentByHierarchy(arg_hierarchy);
        EAB_Level l             = pf.getLevel();
        
        // If the projection level is All for the current hierarchy, we do nothing
        // Moreover, in this case, there will no be selection on this hierarchy
        if(l.isAllLevel()) {
            memberList.add(l.getHierarchy().getAllMember());
            return memberList;
        }

        // ######################################################################
        // FROM HERE IN THIS LOOP, GBS LEVEL IS NOT ALL FOR THE CURRENT HIERARCHY
        // ######################################################################
        Set<SelectionFragment> sf_list  = this.getSelectionFragmentByHierarchy(arg_hierarchy);
        
        if(sf_list.isEmpty()) {
            // here there is no selection on the current hierarchy
            memberList  = new HashSet<>(l.getLevelMembers());
        } else {
            // @TODO ameliorer ici, prendre en compte plusieurs selections sur la meme hierarchie
            // pour le moment, je considere qu'il n'y a qu'une selection par hierarchie au max
            SelectionFragment sf        = this.getSelectionFragmentByHierarchy(arg_hierarchy).iterator().next();
                
            if(pf.getLevel().equals(sf.getLevel())) {
                // if the selection is on the same level as GBS
                memberList.add(sf.getMemberValue());
            } else {
                // here, there is a selection on the same hierarchy as GBS but different level
                // we return the children members at the level of the projection
                EAB_Level l_tmp = pf.getLevel();
                memberList      = sf.getMemberValue().getChildrenAtLevel(l_tmp);
            }
        }
        
        //
        return memberList;
        
    }
    
    /**
     * Computes the list of members for each hierarchy in a map.
     * @return 
     */
    public Map<EAB_Hierarchy, Set<EAB_Member>> computeMemberListByHierarchy() {
        Map<EAB_Hierarchy, Set<EAB_Member>> result  = new HashMap<>();
        
        for(EAB_Hierarchy h_tmp : this.getCube().getHierarchyList()) {
            result.put(h_tmp, this.computeMemberListByHierarchy(h_tmp));
        }

        return result;
    }
    
    /**
     * Computes the number of cells in the result without computing cells.
     * @return 
     */
    public Integer computeNumberOfCells() {
        Integer result  = 1;
        
        Map<EAB_Hierarchy, Set<EAB_Member>> mlbh    = this.computeMemberListByHierarchy();
        
        for(EAB_Hierarchy eab_h : this.getCube().getHierarchyList()) {
            result  = result * mlbh.get(eab_h).size();
        }
        
        // result may be negative
        if(result < 0) {
            return Integer.MAX_VALUE;
        }
        
        return result;
    }
    
    /**
     * Return hierarchies for which projections are under selection.
     * For instance, if we have projection [CUSTOMER][COUNTRY] and selection [CUSTOMER][REGION].[EUROPE],
     * the hierarchy CUSTOMER will be returned
     * @return 
     */
    public Set<EAB_Hierarchy> getHierarchyWithProjectionUnderSelection() {
        Set<EAB_Hierarchy> r    = new HashSet<>();
        
        for(EAB_Hierarchy h_tmp : this.getHierarchyWithProjectionOtherThanAll()) {
            ProjectionFragment pf_tmp   = this.getProjectionFragmentByHierarchy(h_tmp);
            EAB_Level eab_l = pf_tmp.getLevel();
            for(SelectionFragment sf_tmp : this.getSelectionFragmentByHierarchy(eab_l.getHierarchy())) {
                if(!sf_tmp.getLevel().equals(eab_l)) {
                    r.add(eab_l.getHierarchy());
                }
            }
        }
        
        return r;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.measureList);
        hash = 79 * hash + Objects.hashCode(this.projectionList);
        hash = 79 * hash + Objects.hashCode(this.selectionList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryTriplet other = (QueryTriplet) obj;
        if (!Objects.equals(this.measureList, other.measureList)) {
            return false;
        }
        if (!Objects.equals(this.projectionList, other.projectionList)) {
            return false;
        }
        if (!Objects.equals(this.selectionList, other.selectionList)) {
            return false;
        }
        return true;
    }
    
    
    
    /**
     * Executes the current query.
     * If the query is obtained from a conversion from a mdx query, the original query is executed.
     * If not, query is converted to mdx for execution.
     * @param arg_store
     * @return the Result object
     */
    @Override
    public Result execute(Boolean arg_store) {
        
        BenchmarkEngine be          = this.getCube().getBencharkEngine();
        
        // if the query has been obtained from a MDX query, we execute the original query
        if(this.getOriginalQuery() != null) {
            if(this.getOriginalQuery() instanceof QueryMdx) {
//                System.out.println("Executing original query:");
//                System.out.println(this.getOriginalQuery());
                QueryMdx q_mdx_tmp  = (QueryMdx)this.getOriginalQuery();
                return q_mdx_tmp.execute(arg_store);
            }
        }
        
        // if not, we convert the query to mdx and we execute it
        try {
            QueryConverter qc   = new QueryConverter(be);
            QueryMdx q_mdx      = qc.toMdx(this);
            
            //System.out.println("triplet:" + this.toString());
            //System.out.println("mdx:" + q_mdx.toString());
            
            return q_mdx.execute(arg_store);
        } catch(Exception arg_e) {
            System.out.println("Problem when executing the query!");
            arg_e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 
     * @param arg_store
     * @return 
     */
    public String executeWithOlap4j(Boolean arg_store) {
        String res  = "";
        
        FalsetoQueryConverter fc    = new FalsetoQueryConverter(this.getCube().getBencharkEngine());
        String mdxQuery             = fc.convertQuery(this).toMDX().toString();
        
//        System.out.println("Requete OLAP: " + mdxQuery);
        
        try {
            CellSet result  = this.getCube().getBencharkEngine().getConnection().getOlap4jConnection().createStatement().executeOlapQuery(mdxQuery);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            
            // on affiche le resultat grace au rectangular formatter
            new RectangularCellSetFormatter(true).format(result, pw);
//            new TraditionalCellSetFormatter().format(result, pw);
        
            res += sw.toString();
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
        
        return res;
    }
    
    /**
     * 
     * @param arg_store
     * @return 
     */
    public ResultStructure executePartially(Boolean arg_store) {
        ResultStructure res = new ResultStructure(this);
        
        res.computeMemberListByHierarchy();
        //res.computeCellList();
        
        return res;        
    }
    
    /**
     * 
     * @return 
     */
    public Result getResult() {
        if(this.result != null) {
            return this.result;
        } else {
        	return this.execute(Boolean.TRUE);
        	//return this.execute(Boolean.FALSE);
        }
    }
    

    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String result   = "";
        
        // MEASURES
        if(this.measureList.isEmpty()) {
            result  += "Default Measure (" + this.getCube().getDefaultMeasure().getName() + ")";
        }
        
        for (MeasureFragment mf : this.measureList) {
            result  += mf.toString() + ", ";
        }
        //result  = result.substring(0, result.length() - 2);
        result  += System.getProperty("line.separator");
        
        // PROJECTIONS
        for (ProjectionFragment pf : this.projectionList) {
            if(!pf.getLevel().isAllLevel()) {
                result  += pf.toString() + ", ";
            }
        }
        //result  = result.substring(0, result.length() - 2);
        result  += System.getProperty("line.separator");
        
        // SELECTIONS
        for (SelectionFragment sf : this.selectionList) {
            result  += sf.toString() + ", ";
        }
        //result  = result.substring(0, result.length() - 2);
        result  += System.getProperty("line.separator");
        
        return result;
    }
    
}
