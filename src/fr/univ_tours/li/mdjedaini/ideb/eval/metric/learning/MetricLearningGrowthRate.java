/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.learning;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskResolution;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricLearningGrowthRate extends Metric {
    
    // 
    Double p_l0;    // probability to master the skill before starting
    Double p_t;     // probability to master the skill after an exercice
    Double p_g;     // probability to guess even we do not master the skill
    Double p_s;     // probability to fail even if we master the skill
    
    TaskResolution tr;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricLearningGrowthRate(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric: Learning Growth Rate";
        this.description    = "Evaluates the evolution of the measured knowledge...";
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
        
//        this.tr = arg_tr;
//        
//        this.tr.getTaskBundle().getCurrentUserLog().execute(Boolean.TRUE);
//        
//        Integer denum   = arg_tr.getTask().getTargetDiscoveryList().size();
//        
//        for(AbstractDiscovery ad_tmp : arg_tr.getTask().getTargetDiscoveryList()) {
//            Double r_tmp    = this.applyForDiscovery(ad_tmp);
//            result          = result + r_tmp;
//        }
//        
//        // result is the average of found discoveries...
//        result  = result / denum;
        
        return result;
    }
    
    /**
     * Apply the metric for a single discovery.
     * @param arg_ad
     * @return 
     */
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
//        // result is initialized at 1., which is neutral value for multiplication
//        result  = 1.;
//        
//        System.out.println("Computing growth rate, this is the pli list:");
//        System.out.println(pliList);
//        
//        // growth rate list
//        List<Double> grl    = new ArrayList<>();
//        for(int i = 1; i < pliList.size(); i++) {
//            Double previousPli  = pliList.get(i-1);
//            Double currentPli   = pliList.get(i);
//
//            //
//            if(previousPli != 0) {
//                Double gr   = (1 + ((currentPli - previousPli) / previousPli));
//                //Double gr   = (currentPli - previousPli) / previousPli;
//                grl.add(gr);
//            }
//        }
//        
//        if(grl.isEmpty()) return 0.;
//        
//        Double sum  = 0.;
//        result  = grl.get(0);
//        for(int i = 1; i < grl.size(); i++) {
//            result  = result * grl.get(i);
//            sum     = sum + grl.get(i);
//        }
//        
//        Integer nbQueries   = this.tr.getWorkSession().getNumberOfQueries();
//       
//        //result              = Math.pow(result, 1/nbQueries);
//        
//        result  = sum / (nbQueries - 1);
//        //result  = currentPLI;
//        
//        return result;
//    }
    
}
