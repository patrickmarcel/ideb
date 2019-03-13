/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.novelty;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricRelevantNewInformation extends Metric {
    
    /**
     * 
     * @param arg_be 
     */
    public MetricRelevantNewInformation(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Relevant New Information";
        this.description    = "Relevant new information, computed as the average entropy over the session.";
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
        
        for(Query q_tmp : arg_tr.getWorkSession().getQueryList()) {
            
            Double currentEntropy   = fr.univ_tours.li.mdjedaini.ideb.tools.Utils.computeInterest(q_tmp.getResult().getCellList().getCellCollection());
            result.queryScoreList.add(currentEntropy);
            
        }
        
        result.score            = Stats.average(result.queryScoreList);
        
        return result;
    }
    
}
