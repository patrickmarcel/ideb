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
public class RealUserRecommander implements I_SUT {

    //
    EAB_Cube cube;
    Log log;
    
    Integer nbMaxOfMeasures;
    Integer nbMaxOfNonAllHierarchies;
    
    Integer projectionProbability;
    Integer selectionProbability;
    
    /**
     * 
     * @param arg_cube
     * @param arg_log 
     */
    public RealUserRecommander(EAB_Cube arg_cube, Log arg_log) {
        this.cube   = arg_cube;
        this.log    = arg_log;
        this.projectionProbability  = 50;
        this.selectionProbability   = 50;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "I am the Real User Recommender";
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        List<Query> result  = new ArrayList<>();

        result.add(this.generateRandomQuery());
        
        return result;
    }
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        Random randomizer   = new Random();
        
        // ADD MEASURES TO THE RANDOM QUERY
        Integer nbMeasures  = randomizer.nextInt(this.cube.cubeMeasureList.size());
        while(nbMeasures == 0) {
            nbMeasures  = randomizer.nextInt(this.cube.cubeMeasureList.size());
        }
        
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
        ProjectionFragment pf   = new ProjectionFragment(arg_qt, arg_h.getRandomLevel());
        arg_qt.addProjection(pf);
        return pf;
    }
    
    /**
     * 
     * @param arg_h
     * @return 
     */
    private SelectionFragment generateSelectionOnHierarchy(QueryTriplet arg_qt, EAB_Hierarchy arg_h) {
        SelectionFragment sf    = new SelectionFragment(arg_qt, arg_h.getRandomMember());
        arg_qt.addSelection(sf);
        return sf;
    }
    
    /**
     * 
     * @param arg_h
     * @return 
     */
    private void generateProjectionAndSelectionOnHierarchy(QueryTriplet arg_qt, EAB_Hierarchy arg_h) {
        // we first generate the projection level
        ProjectionFragment pf   = this.generateProjectionOnHierarchy(arg_qt, arg_h);
        EAB_Level l_projection  = pf.getLevel();
        
        // the selection must be done at a level higher than the projection level
        EAB_Level l_selection   = arg_h.getRandomLevelAboveLevel(l_projection);
        SelectionFragment sf    = new SelectionFragment(arg_qt, l_selection.getRandomMember());
        arg_qt.addSelection(sf);
    }
    
}
