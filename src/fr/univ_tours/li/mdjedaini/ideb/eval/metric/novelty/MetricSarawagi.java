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
import fr.univ_tours.li.mdjedaini.ideb.algo.misc.IPF_Query;
import fr.univ_tours.li.mdjedaini.ideb.algo.misc.KL_Divergence;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricSarawagi extends Metric {
    
    Exploration taskResolution;
    
    KL_Divergence kl    = new KL_Divergence();
    IPF_Query idfq      = new IPF_Query();
    
    /**
     * 
     * @param arg_be 
     */
    public MetricSarawagi(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - SaRaWaGi";
        this.description    = "Computes differences between expected distribution and actual distribution...";
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
        
        this.taskResolution = arg_tr;
        
        List<Double> entropyList    = new ArrayList<>();
        
        for(Query q_tmp : arg_tr.getWorkSession().getQueryList()) {
            
            QueryTriplet qt_tmp     = (QueryTriplet)q_tmp;
            Double currentEntropy   = this.applyForQuery(qt_tmp);
//            Double currentEntropy   = fr.univ_tours.li.mdjedaini.eab.tools.Utils.computeKL(qt_tmp.getResult().getCellList().getCellCollection());
            if(!currentEntropy.isNaN()) {
                entropyList.add(currentEntropy);
            }
            
        }
        
        Double sum  = 0.;
        for(Double d_tmp : entropyList) {
            sum += d_tmp;
        }
        
        result.score    = sum / entropyList.size();
        
        return result;
    }
    
    /**
     * 
     * @param arg_q
     * @return 
     */
    public Double applyForQuery(Query arg_q) {
        Double result   = 0.5;
        
        result  = this.computeDistanceFromUniform(new ArrayList<>(), arg_q);
        
        return result;
    }
    
    /**
     * 
     * @param known_cells
     * @param arg_q
     * @return 
     */
    private Double computeDistanceFromUniform(List<EAB_Cell> known_cells, Query arg_q) {
        Double result   = 0.;
        
        List<EAB_Cell> cellList = new ArrayList<>(arg_q.getResult().getCellList().getCellCollection());
        
        List<Double> uniform    = new ArrayList<>();
        List<Double> actual     = new ArrayList<>();
        
        Double sum  = 0.;
        for(EAB_Cell c_tmp : cellList) {
            sum += c_tmp.getValueAsDouble();
            actual.add(c_tmp.getValueAsDouble());
        }
        
        // uniformize arrays
        uniform = new ArrayList<>(Collections.nCopies(cellList.size(), sum / cellList.size()));

        result  = this.kl.computeNormalized(actual, uniform);
        
        return result;
    }
    
    
}
