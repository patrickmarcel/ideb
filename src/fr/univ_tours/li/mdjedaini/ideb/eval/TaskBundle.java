/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.struct.UserLog;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 * 
 * Bundle containing all the necessary data
 * 
 */
public class TaskBundle {

    // task related to the bundle
    Task task;
    
    // intial logs...
    Log visibleLog;
    Log hiddenLog;
    
    // work session corresponds to the seed session before the process starts
    Session workSession;
    
    /**
     * Instantiates a task bundle from a task object.
     * Work session is copied from the seed session...
     * @param arg_task
     */
    public TaskBundle(Task arg_task) {
        this.task               = arg_task;
        this.workSession        = new Session(this.task.getSeedSession());
    }
    
    /**
     * 
     * @param arg_log 
     */
    public void setVisibleLog(Log arg_log) {
        // we copy the new log
        this.visibleLog = new Log(arg_log);
    }
    
    /**
     * 
     * @param arg_log 
     */
    public void setHiddenLog(Log arg_log) {
        this.hiddenLog  = new Log(arg_log);
    }
    
    /**
     * Provides the cube instance.
     * A task concerns only one cube. So the cube returned can be taken anywhere from the seed session...
     * SUT need the cube instance to work on it...
     * @return 
     */
    public EAB_Cube getCube() {
        //return this.task.getBenchmarkEngine().getInternalCube();
        return this.workSession.getLastQuery().getCube();
    }
    
    /**
     * Retrieves the visible log for this current task.
     * The visible log is the same for each SUT resolution. It is shared between
     * all the tasks that belongs to the same SUT resolution instance.
     * @return 
     */
    public Log getLog() {
        return this.visibleLog;
    }

    /**
     * 
     * @return 
     */
    public Task getTask() {
        return task;
    }
    
    /**
     * Provides the user concerned by this task bundle.
     * @return 
     */
    public User getCurrentUser() {
        return this.task.getUser();
    }
    
    /**
     * 
     * @return 
     */
    public List<User> getUsers() {
        ArrayList<User> result  = new ArrayList(this.getBenchmarkEngine().getBenchmarkData().getUserList());
        return result;
    }
    
    /**
     * 
     * @param arg_user
     * @return 
     */
    public List<Session> getUserSessions(User arg_user) {
        return this.visibleLog.extractUserSessionList(arg_user);
    }
    
    /**
     * Retrieves discovery list for the current user.
     * @return 
     */
    public Collection<AbstractDiscovery> getCurrentUserDiscoveryList() {
        return this.getBenchmarkEngine().getBenchmarkData().getDiscoveryList().get(this.getCurrentUser());
    }
    
    /**
     * Retrieves the sessions of the current user.
     * @return 
     */
    public List<Session> getCurrentUserSessions() {
        return this.getUserSessions(this.getCurrentUser());
    }
    
    /**
     * 
     * @return 
     */
    public UserLog getCurrentUserLog() {
        return this.getBenchmarkEngine().getBenchmarkData().getUserLog(this.getCurrentUser());
    }
    
    /**
     * 
     * @return 
     */
    public Set<Query> getQueryListContainingDiscoveries() {
        Set<Query> distinctQueriesOfDiscovery   = new HashSet<>();
        
        // collect queries from discovery
        for(AbstractDiscovery ad_tmp : this.task.getTargetDiscoveryList()) {
            for(EAB_Cell c_tmp : ad_tmp.getCellList().getCellCollection()) {
                distinctQueriesOfDiscovery.add(c_tmp.getResult().getQuery());
            }
            
        }
        
        return distinctQueriesOfDiscovery;
    }

    /**
     * 
     * @return 
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.task.getBenchmarkEngine();
    }
    
    /**
     * Retrieves the current session.
     * Current session is also known as the work session. It is the session
     * being developed by the SUT and the user.
     * @return 
     */
    public Session getCurrentSession() {
        return this.workSession;
    }
    
}