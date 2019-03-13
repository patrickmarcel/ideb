/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.UserLog;

/**
 *
 * @author mahfoud
 */
public class PickRandomFromLogRecommander implements I_SUT {

    //

    /**
     *
     */
    public EAB_Cube cube;

    /**
     *
     */
    public Log log;
    
    // task bundle

    /**
     *
     */
    public TaskBundle tb;
    
    /**
     * 
     */
    public PickRandomFromLogRecommander() {
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "I am the Pick Random Query List From Log Recommender";
    }
    
    /**
     * Recommends a random query from the log.
     * We first pick a non empty session, and then we take a random query from it.
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        List<Query> result  = new ArrayList<>();
        
        UserLog ul  = this.cube.getBencharkEngine().getBenchmarkData().getUserLog(this.tb.getCurrentUser());
        
        result.addAll(ul.pickRandomQueryList(1));
        
        return result;
    }
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        System.out.println("I am the Pick Random From Log Recommander: I am reading the task bundle...");
        this.cube   = arg_tb.getCube();
        this.log    = arg_tb.getLog();
        this.tb     = arg_tb;
    }
    
}
