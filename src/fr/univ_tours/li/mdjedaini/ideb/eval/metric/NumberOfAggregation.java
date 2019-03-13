/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class NumberOfAggregation extends Metric {
    
    CellList sutCellList;
    CellList focusZone;
    
    /**
     * 
     * @param arg_be 
     */
    public NumberOfAggregation(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Number of aggregations";
        this.description    = "Number of hierarchies for which an aggregator is selected.";
    }
    
    /**
     * Computes the average recall for all the target discoveries for this task.
     * 
     * @param arg_tr
     * @return 
     */
    public MetricScore apply(Exploration arg_tr) {
        MetricScore result  = new MetricScore(this, arg_tr);
        
        List<Double> queryScoreList = new ArrayList<>();
        Session workSession         = arg_tr.getWorkSession();

        for(Query q_tmp : workSession.getQueryList()) {
            QueryTriplet qt = (QueryTriplet)q_tmp;
            queryScoreList.add(new Double(qt.getHierarchyWithProjectionOtherThanAll().size()));
        }
        
        result.score            = Stats.average(queryScoreList);
        result.addScoreList(queryScoreList);
        
        return result;
    }
    
}
