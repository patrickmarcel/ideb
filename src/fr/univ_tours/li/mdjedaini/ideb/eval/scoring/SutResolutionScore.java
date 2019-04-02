/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.scoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.algo.suts.I_SUT;
import fr.univ_tours.li.mdjedaini.ideb.eval.SUTResolution;
import fr.univ_tours.li.mdjedaini.ideb.eval.Task;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskResolution;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 *
 * @author mahfoud
 */
public class SutResolutionScore {
    
    //
    I_SUT sut;

    /**
     *
     */
    public SUTResolution sutResolution;

    /**
     *
     */
    public SUTScorer sutScorer;
    
//    public Map<TaskResolution, TaskResolutionScore> taskResolutionToScore;

    /**
     *
     */
    public Map<Task, TaskResolutionScore> taskToScore;

    /**
     *
     */
    public Map<Metric, Double> metricToScore;
    
    /**
     * 
     * @param arg_sut
     * @param arg_ss 
     */
    public SutResolutionScore(I_SUT arg_sut, I_SUTScorer arg_ss) {
        this.sut            = arg_sut;
        this.sutScorer      = (SUTScorer)arg_ss;
        this.taskToScore    = new HashMap<>();
        this.metricToScore  = new HashMap<>();
    }
    
    /**
     * 
     * @param arg_sutr
     * @param arg_sutScorer 
     */
    public SutResolutionScore(SUTResolution arg_sutr, SUTScorer arg_sutScorer) {
        this.sut            = arg_sutr.getSut();
        this.sutResolution  = arg_sutr;
        this.sutScorer      = arg_sutScorer;
        this.taskToScore    = new HashMap<>();
        this.metricToScore  = new HashMap<>();
    }
    
    /**
     * 
     * @param arg_tr
     * @param arg_ts 
     */
    public void registerTaskScore(TaskResolution arg_tr, TaskResolutionScore arg_ts) {
        // ! la ligne suivante cause des problemes de memoire!
        //this.TaskResolutionToScore.put(arg_tr, arg_ts);
        this.taskToScore.put(arg_tr.getTask(), arg_ts);
        
        // ceci n'est pas optimal car on refait le calcul a chaque fois, mais
        // le calcul est tres rapide donc c est bon...
        this.prepare();
    }

    /**
     * 
     * @return 
     */
    public Map<Metric, Double> getMetricToScore() {
        return metricToScore;
    }
    
    /**
     * Returns the set of metrics used for this evaluation...
     * It is taken from the SUT resolution, but I think it should be better taken
     * from the Evaluator...
     * @return 
     */
    public List<Metric> getMetricList() {
        return this.sutScorer.metricList;
    }

    /**
     * 
     * @return 
     */
    public Map<Task, TaskResolutionScore> getTaskToScore() {
        return taskToScore;
    }
    
    /**
     * 
     * @param arg_mid
     * @param arg_value 
     */
    public void setScoreByMetric(Integer arg_mid, Double arg_value) {
        //this.metricScores.put(arg_mid, arg_value);
    }
    
    /**
     * Prepares the SUT score before being used.
     * Mainly, its role is to aggregate the scores of each tasks so that the 
     * SUT gets a unique score per metric.
     */
    public void prepare() {
        
        for(Metric m_tmp : this.getMetricList()) {
            
            List<Double> scores = new ArrayList<>();
            
            for(TaskResolutionScore trs : this.taskToScore.values()) {
                scores.add(trs.getMetricToScore().get(m_tmp));
            }
            
            Double aggregatedScore  = Stats.average(scores);
            this.metricToScore.put(m_tmp, aggregatedScore);
        }
        
    }
    
    /**
     * 
     */
    public void exportToCsv() {
        File f_tmp  = new File("output/");
        if(f_tmp.exists()) {
            f_tmp.mkdir();
        }
        
        String fileName = "output/" + this.sut.getName() + "-" + Instant.now().toEpochMilli() + ".csv";
        this.exportToCsv(fileName);
    }
    
    /**
     * 
     * @param arg_fileName 
     */
    public void exportToCsv(String arg_fileName) {
        
        try {
            
            FileWriter writer   = new FileWriter(arg_fileName);
            
            Map<Metric, Double> metricSum   = new HashMap<>();
                    
            for(Metric m_tmp : this.getMetricList()) {
                metricSum.put(m_tmp, 0.);
                writer.append(m_tmp.getName());
                writer.append(',');
            }

            writer.append(System.lineSeparator());

            
            
            for(TaskResolutionScore trs_tmp : this.taskToScore.values()) {
                
                for(Metric m_tmp : this.getMetricList()) {
                    metricSum.replace(m_tmp, metricSum.get(m_tmp) + trs_tmp.explorationScorePerMetric.get(m_tmp));
                    writer.append(trs_tmp.explorationScorePerMetric.get(m_tmp).toString());
                    writer.append(",");
                }
                
                writer.append(System.lineSeparator());
            }

            writer.append(System.lineSeparator());
            writer.append(System.lineSeparator());
            writer.append(System.lineSeparator());
            
            for(Metric m_tmp : this.getMetricList()) {
                Double avg  = metricSum.get(m_tmp) / this.taskToScore.size();
                writer.append(avg.toString());
                writer.append(',');
            }
            
	    writer.flush();
	    writer.close();
            
	} catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public I_SUT getSut() {
        return sut;
    }

    /**
     *
     * @param sut
     */
    public void setSut(I_SUT sut) {
        this.sut = sut;
    }

    /**
     *
     * @return
     */
    public SUTResolution getSutResolution() {
        return sutResolution;
    }

    /**
     *
     * @param sutResolution
     */
    public void setSutResolution(SUTResolution sutResolution) {
        this.sutResolution = sutResolution;
    }

    /**
     *
     * @return
     */
    public SUTScorer getSutScorer() {
        return sutScorer;
    }

    /**
     *
     * @param sutScorer
     */
    public void setSutScorer(SUTScorer sutScorer) {
        this.sutScorer = sutScorer;
    }

    /**
     *
     * @return
     */
    public List<TaskResolutionScore> getTaskResolutionScoreList() {
        return new ArrayList<>(this.taskToScore.values());
    }
    
}
