package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.math.MathException;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.Belief;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.User;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.UserHistory;
import fr.univ_tours.li.mdjedaini.ideb.io.CsvLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * @author patrick
 *
 */
public class TestInterestingness {

	
	static BenchmarkEngine be ;
	static Log l;
	static HashMap<String, User> userList;
	static double percentageOfPast=0.3;
	static String pathToResult="output/interestingness/";
	
	// test data (smartBI DB + fake labels)
	static String queryLabelFile="/Users/patrick/git/ideb/res/Labels/fakeForTest/dopanCleanLogWithVeronikaLabels-FOCUS.csv";
	static String sessionLabelFile="/Users/patrick/git/ideb/res/Labels/fakeForTest/skillScorePerExploration.csv";
	static String logDirectory="res/logs/smartBI/fakeForTestOnly/";

	//dopan
	//static String queryLabelFile="/Users/patrick/git/ideb/res/Labels/dopan/dopanCleanLogWithVeronikaLabels-FOCUS.csv";
	//static String sessionLabelFile="/Users/patrick/git/ideb/res/Labels/dopan/skillScorePerExploration.csv";
	//static String logDirectory="res/logs/dopan/cleanLogs/";
	
	
	// dopan - test
	//static String queryLabelFile="/Users/patrick/git/ideb/res/Labels/fakeForTest/dopanCleanLogWithVeronikaLabels-FOCUS.csv";
	//static String sessionLabelFile="/Users/patrick/git/ideb/res/Labels/fakeForTest/skillScorePerExploration.csv";
	//static String logDirectory="res/logs/dopan/forTests/";
	
	
	
	public static void main(String[] args) throws MathException, IOException {
		
		long startTime = System.currentTimeMillis();

       
		// DOPAN
        //testSQLServer();
        
		// local SmartBI - for tests
        testSmartBImysql();   
        
        createUsers();
        
        readLabels();
        
        // printing users
        for(User u: userList.values()){
        	System.out.println(u.toString());
        }
        
        
        
        //testCorrelation();
        
        metricsBySessionGrade();
        
        long stopTime = System.currentTimeMillis();        
        System.out.println("Done in: " + (double) (stopTime - startTime)/1000 + " seconds");
    
    }
	
	public static void computeHistory(User u, int numberOfPastQueries){
		if(numberOfPastQueries<=u.getNumberOfQueries()){
			u.computeHistoryIterative(numberOfPastQueries);
		}
	}
		
	public static void computeHistory(User u){
			u.computeHistory(percentageOfPast);		
	}
	
