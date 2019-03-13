/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * The perfect recommender knows the hidden session.
 * It acts like this: It takes the hidden session and split it into different 
 * sequences of successive queries.
 * Each time, the algorithm recommends the next sequence of queries, until it has 
 * nothing to recommend.
 * @author mahfoud
 */
public class SUT_ReplayUserLog implements I_SUT {

    //
    EAB_Cube cube;
    Log log;
    
    TaskBundle tb;
    
    List<List<Query>> recommandationList;
    
    Integer nbMaxOfQueriesToRecommand;
    
    /**
     * 
     */
    public SUT_ReplayUserLog() {
        this.recommandationList = new ArrayList<>();
    }
    
    /**
     * 
     */
    public void init() {
        
        
        for(Session s_tmp : this.tb.getCurrentUserSessions()) {

            for(Query q_tmp : s_tmp.getQueryList()) {
                List<Query> toRecommand = new ArrayList<>();
                toRecommand.add(q_tmp);
                this.recommandationList.add(toRecommand);
            }
            
        }
        
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        return this.nextQuerySequence();
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        this.tb     = arg_tb;
        this.cube   = arg_tb.getCube();
        this.log    = arg_tb.getLog();
        
        // computes the hidden session
        this.init();
    }
    
    /**
     * Recommends the next query sequence, and deletes it so it is not recommended twice.
     * 
     * @return 
     */
    public List<Query> nextQuerySequence() {
        List<Query> result  = new ArrayList<>();

        if(this.recommandationList.size() > 0) {
            result.addAll(this.recommandationList.get(0));
            this.recommandationList.remove(0);
        }
        
        return result;
    }
    
}
