/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.algo.suts.I_SUT;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 * A task resolution extends a session.
 * It represents the session that is obtained by the task resolution process.
 * We record the session and other things like what generated the query, the user or the SUT,
 * and many other things to keep details on the evaluation.
 * @author mahfoud
 */
public class SUTResolution {
    
    //
    I_SUT sut;
    
    // logs from the SUT point of view
    Log visibleLog;
    Log hiddenLog;
    
    // keep information the whole resolution
    Map<Task, TaskResolution> taskToResolution;
    
    /**
     * 
     * @param arg_sut 
     */
    public SUTResolution(I_SUT arg_sut) {
        this.sut                = arg_sut;
        this.taskToResolution   = new HashMap<>();
    }
    
    /**
     * 
     * @param arg_tr 
     */
    public void addTaskResolution(TaskResolution arg_tr) {
        this.taskToResolution.put(arg_tr.getTask(), arg_tr);
    }

    /**
     * 
     * @return 
     */
    public I_SUT getSut() {
        return sut;
    }

    /**
     * 
     * @return 
     */
    public Collection<TaskResolution> getTaskResolutionList() {
        return this.taskToResolution.values();
    }
    
    /**
     * 
     * @return 
     */
    public String getSummary() {
        String summary  = "";
        
        summary += "Summary of the resolution for the SUT " + this.getSut().getName();
        
        for(Exploration tr_tmp : this.getTaskResolutionList()) {
            summary += tr_tmp.getSummary();
            summary += System.lineSeparator();
        }
        
        return summary;
    }
    
    /**
     * 
     * @return 
     */
    public Log getVisibleLog() {
        return this.visibleLog;
    }

    /**
     * 
     * @return 
     */
    public Log getHiddenLog() {
        return this.hiddenLog;
    }

    
    
}
