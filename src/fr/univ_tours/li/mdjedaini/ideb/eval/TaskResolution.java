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
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.I_SUT;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Pair;

/**
 * Task resolution  objects contains all the details of what happened during a
 * resolution of task by a SUT.
 * It represents the session that is obtained by the task resolution process.
 * We record the session and other things like what generated the query, the user or the SUT,
 * and many other things to keep details on the evaluation.
 * @author mahfoud
 */
public class TaskResolution extends Exploration {
    
    I_SUT sut;
    Task task;
    TaskBundle tb;
    
    // All the following lists evolve in parallel
    List<String> playerList;
    
    // position and size of each recommendation
    List<Pair<Integer, Integer>> sutRecommendationList;
    List<Pair<Integer, Integer>> userQueryList;

    List<Long> timestampBeforeQueryRecommendation;
    List<Long> timestampAfterQueryRecommendation;
    List<Long> timestampBeforeQueryExecution;
    List<Long> timestampAfterQueryExecution;
    
    /**
     * 
     * @param arg_be
     * @param arg_sut
     * @param arg_task
     * @param arg_tb 
     */
    public TaskResolution(BenchmarkEngine arg_be, I_SUT arg_sut, Task arg_task, TaskBundle arg_tb) {
        super(arg_be, arg_tb.getCurrentSession());
        
        this.sut    = arg_sut;
        this.task   = arg_task;
        this.tb     = arg_tb;
        
        this.workSession    = arg_tb.getCurrentSession();
        
        this.queryList      = new ArrayList<>();
        this.playerList     = new ArrayList<>();
        
        this.timestampBeforeQueryRecommendation = new ArrayList<>();
        this.timestampAfterQueryRecommendation  = new ArrayList<>();
        
        this.timestampBeforeQueryExecution      = new ArrayList<>();
        this.timestampAfterQueryExecution       = new ArrayList<>();
        
        this.sutRecommendationList  = new ArrayList<>();
        this.userQueryList          = new ArrayList<>();
    }
    
    /**
     * 
     */
    public void init() {
        this.initialTimestamp   = this.getCurrentTimestamp();
        
        // execute queries and store results for further evaluation...
//        this.getWorkSession().execute(Boolean.TRUE);
    }
    
