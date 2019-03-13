/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryEditionDistance;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.neighborhood.I_QueryNeighborhood;
import fr.univ_tours.li.mdjedaini.ideb.neighborhood.QueryNeighborhood;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricClickPerQuery extends Metric {
    
    I_QueryNeighborhood qn;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricClickPerQuery(BenchmarkEngine arg_be) {
        super(arg_be);
        this.qn             = new QueryNeighborhood();
        this.name           = "Metric - Click Per Query.";
        this.description    = "Number of queries following a given query that are at a distance of 1 from it.";
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

        QueryEditionDistance qed    = new QueryEditionDistance();
        
        // get the work session
        Session workSession = arg_tr.getWorkSession();

        for(int i = 0; i < arg_tr.getWorkSession().getNumberOfQueries(); i++) {
            
            Query q_i   = arg_tr.getWorkSession().getQueryByPosition(i);
            Integer cpq = 0;
            
            for(int j = i+1; j < arg_tr.getWorkSession().getNumberOfQueries(); j++) {
                Query q_j   = arg_tr.getWorkSession().getQueryByPosition(j);
                
                if(qed.distance(q_i, q_j) == 1) {
                    cpq++;
                }
                
            }
            
            result.queryScoreList.add(cpq.doubleValue());
            
        }
        
        result.score    = Stats.average(result.queryScoreList);
        
        return result;
    }
    
}
