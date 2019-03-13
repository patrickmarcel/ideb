/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mondrian.olap.Member;
import mondrian.olap.Position;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Measure;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 *
 * @author mahfoud
 */
public class QueryStats {

    /**
     * 
     */
    public QueryStats() {
        
    }
    
    /**
     *
     * @return
     */
    public Integer numberOfFilters() {
        return 0;
    }
    
    /**
     *
     * @return
     */
    public Integer numberOfAggregations() {
        return 0;
    }
    
    /**
     * 
     * @param arg_exp 
     */
    public void computeMemberFrequency(Exploration arg_exp) {
        Map<String, Integer> occurrences    = new HashMap<>();
            
        for(Query q_tmp : arg_exp.getWorkSession().getQueryList()) {
            Result res  = q_tmp.execute(Boolean.TRUE);
                
            mondrian.olap.Result mr = res.getMondrianResult();
                
            for(Position p_tmp : mr.getAxes()[0].getPositions()) {
                    
                for(Member m_tmp : p_tmp) {
                        
                    if(!occurrences.containsKey(m_tmp.getUniqueName())) {
                        occurrences.put(m_tmp.getUniqueName(), 1);
                    } else {
                        occurrences.put(m_tmp.getUniqueName(), occurrences.get(m_tmp.getUniqueName()) + 1);
                    }
                        
                }
                    
            }
                
            if(mr.getAxes().length > 1) {
                
                for(Position p_tmp : mr.getAxes()[1].getPositions()) {
                    
                    for(Member m_tmp : p_tmp) {
                        
                        if(!occurrences.containsKey(m_tmp.getUniqueName())) {
                            occurrences.put(m_tmp.getUniqueName(), 1);
                        } else {
                            occurrences.put(m_tmp.getUniqueName(), occurrences.get(m_tmp.getUniqueName()) + 1);
                        }
                            
                    }
                    
                }
                   
            }
            
        }
        
//        for(String k_tmp : occurrences.keySet()) {
//            System.out.println(k_tmp + "; " + occurrences.get(k_tmp));
//        }
        
    }
    
    /**
     * 
     * @param arg_exp 
     */
    public void computeQueryNumberPerMember(List<Query> arg_exp) {
        Map<String, Integer> occurrences    = new HashMap<>();
            
        for(Query q_tmp : arg_exp) {
            Result res  = q_tmp.execute(Boolean.TRUE);
            
            Set<Member> memberInQuery   = new HashSet<Member>();
            
            mondrian.olap.Result mr = res.getMondrianResult();
                
            // member is in columns?
            for(Position p_tmp : mr.getAxes()[0].getPositions()) {
                for(Member m_tmp : p_tmp) {
                    memberInQuery.add(m_tmp);
                }
            }
            
            // member is in rows?
            if(mr.getAxes().length > 1) {
                for(Position p_tmp : mr.getAxes()[1].getPositions()) {
                    for(Member m_tmp : p_tmp) {
                        memberInQuery.add(m_tmp);
                    }
                }
            }
            
            // update global occurrences record
            for(Member m_tmp : memberInQuery) {
                if(!occurrences.containsKey(m_tmp.getUniqueName())) {
                    occurrences.put(m_tmp.getUniqueName(), 1);
                } else {
                    occurrences.put(m_tmp.getUniqueName(), occurrences.get(m_tmp.getUniqueName()) + 1);
                }
            }
            
        }
        
        for(String k_tmp : occurrences.keySet()) {
            System.out.println(k_tmp + "; " + occurrences.get(k_tmp));
        }
        
    }
    
