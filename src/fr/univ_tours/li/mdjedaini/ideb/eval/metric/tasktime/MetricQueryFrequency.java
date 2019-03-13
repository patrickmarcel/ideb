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
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement.MetricNumberOfQueries;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;

/**
 * This metric evaluates the time taken to resolve a task.
 * 
 * @author mahfoud
 */
public class MetricQueryFrequency extends Metric {
    
    /**
     * 
     * @param arg_be 
     */
    public MetricQueryFrequency(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Query frequency";
        this.description    = "Number of queries per second...";
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
        
        // at the first query, we approximate 1 query per second
        result.queryScoreList.add(1.);
        
        for(int i = 1; i < arg_tr.getWorkSession().getNumberOfQueries(); i++) {
            Long elapsedTime    = arg_tr.getWorkSession().timeAfterQueryExecutionList.get(i) - arg_tr.getWorkSession().timeBeforeQueryExecutionList.get(0);
            Double etInSeconds  = (elapsedTime.doubleValue() / 1000);
            Double frequency    = (i+1) / etInSeconds;
            result.queryScoreList.add(frequency);
        }
        
        MetricNumberOfQueries mnoc  = new MetricNumberOfQueries(this.benchmarkEngine);
        MetricElapsedTime met       = new MetricElapsedTime(this.benchmarkEngine);

        Double nbQueries            = mnoc.apply(arg_tr).score;
        Double time                 = met.apply(arg_tr).score;
        
        // frequency = nbQueries / (time * 1000) pour avoir des secondes
        //result.score    = nbQueries / time;
        
        // aggregated value is the last value of the exploration
        result.score    = (Double)result.queryScoreList.get(result.queryScoreList.size() - 1);
        
        return result;
    }
    
    
    
}
