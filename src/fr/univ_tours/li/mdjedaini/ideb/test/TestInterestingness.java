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

import org.apache.commons.math.MathException;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.User;
import fr.univ_tours.li.mdjedaini.ideb.io.CsvLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
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
	
	//static String schemaSmartBI="res/cubeSchemas/smartBI.xml";
	static String schemaSmartBI="res/cubeSchemas/smartBI-march2019.xml";
	//static String schemaDOPAN="res/cubeSchemas/DOPAN_DW3.xml";
	static String schemaDOPAN="res/cubeSchemas/DOPAN_DW3-agg.xml";

	static String test="smartbi";
	//static String test="dopan-from-local";
	//static String test="dopan-on-server";
	
	// test data (smartBI DB + fake labels)
	//static String SMARTBIqueryLabelFile="res/Labels/fakeForTest/fake1sessionQueries.csv";
	//static String SMARTBIsessionLabelFile="res/Labels/fakeForTest/fake1sessionSession.csv";
	//static String SMARTBIlogDirectory="res/logs/smartBI/fakeForTestOnly/user-I051435_session-61125156E07AA4689839C9821FE65B05F7C2143440172F36ADC94A8AB93CE330_analysis-1.csv";
	
	
	
	//smartBI
	static String SMARTBIqueryLabelFile="res/Labels/smartBI/queryLabels.csv";
	static String SMARTBIsessionLabelFile="res/Labels/smartBI/sessionLabels.csv";
	static String SMARTBIlogDirectory="res/logs/smartBI/logs-orig/";
	

	//dopan
	static String DOPANqueryLabelFile="res/Labels/dopan/dopanCleanLogWithVeronikaLabels-FOCUS.csv";
	static String DOPANsessionLabelFile="res/Labels/dopan/sessionFocusLabels.csv";
	static String DOPANlogDirectory="res/logs/dopan/cleanLogs/";
	
	// dopan - test
	//static String queryLabelFile="res/Labels/fakeForTest/dopanCleanLogWithVeronikaLabels-FOCUS.csv";
	//static String sessionLabelFile="res/Labels/fakeForTest/skillScorePerExploration.csv";
	//static String logDirectory="res/logs/dopan/forTests/";
	// dopan - debug
	//static String DOPANqueryLabelFile="res/Labels/fakeForTest/fake12-queries.csv";
	//static String DOPANlogDirectory="res/logs/dopan/forTests/dibstudent09--2016-09-26--20-47.log";
	//static String DOPANsessionLabelFile="res/Labels/fakeForTest/fake12sessionLabel.csv";
	
	public static void main(String[] args) throws MathException, IOException {
		
		//mergeInXLS();
		
		long startTime = System.currentTimeMillis();

		if(test.equals("dopan-from-local")){
			// DOPAN 
			//connectDopanSQLServer("jdbc:sqlserver://10.195.25.10:54027");
			
			//connectDopanSQLServer("jdbc:sqlserver://10.195.25.10:54437/db_test_20190313");
			
			connectDopanSQLServer("jdbc:sqlserver://10.195.25.10\\db_dopan:54437;databaseName=db_dopan;user=dopan;password=diblois"); 
			
	        createUsersDOPAN();
	        readLabelsDOPAN();
		}
		else{
			if(test.equals("dopan-on-server")){
				// DOPAN with local url
				connectDopanSQLServer("jdbc:sqlserver://localhost:1433");
		        createUsersDOPAN();
		        readLabelsDOPAN();
			}else{
				if(test.equals("smartbi")){
					//generateSmartLabels();
					 connectSmartBImysql(); 
					createUsersSmartBI();
					readLabelsSmartBI();
				}
			}
		}
		
	
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
		 //String timestamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	     //String fileName = pathToResult  + "compareToSessionGrade" +"-" +  timestamp + ".csv";	        
	     //FileWriter writer   = new FileWriter(fileName);
	     //writer.write("session ;user;query position;cell hashcode;novelty;outlierness;relevance;surprise;query label;session label\n");
		
		
		for(User u : userList.values()){
			String username=u.getName();
			HashMap<Session, Character> us = u.getSessionLabels();
			for(Session s : us.keySet()){
				
				//set result file
				String timestamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
				String fileName=pathToResult +"RESULT-" +timestamp + "-" +s.getMetadata("filename")+".csv";
				FileWriter writer   = new FileWriter(fileName);
			    writer.write("session ;user;query position;cell hashcode;cell computation time;novelty;outlierness;relevance;surprise;query label;session label\n");
				
				
				Character sessionLabel= us.get(s);
				int sessionHashcode=s.hashCode();
				String sessionName = s.getMetadata("filename");
				int queryPos=0;
				for(Query q : s.getQueryList()){
					int queryHashcode = q.hashCode();
					//long startHistoryTime = System.currentTimeMillis();
					computeHistory(u,queryPos);
					//System.out.println("history computed in: " + (System.currentTimeMillis()-startHistoryTime));

					//u.getHistory().printMembers();
					int queryLabel=u.getCurrentQueryLabel();
					//System.out.println(q.getResult().getCellList().getCellCollection());
					for(EAB_Cell c : q.getResult().getCellList().getCellCollection()){
						
						
						
						long startCellTime = System.currentTimeMillis();
						int cellHashcode=c.hashCode();
						boolean novelty=c.binaryNovelty(u.getHistory());					
						//System.out.println("novelty computed in: " + (System.currentTimeMillis()-startCellTime));

						//long startOutlierTime = System.currentTimeMillis();
						double outlierness=c.outlierness(q.getResult().getCellList());
						//System.out.println("outlier computed in: " + (System.currentTimeMillis()-startOutlierTime));
						
						//long startRelevanceTime = System.currentTimeMillis();
						double relevance=c.simpleRelevance(u.getHistory());
						//System.out.println("relevance computed in: " + (System.currentTimeMillis()-startRelevanceTime));
						
						//long startSurpriseTime = System.currentTimeMillis();
						double surprise=c.surprise(u.getHistory()); // can be infinity, should be transformed before going to csv/excel?
						//System.out.println("surprise computed in: " + (System.currentTimeMillis()-startSurpriseTime));
					
						long stopCellTime = System.currentTimeMillis();
						
						// put in file
						String current="";
						current = current + sessionName + ";";
						current = current+ username + ";";
						//current = current+ queryHashcode + ";";
						current = current+ (queryPos+1) + ";";
						current = current+ cellHashcode + ";";
						current = current+ (stopCellTime-startCellTime) + ";";
						current = current+novelty + ";";
						current = current+outlierness+ ";";
						current = current+ relevance + ";";
						current = current+surprise + ";";
						current = current+queryLabel + ";";
						current = current+sessionLabel ;
						
						
						current = current+"\n";
						writer.write(current); 
						
						System.gc();
						//System.out.println(current);
					}
					queryPos++;
					//System.out.println("Flushing session: "+fileName);
					writer.flush();
					
				}
				writer.close();
				System.out.println("Done with session: " + sessionName);

			}
			System.out.println("Done with user: " + u.getName());

			
		}
		
		
		
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
	
	
	// for DOPAN format
	public static void readLabelsDOPAN() throws FileNotFoundException{
		System.out.println("Reading labels");
		
			
		// CLEAN FILES, REMOVE NAMES IN FILES AND IN FILENAMES!!!!!!
		
		
		System.out.println("First reading query labels");
		
		Scanner scanner = new Scanner(new File(DOPANqueryLabelFile));
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
		scanner = new Scanner(new File(DOPANsessionLabelFile));
		scanner.useDelimiter(";");
        
        scanner.nextLine(); // reads header line
        while(scanner.hasNextLine()){
        	
        	// get session and query label
        	String line = scanner.nextLine();
        	String[] ts = line.split(";");
        	String filename=ts[0];
        	//Character label = new Character(ts[1].charAt(0)); //for ABC labels
        	Character label = new Character(ts[2].charAt(0)); //for ABCD labels
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
	
	
	
	// for SmartBI format
	public static void readLabelsSmartBI() throws FileNotFoundException{
		System.out.println("Reading labels");
		
		
		// CLEAN FILES, REMOVE NAMES IN FILES AND IN FILENAMES!!!!!!
		
		
		System.out.println("First reading query labels");
		
		Scanner scanner = new Scanner(new File(SMARTBIqueryLabelFile));
		scanner.useDelimiter(";");
        
        scanner.nextLine(); // reads header line
        while(scanner.hasNextLine()){
        	
        	// get username, query nb and query label
        	String line = scanner.nextLine();
        	String[] ts = line.split(";");
        	String filename=ts[0];
        	int label = new Integer(ts[2]);
        	
        	int queryNb = new Integer(ts[1]);
        	
        	//System.out.println(queryNb);
        	//System.out.println(sessionName);
        	
        	String username=filename.split("_session")[0];
      	
        	// get user history and put labels
        	if(userList.containsKey(username)){
            	//System.out.println(username);

        		User u=userList.get(username);
            	//System.out.println(filename);
            	//System.out.println(queryNb);
            	//System.out.println(u.getTheSessions().get(filename).getNumberOfQueries());
        		u.putLabel(filename, queryNb, label);
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
		scanner = new Scanner(new File(SMARTBIsessionLabelFile));
		scanner.useDelimiter(";");
        
        //scanner.nextLine(); // reads header line
        while(scanner.hasNextLine()){
        	
        	// get session and query label
        	String line = scanner.nextLine();
        	String[] ts = line.split(";");
        	String filename=ts[0];
        	Character label = new Character(ts[1].charAt(0)); //for ABCD labels
        	//System.out.println(label);
        	String username=filename.split("_session")[0];
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
	
	
	
	// for DOPAN
	public static void createUsersDOPAN(){
		System.out.println("Creating users");
		
		userList=new HashMap<String, User>();
		
		int i=1;
		for(Session s :l.getSessionList()){
			
			String filename=s.getMetadata("filename");
			System.out.println("processing session "+ i++ + ": " + filename);
			
			String username=filename.split("--")[0]; //warning only for dopan!!! should be done in logLoader!!
			//System.out.println("username: " + username);
			//System.out.println("sessionname: " + filename);
			
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
	
	
	// for SmartBI
	public static void createUsersSmartBI(){
		System.out.println("Creating users");
		
		userList=new HashMap<String, User>();
		
		int i=1;
		for(Session s :l.getSessionList()){
			
			String filename=s.getMetadata("filename");
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
    public static void connectSmartBImysql() throws MathException {
        Parameters params   = new Parameters();
        
        params.driver   = "com.mysql.jdbc.Driver";
        params.jdbcUrl  = "jdbc:mysql://127.0.0.1/smartbi";
        params.user     = "root";
        params.password = "";
        params.schemaFilePath   = schemaSmartBI;
        //params.schemaFilePath   = "res/cubeSchemas/smartBI.xml";
        params.rebuildConnectionString();
        
         be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
       
        System.out.println("Importing logs");
        CsvLogLoader sll = new CsvLogLoader(be, SMARTBIlogDirectory);      
        l   = sll.loadLog();
        
        
      
    }
    
    

    
    
    
    /**
     * @throws MathException 
     * 
     */
    public static void connectDopanSQLServer(String url) throws MathException {
        Parameters params   = new Parameters();
        
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        params.jdbcUrl          = url;
        //params.user             = "mahfoud";
        //params.password         = "AvH4My327-vd";
        params.user             = "patrick";       
        params.password         = "oopi7taing7shahD";
        //params.schemaFilePath   = "res/cubeSchemas/DOPAN_DW3.xml";
        params.schemaFilePath   = schemaDOPAN;
        //params.schemaFilePath   = "/Users/patrick/Documents/RECHERCHE/STUDENTS/Mahfoud/dopan/DOPAN_DW3.xml";
        //params.cubeName         = "Cube1MobProInd";
        //params.cubeName         = "Cube4Chauffage";
        
        BenchmarkEngine be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
        
        //SaikuLogLoader sll = new SaikuLogLoader(be, "/Users/patrick/Documents/RECHERCHE/STUDENTS/Mahfoud/logs/DopanLogsNettoyes/cleanLogs/dibstudent03--2016-09-24--23-01.log");
        //SaikuLogLoader sll = new SaikuLogLoader(be, "res/logs/dopan/cleanLogs/dibstudent03--2016-09-24--23-01.log");
        SaikuLogLoader sll = new SaikuLogLoader(be, DOPANlogDirectory);
        
        l   = sll.loadLog();
             
      
      
    }
    
    
   public static void generateSmartLabels() throws IOException{
    	
    	FileWriter writer   = new FileWriter("/Users/patrick/git/ideb/res/Labels/smartBI/queryLabels.csv");
 	    writer.write("log;query; label\n");
 		 
    	Scanner scanner = new Scanner(new File("/Users/patrick/git/ideb/res/Labels/smartBI/forQueryLabels.csv"));
    			
    	scanner.useDelimiter(";");
    	        
    	scanner.nextLine(); // reads header line
    	while(scanner.hasNextLine()){
    	        	
    		String line = scanner.nextLine();
    		System.out.println(line);
    	    String[] ts = line.split(";");
    	    String user=ts[0];
    	    String session=ts[1];
    	    String analysis=ts[2];
    	    int nbQueries=new Integer(ts[4]);
    	    String contribution=ts[5];
    	    contribution=contribution.replace(" ","");
    	    
    	    String toWrite="user-"+user+"_session-"+session+ "_analysis-"+analysis +".csv;";
    	    
    	    //1-7, 10, 14-15
    	    
    	    if(contribution.compareTo("-")==0){
    	    	for(int i=0;i<nbQueries;i++){
    	    		writer.write(toWrite+i+";"+0+"\n");
    	    	}
    	    }
    	    else{
    	    	HashSet<Integer> hs=new HashSet<Integer>();
        	    String[] cts=contribution.split(",");
        	    for(int i=0;i<cts.length;i++){
        	    	System.out.println(cts[i]);
        	    	String[] nb=cts[i].split("-");
        	    	if(nb.length==1){
        	    		hs.add(new Integer(nb[0]));
        	    	}
        	    	else{
        	    		for(int k=new Integer(nb[0]);k<=new Integer(nb[1]);k++)
        	    			hs.add(k);
        	    	}
        	    	
        	    }
        	    
        	    for(int i=0;i<nbQueries;i++){
        	    	 toWrite="user-"+user+"_session-"+session+ "_analysis-"+analysis +".csv;";
        	    	if(hs.contains(i)){
        	    		writer.write(toWrite+i+";"+1+"\n");
        	    	}
        	    	else{
        	    		writer.write(toWrite+i+";"+0+"\n");
        	    	}
        	    }
    	    }
    	        
    	    
    	        	
    	    writer.flush();
    	}
    	scanner.close();
    	writer.close();
    }
    
   
   public static void mergeInXLS() throws IOException{
	   
	   File file   = new File("/Users/patrick/git/ideb/output/interestingness/SmartBI/results18032019");
       
	   
	   String timestamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	   String fileName=pathToResult +"RESULT-" +timestamp + ".csv";
	   FileWriter writer   = new FileWriter(fileName);
	   writer.write("session ;user;query position;cell hashcode;cell computation time;novelty;outlierness;relevance;surprise;query label;session label\n");
		
	   
	   for(String currentFileName : file.list()) {
		   String absoluteFileName = file.getAbsolutePath() + "/" + currentFileName;
		   File f_tmp              = new File(absoluteFileName);    
               
		   Scanner scanner = new Scanner(f_tmp); 
	    	        
		   scanner.nextLine(); // reads header line
	    	while(scanner.hasNextLine()){
	    	        	
	    		String line = scanner.nextLine();
	    		String[] ts = line.split(";");
	    		String toWrite="";
	    		
	    		for(int i = 0;i<ts.length;i++){
	    			if(i==5){
	    				if(ts[i].equals("true")){
		    				toWrite=toWrite+1+";";
		    			}
	    				else{
		    				toWrite=toWrite+0+";";
		    			}
	    			}
	    			if(i==10){
	    				if(ts[i].equals("A")){
		    				toWrite=toWrite+1+";";
		    			}
	    				else{
	    					if(ts[i].equals("B")){
			    				toWrite=toWrite+2+";";
			    			}
		    				else{
		    					if(ts[i].equals("C")){
				    				toWrite=toWrite+3+";";
				    			}
			    				else{
			    					if(ts[i].equals("D")){
					    				toWrite=toWrite+4+";";
					    			}
				    			}
			    			}
		    			}
	    			}
	    			
	    			if(i==6 || i==7 || i==8){
	    				toWrite=toWrite+ts[i].replace('.', ',')+";";
	    			}
	    			if(i!=5 && i!=6 && i!=7 && i!=8 && i!=10){
	    				toWrite=toWrite+ts[i]+";";	
	    			}
	    			
	    		}
	    		
	    		writer.write(toWrite);
	    		writer.write("\n");
               
	    	}
	    	scanner.close();
      
	   
	   }
	   writer.close();
    
   }
}
