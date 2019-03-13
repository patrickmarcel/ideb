/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public interface I_TaskGenerator {
    
    /**
     * Generates a list of tasks.
     * @param arg_nbTask
     * @return 
     */
    public List<Task> generateTaskList(Integer arg_nbTask);
    
    /**
     * Generates a given task for a given user
     * @param arg_user
     * @return 
     */
    public Task generateTaskForUser(User arg_user);
    
}
