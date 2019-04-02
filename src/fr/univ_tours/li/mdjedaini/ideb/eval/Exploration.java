/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.FocusZone;
import fr.univ_tours.li.mdjedaini.ideb.algo.I_FocusDetector;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryAsCellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.struct.UserLog;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 * Task resolution  objects contains all the details of what happened during a
 * resolution of task by a SUT.
 * It represents the session that is obtained by the task resolution process.
 * We record the session and other things like what generated the query, the user or the SUT,
 * and many other things to keep details on the evaluation.
 * @author mahfoud
 */
public class Exploration {
    
    //
    BenchmarkEngine be;
    
    Session workSession;
    
    // when the evaluation starts (not contained in list below)
    Long initialTimestamp;
    Long finalTimestamp;
    
    // All the following lists evolve in parallel
    List<Query> queryList;
    
    // focus zone used for computation
    List<FocusZone> focusZoneList;
    
    /**
     * 
     * @param arg_be
     * @param arg_session 
     */
    public Exploration(BenchmarkEngine arg_be, Session arg_session) {
        this.be             = arg_be;
        this.workSession    = arg_session;
        
        this.queryList      = new ArrayList<>();
    }
    
    /**
     * 
     */
    public void init() {
        this.initialTimestamp   = this.getCurrentTimestamp();
        
        // execute queries and store results for further evaluation...
        //this.getWorkSession().execute(Boolean.TRUE);
    }
    
    /**
     * Returns the current timestamp.
     * @return 
     */
    public Long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }
    
    /**
     * 
     * @return 
     */
    public User getCurrentUser() {
        return this.getWorkSession().getUser();
    }
    
    /**
     * 
     * @return 
     */
    public UserLog getCurrentUserLog() {
        User u  = this.getWorkSession().getUser();
        return this.getBenchmarkEngine().getBenchmarkData().getUserLog(u);
    }
    
    /**
     * 
     * @return 
     */
    public List<Session> getCurrentUserSessions() {
        if(this.getCurrentUser() == null) {
            return new ArrayList<>();
        }
        User u  = this.getWorkSession().getUser();
        UserLog ul  = this.getBenchmarkEngine().getBenchmarkData().getUserLog(u);
        return new ArrayList<>(ul.getSessionList());
    }
    
    /**
     * Transforms focus zones into discoveries to be used by metrics.
     * @return List of focus zones
     */
    public Collection<AbstractDiscovery> getTargetDiscoveryList() {
        List<AbstractDiscovery> result  = new ArrayList<>();
        
        for(FocusZone fz_tmp : this.focusZoneList) {
            DiscoveryAsCellList dacl    = new DiscoveryAsCellList();
            dacl.addCellList(fz_tmp.getCellList());
            result.add(dacl);
        }
        
        return result;
    }
    
    /**
     * NE PAS APPELER CETTE FONCTION FINALIZE sinon ca cause des pb avec java
     */
    public void prepare() {
        this.finalTimestamp = this.getCurrentTimestamp();
    }
    
    /**
     * 
     * @return 
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.be;
    }

    /**
     * Computes the focus zones of current exploration.
     * @param arg_focusDetection Algorithm used for focus zone computation
     */
    public void computeFocusZone(I_FocusDetector arg_focusDetection) {
       this.focusZoneList   = arg_focusDetection.detectFocusZones(this);
    }
    
    /**
     * 
     * @return 
     */
    public Session getWorkSession() {
        return this.workSession;
    }
    
    /**
     * 
     * @return 
     */
    public Long getTotalTime() {
        return (this.getFinalTimestamp() - this.getInitialTimestamp());
    }
    
    /**
     * 
     * @return 
     */
    public List<Query> getUserQueryList() {
        return this.workSession.getQueryList();
    }

    /**
     * 
     * @return 
     */
    public Integer getNumberOfQueriesInWorkSession() {
        return this.getWorkSession().getNumberOfQueries();
    }
    
    /**
     * 
     * @return 
     */
    public Long getInitialTimestamp() {
        return this.initialTimestamp;
    }

    /**
     * 
     * @return 
     */
    public long getFinalTimestamp() {
        return this.finalTimestamp;
    }
    
    /**
     * Retrieves the focus zone of the exploration
     * @return 
     */
    public List<FocusZone> getFocusZoneList() {
        return this.focusZoneList;
    }
    
    /**
     * 
     * @return 
     */
    public String getSummary() {
        String summary  = "";
        
        summary = "Exploration summary... ";

        return summary;
    }
    
}
