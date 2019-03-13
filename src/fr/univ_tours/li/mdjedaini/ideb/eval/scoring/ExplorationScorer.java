/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.scoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.I_FocusDetector;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;

/**
 * Responsible for scoring a task output.
 * @author mahfoud
 */
public class ExplorationScorer implements I_ExplorationScorer {
    
    BenchmarkEngine benchmarkEngine;
    
    // list of metrics
    List<Metric> metricList;

    Collection<AbstractDiscovery> targetDiscoveryList;
    
    List<Map<AbstractDiscovery, List<EAB_Cell>>> cellDiscovered;
    
    Map<AbstractDiscovery, List<Boolean>> successListByDiscovery;
    
    Map<AbstractDiscovery, AbstractDiscovery> initialTargetDiscoveryToDiscovery;
    Map<AbstractDiscovery, AbstractDiscovery> discoveryToInitialTargetDiscovery;
    
    /**
     *
     */
    public String computationTime  = "Metric name; Time; Number of queries";
    
    I_FocusDetector focusDetection;
    
    /**
     * 
     * @param arg_be 
     */
    public ExplorationScorer(BenchmarkEngine arg_be) {
        this.benchmarkEngine    = arg_be;
        this.focusDetection     = arg_be.getFocusDetector();
        this.metricList         = new ArrayList<>();
    }

    /**
     * 
     * @param arg_be
     * @param arg_focusDetector 
     */
    public ExplorationScorer(BenchmarkEngine arg_be, I_FocusDetector arg_focusDetector) {
        this.benchmarkEngine    = arg_be;
        this.focusDetection     = arg_focusDetector;
        this.metricList         = new ArrayList<>();
    }
    
    /**
     * 
     * @param arg_be
     * @param arg_metricList 
     */
    public ExplorationScorer(BenchmarkEngine arg_be, List<Metric> arg_metricList) {
        this(arg_be);
        this.metricList         = arg_metricList;
    }
    
    /**
     * 
     * @param arg_m 
     */
    public void addMetric(Metric arg_m) {
        this.metricList.add(arg_m);
    }

    /**
     * 
     * @param arg_tr
     * @return 
     */
    @Override
    public ExplorationScore score(Exploration arg_tr) {
        ExplorationScore result = new ExplorationScore(arg_tr, this);

        // I execute the exploration prior to evaluating it
        System.out.println("I am executing the session..." + arg_tr.getWorkSession().getMetadata("name"));
        arg_tr.getWorkSession().execute(Boolean.TRUE);
        System.out.println("Session successfully executed");
        
        // computes the timestamps if they have not been computed yet (for cubeload)
        arg_tr.getWorkSession().prepare();
        
        // uses the focus detection for computing focus zone
        arg_tr.computeFocusZone(this.focusDetection);
                
        List<Thread> threadList = new ArrayList<>();
        
        for(Metric m_tmp : this.metricList) {
        
            // parallize this
            MetricOnExplorationComputer moec    = new MetricOnExplorationComputer(m_tmp, arg_tr, result);
            threadList.add(moec);
            moec.start();
            
//            // list of scores for each query
//            List<Double> record = new ArrayList<>();
//            
//            System.out.println("Computing score for metric " + m_tmp.getName());
//            long before = System.currentTimeMillis();
//            MetricScore ms      = m_tmp.apply(arg_tr);
//            long after = System.currentTimeMillis();
//            long elapsed    = after - before;
//            this.computationTime    += m_tmp.getName() + ";" + elapsed + ";" + arg_tr.getWorkSession().getNumberOfQueries();
//            this.computationTime    += System.lineSeparator();
//                        
//            result.registerScore(m_tmp, ms);
            
        }
        
        // *** waiting for all the converter threads to finish...
        try {
            for(Thread t_tmp : threadList) {
                t_tmp.join();
            }
            System.out.println("Threads have finished to compute the metrics for the given exploration...");
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
//        // *** converter threads have finished...
        
        // clear the stored results to avoid heap space exception
        arg_tr.getWorkSession().clear();
        
        return result;
    }
   
    /**
     * Inner class for parallelizing the computation of the metrics per exploration
     */
    public class MetricOnExplorationComputer extends Thread {

        Metric m_tmp;
        Exploration e_tmp;
        ExplorationScore result;
        
        /**
         * 
         * @param arg_m
         * @param arg_e 
         */
        public MetricOnExplorationComputer(Metric arg_m, Exploration arg_e, ExplorationScore arg_es) {
            this.m_tmp  = arg_m;
            this.e_tmp  = arg_e;
            this.result = arg_es;
        }
               
        /**
         * 
         */
        @Override
        public void run() {
            // list of scores for each query
            List<Double> record = new ArrayList<>();
            
            System.out.println("Computing score for metric " + m_tmp.getName());
            long before = System.currentTimeMillis();
            MetricScore ms      = m_tmp.apply(e_tmp);
            long after = System.currentTimeMillis();
            long elapsed    = after - before;
//            this.computationTime    += m_tmp.getName() + ";" + elapsed + ";" + arg_tr.getWorkSession().getNumberOfQueries();
//            this.computationTime    += System.lineSeparator();
            
            // method registerScore is synchronized
            result.registerScore(m_tmp, ms);
            
        }
    }
    
}
