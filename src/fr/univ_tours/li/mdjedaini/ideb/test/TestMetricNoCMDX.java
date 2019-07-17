package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math.MathException;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.NumberOfCharacters;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.User;
import fr.univ_tours.li.mdjedaini.ideb.io.CsvLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * @author patrick
 *
 */
public class TestMetricNoCMDX {

	
	static BenchmarkEngine be ;
	static Log l;
	static HashMap<String, User> userList;
	static String pathToResult="output/";
	

	static String schemaDOPAN="res/cubeSchemas/DOPAN_DW3.xml";
	//static String schemaDOPAN="res/cubeSchemas/DOPAN_DW3-agg.xml";

	//static String test="smartbi";
	static String test="dopan-from-local";
	//static String test="dopan-on-server";
	

	

	static String DOPANlogDirectory="res/logs/dopan/supplementary/"; ///6/10/14?
//	static String DOPANlogDirectory="res/logs/dopan/cleanLogs/"; ///6/10/14?
	//static String DOPANlogDirectory="res/logs/dopan/cleanLogs/dibstudent16--2016-10-06--22-03.log";
	
	
	public static void main(String[] args) throws MathException, IOException, InterruptedException {
		
		
		long startTime = System.currentTimeMillis();

		if(test.equals("dopan-from-local")){
			// DOPAN 
			//connectDopanSQLServer("jdbc:sqlserver://10.195.25.10:54027");
			
			//connectDopanSQLServer("jdbc:sqlserver://10.195.25.10:54437/db_test_20190313");
			
			connectDopanSQLServer("jdbc:sqlserver://10.195.25.10\\db_dopan:54437;databaseName=db_dopan;user=dopan;password=diblois"); 
			
	        
		}
		
		//NumberOfCharacters noc = new NumberOfCharacters(be);
		
		String fileName=pathToResult +"textLenghts" +".csv";
		FileWriter writer   = new FileWriter(fileName);
	    writer.write("sessionID ;query position;MDX text length\n");
	    
	    File file   = new File(DOPANlogDirectory);
        
        
        for(String filename : file.list()) {
                String absoluteFileName = file.getAbsolutePath() + "/" + filename;
                File f_tmp              = new File(absoluteFileName);    
                loadSession(absoluteFileName,  writer,filename); 
        }
        
        
		
	/*
		for(Session s :l.getSessionList()){
			int length=0;
			int i=1;
			String filename=s.getMetadata("filename");
			System.out.println("processing session "+  ": " + filename);
			for(Query q : s.getQueryList()){
				String text=((QueryMdx) q).toString();
	        	
	        	System.out.println(text);
	        	
	            length=text.length();
			}
			
			String line=filename + ";" + i + ";" + length;
			
			writer.write(line);
			
			writer.flush();
			i++;
		
			System.out.println("Done with session: " + filename);
		}
*/
	    
	    
	    writer.close();
        
        long stopTime = System.currentTimeMillis();        
        System.out.println("Done in: " + (double) (stopTime - startTime)/1000 + " seconds");
    
    }
	


	
	   /**
     * 
     * @param arg_sessionFilePath
     * @return 
     */
    public static void loadSession(String arg_sessionFilePath, FileWriter writer, String filename) {
        
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        
      
        
        
        System.out.println("I am parsing the file: " + arg_sessionFilePath);
        
        // pattern for extracting cube name
        Pattern p = Pattern.compile("from \\[(.*?)\\].*");

        File file   = new File(arg_sessionFilePath);
        
        int i=1;
        try {
            //BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arg_sessionFilePath), "UTF-8"));
            String line = null;
            
            String currentQuery = "";
            
            // pour parser une requete, je cherche "select"
            // je prends toutes les lignes suivantes, jusqu'a rencontrer une ligne vide...
            while ((line = br.readLine()) != null) {

                if(line.contains("select")) {
                    
                    // look for the time before query execution
                    String date     = line.substring(0, 23);
                    Date d          = sdf.parse(date);
                    Long tsBefore   = d.getTime();
                    
                    // je recupere la position du mot "select" dans la ligne
                    Integer position    = line.indexOf("select");
                    currentQuery        = line.substring(position, line.length());
                    
                    String line_tmp = br.readLine();
                    while(!line_tmp.equals("")) {
                        currentQuery    += System.lineSeparator();
                        //currentQuery    += System.lineSeparator();
                        currentQuery    += line_tmp;
                        line_tmp    = br.readLine();
                    }
                    
                    // extract cubename from the query text
                    // Normally, the pattern is always found!
                    Matcher m = p.matcher(currentQuery);
                    m.find();
                    String cubeName = m.group(1);
                    
                    //System.out.println(currentQuery);
                    //System.out.println("cubeName: " + cubeName);
                    //System.out.println("-------");
                    
                    // look for the execution time
                    while(!line_tmp.contains("exec:")) {
                        line_tmp    = br.readLine();
                    }
                    
                    // here the line contains exec
                    // look for the time before query execution
                    date            = line_tmp.substring(0, 23);
                    d               = sdf.parse(date);
                    Long tsAfter    = d.getTime();
                    
                    
                    //System.out.println(currentQuery);
                    int length=currentQuery.length() ;
                    
                    String lineToWrite=filename + ";" + i + ";" + length + "\n";
        			
        			writer.write(lineToWrite);
        			
        			writer.flush();
        			i++;
                    
                    //Query q_tmp = new QueryMdx(this.be.getInternalCubeByName(cubeName), currentQuery);
                    
                    
                    
                   
                }
                
            }
 
            br.close();
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
        
      
    }
	
	
	   
    

    
    
    
    /**
     * @throws MathException 
     * 
     */
    public static void connectDopanSQLServer(String url) throws MathException {
        Parameters params   = new Parameters();
        
        params.driver           = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        params.jdbcUrl          = url;
        params.user             = "patrick";       
        params.password         = "oopi7taing7shahD";
        params.schemaFilePath   = schemaDOPAN;
       
        
        BenchmarkEngine be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
     
        SaikuLogLoader sll = new SaikuLogLoader(be, DOPANlogDirectory);
        
   //     l   = sll.loadLog();
          
      
      
    }
    
  
    }
    
   
 