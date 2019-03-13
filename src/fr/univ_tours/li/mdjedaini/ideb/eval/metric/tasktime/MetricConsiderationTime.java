/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasktime;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the time taken to resolve a task.
 * 
 * @author mahfoud
 */
public class MetricConsiderationTime extends Metric {
    
    /**
     * 
     * @param arg_be 
     */
    public MetricConsiderationTime(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Consideration time";
        this.description    = "Time taken for considering the result of a query. Its the time elapsed since until a new query is submitted.";
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
       
        List list   = new ArrayList<>();
        
        Session s   = arg_tr.getWorkSession();
        
        // be careful, CT for the last query is not known...
        for(int i = 0; i < s.getNumberOfQueries() - 1; i++) {
            Long considerationTime  = s.timeBeforeQueryExecutionList.get(i+1) - s.timeAfterQueryExecutionList.get(i);
            list.add(considerationTime.doubleValue() / 1000); // en secondes
        }
        
        // consideration time of the last query is not known
        list.add(0.);
        
        result.addScoreList(list);
        
        result.score    = Stats.average(list);
        
        return result;
    }
    
    
    
}
