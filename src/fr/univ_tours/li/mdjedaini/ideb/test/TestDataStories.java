package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math.MathException;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.MdxToTripletQueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.TripletToMdxQueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.NumberOfCharacters;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.User;
import fr.univ_tours.li.mdjedaini.ideb.io.CsvLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.SaikuLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Measure;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTripletForStories;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * @author patrick
 *
 */
public class TestDataStories {

	
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
	

	static int timeBudget=100; //in ms
	
	static String startingPoint="select NON EMPTY {Hierarchize({[Annee du recensement.Annee_rencesement_Hierarchie_1].[Annee].Members})} ON COLUMNS,\n" + 
			"  NON EMPTY {Hierarchize({[Measures].[Distance trajet domicile - travail (max)]})} ON ROWS\n" + 
			"from [Cube1MobProInd]";
	
	
	
	public static void main(String[] args) throws MathException, IOException, InterruptedException {
		
		
		//long startTime = System.currentTimeMillis();
		
		System.out.println("connection...");

		if(test.equals("dopan-from-local")){
			// DOPAN 
			//connectDopanSQLServer("jdbc:sqlserver://10.195.25.10:54027");
			
			//connectDopanSQLServer("jdbc:sqlserver://10.195.25.10:54437/db_test_20190313");
			
			connectDopanSQLServer("jdbc:sqlserver://10.195.25.10\\db_dopan:54437;databaseName=db_dopan;user=dopan;password=diblois"); 
			
	        
		}
		
		
		QueryTripletForStories seed=generateSeed(startingPoint);
		
		
		QueryTripletForStories[] hs= pickStory(seed);
		
		print(hs);
		
		hs = knapSack(hs,  timeBudget);
		
		print(hs);
		
       // long stopTime = System.currentTimeMillis();        
       // System.out.println("Done in: " + (double) (stopTime - startTime)/1000 + " seconds");
    
    }
	

	static void print(QueryTripletForStories[] hs) {
		int nb=0;
		for(int i=0; i<hs.length;i++) {
			if(hs[i]!=null) {
				nb++;
				System.out.println(hs[i]);
				System.out.println("cost: "+hs[i].getCost());
				System.out.println("interest: "+hs[i].getInterest());
				System.out.println("---------------");
			}	
		}
		System.out.println("Number of queries: "+nb);
	}

	   
	
	static QueryTripletForStories generateSeed(String mdx) {
		
		Query seed = new QueryMdx(be.getDefaultCube(), mdx);
		MdxToTripletQueryConverter qc=new MdxToTripletQueryConverter();
		QueryTriplet qt= (QueryTriplet) qc.convert(seed);
		return new QueryTripletForStories(qt);
	}
    
	   
	static QueryMdx getMDX(QueryTripletForStories triplet) {
		
		TripletToMdxQueryConverter qc=new TripletToMdxQueryConverter();
		return (QueryMdx) qc.convert(triplet);
	}
	    
	/*
	 * returns a clone of current if cannot go further in h
	 */
	static QueryTripletForStories generateDrillDown(QueryTripletForStories current, EAB_Hierarchy h) {
		//TODO
		// in h, get current.level, remove level, add child
		QueryTripletForStories dd = new QueryTripletForStories(current) ; //clone current
		ProjectionFragment p=dd.getProjectionFragmentByHierarchy(h);
		EAB_Level l = p.getLevel();
		if(l.getChildLevel()!=null) {
			ProjectionFragment np=new ProjectionFragment(dd,l.getChildLevel());
			//System.out.println(l.getChildLevel());
			dd.addProjection(np);
		}	
		return dd;
	}
	
	static QueryTripletForStories generateRollup(QueryTripletForStories current, EAB_Hierarchy h) {
		//TODO
		// in h, get current.level, remove level, add child
		return current;
	}
    
	static HashSet<QueryTripletForStories> generateCandidates(QueryTripletForStories current){
		//TODO
		// for all hierarchy of current, generate drilldowns, generate rollups
		return null;
	}
	
	static QueryTripletForStories[] pickStory(QueryTripletForStories seed) {
		//TODO
		// generate candidates, for all candidates get interest and cost, then knapsack
		
		Set<EAB_Hierarchy> s= be.getDefaultCube().getHierarchyList();
		//HashSet<QueryTripletForStories>  theDrills = new HashSet<QueryTripletForStories>();
		int nbH = be.getDefaultCube().getHierarchyList().size();
		QueryTripletForStories[] theDrills = new QueryTripletForStories[nbH];
		Iterator<EAB_Hierarchy> it = s.iterator();
		int i=0;
		while(it.hasNext()) {
			QueryTripletForStories move=generateDrillDown(seed,it.next());
			if(!move.equals(seed)) {
				System.out.println(getMDX(move));
				theDrills[i]=move;
				i++;
			}
		}
		
		
		return theDrills;
	}
	
	static QueryTripletForStories[] knapSack(QueryTripletForStories[] sack, int budget){
		//TODO
		
		int NB_ITEMS = sack.length-1;
		// we use a matrix to store the max value at each n-th item
		int[][] matrix = new int[NB_ITEMS + 1][budget + 1];

		// first line is initialized to 0
		for (int i = 0; i <= budget; i++)
		  matrix[0][i] = 0;

		// we iterate on items
		for (int i = 1; i <= NB_ITEMS; i++) {
		  // we iterate on each capacity
		  for (int j = 0; j <= budget; j++) {
		    if (sack[i - 1].getCost() > j)
		      matrix[i][j] = matrix[i-1][j];
		    else
		      // we maximize value at this rank in the matrix
		      matrix[i][j] = Math.max(matrix[i-1][j], matrix[i-1][j - sack[i-1].getCost()] + sack[i-1].getInterest());
		  }
		}
		
		// prints solution matrix 
		for (int i = 0; i < NB_ITEMS; i++) {
			  for (int j = 0; j <= budget; j++) {
				  System.out.print(" " + matrix[i][j]);
			  }
			  System.out.println(" " );
		}
		
		// finds solution
		
		int capacity=budget;
		int res = matrix[NB_ITEMS][capacity];
		int w = capacity;
		//List<Item> itemsSolution = new ArrayList<>();
		QueryTripletForStories[] result = new QueryTripletForStories[NB_ITEMS] ;

		int pos=0;
		for (int i = NB_ITEMS; i > 0  &&  res > 0; i--) {
		  if (res != matrix[i-1][w]) {
		    //itemsSolution.add(items[i-1]);
			  result[pos++]= sack[i-1];
		    // we remove items value and weight
		    res -= sack[i-1].getInterest();
		    w -= sack[i-1].getCost();
		  }
		}

		
		
		return result;
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
       
        
        be  = new BenchmarkEngine(params);
        
        be.initDatasource();
        
     
        SaikuLogLoader sll = new SaikuLogLoader(be, DOPANlogDirectory);
        
   //     l   = sll.loadLog();
          
      
      
    }
    
  
    }
    
   
 