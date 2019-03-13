/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.novelty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricMaximumEntropy extends Metric {
    
    /**
     * 
     * @param arg_be 
     */
    public MetricMaximumEntropy(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Maximum entropy";
        this.description    = "Computes the maximum entropy (among queries) in the work session";
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
        
        List<Double> entropyList    = new ArrayList<>();
        
        for(Query q_tmp : arg_tr.getWorkSession().getQueryList()) {
            
            QueryTriplet qt_tmp     = (QueryTriplet)q_tmp;
            Double currentEntropy   = fr.univ_tours.li.mdjedaini.ideb.tools.Utils.computeInterest(qt_tmp.getResult().getCellList().getCellCollection());
//            Double currentEntropy   = fr.univ_tours.li.mdjedaini.eab.tools.Utils.computeKL(qt_tmp.getResult().getCellList().getCellCollection());
            if(!currentEntropy.isNaN()) {
                entropyList.add(currentEntropy);
            }
            
        }
        
        result.score    = Collections.max(entropyList);
        
        return result;
    }
    
}
