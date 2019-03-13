/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 *
 * @author mahfoud
 */
public class SUT_UserModel implements I_SUT {

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
    public SUT_UserModel() {
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "SUT-UserModel";
    }
    
    /**
     * Recommends a random query from the log.
     * We first pick a non empty session, and then we take a random query from it.
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        return this.tb.getCurrentUser().getUserModel().playQueryListForTask(this.tb);
    }
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        this.cube   = arg_tb.getCube();
        this.log    = arg_tb.getLog();
        this.tb     = arg_tb;
    }
    
}
