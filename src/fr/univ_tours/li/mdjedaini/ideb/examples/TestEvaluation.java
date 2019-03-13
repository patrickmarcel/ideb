/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.examples;

//import fr.univ_tours.li.mdjedaini.ideb.local.*;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.DistanceBasedFocusDetection;
import fr.univ_tours.li.mdjedaini.ideb.algo.FocusOnFirstQuery;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.CineCube;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.novelty.MetricRelevantNewInformation;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricNoveltyPrimary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricNoveltySecondary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricTaskSuccessPrimary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricTaskSuccessSecondary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricTaskTimePrimary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricTaskTimeSecondary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricUserEngagementPrimary;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.sut.MetricUserEngagementSecondary;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.ExplorationScorer;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.SUTScorer;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.SutResolutionScore;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;


/**
 *
 * @author mahfoud
 */
public class TestEvaluation {
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
    
        TestEvaluation test = new TestEvaluation();
        
        test.evaluateCubeloadLog();
//        test.evaluateDopanLog("res/dopan/cleanLogs/dibstudent04--2016-09-25--21-46.log");
        //test.evaluateDopanLog("res/dopan/cleanLogs/dibstudent07--2016-09-27--20-11.log");
        
    }
    
    /**
     * Evaluates a Dopan session.
     * @param arg_logFile can be a file or a directory. If it is a file, all the files contained in it are evaluated.
     */
    public void evaluateDopanLog(String arg_logFile) {
        
        Parameters params   = new Parameters();
            
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        //params.jdbcUrl          = "jdbc:sqlserver://127.0.0.1:1433";
        params.jdbcUrl          = "jdbc:sqlserver://10.195.25.10:54027";
        params.user             = "mahfoud";
        params.password         = "AvH4My327-vd";
        params.schemaFilePath   = "res/dopan/dopan_dw3.xml";
        
        BenchmarkEngine be  = new BenchmarkEngine(params);
            
        be.initDatasource();
        be.initDefaultModules();
            
        be.setFocusDetector(new FocusOnFirstQuery());
            
        // Instantiation of the exploration scorer which scores exploration
        //ExplorationScorer es    = new ExplorationScorer(be, new FocusOnFirstQuery());
        ExplorationScorer es    = new ExplorationScorer(be, new DistanceBasedFocusDetection());
        
        //es.addMetric(new MetricTaskSuccessPrimary(be));
        //es.addMetric(new MetricTaskSuccessSecondary(be));
        //es.addMetric(new MetricIterativeRecall(be));
        //es.addMetric(new MetricIterativeDistance(be));
        //es.addMetric(new MetricClickDepth(be));
        es.addMetric(new MetricRelevantNewInformation(be));
        
        // pick a random session from the log and execute it
//        SaikuLogLoader  sll = new SaikuLogLoader(be, arg_logFile);
//            
//        Log myLog           = sll.loadLog();
//        System.out.println(myLog);
//            
//        // execute the log and store results...
//        //myLog.execute(Boolean.TRUE);
//            
//        for(Session s_tmp : myLog.getSessionList()) {
//            Exploration e   = new Exploration(be, s_tmp);
//                
//            ExplorationScore trs = es.score(e);
//                
//            System.out.println("Session: " + s_tmp.getMetadata("name"));
//            System.out.println(trs);
//        }
        
    }
    
    /**
     * 
     */
    public void evaluateCubeloadLog() {
        
        try {
            Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
    
        BenchmarkEngine be  = new BenchmarkEngine();
            
        Parameters params       = be.getParameters();
           
        params.driver   = "nl.cwi.monetdb.jdbc.MonetDriver";
        params.jdbcUrl  = "jdbc:monetdb://127.0.0.1:50001/SSB"; 
        params.user     = "root";
        params.password = "motdepasse";
        params.logFilePath  = "Workload-benchmark.xml";
//        params.logFilePath  = "Workload-1484921121.xml";
//        params.nbOfUsers    = 9;
       
        be.initDefaultModules();
        be.initDatasource();
        be.initBenchmark();

        SUTScorer ss    = new SUTScorer(be);
        be.setSutScorer(ss);
        
//        ExplorationScorer es    = new ExplorationScorer(be, new FocusOnFirstQuery());
        
        ss.addMetric(new MetricUserEngagementPrimary(be));
        ss.addMetric(new MetricUserEngagementSecondary(be));
        
        ss.addMetric(new MetricTaskSuccessPrimary(be));
        ss.addMetric(new MetricTaskSuccessSecondary(be));
        
        ss.addMetric(new MetricNoveltyPrimary(be));
        ss.addMetric(new MetricNoveltySecondary(be));
        
        ss.addMetric(new MetricTaskTimePrimary(be));
        ss.addMetric(new MetricTaskTimeSecondary(be));
        
        CineCube cc = new CineCube();
        
        SutResolutionScore sut_rs   = be.evaluateSut(cc);
        
//        for(Session s_tmp : be.getLog().getSessionList()) {
//            Exploration e   = new Exploration(be, s_tmp);
//            
//            ExplorationScore trs = es.score(e);
//            System.out.println(e.getWorkSession().getMetadata("name"));
//            System.out.println("Template: " + e.getWorkSession().getMetadata("template"));
//            System.out.println(trs);
//        }
        
    }
    
}