	public static void metricsBySessionGrade() throws MathException, IOException{
			
		//set result file
		 String timestamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	     String fileName = pathToResult  + "compareToSessionGrade" +"-" +  timestamp + ".csv";	        
	     FileWriter writer   = new FileWriter(fileName);
	     writer.write("session ;user;query position;cell hashcode;novelty;outlierness;relevance;surprise;query label;session label\n");
		
		
		for(User u : userList.values()){
			String username=u.getName();
			HashMap<Session, Character> us = u.getSessionLabels();
			for(Session s : us.keySet()){
				Character sessionLabel= us.get(s);
				int sessionHashcode=s.hashCode();
				String sessionName = s.getMetadata("filename");
				int queryPos=0;
				for(Query q : s.getQueryList()){
					int queryHashcode = q.hashCode();
					computeHistory(u,queryPos);
					//u.getHistory().printMembers();
					int queryLabel=u.getCurrentQueryLabel();
					for(EAB_Cell c : q.getResult().getCellList().getCellCollection()){
						int cellHashcode=c.hashCode();
						boolean novelty=c.binaryNovelty(u.getHistory());
						double outlierness=c.outlierness(q.getResult().getCellList());
						double relevance=c.simpleRelevance(u.getHistory());
						double surprise=c.surprise(u.getHistory()); // can be infinity, should be transformed before going to csv/excel?
						
						// put in file
						String current="";
						current = current + sessionName + ";";
						current = current+ username + ";";
						//current = current+ queryHashcode + ";";
						current = current+ (queryPos+1) + ";";
						current = current+ cellHashcode + ";";
						current = current+novelty + ";";
						current = current+outlierness+ ";";
						current = current+relevance + ";";
						current = current+surprise + ";";
						current = current+queryLabel + ";";
						current = current+sessionLabel + ";";
						
						
						current = current+"\n";
						writer.write(current);
						
						System.out.println(current);
					}
					queryPos++;
				}
				
			}
			
			
		}
		
		writer.close();
		
	}
	
	
	
	
	public static void testCorrelation() throws IOException, MathException{
		//set result file
		 String timestamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	     String fileName = pathToResult  + "past=" + percentageOfPast + "-" +  timestamp + ".csv";	        
	     FileWriter writer   = new FileWriter(fileName);
	     writer.write("user;cell hashcode;novelty;outlierness;relevance;surprise;query label;session label\n");
	        
		for(User u : userList.values()){
			String username=u.getName() + ";";
			
			computeHistory(u);	
		
			for(EAB_Cell c: u.getCurrentQuery().getResult().getCellList().getCellCollection()){
				String current=username;
				current = current+c.hashCode() + ";";
				current = current+c.binaryNovelty(u.getHistory()) + ";";
				current = current+c.outlierness(u.getCurrentQuery().getResult().getCellList()) + ";";
				current = current+c.simpleRelevance(u.getHistory()) + ";";
				current = current+c.surprise(u.getHistory()) + ";";
				current = current+u.getCurrentQueryLabel() + ";";
				current = current+u.getSessionLabel(u.getCurrentSession()) + ";";
				
				
				current = current+"\n";
				writer.write(current);
			}
			
			
		}
		writer.close();
	}
	
	
	public static void readLabels() throws FileNotFoundException{
		System.out.println("Reading labels");
		
		//String path = "res/Labels/dopan/agreedMetricsWithLabels.csv";
		//File f=new File("path");
		//Scanner scanner = new Scanner(f);
		//doesn't work for a reason???
		
		// CLEAN FILES, REMOVE NAMES IN FILES AND IN FILENAMES!!!!!!
		
		
		System.out.println("First reading query labels");
		
		//Scanner scanner = new Scanner(new File("/Users/patrick/git/ideb/res/Labels/dopan/dopanCleanLogWithVeronikaLabels-FOCUS.csv"));
		Scanner scanner = new Scanner(new File(queryLabelFile));
		scanner.useDelimiter(";");
        
        scanner.nextLine(); // reads header line
        while(scanner.hasNextLine()){
        	
        	// get username, query nb and query label
        	String line = scanner.nextLine();
        	String[] ts = line.split(";");
        	String filename=ts[1];
        	int label = new Integer(ts[ts.length -1]);
        	//System.out.println(label);
        	String namesplit[]=filename.split("--");
        	String username=namesplit[0];
        	//System.out.println(username);
        	String remainder[]=namesplit[namesplit.length-1].split("-");
        	int queryNb = new Integer(remainder[remainder.length-1]);
        	//System.out.println(queryNb);
        	String end="log-"+queryNb;
        	String sessionName=filename.replace(end,"log"); 
        	//System.out.println(sessionName);
      	
        	// get user history and put labels
        	if(userList.containsKey(username)){
            	//System.out.println(username);

        		User u=userList.get(username);
            	//System.out.println(sessionName);
            	//System.out.println(queryNb);
        		u.putLabel(sessionName, queryNb, label);
        	}
        	
        	/*
        	Session s =l.getSessionByQueryId(0);
        	System.out.println(s.getSid()+" "+ s.getUser());
        	System.out.println(s.getSummary());
        	//System.out.println(s.getNumberOfQueries());
        	*/
        	
        	
        	
        }
        scanner.close();
        
		System.out.println("Then reading session labels");

       
        // read session labels
		scanner = new Scanner(new File(sessionLabelFile));
		scanner.useDelimiter(";");
        
        scanner.nextLine(); // reads header line
        while(scanner.hasNextLine()){
        	
        	// get session and query label
        	String line = scanner.nextLine();
        	String[] ts = line.split(";");
        	String filename=ts[0];
        	Character label = new Character(ts[1].charAt(0));
        	//System.out.println(label);
        	String namesplit[]=filename.split("--");
        	String username=namesplit[0];
        	//System.out.println(username);
        	
        	User u=userList.get(username);
            	
        	u.putSessionLabel(filename, label);
        }
        	
        	/*
        	Session s =l.getSessionByQueryId(0);
        	System.out.println(s.getSid()+" "+ s.getUser());
        	System.out.println(s.getSummary());
        	//System.out.println(s.getNumberOfQueries());
        	*/
        	

        scanner.close();
       
        
	}
	
	
	public static void createUsers(){
		System.out.println("Creating users");
		
		userList=new HashMap<String, User>();
		
		int i=1;
		for(Session s :l.getSessionList()){
			
			String filename=s.getMetadata("filename");
			System.out.println("processing session "+ i++ + ": " + filename);
			
			String username=filename.split("--")[0]; //warning only for dopan!!! should be done in logLoader!!
			//System.out.println("username: " + username);
			//System.out.println("sessionname: " + filename);
//			String username=filename.split("_session")[0]; //warning only for smartBI!!! should be done in logLoader!!
			
			if(userList.containsKey(username)){
				User u=userList.get(username);
				u.addSession(s);
				userList.put(username, u);
			}
			else{
				User u=new User(username,s);
				
				//UserHistory uh=new UserHistory(s);				
				userList.put(username, u);
				
			}
		}
		System.out.println("End of creating users");
		//System.out.println("Nb of users: " + userList.size());
	}
	
	
	/*
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
       
        
 	   
    }*/
    
    


    
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
       // CsvLogLoader sll = new CsvLogLoader(be, "res/logs/smartBI/IS_ADBIS/logs/");
        // fake logs for tests
        CsvLogLoader sll = new CsvLogLoader(be, logDirectory);
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
        //SaikuLogLoader sll = new SaikuLogLoader(be, "res/logs/dopan/cleanLogs/dibstudent03--2016-09-24--23-01.log");
        SaikuLogLoader sll = new SaikuLogLoader(be, logDirectory);
        
        l   = sll.loadLog();
              
       // System.out.println("Log summary:");
       // System.out.println(l.logSummary());
        
       // for(Session s_tmp : l.getSessionList()) {           
       //     System.out.println(s_tmp.getSummary());
            
        //}
             
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
