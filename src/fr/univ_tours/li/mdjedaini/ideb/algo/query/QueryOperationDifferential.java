/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;

/**
 *
 * @author mahfoud
 */
public class QueryOperationDifferential {

    QueryConverter qc;
    
    /**
     * 
     * @param arg_be 
     */
    public QueryOperationDifferential(BenchmarkEngine arg_be) {
        this.qc = new QueryConverter(arg_be);
    }
    
    /**
     * Computes the edition distance between two olap queries.
     * todo est-ce que les requetes doivent etre sur le meme cube? oui je pense...
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public List<MeasureFragment> addedMeasures(Query arg_q1, Query arg_q2) {
        
        List<MeasureFragment> result  = new ArrayList<>();
                
        QueryTriplet qt1    = this.qc.toQueryTriplet(arg_q1);
        QueryTriplet qt2    = this.qc.toQueryTriplet(arg_q2);
        
        for(MeasureFragment mf_tmp : qt2.getMeasures()) {
            if(!qt1.getMeasures().contains(mf_tmp)) {
                result.add(mf_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * Computes the edition distance between two olap queries.
     * todo est-ce que les requetes doivent etre sur le meme cube? oui je pense...
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public List<MeasureFragment> removedMeasures(Query arg_q1, Query arg_q2) {
        
        List<MeasureFragment> result  = new ArrayList<>();
                
        QueryTriplet qt1    = this.qc.toQueryTriplet(arg_q1);
        QueryTriplet qt2    = this.qc.toQueryTriplet(arg_q2);
        
        for(MeasureFragment mf_tmp : qt1.getMeasures()) {
            if(!qt2.getMeasures().contains(mf_tmp)) {
                result.add(mf_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * Computes the edition distance between two olap queries.
     * todo est-ce que les requetes doivent etre sur le meme cube? oui je pense...
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public List<SelectionFragment> addedFilters(Query arg_q1, Query arg_q2) {
        
        List<SelectionFragment> result  = new ArrayList<>();
                
        QueryTriplet qt1    = this.qc.toQueryTriplet(arg_q1);
        QueryTriplet qt2    = this.qc.toQueryTriplet(arg_q2);
        
        for(SelectionFragment sf_tmp : qt2.getSelectionFragments()) {
            Set<SelectionFragment> q1sf = qt1.getSelectionFragments();
            if(!q1sf.contains(sf_tmp)) {
                result.add(sf_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * Computes the edition distance between two olap queries.
     * todo est-ce que les requetes doivent etre sur le meme cube? oui je pense...
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public List<SelectionFragment> removedFilters(Query arg_q1, Query arg_q2) {
        
        List<SelectionFragment> result  = new ArrayList<>();
                
        QueryTriplet qt1    = this.qc.toQueryTriplet(arg_q1);
        QueryTriplet qt2    = this.qc.toQueryTriplet(arg_q2);
        
        for(SelectionFragment sf_tmp : qt1.getSelectionFragments()) {
            if(!qt2.getSelectionFragments().contains(sf_tmp)) {
                result.add(sf_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * Computes the edition distance between two olap queries.
     * todo est-ce que les requetes doivent etre sur le meme cube? oui je pense...
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public Map<EAB_Hierarchy, List<ProjectionFragment>> drillDown(Query arg_q1, Query arg_q2) {
        Map<EAB_Hierarchy, List<ProjectionFragment>> result = new HashMap<>();
       
        QueryTriplet qt1    = this.qc.toQueryTriplet(arg_q1);
        QueryTriplet qt2    = this.qc.toQueryTriplet(arg_q2);
        
        for(EAB_Hierarchy h_tmp : qt1.getCube().getHierarchyList()) {
            
            ProjectionFragment pf1  = qt1.getProjectionFragmentByHierarchy(h_tmp);
            ProjectionFragment pf2  = qt2.getProjectionFragmentByHierarchy(h_tmp);

            if(pf2.getLevel().isChildOf(pf1.getLevel())) {
                List<ProjectionFragment> r_tmp  = new ArrayList<>();
                r_tmp.add(pf1);
                r_tmp.add(pf2);
                result.put(h_tmp, r_tmp);
            }
            
        }
        
        return result;
    }
    
    /**
     * Computes the edition distance between two olap queries.
     * todo est-ce que les requetes doivent etre sur le meme cube? oui je pense...
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public Map<EAB_Hierarchy, List<ProjectionFragment>> rollUp(Query arg_q1, Query arg_q2) {
        Map<EAB_Hierarchy, List<ProjectionFragment>> result = new HashMap<>();
       
        QueryTriplet qt1    = this.qc.toQueryTriplet(arg_q1);
        QueryTriplet qt2    = this.qc.toQueryTriplet(arg_q2);
        
        for(EAB_Hierarchy h_tmp : qt1.getCube().getHierarchyList()) {
            
            ProjectionFragment pf1  = qt1.getProjectionFragmentByHierarchy(h_tmp);
            ProjectionFragment pf2  = qt2.getProjectionFragmentByHierarchy(h_tmp);

            if(pf1.getLevel().isChildOf(pf2.getLevel())) {
                List<ProjectionFragment> r_tmp  = new ArrayList<>();
                r_tmp.add(pf1);
                r_tmp.add(pf2);
                result.put(h_tmp, r_tmp);
            }
            
        }
        
        return result;
    }
    
}

