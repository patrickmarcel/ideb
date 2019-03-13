/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.scoring;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;

/**
 *
 * @author mahfoud
 */
public class MetricScore {
    
    /**
     *
     */
    public Metric m;

    /**
     *
     */
    public Exploration tr;

    /**
     *
     */
    public Double score;

    /**
     *
     */
    public List queryScoreList;

    /**
     *
     */
    public List<Double> computationTimeList;    // in milliseconds

    /**
     * 
     * @param arg_m
     * @param arg_tr 
     */
    public MetricScore(Metric arg_m, Exploration arg_tr) {
        this.m              = arg_m;
        this.tr             = arg_tr;
        this.queryScoreList = new ArrayList<>();
    }
    
    /**
     * 
     * @param arg_list 
     */
    public void addScoreList(List arg_list) {
        this.queryScoreList = arg_list;
    }
    
    /**
     * Finalizes the result score.
     * Among other things, it computes the aggregated value of the metric...
     */
    public void finalize() {
        
    }
    
}
