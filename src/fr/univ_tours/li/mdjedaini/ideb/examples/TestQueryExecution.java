/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.examples;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.EAB_Connection;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.io.MondrianResultWriter;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;


/**
 * This class builds graphs for each provided session.
 * Graphs are actually graph of cells composed by all the cells available in the query results for each session...
 * Some measures are also computed...
 * @author mahfoud
 */
public class TestQueryExecution {
    
    /*
    Main function
    */
    public static void main(String[] args) {
        
        Parameters params   = new Parameters();
        
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        params.jdbcUrl          = "jdbc:sqlserver://127.0.0.1:1433";
        params.user             = "mahfoud";
        params.password         = "AvH4My327-vd";
        params.schemaFilePath   = "res/dopan/dopan_dw3.xml";
        
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        params.jdbcUrl          = "jdbc:sqlserver://127.0.0.1:1433";
        params.user             = "mahfoud";
        params.password         = "AvH4My327-vd";
        params.schemaFilePath   = "res/dopan/dopan_dw3.xml";
        
        BenchmarkEngine be  = new BenchmarkEngine(params);
        
        // creates and set connection
        EAB_Connection c_tmp    = new EAB_Connection(be);
        c_tmp.open();
        be.getBenchmarkData().setC(c_tmp);
        
        // load cubes and save them in memory
        be.initCubeList();
        
        // pick a random session from the log and execute it
        SaikuLogLoader  sll = new SaikuLogLoader(be, "res/dopan/cleanLogs/");
        Log myLog           = sll.loadLog();
        
        for(Session s_tmp : myLog.getSessionList()) {
            for(Query q_tmp : s_tmp.getQueryList()) {
                Result res  = q_tmp.execute(Boolean.TRUE);
                
                MondrianResultWriter mrw    = new MondrianResultWriter();
                String resHtml              = mrw.writeResult(res);
                //System.out.println(resHtml);
                
                QueryConverter qc   = new QueryConverter(be);
                qc.convertToQueryTriplet((QueryMdx)q_tmp);
                
            }
        }
        
    }
    
}
