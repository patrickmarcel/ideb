package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mahfoud
 */
public interface I_SUT {
    
    /**
     * Recommends a session or a query.
     * @return 
     */
    public List<Query> recommand();
    
    /**
     * 
     * @return 
     */
    public String getName();
    
    /**
     * SUT must read data concerning a given task, before evaluating it.
     * It contains the bundle, and also the current task resolution, so it can also
     * recommend based on recent queries.
     * @param arg_tb 
     */
    public void readTaskBundle(TaskBundle arg_tb);
    
}
