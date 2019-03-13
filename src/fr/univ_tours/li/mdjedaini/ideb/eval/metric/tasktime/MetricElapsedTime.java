/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasktime;

import java.util.ArrayList;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;

/**
 * This metric evaluates the time taken to resolve a task.
 * 
 * @author mahfoud
 */
public class MetricElapsedTime extends Metric {
    
    /**
     * 
     * @param arg_be 
     */
    public MetricElapsedTime(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Elapsed time";
        this.description    = "Time of the complete work session...";
    }
    
    /**
     * Generates a random double, between 0.0 and 1.0.
     * 
     * @param arg_tr
     * @return 
     */
    @Override
    public MetricScore apply(Exploration arg_tr) {
        MetricScore result  = new MetricScore(this, arg_tr);
        
        result.queryScoreList   = new ArrayList<>();

        for(int i = 0; i < arg_tr.getWorkSession().getNumberOfQueries(); i++) {
            Long elapsedTime    = arg_tr.getWorkSession().timeAfterQueryExecutionList.get(i) - arg_tr.getWorkSession().timeBeforeQueryExecutionList.get(0);
            result.queryScoreList.add(elapsedTime.doubleValue() / 1000);
        }
        
        Long finalTime      = arg_tr.getWorkSession().timeAfterQueryExecutionList.get(arg_tr.getWorkSession().getNumberOfQueries() - 1);
        Long initialTime    = arg_tr.getWorkSession().timeBeforeQueryExecutionList.get(0);
        
        Long time   = finalTime - initialTime;
        
        //result.score    = time.doubleValue();
        
        // aggregated value is the last value of the exploration
        result.score    = (Double)result.queryScoreList.get(result.queryScoreList.size() - 1);
        
        return result;
    }
    
    
    
}
