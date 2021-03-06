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
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricIterativePrecision extends Metric {
    
    CellList sutCellList;
    CellList focusZone;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricIterativePrecision(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Iterative Precision";
        this.description    = "Computes the precision from one query to the next one, in terms of cells...";
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
        
        queryScoreList.add(0.5);
        
        for(int i = 1; i < arg_tr.getWorkSession().getNumberOfQueries(); i++) {
            Double precision    = this.computeRecall(workSession.getQueryByPosition(i), workSession.getQueryByPosition(i-1));
            queryScoreList.add(precision);
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
    public Double computeRecall(Query arg_q1, Query arg_q2) {
        Double result   = 0.;
        
        CellList retrievedSet   = arg_q1.getResult().getCellList();
        CellList relevantSet    = arg_q2.getResult().getCellList();
        
        if(retrievedSet.nbOfCells() == 0) {
            return 0.;
        }
        
        Double acc      = 0.;
        
        for(EAB_Cell c_tmp : retrievedSet.getCellCollection()) {
            if(relevantSet.contains(c_tmp)) {
                acc++;
            }
        }
        
        result  = acc / retrievedSet.nbOfCells();
        
        // compute the ratio of ratios
        return result;
    }
    
}
