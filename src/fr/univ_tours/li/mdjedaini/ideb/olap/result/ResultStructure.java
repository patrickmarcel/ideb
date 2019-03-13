/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap.result;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mahfoud
 */
public class ResultStructure {
    
    //
    QueryTriplet q;
    
    //
    CellList cellList;
    Map<EAB_Hierarchy, Set<EAB_Member>> memberListByHierarchy;
    
    /**
     * 
     * @param arg_q 
     */
    public ResultStructure(QueryTriplet arg_q) {
        this.q                      = arg_q;
        this.memberListByHierarchy  = new HashMap<>();
    }

    
    public ResultStructure(Map<EAB_Hierarchy, Set<EAB_Member>> memberListByHierarchy) {
        //this.q                      = arg_q;
        this.memberListByHierarchy  =memberListByHierarchy;
    }
    
    /**
     * 
     * @return 
     */
    public CellList getCellList() {
        return this.cellList;
    }

    /**
     * 
     * @return 
     */
    public QueryTriplet getQuery() {
        return this.q;
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Cube getCube() {
        return this.getQuery().getCube();
    }
    
    /**
     * Return hierarchies for which there is only one member different than All.
     * 
     * @return 
     */
    public Set<EAB_Hierarchy> getUniMemberHierarchy() {
        Set<EAB_Hierarchy> r    = new HashSet<>();
        
        for(Set<EAB_Member> m_list : this.memberListByHierarchy.values()) {
            if(m_list.size() == 1 && !m_list.iterator().next().isAll()) {
                r.add((EAB_Hierarchy)m_list.iterator().next().getLevel().getHierarchy());
            }
        }
        
        return r;
    }
    
    /**
     * 
     * @return 
     */
    public Set<EAB_Hierarchy> getMultiMemberHierarchy() {
        Set<EAB_Hierarchy> r    = new HashSet<>();
        
        for(Set<EAB_Member> m_list : this.memberListByHierarchy.values()) {
            if(m_list.size() > 1) {
                r.add((EAB_Hierarchy)m_list.iterator().next().getLevel().getHierarchy());
            }
        }
        
        return r;
    }
    
    /**
     * Computes the number of cells in the result without computing cells.
     * @return 
     */
    public Integer computeNumberOfCells() {
        Integer result  = 1;
        
        this.computeMemberListByHierarchy();
        
        for(EAB_Hierarchy eab_h : this.getCube().getHierarchyList()) {
            result  = result * this.memberListByHierarchy.get(eab_h).size();
        }
        
        // result may be negative
        if(result < 0) {
            return Integer.MAX_VALUE;
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Integer computeNumberOfGroups() {
        Integer result  = 1;
        
        for(EAB_Hierarchy eab_h : this.getMultiMemberHierarchy()) {
            result  = result * this.memberListByHierarchy.get(eab_h).size();
        }
        
        return result;
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
        ProjectionFragment pf   = this.getQuery().getProjectionFragmentByHierarchy(arg_hierarchy);
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
        Set<SelectionFragment> sf_list  = this.getQuery().getSelectionFragmentByHierarchy(arg_hierarchy);
        
        if(sf_list.isEmpty()) {
            // here there is no selection on the current hierarchy
            memberList  = new HashSet<>(l.getLevelMembers());
        } else {
            // @TODO ameliorer ici, prendre en compte plusieurs selections sur la meme hierarchie
            // pour le moment, je considere qu'il n'y a qu'une selection par hierarchie au max
            SelectionFragment sf        = this.getQuery().getSelectionFragmentByHierarchy(arg_hierarchy).iterator().next();
                
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
     * 
     * @return 
     */
    public Map<EAB_Hierarchy, Set<EAB_Member>> getMemberListByHierarchy() {
        return this.memberListByHierarchy;
    }
    
    /**
     * Computes the list of members for each hierarchy in a map.
     * @return 
     */
    public Map<EAB_Hierarchy, Set<EAB_Member>> computeMemberListByHierarchy() {

        for(EAB_Hierarchy h_tmp : this.getCube().getHierarchyList()) {
            this.memberListByHierarchy.put(h_tmp, this.computeMemberListByHierarchy(h_tmp));
        }

        return this.memberListByHierarchy;
        
    }
    
    /**
     * Computes the list of the cells contained in the result.
     * The cells are computed only if they have not been computed yet...
     * 
     * @return 
     */
//    public CellList computeCellList() {
//        CellList result = new CellList();
//        
//        //System.out.println("Computing cell list of query " + this.getQuery().getQid() + " - " + this.computeNumberOfCells() + " cells...");
//        //System.out.println(this.getQuery());
//        // we do not recreate the cells if they exist...
//        if(this.cellList != null) {
//            //System.err.println("Les cellules sont deja calculees...");
////            return this.cellList;
//        }
//        
//        // pour l'instant, je ne stocke pas le resultat!
//        // /!\ je dois initialiser la structure
//        // this.cellList   = new CellList();
//        
//        this.computeMemberListByHierarchy();
//        
//        List<Set<EAB_Member>> ml = new ArrayList<>();
//        ml.addAll(this.memberListByHierarchy.values());
//        
//        // BE CAREFUL: cartesianProduct takes as argument a List of Set and returns a Set of List
//        Set<List<EAB_Member>> cartesianProduct  = com.google.common.collect.Sets.cartesianProduct(ml);
//
//        // we go through the set of lists
//        Iterator<List<EAB_Member>> it_cartesianProduct  = cartesianProduct.iterator();
//        // id temporaire pour l'ajout dans la cellList
//        Integer id_tmp  = 0;
//        while(it_cartesianProduct.hasNext()) {
//            
//            for(MeasureFragment mf_tmp : this.q.getMeasures()) {
//                EAB_Cell c_tmp  = new EAB_Cell(this, mf_tmp.getMeasure());
//                id_tmp++;
//                //c_tmp.addQueryByID(this.qid);
//            
//                Iterator<EAB_Member> it_member  = it_cartesianProduct.next().iterator();
//                while(it_member.hasNext()) {
//                    EAB_Member eab_mtmp = it_member.next();
//                    // add the member to the current cell
//                    // System.out.println("Adding member - " + eab_mtmp.getUniqueName());
//                    c_tmp.addMember(eab_mtmp);
//                }
//                
//                result.addCell(c_tmp);
//            }
//            
//        }
//        
//        return result;
//        
//    }
    
    /**
     * 
     * @return 
     */
//    public Set<CellGroup> computeCellGroupList() {
//        Set<CellGroup> result   = new HashSet<>();
//
//        for(EAB_Hierarchy h_tmp : this.getMultiMemberHierarchy()) {
//            for(CellGroup cg_tmp : this.computeCellGroupFromVariant(h_tmp)) {
//                result.add(cg_tmp);
//            }
//        }
//        
//        return result;
//    }
    
//    /**
//     * 
//     * @param arg_variantHierarchy
//     * @return 
//     */
//    public Set<CellGroup> computeCellGroupFromVariant(EAB_Hierarchy arg_variantHierarchy) {
//        Set<CellGroup> result   = new HashSet<>();
//        
//        EAB_Cell c  = this.getCellList().getCellCollection().iterator().next();
//        
//        Map<EAB_Hierarchy, EAB_Member> criteria = new HashMap<>();
//        criteria.put(arg_variantHierarchy, c.getMemberByHierarchy(arg_variantHierarchy));
//        
//        Set<EAB_Cell> cubeFace  = this.getCellList().searchCell(criteria);
//        for(EAB_Cell c_tmp : cubeFace) {
//            result.add(this.computeCellGroupFromCellAndVariant(c_tmp, arg_variantHierarchy));
//        }
//
//        return result;
//    }
    
//    /**
//     * 
//     * @param arg_c
//     * @param arg_variantHierarchy
//     * @return 
//     */
//    public CellGroup computeCellGroupFromCellAndVariant(EAB_Cell arg_c, EAB_Hierarchy arg_variantHierarchy) {
//        Map<EAB_Hierarchy, EAB_Member> invariantList    = new HashMap<>();
//        
//        for(EAB_Hierarchy h_tmp : this.memberListByHierarchy.keySet()) {
//            if(!h_tmp.equals(arg_variantHierarchy)) {
//                invariantList.put(h_tmp, arg_c.getMemberByHierarchy(h_tmp));
//            }
//        }
//        
//        CellGroup g = new CellGroup(this, arg_variantHierarchy, invariantList);
//        
//        for(EAB_Cell c_tmp : this.getCellList().searchCell(invariantList)) {
//            g.addCell(c_tmp);
//        }
//        
//        return g;
//    }
    
    /**
     * 
     * @return 
     */
    public String toString() {
        String result   = "";
        
        for(EAB_Hierarchy eab_h : this.getUniMemberHierarchy()) {
            result += this.memberListByHierarchy.get(eab_h);
        }
                
        result  += System.lineSeparator();
        result  += "----------------------";
        result  += System.lineSeparator();
        
        for(EAB_Hierarchy eab_h : this.getMultiMemberHierarchy()) {
            for(EAB_Member eab_m : this.memberListByHierarchy.get(eab_h)) {
                result  += eab_m.toString() + " | ";
            }
            result  += System.lineSeparator();
        }
        
        return result;
    }
    
}
