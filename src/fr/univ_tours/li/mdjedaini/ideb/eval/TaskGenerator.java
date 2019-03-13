/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class TaskGenerator implements I_TaskGenerator {

    SUT_Evaluator evaluator;
    
    /**
     * 
     * @param arg_evaluator 
     */
    public TaskGenerator(SUT_Evaluator arg_evaluator) {
        this.evaluator  = arg_evaluator;
    }
    
    /**
     * Generates a list of tasks relatively to a given log.
     * For the benchmark, we generate tasks from the user log.
     * @param arg_nbTask
     * @return 
     */
    @Override
    public List<Task> generateTaskList(Integer arg_nbTask) {
        List<Task> taskList = new ArrayList<>();
        
        Random rg       = new Random();
        
        for(int i = 0; i < arg_nbTask; i++) {
            Integer uid = rg.nextInt(this.getBenchmarkEngine().getBenchmarkData().getUserList().size());
            User u_tmp  = this.getBenchmarkEngine().getBenchmarkData().getUserById(uid);
            
            // We only generate tasks for users with a non empty log
            List<Session> userHiddenSessions    = this.evaluator.hiddenLog.extractUserSessionList(u_tmp);
            if(userHiddenSessions.size() > 0) {
//                System.err.println("Generating a task for user " + u_tmp.getUid() + ": he has " + userHiddenSessions.size() + " sessions in the hidden log...");
                Task t_tmp  = this.generateTaskForUser(u_tmp);
                taskList.add(t_tmp);
            } else {
                // to make sure we have exactly 100 sessions
                i--;
            }
            
        }

        return taskList;
    }

    /**
     * Generates a task for a given user.
     * So far, the seed queries are generated using the random recommender.
     * Normally, we have checked that the user has at least one session...
     * @param arg_user
     * @return 
     */
    @Override
    public Task generateTaskForUser(User arg_user) {
        List<Session> us    = this.evaluator.hiddenLog.extractUserSessionList(arg_user);

        // normally, us should never be empty...
        Session seed        = new Session();
        seed.setUser(arg_user);
        
        // Take session from user hidden log
        Session target      = us.get(0);
        
        // I do +1 -1 to avoid having 0 as argument to nextInt
        Random rg   = new Random();
        Integer nbq = rg.nextInt(target.getNumberOfQueries() +1) -1;
        
        for(int i = 0; i < nbq; i++) {
            seed.addQuery(target.getQueryByPosition(i));
        }
        
        System.out.println("Task based on: Session " + target.getSid());
        System.out.println("Seed has   : " + seed.getNumberOfQueries() + " queries.");
        System.out.println("Target has : " + target.getNumberOfQueries() + " queries.");
        
        Collection<AbstractDiscovery> dList   = this.getBenchmarkEngine().getBenchmarkData().getDiscoveryList().get(arg_user);
        Task t  = new Task(arg_user, seed, target, dList);
        
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
