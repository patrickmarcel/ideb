package fr.univ_tours.li.mdjedaini.ideb.interestingness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;




public class User {
	
	UserHistory history;
	Query currentQuery;
	//EAB_Cube cube;
	String name;
	Session currentSession;
	int numberOfQueries;
	int positionInCurrentSession;
	int currentQueryLabel;

	HashMap<String, Session> theSessions;
	//HashMap<EAB_Cell, ArrayList<Integer>> theLabels;
	HashMap<Query, Integer> theLabels;
	HashMap<Session, Character> sessionLabels;
	
	public User(String name){
		this.name=name;
		numberOfQueries=0;
		theSessions=new HashMap<String, Session>();
		theLabels=new  HashMap<Query, Integer> ();
		sessionLabels=new  HashMap<Session, Character> ();
		//theLabels=new  HashMap<EAB_Cell, ArrayList<Integer>> ();
		//history=new UserHistory();
	}
	
	public User(String name, Session s){
		this(name);
		addSession(s);
	}
	
	public int getNumberOfQueries(){
		return numberOfQueries;
	}
	
	public Query getCurrentQuery(){
		return currentQuery;
	}
	
	public Session getCurrentSession(){
		return currentSession;
	}
	
	public UserHistory getHistory(){
		return history;
	}

	public int getCurrentQueryLabel(){
		return currentQueryLabel;
	}
	
	public void addSession(Session s){
		String sessionName=s.getMetadata("filename");
		theSessions.put(sessionName,s);
		numberOfQueries+=s.getQueryList().size();
		//this.add(s.getCellList());
			
	}
	
	public String getName(){
		return name;
	}
	
	public Belief getBelief() {
		return history.getBelief();
	}
	
	public void computeHistory(double percentageOfPast){
		int numberOfPastQueries = (int) (this.numberOfQueries * percentageOfPast);
		computeHistory(numberOfPastQueries);
	}
	
	public void computeHistory(int numberOfPastQueries){
		// find current session and query
		// instantiate history
		history=new UserHistory();
		
		Iterator<Session> its=theSessions.values().iterator();
		boolean reached=false;
		int count=1;
		while(its.hasNext() && !reached){
			Session s =its.next();
			Iterator<Query> itq= s.getQueryList().iterator();
			while(itq.hasNext() && !reached){
				Query q =itq.next();
				if(count<numberOfPastQueries){
					history.add(q.getResult());
					count ++;
				}
				else{
					currentSession=s;
					positionInCurrentSession=count;
					currentQuery=q;
					currentQueryLabel=theLabels.get(q);
					reached=true;
				}
			}
		}
		
		
	}
	
	public HashMap<Query, Integer> getLabels(){
		return  theLabels;
	}
	
	public HashMap<Session, Character> getSessionLabels(){
		return  sessionLabels;
	}
	
	public void putSessionLabel(String sessionName, Character label){
		Session s = theSessions.get(sessionName);
		sessionLabels.put(s, label);
	}
	
	public Character getSessionLabel(Session s){
		return sessionLabels.get(s);
	}
	
	public void putLabel(String sessionName, int queryNb, int label){
		if(theSessions.containsKey(sessionName)){
			//System.out.println(" FOUND: " + sessionName);
			//find session
			Session s = theSessions.get(sessionName);
			//find query
			Query q = s.getQueryByPosition(queryNb);
			theLabels.put(q, label);
						
			
		}
		else
			System.out.println("NOT FOUND: " + sessionName);
		
	}
	
	
	
	
	/*
	public HashMap<EAB_Cell, ArrayList<Integer>> getLabels(){
		return  theLabels;
	}
	
	
	
	public void putLabel(String sessionName, int queryNb, int label){
		if(theSessions.containsKey(sessionName)){
			//System.out.println(" FOUND: " + sessionName);
			//find session
			Session s = theSessions.get(sessionName);
			//find query
			Query q = s.getQueryByPosition(queryNb);
			//associate cells of q with label
			for(EAB_Cell c : q.getResult().getCellList().getCellCollection()){
				if(theLabels.containsKey(c)){
					// check if label already there
					if(theLabels.get(c).size()<2){
						int alreadyThere=theLabels.get(c).get(0);
						if(label!=alreadyThere)
							theLabels.get(c).add(label);
					}
					
				}
				else{
					ArrayList<Integer> labels=new ArrayList<Integer>();
					labels.add(label);
					theLabels.put(c, labels);
				}				
			}
		}
		else
			System.out.println("NOT FOUND: " + sessionName);
		
	}
	*/

	
}
