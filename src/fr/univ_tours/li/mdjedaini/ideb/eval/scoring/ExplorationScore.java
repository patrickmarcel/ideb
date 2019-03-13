/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;

/**
 *
 * @author mahfoud
 */
public class ExplorationScore {
    
    //

    /**
     *
     */
    public Exploration exploration;

    /**
     *
     */
    public I_ExplorationScorer explorationScorer;
    
    //

    /**
     *
     */
    public Map<Metric, Double> explorationScorePerMetric;

    /**
     *
     */
    public Map<Metric, List<Double>> metricToQueryScoreList;

    /**
     *
     */
    public Map<Metric, List<Double>> metricToComputationTimeList;

    /**
     *
     */
    public List<Map<Metric, Double>> queryToMetricScoreList;
    
    /**
     * Copy constructor.
     * @param arg_es 
     */
    public ExplorationScore(ExplorationScore arg_es) {
        this.exploration                    = arg_es.exploration;
        this.explorationScorer              = arg_es.explorationScorer;
        this.explorationScorePerMetric      = arg_es.explorationScorePerMetric;
        this.metricToQueryScoreList         = arg_es.metricToQueryScoreList;
        this.metricToComputationTimeList    = arg_es.metricToComputationTimeList;
        this.queryToMetricScoreList         = arg_es.queryToMetricScoreList;
    }
    
    /**
     * 
     * @param arg_exp
     * @param arg_expScorer 
     */
    public ExplorationScore(Exploration arg_exp, I_ExplorationScorer arg_expScorer) {
        this.exploration                = arg_exp;
        this.explorationScorer          = arg_expScorer;
        this.explorationScorePerMetric  = new HashMap<>();
        this.metricToQueryScoreList     = new HashMap<>();
        this.metricToComputationTimeList = new HashMap<>();
        this.queryToMetricScoreList     = new ArrayList<>();
    }
    
    /**
     * Registers the score of a given metric.
     * The SUT output is indeed evaluated at different steps, at each time it recommends something
     * @param arg_m
     * @param arg_score 
     */
    public synchronized void registerScore(Metric arg_m, MetricScore arg_score) {
        this.explorationScorePerMetric.put(arg_m, arg_score.score);
        this.metricToQueryScoreList.put(arg_m, arg_score.queryScoreList);
        
        // add score list per query
        for(int i = 0; i < this.exploration.getNumberOfQueriesInWorkSession(); i++) {
            
            this.queryToMetricScoreList.add(new HashMap<>());
            
            for(Metric m_tmp : this.metricToQueryScoreList.keySet()) {
                this.queryToMetricScoreList.get(i).put(m_tmp, this.metricToQueryScoreList.get(m_tmp).get(i));
            }

        }
    }
    
    /**
     * Returns the exploration corresponding to this score.
     * @return 
     */
    public Exploration getExploration() {
        return this.exploration;
    }

    /**
     * 
     * @return 
     */
    public I_ExplorationScorer getExplorationScorer() {
        return this.explorationScorer;
    }

    /**
     * 
     * @return 
     */
    public Map<Metric, Double> getMetricToScoreList() {
        return this.explorationScorePerMetric;
    }

    /**
     * 
     * @return 
     */
    public Map<Metric, Double> getMetricToScore() {
        return this.explorationScorePerMetric;
    }

    /**
     * 
     * @param arg_name
     * @return 
     */
    public Metric getMetricPerName(String arg_name) {
        for(Metric m_tmp : this.getMetricToScore().keySet()) {
            if(m_tmp.getName().equals(arg_name)) {
                return m_tmp;
            }
        }
        return null;
    }
    
    /**
     * 
     * @return 
     */
    public Map<Metric, List<Double>> getMetricToQueryScoreList() {
        return this.metricToQueryScoreList;
    }

    /**
     *
     * @return
     */
    public List<Map<Metric, Double>> getQueryToMetricScoreList() {
        return this.queryToMetricScoreList;
    }

    /**
     * 
     * @return 
     */
    public Map<Metric, Double> getExplorationScorePerMetric() {
        return explorationScorePerMetric;
    }

    /**
     * 
     * @return 
     */
    public Map<Metric, List<Double>> getMetricToComputationTimeList() {
        return metricToComputationTimeList;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String result   = "";
        
        for(Metric m_tmp: this.getMetricToScore().keySet()) {
            result  += System.lineSeparator();
            result  += "Metric: " + m_tmp.getName();
            result  += System.lineSeparator();
            result  += "Global score: " + this.explorationScorePerMetric.get(m_tmp);
            result  += System.lineSeparator();
            result  += "Detailed score:";
            result  += this.metricToQueryScoreList.get(m_tmp);
        }

        return result;
    }
    
}