    /**
     * Computes the manifold query.
     * 
     * @param arg_exp 
     * @return  
     */
    public Query computeManifoldQuery(List<Query> arg_exp) {
        EAB_Cube cube       = arg_exp.get(0).getCube();
        QueryTriplet result = new QueryTriplet(cube);
        
        MdxToTripletQueryConverter qc   = new MdxToTripletQueryConverter();
        
        // transform query list to query triplet list
        List<QueryTriplet> qtList   = new ArrayList<>();
        for(Query q_tmp : arg_exp) {
            if(q_tmp instanceof QueryTriplet) {
                QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
                qtList.add(qt_tmp);
            } else {
                QueryTriplet qt_tmp = (QueryTriplet)qc.convert(q_tmp);
                qtList.add(qt_tmp);
            }
        }
        
        List<EAB_Measure> manifoldMeasureList   = new ArrayList<>();
        List<EAB_Member> manifoldSelectionList  = new ArrayList<>();
        List<EAB_Level> manifoldProjectionList  = new ArrayList<>();
        
        List<EAB_Measure> measureList           = new ArrayList<>();
        
        // treat measures
        for(Query q_tmp : qtList) {
            QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            
            for(MeasureFragment mf_tmp : qt_tmp.getMeasures()) {
                measureList.add(mf_tmp.getMeasure());
            }
            
            EAB_Measure modeMeasure = (EAB_Measure)Stats.mode(new ArrayList<Object>(measureList));
            manifoldMeasureList.add(modeMeasure);
        }
        
        // treat hierarchies
        for(EAB_Hierarchy h_tmp : cube.getHierarchyList()) {
            
            List<EAB_Member> selectionList  = new ArrayList<>();
            List<EAB_Level> projectionList  = new ArrayList<>();
            
            for(Query q_tmp : qtList) {
                QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
                for(SelectionFragment sf_tmp : qt_tmp.getSelectionFragmentByHierarchy(h_tmp)) {
                    selectionList.add(sf_tmp.getMemberValue());
                }
                
                // only one projection fragment per hierarchy
                ProjectionFragment pf_tmp   = qt_tmp.getProjectionFragmentByHierarchy(h_tmp);
                if(!pf_tmp.getLevel().isAllLevel()) {
                    projectionList.add(pf_tmp.getLevel());
                }
                
            }
            
//            System.out.println("For hierarchy " + h_tmp.getUniqueName());
            
            if(!selectionList.isEmpty()) {
                EAB_Member mostFrequentMember   = (EAB_Member)Stats.mode(new ArrayList<Object>(selectionList));
//                System.out.println("Most frequent member: " + mostFrequentMember.getUniqueName());
                manifoldSelectionList.add(mostFrequentMember);
            }
            
            if(!projectionList.isEmpty()) {
                EAB_Level mostFrequentLevel     = (EAB_Level)Stats.mode(new ArrayList<Object>(projectionList));
//                System.out.println("Most frequent level: " + mostFrequentLevel.getUniqueName());
                manifoldProjectionList.add(mostFrequentLevel);
            }
            
            for(EAB_Measure m_tmp : manifoldMeasureList) {
                MeasureFragment mf  = new MeasureFragment(result, m_tmp);
                result.addMeasure(mf);
            }
            
            for(EAB_Member m_tmp : manifoldSelectionList) {
                SelectionFragment sf    = new SelectionFragment(result, m_tmp);
                result.addSelection(sf);
            }
            
            for(EAB_Level l_tmp : manifoldProjectionList) {
                ProjectionFragment pf   = new ProjectionFragment(result, l_tmp);
                result.addProjection(pf);
            }
            
        }

        return result;
    }
    
