/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryEditionDistance;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricIterativeIdentity extends Metric {
    
    CellList sutCellList;
    CellList focusZone;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricIterativeIdentity(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Iterative Identity";
        this.description    = "Verifies if the query is identical to the previous one...";
    }
    
    /**
     * Computes the average recall for all the target discoveries for this task.
     * 
     * @param arg_tr
     * @return 
     */
    public MetricScore apply(Exploration arg_tr) {
        MetricScore result  = new MetricScore(this, arg_tr);
        
        QueryEditionDistance qed    = new QueryEditionDistance();
        
        List<Double> queryScoreList = new ArrayList<>();
        Session workSession         = arg_tr.getWorkSession();
        
        queryScoreList.add(0.);
        
        for(int i = 1; i < arg_tr.getWorkSession().getNumberOfQueries(); i++) {
            Double distance = this.computeIdentity(workSession.getQueryByPosition(i), workSession.getQueryByPosition(i-1));
            queryScoreList.add(distance);
        }
        
        result.score            = Stats.average(queryScoreList);
        result.addScoreList(queryScoreList);
        
        return result;
    }

    /**
     * Computes the recall between two queries.
     * First query is the retrieved set, and second query is the relevant set of cells.
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public Double computeIdentity(Query arg_q1, Query arg_q2) {
        Double result   = 0.;

        QueryConverter qc   = new QueryConverter(this.getBenchmarkEngine());
        
        if(qc.toQueryTriplet(arg_q1).equals(qc.toQueryTriplet(arg_q2))) {
            result  = 1.;
        }
        
        return result;
    }
    
}
