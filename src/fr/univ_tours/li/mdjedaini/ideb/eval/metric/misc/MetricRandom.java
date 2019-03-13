/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.misc;

import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 *
 * @author mahfoud
 */
public class MetricRandom extends Metric {
    
    /**
     * 
     * @param arg_be 
     */
    public MetricRandom(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric Random";
        this.description    = "Random metric, just for the purpose of testing the benchmark process...";
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
        Random rg = new Random();
        
        for(int i = 0; i < arg_tr.getWorkSession().getNumberOfQueries(); i++) {
            result.queryScoreList.add(rg.nextDouble());
        }
        
        result.score    = Stats.average(result.queryScoreList);
        
        return result;
    }
    
    
    
}
