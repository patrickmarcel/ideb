/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.examples;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.CellCoverageRecommander;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.I_SUT;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.NaiveRecommander;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.RandomRecommander;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeRecall;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricTaskSuccessPrimary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricTaskSuccessSecondary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.tasktime.MetricElapsedTime;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.I_SUTScorer;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.SutResolutionScore;


/**
 *
 * @author mahfoud
 */
public class TestSUTEvaluation {
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        
        try {
            Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
        
    
        // Generates 10 benchmark engine instances
        for(int i = 0; i < 1; i++) {
            BenchmarkEngine be  = new BenchmarkEngine();
            
            be.getParameters().driver   = "nl.cwi.monetdb.jdbc.MonetDriver";
            be.getParameters().jdbcUrl  = "jdbc:monetdb://127.0.0.1:50001/SSB"; 
            be.getParameters().user     = "root";
            be.getParameters().password = "motdepasse";
            
            be.getParameters().schemaFilePath   = "res/cubeSchemas/ssb.xml";
            be.getParameters().logFilePath      = "res/logs/ssb/generatedByCubeload/Workload-benchmark.xml";
            
            be.getParameters().nbOfUsers    = 9;
            be.init();
            
            I_SUTScorer ss  = be.getSUTScorer();
           
            ss.addMetric(new MetricTaskSuccessPrimary(be));
            ss.addMetric(new MetricTaskSuccessSecondary(be));
            ss.addMetric(new MetricIterativeRecall(be));
            ss.addMetric(new MetricElapsedTime(be));
            
//            
//            ss.addMetric(new MetricTaskTimePrimary(be));
//            ss.addMetric(new MetricTaskTimeSecondary(be));
//            
//            //            
//            ss.addMetric(new MetricNoveltyPrimary(be));
//            ss.addMetric(new MetricNoveltySecondary(be));
//            
//            //            
//            ss.addMetric(new MetricLearningCognitionPrimary(be));
//            ss.addMetric(new MetricLearningCognitionSecondary(be));
//            
////            ss.addMetric(new MetricMaximumEntropy(be));
//            ss.addMetric(new MetricPT(be));
//            ss.addMetric(new MetricPL0(be));
            
//            ss.addMetric(new MetricIPF(be));
//            ss.addMetric(new MetricSarawagi(be));

            be.getEvaluator().setNumberOfTasks(25);
            be.getEvaluator().setNumberOfChances(10);
            
            List<I_SUT> sutList = new ArrayList<>();
//            
            sutList.add(new RandomRecommander());
            sutList.add(new NaiveRecommander());
//            sutList.add(new FalsetoRecommander(0.5));
//            sutList.add(new CineCube());
//            sutList.add(new SUT_UserModel());
            sutList.add(new CellCoverageRecommander());
            
            for(I_SUT sut_tmp : sutList) {
                SutResolutionScore score    = be.evaluateSut(sut_tmp);
                score.exportToCsv("output/live-test-" + sut_tmp.getName() + ".csv");
            }
            
        }
        
    }
    
}
