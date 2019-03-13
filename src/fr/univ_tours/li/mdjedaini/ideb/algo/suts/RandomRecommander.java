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
 * The random recommender recommends random queries.
 * It just generate random queries for a given cube. It is used for test purposes.
 * @author mahfoud
 */
public class RandomRecommander implements I_SUT {

    //

    /**
     *
     */
    public EAB_Cube cube;

    /**
     *
     */
    public Log log;
    
    Integer nbMaxOfMeasures;
    Integer nbMaxOfNonAllHierarchies;
    
    Integer projectionProbability;
    Integer selectionProbability;
    
    /**
     * 
     */
    public RandomRecommander() {
        this.nbMaxOfMeasures            = 1;
        this.nbMaxOfNonAllHierarchies   = 1;
        
        this.projectionProbability  = 50;
        this.selectionProbability   = 50;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "SUT-Random";
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        
        List<Query> result  = new ArrayList<>();

        Query q_tmp = this.generateRandomQuery();
        
//        System.out.println("RR: Recommendation done!");
//        System.out.println(q_tmp);
        
        result.add(q_tmp);
        
        return result;
    }
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
//        System.out.println("I am the Random Recommander: I am reading the task bundle...");
        this.cube   = arg_tb.getCube();
        this.log    = arg_tb.getLog();
    }
    
    /**
     * 
     * @return 
     */
    public Query generateRandomQuery() {
        Query result            = new QueryTriplet(this.cube);
        QueryTriplet qt_result  = (QueryTriplet)result;
        
        // generate the measures
        this.generateMeasures(qt_result);
        
        // for each hierarchy generate random (selection and/or projection)
        for(EAB_Hierarchy h_tmp : this.cube.getHierarchyList()) {
            
            // nb max of non-all projection reached, we exit the loop
            if(qt_result.getHierarchyWithProjectionOtherThanAll().size() >= this.nbMaxOfNonAllHierarchies) {
                break;
            }
            
            Random randomizer   = new Random();
            Integer s_random    = randomizer.nextInt(100);
            Integer p_random    = randomizer.nextInt(100);
            
            // what to generate on the given hierarchy h_tmp: selection and/or projection
            if(s_random < this.selectionProbability && p_random < this.projectionProbability) {
                this.generateProjectionAndSelectionOnHierarchy(qt_result, h_tmp);
            } else {
                if(s_random < this.selectionProbability) {
                    this.generateSelectionOnHierarchy(qt_result, h_tmp);
                }
                if(p_random < this.projectionProbability) {
                    this.generateProjectionOnHierarchy(qt_result, h_tmp);
                }
            }
            
        }
        
        // return the query built
        return result;
    }
    
    /**
     * 
     */
    private void generateMeasures(QueryTriplet arg_qt) {
//        System.out.println("RR: Generating measures...");
        Random randomizer   = new Random();
        
        // ADD MEASURES TO THE RANDOM QUERY
        // we have to add +1 because the upper bound of nextint is excluded
        Integer nbMeasures  = randomizer.nextInt(this.nbMaxOfMeasures) + 1;
//        while(nbMeasures == 0) {
//            nbMeasures  = randomizer.nextInt(this.nbMaxOfMeasures + 1);
//        }
        
        for (int i = 0; i < nbMeasures; i++) {
            MeasureFragment mf  = new MeasureFragment(arg_qt, this.cube.getRandomMeasure());
            arg_qt.addMeasure(mf);
        }
    }
    
    /**
     * 
     * @param arg_h
     * @return 
     */
    private ProjectionFragment generateProjectionOnHierarchy(QueryTriplet arg_qt, EAB_Hierarchy arg_h) {
        EAB_Level l_tmp         = arg_h.getRandomLevel();
        ProjectionFragment pf   = new ProjectionFragment(arg_qt, l_tmp);
        arg_qt.addProjection(pf);
        return pf;
    }
    
    /**
     * 
     * @param arg_h
     * @return 
     */
    private SelectionFragment generateSelectionOnHierarchy(QueryTriplet arg_qt, EAB_Hierarchy arg_h) {
//        System.out.println("RR: Generation selection on hierarchy " + arg_h.getName());
        EAB_Member m_tmp        = arg_h.getRandomMember();
        SelectionFragment sf    = new SelectionFragment(arg_qt, m_tmp);
        arg_qt.addSelection(sf);
        return sf;
    }
    
    /**
     * 
     * @param arg_h
     * @return 
     */
    private void generateProjectionAndSelectionOnHierarchy(QueryTriplet arg_qt, EAB_Hierarchy arg_h) {
//        System.out.println("RR: Generation projection AND selection on hierarchy " + arg_h.getName());
        // we first generate the projection level
        ProjectionFragment pf   = this.generateProjectionOnHierarchy(arg_qt, arg_h);
        EAB_Level l_projection  = pf.getLevel();
        
        // the selection must be done at a level higher than the projection level
        EAB_Level l_selection   = arg_h.getRandomLevelAboveLevel(l_projection);
        EAB_Member m_tmp        = l_selection.getRandomMember();
        SelectionFragment sf    = new SelectionFragment(arg_qt, m_tmp);
        arg_qt.addSelection(sf);
    }
    
}
