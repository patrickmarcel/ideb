/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.univ_tours.li.mdjedaini.ideb.eval.metric.learning;

import java.util.HashMap;
import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.AggregationDepth;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.FilterDepth;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIsRefine;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIsRelax;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeCommonAggregation;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeCommonFilters;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeCommonMeasure;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeDistance;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeIdentity;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativePrecision;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeRecall;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricNbOfCells;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.NumberOfAggregation;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.NumberOfFilters;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.NumberOfMeasures;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement.MetricClickDepth;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement.MetricClickPerQuery;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement.MetricNumberOfQueries;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement.MetricQueryRelativePosition;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.novelty.MetricIncreaseInViewArea;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.novelty.MetricRelevantNewInformation;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasktime.MetricConsiderationTime;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasktime.MetricElapsedTime;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasktime.MetricExecutionTime;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasktime.MetricQueryFrequency;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricMLContribution extends Metric {
    
    Exploration tr;
    
    private static Map<Metric, Double> featureWeight   = new HashMap<>();
    private static Boolean initialized                 = Boolean.FALSE;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricMLContribution(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Query Contribution";
        this.description    = "Computes the contribution of a query based on a previously trained SVM model of query contribution";
        
        if(!MetricMLContribution.initialized) {
            featureWeight.put(new NumberOfFilters(arg_be), 0.126);
            featureWeight.put(new NumberOfMeasures(arg_be), 0.098);
            featureWeight.put(new NumberOfAggregation(arg_be), 0.220);
            featureWeight.put(new AggregationDepth(arg_be), -0.058);
            featureWeight.put(new FilterDepth(arg_be), -0.122);
        
            featureWeight.put(new MetricIterativePrecision(arg_be), 0.101);
            featureWeight.put(new MetricIterativeDistance(arg_be), -0.065);
            featureWeight.put(new MetricIterativeRecall(arg_be), 0.117);
            featureWeight.put(new MetricNbOfCells(arg_be), -0.110);
        
            featureWeight.put(new MetricNumberOfQueries(arg_be), -0.057);
            featureWeight.put(new MetricQueryRelativePosition(arg_be), -0.086);
            featureWeight.put(new MetricClickPerQuery(arg_be), 0.054);
            featureWeight.put(new MetricClickDepth(arg_be), 0.216);
        
            featureWeight.put(new MetricIncreaseInViewArea(arg_be), 0.028);
            featureWeight.put(new MetricRelevantNewInformation(arg_be), -0.015);
        
            featureWeight.put(new MetricConsiderationTime(arg_be), 0.102);
            featureWeight.put(new MetricExecutionTime(arg_be), -0.101);
            featureWeight.put(new MetricElapsedTime(arg_be), -0.131);
            featureWeight.put(new MetricQueryFrequency(arg_be), -0.002);
        
            featureWeight.put(new MetricIsRefine(arg_be), 0.295);
            featureWeight.put(new MetricIsRelax(arg_be), 0.092);
            featureWeight.put(new MetricIterativeCommonAggregation(arg_be), 0.196);
            featureWeight.put(new MetricIterativeCommonFilters(arg_be), 0.176);
            featureWeight.put(new MetricIterativeCommonMeasure(arg_be), 0.009);
            featureWeight.put(new MetricIterativeIdentity(arg_be), 0.441);
        }
        
        // set initialized to true
        MetricMLContribution.initialized = Boolean.TRUE;
        
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

        Map<Metric, MetricScore> resultPerMetric    = new HashMap<>();
              
        // we compute the descriptors for each exploration (for each queries in each explorations)
        for(Metric m_tmp : featureWeight.keySet()) {
            MetricScore ms_tmp  = m_tmp.apply(arg_tr);
            resultPerMetric.put(m_tmp, ms_tmp);
        }
        
        // for each query of the work session
        for(int i = 0; i < arg_tr.getNumberOfQueriesInWorkSession(); i++) {
            
            Double contribution = 0.;
            
            // compute scalar product
            for(Metric m_tmp : featureWeight.keySet()) {
                MetricScore ms_tmp  = resultPerMetric.get(m_tmp);
                Double weight       = featureWeight.get(m_tmp);
                
                // add the weight * value of the current metric
                contribution    += weight * (Double)ms_tmp.queryScoreList.get(i);
            }
            
            // contribution is computed now
            result.queryScoreList.add(contribution);
        }
        
        // aggregation must be performed with the KT
        // we put an arbitrary aggregation for compatibility purposes
        result.score    = Stats.average(result.queryScoreList);
        
        return result;
    }
    
    
    
//    /**
//     * Apply the metric for a single discovery.
//     * @param arg_ad
//     * @return 
//     */
//    public MetricScore applyForDiscovery(AbstractDiscovery arg_ad) {
//        Double result   = 0.5;
//        
//        // the discovery we work on
//        CellList discoveryCells     = arg_ad.getCellList();
//        
//        // success list for the discovery
//        List<Boolean> successList   = MetricLearningUtils.computeSuccessList(this.tr, arg_ad);
//        
//        Map<String, Double> params  = MetricLearningUtils.initializeParameters(this.tr, arg_ad);
//        
//        List<Double> pliList        = MetricLearningUtils.computePliList(tr, arg_ad, successList, params);
//        
//        // the result is the last pli
//        result  = pliList.get(pliList.size() - 1);
//        
//        return result;
//    }
    
}
