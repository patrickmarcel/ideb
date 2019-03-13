/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryAsCellWithOlapNeighbors;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class TaskGenerator2 implements I_TaskGenerator {

    SUT_Evaluator evaluator;
    Boolean storeTaskList;
    
    List<Task> storedTaskList;
    
    /**
     * 
     * @param arg_evaluator
     * @param arg_storeTaskList
     */
    public TaskGenerator2(SUT_Evaluator arg_evaluator, Boolean arg_storeTaskList) {
        this.evaluator      = arg_evaluator;
        this.storeTaskList  = arg_storeTaskList;
        this.storedTaskList = new ArrayList<>();
    }
    
    /**
     * 
     * @param arg_nbTask
     * @return 
     */
    @Override
    public List<Task> generateTaskList(Integer arg_nbTask) {
        
        if(this.storeTaskList && !this.storedTaskList.isEmpty()) {
            System.out.println("Reuse of stored task list...");
            return this.storedTaskList;
        }
        
        List<Task> taskList = new ArrayList<>();
        
        Random rg       = new Random();
        
        for(int i = 0; i < arg_nbTask; i++) {
            List<User> userList = new ArrayList<>(this.getBenchmarkEngine().getBenchmarkData().getUserList());
            
            Collections.shuffle(userList);
            User u_tmp  = userList.get(0);
            
            while(this.evaluator.visibleLog.extractUserSessionList(u_tmp).size() == 0) {
                System.out.println("User selected has 0 sessions in visible log, we choose another user!");
                Collections.shuffle(userList);
                u_tmp  = userList.get(0);
            }
            
            Task t_tmp  = this.generateTaskForUser(u_tmp);
            
            taskList.add(t_tmp);
        }
        
        // store task list
        if(this.storeTaskList) {
            this.storedTaskList = taskList;
        }
        
        return taskList;
    }

    /**
     * 
     * @param arg_user
     * @return 
     */
    @Override
    public Task generateTaskForUser(User arg_user) {
        List<Session> us    = this.evaluator.hiddenLog.extractUserSessionList(arg_user);
//        List<Session> us    = this.evaluator.visibleLog.extractUserSessionList(arg_user);
        
        // normally, us should never be empty...
        Session seed        = new Session();
        seed.setUser(arg_user);

        // pick a random cell, and create the query that returns the cell
        EAB_Cell randomCell = this.evaluator.hiddenLog.pickRandomCell();
//        EAB_Cell randomCell = this.evaluator.visibleLog.pickRandomCell();
        DiscoveryAsCellWithOlapNeighbors dacwon = new DiscoveryAsCellWithOlapNeighbors(randomCell);
        
        // avoid cells without neghborhood
        while(dacwon.getCellList().nbOfCells().equals(0)) {
            System.err.println("Neighborhood is empty, I choose another cell!");
            randomCell = this.evaluator.hiddenLog.pickRandomCell();
//            randomCell = this.evaluator.visibleLog.pickRandomCell();
            dacwon = new DiscoveryAsCellWithOlapNeighbors(randomCell);
        }
        
        // add query to the seed
        Query q_tmp = randomCell.getQueryForCell();
        seed.addQuery(q_tmp);
        
        Collection<AbstractDiscovery>   dList   = new ArrayList<>();
        
        // add the discovery to the list
        dList.add(dacwon);
        
        // target session is no longer used...
        Task t  = new Task(arg_user, seed, null, dList);
        
        return t;
    }
    
    /**
     * 
     * @return 
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.evaluator.getBenchmarkEngine();
    }       
    
}
