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
import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.AligonQuerySimilarity;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricIterativeSimilarity extends Metric {
    
    CellList sutCellList;
    CellList focusZone;
    AligonQuerySimilarity aqs;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricIterativeSimilarity(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Iterative Similarity";
        this.description    = "Computes iteratively the similarity between two queries";
        this.aqs            = new AligonQuerySimilarity(this.getBenchmarkEngine());
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
            Double similarity   = this.aqs.similarity(workSession.getQueryByPosition(i), workSession.getQueryByPosition(i-1));
            
            queryScoreList.add(similarity);
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
    public Integer commonFilters(Query arg_q1, Query arg_q2) {
        Integer result   = 0;

        QueryConverter qc   = new QueryConverter(this.getBenchmarkEngine());
        
        for(SelectionFragment sf_tmp : qc.toQueryTriplet(arg_q1).getSelectionFragments()) {    
            if(qc.toQueryTriplet(arg_q2).getSelectionFragments().contains(sf_tmp)) {
                result++;
            }
        }
        
        return result;
    }
    
}