    /**
     * Adds the provided query list to this task resolution object.
     * arg_timestamList contains 4 timestamps for each query, that are timestamps:
     * before and after recommendation, and before and after execution...
     * @param arg_queryList
     * @param arg_timestampList
     * @param arg_player 
     */
    public void addQueryList(List<Query> arg_queryList, List<List<Long>> arg_timestampList, String arg_player) {
        
        System.out.println(arg_player + " played " + arg_queryList.size() + " queries for task " + this.getTask().getTid());
        
        this.workSession.addQueryList(arg_queryList);
        
        Integer position    = this.queryList.size();
        Integer size        = arg_queryList.size();
        
        Pair<Integer, Integer> pair = new Pair(position, size);
        
        for(int i = 0; i < arg_queryList.size(); i++) {
            Query q_tmp = arg_queryList.get(i);
            
            this.queryList.add(q_tmp);
            this.playerList.add(arg_player);
            
            if(arg_player.equals("SUT")) {
                this.sutRecommendationList.add(pair);
            } else {
                this.userQueryList.add(pair);
            }
            
            // add timestamps for tracking time
            this.timestampBeforeQueryRecommendation.add(arg_timestampList.get(i).get(0));
            this.timestampAfterQueryRecommendation.add(arg_timestampList.get(i).get(1));
            
            // if queries are not executed, the execution timestamp are the same as
            // recommendation timestamps...
            if(!this.getBenchmarkEngine().getEvaluator().executeQueries) {
            //if(arg_timestampList.iterator().next().size() < 4) {
                this.timestampBeforeQueryExecution.add(arg_timestampList.get(i).get(0));
                this.timestampAfterQueryExecution.add(arg_timestampList.get(i).get(1));
            } else {
                this.timestampBeforeQueryExecution.add(arg_timestampList.get(i).get(2));
                this.timestampAfterQueryExecution.add(arg_timestampList.get(i).get(3));
            }
        
        }
        
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
     */
    public void prepare() {
        this.finalTimestamp = this.getCurrentTimestamp();
    }
    
    /**
     * 
     * @return 
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.task.getBenchmarkEngine();
    }
    
    /**
     * 
     * @param arg_retrievedCells 
     */
//    public void addRetrievedCells(Map<AbstractDiscovery, List<EAB_Cell>> arg_retrievedCells) {
//        this.cellDiscovered.add(arg_retrievedCells);
//    }
    
    /**
     * 
     * @return 
     */
    public CellList retrieveSutCellList() {
        CellList cl_tmp = new CellList();
        
        // get all the cells discovered at each step
        for(Query q_tmp : this.getSutQueryList()) {
            QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            
            for(EAB_Cell c_tmp : qt_tmp.getResult().getCellList().getCellCollection()) {
                cl_tmp.addCell(c_tmp);
            }
        }
        
        return cl_tmp;
    }

    /**
     * 
     * @return 
     */
    public TaskBundle getTaskBundle() {
       return this.tb; 
    }
    
    /**
     * 
     * @param arg_tb 
     */
    public void setTaskBundle(TaskBundle arg_tb) {
        this.tb = arg_tb;
    }
    
    /**
     * 
     * @return 
     */
    public Session getWorkSession() {
        return this.workSession;
    }
    
    /**
     * Retrives the seed session.
     * Seed session is the session that the benchmark provided to the SUT for
     * completion...
     * @return 
     */
    public Session getSeedSession() {
        return this.getTask().getSeedSession();
    }
    
    /**
     * Retrieves the list of queries for a given player.
     * @param arg_player
     * @return 
     */
    public List<Query> getPlayerQueryList(String arg_player) {
        
        if(arg_player.equals("SUT")) {
            return this.getSutQueryList();
        } else {
            return this.getUserQueryList();
        }
        
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
        List<Query> result  = new ArrayList<>();
        
        for(int i = 0; i < this.queryList.size(); i++) {
            if(this.playerList.get(i).equals("USER")) {
                result.add(this.queryList.get(i));
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Long getTotalSutRecommendationTime() {
        Long result = 0L;
        
        for(Pair<Integer, Integer> p_tmp : this.sutRecommendationList) {
            Integer position    = p_tmp.getKey();
            Long delta          = this.timestampAfterQueryRecommendation.get(position) - this.timestampBeforeQueryRecommendation.get(position);
            result              += delta;
        }
                
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Integer getNumberOfQueriesInSeedSession() {
        return this.getSeedSession().getNumberOfQueries();
    }
    
    /**
     * 
     * @return 
     */
    public Long getTotalQueryExecutionTime() {
        Long result = 0L;
        
        for(int i = 0; i < this.queryList.size(); i++) {
            Long delta          = this.timestampAfterQueryExecution.get(i) - this.timestampBeforeQueryExecution.get(i);
            result              += delta;
        }
                
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public List<Query> getSutQueryList() {
        List<Query> result  = new ArrayList<>();
        
        for(int i = 0; i < this.queryList.size(); i++) {
            if(this.playerList.get(i).equals("SUT")) {
                result.add(this.queryList.get(i));
            }
        }
        
        return result;
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
     * 
     * @return 
     */
    @Override
    public Collection<AbstractDiscovery> getTargetDiscoveryList() {
        return this.getTaskBundle().getTask().getTargetDiscoveryList();
    }
    
    /**
     * 
     * @return 
     */
    public Task getTask() {
        return task;
    }
    
    /**
     * 
     * @return 
     */
    public String getSummary() {
        String summary  = "";
        
        summary = "Task resolution for task " + this.getTask().getTid();

        summary += System.getProperty("line.separator");
        summary += this.getWorkSession().getSummary();
        summary += System.getProperty("line.separator");

        summary += System.getProperty("line.separator");
        summary += this.getTask().getTargetDiscoveryList();
        summary += System.getProperty("line.separator");
        
        summary += System.getProperty("line.separator");
        summary += "User query list";
        summary += System.getProperty("line.separator");
        
        for(Session s_tmp : this.getTaskBundle().getCurrentUserSessions()) {
            
            for(Query q_tmp : s_tmp.getQueryList()) {
                
                summary += q_tmp;
                summary += System.getProperty("line.separator");
                
            }
            
        }
        
        summary += System.getProperty("line.separator");
        summary += "SUT query list";
        summary += System.getProperty("line.separator");
        summary += this.getSutQueryList();
        summary += System.getProperty("line.separator");
        
        return summary;
    }
    
}
