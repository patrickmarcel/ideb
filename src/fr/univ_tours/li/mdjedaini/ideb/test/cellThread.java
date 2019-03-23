package fr.univ_tours.li.mdjedaini.ideb.test;

import java.io.FileWriter;
import java.io.IOException;

import fr.univ_tours.li.mdjedaini.ideb.interestingness.User;
import fr.univ_tours.li.mdjedaini.ideb.interestingness.UserHistory;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;

/**
 * @author patrick
 *
 *	Too many DB connections!
 *
 *
 */
public class cellThread implements Runnable {
	
	EAB_Cell c;
	User u;
	Query q;
	String sessionName;
	String username;
	int queryPos;
	int queryLabel;
	Character sessionLabel;
	FileWriter writer ;
	
	public cellThread(EAB_Cell c, User u, Query q, String sessionName, 
			String userName, int queryPos, int queryLabel, Character sessionLabel, FileWriter writer){
		this.c=c;
		this.u=u;
		this.q=q;
		this.sessionName=sessionName;
		this.username=userName;
		this.queryPos=queryPos;
		this.queryLabel=queryLabel;
		this.sessionLabel=sessionLabel;
		this.writer=writer;
	}
	
	
	@Override
	public void run() {
		try {
			
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
			
			TestInterestingnessMTC.writeResult(writer, current);
			//writer.write(current); 
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
