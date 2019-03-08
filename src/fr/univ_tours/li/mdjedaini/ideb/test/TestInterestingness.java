package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
	static HashMap<String, UserHistory> userList;
	
	public static void main(String[] args) throws MathException, FileNotFoundException {
        
		// DOPAN
        //testSQLServer();
        
		// local SmartBI
        testSmartBImysql();   
        
        createUsers();
        for(String name : userList.keySet()){
        	UserHistory uh=userList.get(name);
        	System.out.println("user:" + uh.getName());
        	System.out.println(uh.getBelief().toString());
        }
        
        //testInterestingness();
        //testLabelRead();
    
    }
	
	
	public static void testLabelRead() throws FileNotFoundException{
		//String path = "res/Labels/dopan/agreedMetricsWithLabels.csv";
		//File f=new File("path");
		//Scanner scanner = new Scanner(f);
		//doesn't work for a reason???
		
		Scanner scanner = new Scanner(new File("/Users/patrick/git/ideb/res/Labels/dopan/agreedMetricsWithLabels.csv"));
        scanner.useDelimiter(";");
        
        
        //while(scanner.hasNextLine()){
        	scanner.nextLine(); // reads header line
        	String line = scanner.nextLine();
        	String[] ts = line.split(";");
        	System.out.println(ts[0]);
        	System.out.println(ts[ts.length -1]);
            //System.out.print(scanner.next()+"|");
        	
        	Session s =l.getSessionByQueryId(0);
        	System.out.println(s.getSid()+" "+ s.getUser());
        	System.out.println(s.getSummary());
        	//System.out.println(s.getNumberOfQueries());
        	
        //}
        scanner.close();
       
	}
	
	
	public static void createUsers(){
		userList=new HashMap<String, UserHistory>();
		
		int i=1;
		for(Session s :l.getSessionList()){
			System.out.println("processing session "+ i++);
			
			String filename=s.getMetadata("filename");
//			String username=filename.split("--")[0]; //warning only for dopan!!! should be done in logLoader!!
			String username=filename.split("_session")[0]; //warning only for smartBI!!! should be done in logLoader!!
			
			if(userList.containsKey(username)){
				UserHistory uh=userList.get(username);
				uh.addSession(s);
				userList.put(username, uh);
			}
			else{
				UserHistory uh=new UserHistory(username,s);				
				userList.put(username, uh);
				
			}
		}
		
		System.out.println("Nb of users: " + userList.size());
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
     * This one has smaller cube and is for test purpose. Its logs are not labeled.
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
        
        //CsvLogLoader sll = new CsvLogLoader(be, "res/logs/smartBI/IS_ADBIS/logs/user-a_session-3C604A43A349CDC4130E5F83A8625EE6C83A2283F66A05BD44300AE9E67250DE_analysis-1.csv");
        
       // read all log files in directory       
        CsvLogLoader sll = new CsvLogLoader(be, "res/logs/smartBI/IS_ADBIS/logs/");
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
        for(Session q_tmp : l.getSessionList()) {
            System.out.println("session user, id, filenamemetadata");
            System.out.println(q_tmp.getUser());
            System.out.println(q_tmp.getMetadata("filename"));
            
        }
        
        //System.out.println(l);
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
        
        l   = sll.loadLog();
              
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