    /**
     * Computes the manifold query.
     * 
     * @param arg_exp 
     * @return  
     */
    public Query computeCoveringQuery(List<Query> arg_exp) {
        EAB_Cube cube       = arg_exp.get(0).getCube();
        QueryTriplet result = new QueryTriplet(cube);
        
        MdxToTripletQueryConverter qc   = new MdxToTripletQueryConverter();
        
        // transform query list to query triplet list
        List<QueryTriplet> qtList   = new ArrayList<>();
        for(Query q_tmp : arg_exp) {
            if(q_tmp instanceof QueryTriplet) {
                QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
                qtList.add(qt_tmp);
            } else {
                QueryTriplet qt_tmp = (QueryTriplet)qc.convert(q_tmp);
                qtList.add(qt_tmp);
            }
        }
        
        List<EAB_Measure> manifoldMeasureList   = new ArrayList<>();
        List<EAB_Member> manifoldSelectionList  = new ArrayList<>();
        List<EAB_Level> manifoldProjectionList  = new ArrayList<>();
        
        List<EAB_Measure> measureList           = new ArrayList<>();
        
        // treat measures
        for(Query q_tmp : qtList) {
            QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            
            for(MeasureFragment mf_tmp : qt_tmp.getMeasures()) {
                measureList.add(mf_tmp.getMeasure());
            }
            
            EAB_Measure modeMeasure = (EAB_Measure)Stats.mode(new ArrayList<Object>(measureList));
            manifoldMeasureList.add(modeMeasure);
        }
        
        // treat hierarchies
        for(EAB_Hierarchy h_tmp : cube.getHierarchyList()) {
            
            List<EAB_Member> selectionList  = new ArrayList<>();
            List<EAB_Level> projectionList  = new ArrayList<>();
            
            for(Query q_tmp : qtList) {
                QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
                for(SelectionFragment sf_tmp : qt_tmp.getSelectionFragmentByHierarchy(h_tmp)) {
                    selectionList.add(sf_tmp.getMemberValue());
                }
                
                // only one projection fragment per hierarchy
                ProjectionFragment pf_tmp   = qt_tmp.getProjectionFragmentByHierarchy(h_tmp);
                if(!pf_tmp.getLevel().isAllLevel()) {
                    projectionList.add(pf_tmp.getLevel());
                }
                
            }
            
//            System.out.println("For hierarchy " + h_tmp.getUniqueName());
            // if there is no selection, we 
            if(!selectionList.isEmpty()) {
                
                EAB_Level higherLevel   = selectionList.get(0).getLevel();
                
                if(selectionList.size() > 1) {
                    for(int i = 1; i < selectionList.size(); i++) {
                        System.out.println("Position " + i);
                        if(higherLevel.isChildOf(selectionList.get(i).getLevel())) {
                            higherLevel = selectionList.get(i).getLevel();
                        }
                    }
                }
                
                // add all filters that are at the higher level
                for(EAB_Member m_tmp : selectionList) {
                    if(m_tmp.getLevel().equals(higherLevel)) {
                        manifoldSelectionList.add(m_tmp);
                    }
                }
                
            }
            
            // projections...
            if(!projectionList.isEmpty()) {
                EAB_Level mostRelaxedLevel      = projectionList.get(0);
                
                if(projectionList.size() > 1) {
                    for(int i = 1; i < projectionList.size(); i++) {
                        if(mostRelaxedLevel.isChildOf(projectionList.get(i))) {
                            mostRelaxedLevel    = projectionList.get(i);
                        }
                    }
                }
                System.out.println("Most relaxed level: " + mostRelaxedLevel.getUniqueName());
                manifoldProjectionList.add(mostRelaxedLevel);
            }
            
            for(EAB_Measure m_tmp : manifoldMeasureList) {
                MeasureFragment mf  = new MeasureFragment(result, m_tmp);
                result.addMeasure(mf);
            }
            
            for(EAB_Member m_tmp : manifoldSelectionList) {
                SelectionFragment sf    = new SelectionFragment(result, m_tmp);
                result.addSelection(sf);
            }
            
            for(EAB_Level l_tmp : manifoldProjectionList) {
                ProjectionFragment pf   = new ProjectionFragment(result, l_tmp);
                result.addProjection(pf);
            }
            
        }

        return result;
    }
    
    /**
     * 
     * @param arg_exp
     * @return 
     */
    public Map<EAB_Hierarchy, Map<EAB_Member, Integer>> computeMemberFrequencyInQueryText(Exploration arg_exp) {
        
        Map<EAB_Hierarchy, Map<EAB_Member, Integer>> result = new HashMap<>();
        
        // for all the hierarchies of the cube
        for(EAB_Hierarchy h_tmp : arg_exp.getWorkSession().pickRandomQuery().getCube().getHierarchyList()) {
        
            Map<EAB_Member, Integer> memberToFrequency    = new HashMap<>();
        
            // for each query in the exploration
            for(Query q_tmp : arg_exp.getWorkSession().getQueryList()) {
                QueryConverter qc    = new QueryConverter(arg_exp.getBenchmarkEngine());
                
                QueryTriplet qt_tmp = qc.convertToQueryTriplet((QueryMdx)q_tmp);
            
                System.out.println("Query triplet:");
                System.out.println(qt_tmp);
                
                // for each selection fragment
                for(SelectionFragment sf_tmp : qt_tmp.getSelectionFragmentByHierarchy(h_tmp)) {
                    EAB_Level l_tmp     = sf_tmp.getLevel();
                    EAB_Member m_tmp    = sf_tmp.getMemberValue();
                    
                    // only if the level is child of level All
                    if(l_tmp.isDirectChildOf(h_tmp.getAllLevel())) {
                    
                        // increment frequency if present
                        if(memberToFrequency.containsKey(m_tmp)) {
                            Integer int_ref = memberToFrequency.get(m_tmp) + 1;
                            memberToFrequency.replace(m_tmp, int_ref);
                        } else {
                            memberToFrequency.put(m_tmp, 1);
                        }
                    
                    }
                }
            }
        
            // we add the frequency for the current hierarchy
            result.put(h_tmp, memberToFrequency);
        }
        
        return result;
    }
    

}
        
        
    