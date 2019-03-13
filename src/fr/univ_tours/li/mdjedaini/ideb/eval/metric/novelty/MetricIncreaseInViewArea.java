/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.novelty;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricIncreaseInViewArea extends Metric {
    
    CellList acc;   // accumulator for cells viewed during the exploration
    
    /**
     * 
     * @param arg_be 
     */
    public MetricIncreaseInViewArea(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Increase in View Area";
        this.description    = "Evaluates the increase in view area of a query compared to cells viewed previously in the exploration";
        this.acc            = new CellList();
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
        
        List<Object> scorePerQuery  = new ArrayList<>();
        
        for(Query q : arg_tr.getWorkSession().getQueryList()) {
            // SUT cell list contains cells in the work session
            CellList queryCellList  = q.getResult().getCellList();
        
            Double num      = queryCellList.minus(this.acc).nbOfCells().doubleValue();
            Double denum    = queryCellList.union(this.acc).nbOfCells().doubleValue();
            
            Double localScore   = num / denum;
            
            scorePerQuery.add(localScore);
            
            this.acc.addCellCollection(queryCellList.getCellCollection());
        }
        
        result.addScoreList(scorePerQuery);
        result.score    = Stats.average(result.queryScoreList);
        
        return result;
    }
    
    
    
}
