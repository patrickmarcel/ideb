/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricNumberOfQueries extends Metric {
    
    /**
     * 
     * @param arg_be 
     */
    public MetricNumberOfQueries(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric Number of queries...";
        this.description    = "Number of queries in the work session...";
    }

    /**
     * Generates a random double, between 0.0 and 1.0.
     * 
     * @param arg_tr
     * @return 
     */
    public MetricScore apply(Exploration arg_tr) {
        MetricScore result  = new MetricScore(this, arg_tr);
        
        List<Double> resultList = new ArrayList<>();
        
        for(int i = 1; i <= arg_tr.getWorkSession().getQueryList().size(); i++) {
            resultList.add(new Double(i));
        }
        
        result.addScoreList(resultList);
        
        // aggregated value is the last value of the exploration
        result.score    = (Double)result.queryScoreList.get(result.queryScoreList.size() - 1);
        
        return result;
    }
    
    
    
}
