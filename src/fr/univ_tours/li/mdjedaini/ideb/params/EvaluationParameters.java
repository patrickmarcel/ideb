/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.params;

import java.util.HashMap;

/**
 *
 * @author mahfoud
 */
public class EvaluationParameters {
    
    /**
     * Metric with associated coefficient
     */
    HashMap<Integer, Double> metricList;
    
    /**
     * Get the list of metrics
     * @return 
     */
    HashMap<Integer, Double> getMetricList() {
        return this.metricList;
    }
    
}
