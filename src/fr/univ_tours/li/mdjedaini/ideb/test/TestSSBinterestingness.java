package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math.MathException;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.User;
import fr.univ_tours.li.mdjedaini.ideb.io.CsvLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.XMLLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * @author patrick
 *
 */
public class TestSSBinterestingness {

	
	static BenchmarkEngine be ;
	static Log l;
	static HashMap<String, User> userList;
	static double percentageOfPast=0.3;

	
	static String pathToResult="output/interestingness/";
	//static String pathToResult="./";

	//SSB
	static String SSBlogDirectory="/Users/patrick/git/ideb/res/logs/ssb/generatedByCubeload/Workload-1491558570.xml";
	//static String SSBlogDirectory="Workload.xml";
	//static String SSBlogDirectory="Workload-lab.xml";

	//static String schema="/Users/patrick/Documents/ENSEIGNEMENTS/BD/BDMA/Lab/querying/SSBMonetDB-f23.xml";
	//static String schema="res/cubeSchemas/ssb.xml";
	//static String schema="res/cubeSchemas/ssb-oracle.xml";
	static String schema="res/cubeSchemas/ssb-monet.xml";
	//static String schema="ssb-oracle.xml";
	
	public static void main(String[] args) throws MathException, IOException, InterruptedException {
		
		long startTime = System.currentTimeMillis();

		//connectSSBoracle();
		
		//connectSSBmysql(); 
		connectSSBmonetdb();
		
		createUsers();
	      
        
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
	
	
	
	public static void metricsBySessionGrade() throws MathException, IOException, InterruptedException{
			
		int nbQueries=0; //queries???
		int nbProc = Runtime.getRuntime().availableProcessors();
		
		for(User u : userList.values()){
			String username=u.getName();
			//HashMap<Session, Character> us = u.getSessionLabels();
			for(Session s : u.getTheSessions().values()){
				
				
				//set result file
				String timestamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
				String fileName=pathToResult +"RESULT-" +timestamp + "-" +s.getMetadata("name")+".csv";
				FileWriter writer   = new FileWriter(fileName);
			    writer.write("session ;user;query position;cell hashcode;cell computation time;novelty;outlierness;relevance;surprise;query label;session label\n");
				
				int sessionHashcode=s.hashCode();
				String sessionName = s.getMetadata("name");
				int queryPos=0;
				for(Query q : s.getQueryList()){
					
					double x = Math.random();
					//System.out.println("x: " + x);
					if(x>0.4){
						int queryHashcode = q.hashCode();
						
						computeHistory(u,queryPos);

						//u.getHistory().printMembers();
						int queryLabel=u.getCurrentQueryLabel();
					
						
						ExecutorService executor = Executors.newFixedThreadPool(nbProc);
						
						for(EAB_Cell c : q.getResult().getCellList().getCellCollection()){					
																
							Thread t=new Thread(new  cellThread(c, u,q, sessionName, 
									username,  queryPos,  queryLabel,  'z', writer));			

							executor.execute(t);		 								
						}	
											
				        executor.shutdown();			        	        
				        while (!executor.isTerminated()) {
				        }
				        
						queryPos++;
						nbQueries++;
					}
					else{
						//do nothing
					}
					
				
				}
				writer.close();
				System.out.println("Done with session: " + sessionName);

			}
			System.out.println("Done with user: " + u.getName());

			
		}
		System.out.println("Done with nbQueries: " + nbQueries);
		
	}
	
	
	

	public static synchronized void writeResult(FileWriter writer, String line) throws IOException{
		
		writer.write(line);
		writer.flush();
		
	}
	
	

	
	
	
	
	
	
	// for SmartBI
	public static void createUsers(){
		System.out.println("Creating users");
		
		userList=new HashMap<String, User>();
		
		int i=1;
		for(Session s :l.getSessionList()){
			
			String filename=s.getMetadata("name");
			System.out.println("processing session "+ i++ + ": " + filename);
			
			//System.out.println("username: " + username);
			//System.out.println("sessionname: " + filename);
			String username=filename.split("_session")[0]; //warning only for smartBI!!! should be done in logLoader!!
			
			//System.out.println("USERNAME: " + username);
			
			if(userList.containsKey(username)){
				User u=userList.get(username);
				u.addSession(s);
				userList.put(username, u);
			}
			else{
				User u=new User(username,s);
				userList.put(username, u);
				
			}
		}
		System.out.println("End of creating users");
		//System.out.println("Nb of users: " + userList.size());
	}
	
	
	
	
	
 


    
    /**
     * This one has smaller cube and is for test purpose. Its logs are not labeled.
     * @throws MathException 
     * 
     */
    public static void connectSSBmysql() throws MathException {
        Parameters params   = new Parameters();      
       
        // mysql
        params.driver   = "com.mysql.jdbc.Driver";
        params.jdbcUrl  = "jdbc:mysql://127.0.0.1/ssb";
        params.user     = "root";
        params.password = "";
        params.schemaFilePath   = schema;
        params.rebuildConnectionString();
        
        be  = new BenchmarkEngine(params);       
        be.initDatasource();
        
       
        System.out.println("Importing logs");

        XMLLogLoader sll = new XMLLogLoader(be, SSBlogDirectory);      
        l   = sll.loadLog();
        
        
      
    }
    
    public static void connectSSBmonetdb() throws MathException {
        Parameters params   = new Parameters();
              
        //monetDB
        params.driver   = "nl.cwi.monetdb.jdbc.MonetDriver";
        params.jdbcUrl  = "jdbc:monetdb://127.0.0.1/SSB";
        //jdbc:monetdb://localhost:50000/mydb
        params.user     = "monetdb";
        params.password = "monetdb";
        params.schemaFilePath   = schema;

        params.cubeName         = "SSB";
        

        params.rebuildConnectionString();
        
        be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
       
        System.out.println("Importing logs");

        XMLLogLoader sll = new XMLLogLoader(be, SSBlogDirectory);      
        l   = sll.loadLog();
        
        
      
    }
    
    public static void connectSSBoracle() throws MathException {
        Parameters params   = new Parameters();
       
        params.driver   = "oracle.jdbc.OracleDriver";
        // localhost:1521 pour vm salle 341/5
        params.jdbcUrl  = "jdbc:oracle:thin:@127.0.0.1:1521:db01"; // port 1521? or 11521?
        //params.jdbcUrl  = "jdbc:oracle:thin:@127.0.0.1:55000:db01"; // on oracle vm
        params.user     = "oracle";
        params.password = "oracle";
        
        params.schemaFilePath   = schema;
        params.cubeName         = "SSB";

        params.rebuildConnectionString();
        
        BenchmarkEngine be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
       
        System.out.println("Importing logs");

        XMLLogLoader sll = new XMLLogLoader(be, SSBlogDirectory);      
        l   = sll.loadLog();
        
        
      
    }
    
    
    
    
 
    
}
