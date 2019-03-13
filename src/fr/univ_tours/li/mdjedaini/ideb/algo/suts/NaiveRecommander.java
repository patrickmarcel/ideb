/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 * 
 * @author mahfoud
 */
public class NaiveRecommander implements I_SUT {

    // task bundle
    TaskBundle tb;
    
    //
    EAB_Cube cube;
    Log log;
    
    /**
     * 
     */
    public NaiveRecommander() {
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        List<Query> result  = new ArrayList<>();

        // we recommand next query if last query exists...
        if(this.tb.getCurrentSession().getNumberOfQueries() > 0) {
            result.add(this.generateNextQuery(this.tb.getCurrentSession().getLastQuery()));
        } else {
            // if the session is empty, we rely on random recommander...
            RandomRecommander rr    = new RandomRecommander();
            rr.readTaskBundle(this.tb);
            result.add(rr.recommand().iterator().next());
        }
        
        return result;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "SUT-Naive";
    }
    
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        this.tb     = arg_tb;
        this.cube   = arg_tb.getCube();
        this.log    = arg_tb.getLog();
    }
    
    /**
     * 
     * @param arg_q
     * @return 
     */
    public Query generateNextQuery(Query arg_q) {
        Query result            = new QueryTriplet(this.cube);
        
        QueryTriplet previous   = (QueryTriplet)arg_q;

//        System.out.println("Requete d'entree");
//        System.out.println(previous);
        
        QueryTriplet qt_result  = (QueryTriplet)result;

        // ###############
        // we copy the query
        for(MeasureFragment mf_tmp : previous.getMeasures()) {
            MeasureFragment mf  = new MeasureFragment(qt_result, mf_tmp.getMeasure());
            qt_result.addMeasure(mf);
        }
        
        for(ProjectionFragment pf_tmp : previous.getProjectionFragments()) {
            ProjectionFragment pf   = new ProjectionFragment(qt_result, pf_tmp.getLevel());
            qt_result.addProjection(pf);
        }
        
        for(SelectionFragment sf_tmp : previous.getSelectionFragments()) {
            SelectionFragment sf    = new SelectionFragment(qt_result, sf_tmp.getMemberValue());
            qt_result.addSelection(sf);
        }
        
        // query is copied
        // ###############
        
        Random rg           = new Random();
        Integer probability = rg.nextInt(100);
        
        // equa probability of changing projection, or selection...
        // if the query has no selection, we translate the projection...
        if(qt_result.getHierarchyWithProjectionOtherThanAll().size() < 2) {
        //if(probability < 33) {
            this.translateProjection(result);
        } else if(probability > 33 && probability < 66) {
            this.translateSelection(result);
        } else {
//            System.out.println("I apply a filter...");
            this.applyFilter(result);
        }
        
        
//        System.out.println("Requete de sortie");
//        System.out.println(result);
        
        // return the query built
        return result;
    }
    
    /**
     * 
     * @param arg_q 
     */
    public void translateMeasure(Query arg_q) {
        System.out.println("I do not manage measure translations for the moment...");
    }
    
    /**
     * 
     * @param arg_q 
     */
    public void translateProjection(Query arg_q) {
        QueryTriplet qt_result  = (QueryTriplet)arg_q;
        
        // normally, pf_tmp should never be null because we instantiate each hierarchy with All projection
        EAB_Hierarchy h_tmp         = this.cube.getRandomHierarchy();
        ProjectionFragment pf_tmp   = qt_result.getProjectionFragmentByHierarchy(h_tmp);
        
        EAB_Level l_tmp = this.cube.getRandomLevelByHierarchy(h_tmp);
        
        //if(!l_tmp.equals(pf_tmp.getLevel())) {
        
        // a new possibility is to check if the level is directly linked to the previous...
        if(l_tmp.isDirectChildOf(pf_tmp.getLevel()) || pf_tmp.getLevel().isDirectChildOf(l_tmp)) {
            ProjectionFragment pf   = new ProjectionFragment(qt_result, l_tmp);
            qt_result.addProjection(pf);
        } else {
            // replay the function until its ok...
            this.translateProjection(arg_q);
        }
        
    }
    
    /**
     * 
     * @param arg_q 
     */
    public void translateSelection(Query arg_q) {
        QueryTriplet qt_result  = (QueryTriplet)arg_q;
        
        // if no projection different than All, we cannot add a filter
        if(qt_result.getSelectionFragments().isEmpty()) {
            return;
        }
        
        // we previously checked (before calling the function) that there are selections...
        SelectionFragment sf_chosen     = qt_result.getSelectionFragments().iterator().next();
        
        // we pick a random selection fragment...
        EAB_Member m_old    = sf_chosen.getMemberValue();
        EAB_Member m_new    = m_old.getLevel().getRandomMember();
            
        SelectionFragment sf    = new SelectionFragment(qt_result, m_new);
            
        qt_result.removeSelection(sf_chosen);
        qt_result.addSelection(sf);
        
    }
    
    /**
     * 
     * @param arg_q 
     */
    public void applyFilter(Query arg_q) {
        QueryTriplet qt_result  = (QueryTriplet)arg_q;
        
        // if no projection different than All, we cannot add a filter
        if(qt_result.getHierarchyWithProjectionOtherThanAll().isEmpty()) {
            return;
        }
        
        // hierarchy non All
        EAB_Hierarchy h_tmp     = qt_result.getHierarchyWithProjectionOtherThanAll().iterator().next();
        
        EAB_Level l_tmp         = qt_result.getProjectionFragmentByHierarchy(h_tmp).getLevel();

        // we remove the previous filters...
        qt_result.removeSelectionByHierarchy(h_tmp);
        
        if(l_tmp.getParentLevel().isAllLevel()) {
            EAB_Member m_tmp            = l_tmp.getRandomMember();
            SelectionFragment sf_tmp    = new SelectionFragment(qt_result, m_tmp);
            qt_result.addSelection(sf_tmp);
        } else {
            EAB_Member m_tmp            = l_tmp.getParentLevel().getRandomMember();
            SelectionFragment sf_tmp    = new SelectionFragment(qt_result, m_tmp);
            qt_result.addSelection(sf_tmp);
        }
        
    }
    
}
