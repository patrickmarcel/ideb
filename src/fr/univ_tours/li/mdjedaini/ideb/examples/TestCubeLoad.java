/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.examples;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.gen.log.CubeloadGenerator;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;


/**
 *
 * @author mahfoud
 */
public class TestCubeLoad {
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        
        // Initializes a benchmark engine
        BenchmarkEngine be  = new BenchmarkEngine();
        
        Parameters params   = be.getParameters();
        
        //params.schemaFilePath   = "schema.xml";
        params.schemaFilePath   = "cubeload/ssb_for_cubeload/schema_focus.xml";
        params.driver           = "nl.cwi.monetdb.jdbc.MonetDriver";
        params.jdbcUrl          = "jdbc:monetdb://127.0.0.1:50001/SSB";
        params.user             = "root";
        params.password         = "motdepasse";
        params.logFilePath      = "Workload-benchmark.xml";
        
        be.initDefaultModules();
        be.initDatasource();
        //be.initLog();
        
        CubeloadGenerator cg    = new CubeloadGenerator(be);
        
        //cg.schemaFilePath       = "cubeload/ssb_for_cubeload/schema.xml";
        cg.setSchemaFilePath("cubeload/ssb_for_cubeload/schema_focus.xml");
        cg.setProfileFilePath("cubeload/ssb_for_cubeload/profile.xml");
        cg.setDataDirectoryPath("cubeload/ssb_for_cubeload/");
        
        cg.setMinReportSize((Integer) 1);
        cg.setMaxReportSize((Integer) 2);
        
        Log l_tmp   = cg.generateLog();
        
//        System.out.println("Log du benchmark engine");
//        System.out.println(be.getLog().logSummary());
        
        System.out.println("Log genere par cubeload");
        System.out.println(l_tmp.logSummary());
        
    }
    
    
}
