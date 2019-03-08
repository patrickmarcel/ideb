package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.math.MathException;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.io.CsvLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.user.UserHistory;

/**
 * @author patrick
 *
 */
public class TestInterestingness {

	
	static BenchmarkEngine be ;
	static Log l;
	 
	
	public static void main(String[] args) throws MathException, FileNotFoundException {
        
		// DOPAN
        //tdbc.testSQLServer();
        
		// local SmartBI
        testSmartBImysql();   
        
        //testInterestingness();
        testLabelRead();
    
    }
	
	
	public static void testLabelRead() throws FileNotFoundException{
		String path = "res/Labels/dopan/agreedMetricsWithLabels.csv";
		
		Scanner scanner = new Scanner(new File("path"));
        scanner.useDelimiter(";");
        while(scanner.hasNext()){
            System.out.print(scanner.next()+"|");
        }
        scanner.close();
        
	}
	
	
    public static void testInterestingness() throws MathException{
 	   	Query qtest=l.getQueryList().get(0);
        System.out.println("EXECUTED QUERY: " + qtest);

        Result res= qtest.execute(true);
        CellList cl = res.getCellList();
        System.out.println(cl);
        
        Collection<EAB_Cell> coll = cl.getCellCollection();
        Iterator<EAB_Cell> it = coll.iterator();
        EAB_Cell c = it.next();
        System.out.println(c.getMeasure().getName());
        System.out.println(c.getValue());
        
        UserHistory testUH = new UserHistory();  
        cl=l.getCellList();
        testUH.add(cl);
        
        
        System.out.println("METRICS FOR CELL "+    c.toString());

        
        System.out.println("metric nb of alls: " + c.nbAll() );
        System.out.println("metric ratio of alls: " + c.ratioAll() );
        System.out.println("metric novelty: " + c.binaryNovelty(testUH) );
        System.out.println("metric outlierness: " + c.outlierness(cl) );
        System.out.println("metric number of relatives: " + c.numberOfRelatives(cl)); // always 0 in the logs we have
        System.out.println("metric size of detailed area: " + c.sizeOfDetailedArea());
        System.out.println("metric simpleRelevance: " + c.simpleRelevance(testUH));
        System.out.println("metric surprise: " + c.surprise(testUH));
              
        
        //testUH.printMeasures();
        //testUH.printMembers();
              
        
        /*
        EAB_Cube cube=c.getCube();
        for(EAB_Hierarchy h : cube.getHierarchyList()){
        	System.out.println(c.getMemberByHierarchy(h).getLevel().getLevelDepth());
        	System.out.println(h.getMostDetailedLevel().getName());
        	System.out.println(h.getNumberOfLevels());
        }
         

        Collection<EAB_Cell> col=c.detailedAreaOfInterest();
        Iterator<EAB_Cell> it2 = col.iterator();
        while(it2.hasNext()){
        	System.out.println(it2.next().toString());
//        	System.out.println(it2.next().hashCode());

        }
        */
        
 	   
    }
    
    


    
    /**
     * @throws MathException 
     * 
     */
    public static void testSmartBImysql() throws MathException {
        Parameters params   = new Parameters();
        
        params.driver   = "com.mysql.jdbc.Driver";
        params.jdbcUrl  = "jdbc:mysql://127.0.0.1/smartbi";
        params.user     = "root";
        params.password = "";
        params.schemaFilePath   = "res/cubeSchemas/smartBI.xml";
        params.rebuildConnectionString();
        
         be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
        //CsvLogLoader sll = new CsvLogLoader(be, "/Users/patrick/Documents/RECHERCHE/STUDENTS/Mahfoud/smartBI/IS_ADBIS/logs/user-a_session-3C604A43A349CDC4130E5F83A8625EE6C83A2283F66A05BD44300AE9E67250DE_analysis-1.csv");
        CsvLogLoader sll = new CsvLogLoader(be, "res/logs/smartBI/IS_ADBIS/logs/user-a_session-3C604A43A349CDC4130E5F83A8625EE6C83A2283F66A05BD44300AE9E67250DE_analysis-1.csv");
        l   = sll.loadLog();
       // System.out.println(l);
       
        //l.execute(Boolean.FALSE);
        
        /*
         * list queries in log
         * 
        for(Query q_tmp : l.getQueryList()) {
            System.out.println("Query");
            System.out.println(q_tmp);
        }
        
        System.out.println(l);
        */
        
        
        //System.out.println(be.getParameters().getMondrianConnectionString());
    }
    
    

    
    
    
    /**
     * @throws MathException 
     * 
     */
    public static void testSQLServer() throws MathException {
        Parameters params   = new Parameters();
        
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        params.jdbcUrl          = "jdbc:sqlserver://10.195.25.10:54027";
        //params.user             = "mahfoud";
        //params.password         = "AvH4My327-vd";
        params.user             = "patrick";       
        params.password         = "oopi7taing7shahD";
        params.schemaFilePath   = "res/cubeSchemas/DOPAN_DW3.xml";
        //params.schemaFilePath   = "/Users/patrick/Documents/RECHERCHE/STUDENTS/Mahfoud/dopan/DOPAN_DW3.xml";
        //params.cubeName         = "Cube1MobProInd";
        params.cubeName         = "Cube4Chauffage";
        
        BenchmarkEngine be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
        
        //SaikuLogLoader sll = new SaikuLogLoader(be, "/Users/patrick/Documents/RECHERCHE/STUDENTS/Mahfoud/logs/DopanLogsNettoyes/cleanLogs/dibstudent03--2016-09-24--23-01.log");
        SaikuLogLoader sll = new SaikuLogLoader(be, "res/logs/dopan/cleanLogs/dibstudent03--2016-09-24--23-01.log");
        Log l   = sll.loadLog();
              
        System.out.println("Log summary:");
        System.out.println(l.logSummary());
        
        for(Session s_tmp : l.getSessionList()) {           
            System.out.println(s_tmp.getSummary());
            
        }
             
      //l.execute(Boolean.FALSE);
      
      /*
       * list queries in log
       * 
      for(Query q_tmp : l.getQueryList()) {
          System.out.println("Query");
          System.out.println(q_tmp);
      }
      
      System.out.println(l);
      */
      
      
      
      
    }
    
    
    
    
    
}
