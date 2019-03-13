/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.scoring;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.SUTResolution;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskResolution;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;

/**
 * Responsible for scoring a SUT.
 * @author mahfoud
 */
public class SUTScorer implements I_SUTScorer {
    
    BenchmarkEngine benchmarkEngine;
    
    /**
     *
     */
    public ExplorationScorer es;

    /**
     *
     */
    public List<Metric> metricList;
    
    /**
     * 
     * @param arg_be 
     */
    public SUTScorer(BenchmarkEngine arg_be) {
        this.benchmarkEngine    = arg_be;
        this.metricList         = new ArrayList<>();
        this.es                 = new ExplorationScorer(this.benchmarkEngine, this.metricList);
    }
    
    /**
     * 
     * @param arg_metric 
     */
    @Override
    public void addMetric(Metric arg_metric) {
        this.metricList.add(arg_metric);
    }
    
    /**
     * 
     * @param arg_sutr
     * @return 
     */
    @Override
    public SutResolutionScore score(SUTResolution arg_sutr) {
        SutResolutionScore ss    = new SutResolutionScore(arg_sutr, this);

        System.out.println("Computing global SUT score...");
        for(TaskResolution tr_tmp : arg_sutr.getTaskResolutionList()) {
            
            System.out.println("Computing score for task " + tr_tmp.getTask().getTid());
            ExplorationScore e_score    = this.es.score(tr_tmp);
            TaskResolutionScore ts      = new TaskResolutionScore(e_score, tr_tmp);
            
            ss.registerTaskScore(tr_tmp, ts);
            
        }
        
        return ss;
    }

    /**
     * Retrieves the exploration scorer.
     * @return ExplorationScorer in charge of evaluating explorations
     */
    public ExplorationScorer getExplorationScorer() {
        return this.es;
    }
    
    
    
}
