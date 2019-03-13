/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasksuccess;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;

/**
 * This metric evaluates the discovery precision.
 * It is the ratio between the number of discoveries cells found by the number
 * of cells retrieved.
 * @author mahfoud
 */
public class MetricDiscoveryPrecision extends Metric {

    CellList sutCellList;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricDiscoveryPrecision(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Discovery Precision";
        this.description    = "Evaluates the precision of the work session...";
    }
    
    /**
     * Computes the average recall for all the target discoveries for this task.
     * 
     * @param arg_tr
     * @return 
     */
    public MetricScore apply(Exploration arg_tr) {
        MetricScore result  = new MetricScore(this, arg_tr);

        // 
        //arg_tr.getWorkSession().execute(Boolean.TRUE);
        this.sutCellList        = arg_tr.getWorkSession().getCellList();
        Integer nbOfDiscoveries = arg_tr.getTargetDiscoveryList().size();
        
        List<Double> queryScoreList = new ArrayList<>();
        
        for(Query q_tmp : arg_tr.getWorkSession().getQueryList()) {
            // add the cells of the current query to the sut cell list
            this.sutCellList.addCellCollection(q_tmp.getResult().getCellList().getCellCollection());
            queryScoreList.add(this.applyOnQuery(q_tmp, arg_tr));
        }
        
        // compute the global recall for the exploration
        
        CellList focusZoneCells = arg_tr.getTargetDiscoveryList().iterator().next().getCellList();
        
        Double globalRecall     = focusZoneCells.intersection(sutCellList).nbOfCells().doubleValue() / sutCellList.nbOfCells().doubleValue();
        
        // result.score            = Stats.average(queryScoreList);
        result.score            = globalRecall;
        result.addScoreList(queryScoreList);
               
        return result;
    }

    /**
     * Computes the recall at the query level...
     * @param arg_q
     * @param arg_tr
     * @return 
     */
    public Double applyOnQuery(Query arg_q, Exploration arg_tr) {
        Double result   = 0.;
        
        Double acc      = 0.;
        // check if target discoveries have been found or not...
        for(AbstractDiscovery ad_tmp : arg_tr.getTargetDiscoveryList()) {
            acc += this.applyOnDiscovery(arg_q, ad_tmp);
        }
        
        result  = acc / arg_tr.getTargetDiscoveryList().size();
        
        // compute the ratio of ratios
        return result;
    }
    
    /**
     * Computes the recall for a given discovery.
     * @param arg_ad 
     */
    private Double applyOnDiscovery(Query arg_q, AbstractDiscovery arg_ad) {
        Double result   = 0.5;
        
        CellList queryCellList      = arg_q.getResult().getCellList();

        // we have the cells, we compute the ratio...
        Integer denum   = this.sutCellList.nbOfCells();
        Integer num     = 0;
        
        // check whether all cells have been found or not
        for(EAB_Cell c_tmp : arg_ad.getCellList().getCellCollection()) {
            if(queryCellList.contains(c_tmp)) {
                num++;
            }
        }

        // precision for the given discovery
        result  = num.doubleValue() / queryCellList.nbOfCells();
        if(queryCellList.nbOfCells().equals(0)) {
//            System.err.println("La requete a 0 cellules!");
            result  = 0.; // @todo corriger ce bypass!
            //arg_q.execute(Boolean.TRUE);
        } else {
//            System.err.println("La requete a " + queryCellList.nbOfCells() + " cellules!");
        }
        
        return result;
    }
    
}
