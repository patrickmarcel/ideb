/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.user;

import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;

/**
 *
 * @author mahfoud
 */
public interface I_UserSimulator {
    
    /**
     * SUT must read data concerning a given task, before evaluating it.
     * It contains the bundle, and also the current task resolution, so it can also
     * recommend based on recent queries.
     * @param arg_tb 
     */
    public void readTaskBundle(TaskBundle arg_tb);
    
    /**
     * 
     * @return 
     */
    public List<Query> playQueryList();
    
    /**
     * 
     * @param arg_tb
     * @return 
     */
    public List<Query> playQueryListForTask(TaskBundle arg_tb);
    
    /**
     * Whether to follow a recommendation or not.
     * @param arg_queryList
     * @return 
     */
    public boolean followRecommandation(List<Query> arg_queryList);
    
    
}
