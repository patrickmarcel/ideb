package fr.univ_tours.li.mdjedaini.ideb.interestingness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;




public class User {
	
	UserHistory history=null;
	Query currentQuery;
	//EAB_Cube cube;
	String name;
	Session currentSession;
	int numberOfQueries;
	int positionInCurrentSession;
	int currentQueryNumber;
	int currentQueryLabel;
	int currentSessionNumber;
	ArrayList<Integer> tabSessionSize;

	HashMap<String, Session> theSessions;
	//HashMap<EAB_Cell, ArrayList<Integer>> theLabels;
	HashMap<Query, Integer> theLabels;
	HashMap<Session, Character> sessionLabels;
	ArrayList<Session> orderedSessions;
	
	public User(String name){
		this.name=name;
		numberOfQueries=0;
		theSessions=new HashMap<String, Session>();
		theLabels=new  HashMap<Query, Integer> ();
		sessionLabels=new  HashMap<Session, Character> ();
		orderedSessions= new ArrayList<Session> ();
		tabSessionSize = new ArrayList<Integer>();
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
	
	public HashMap<String, Session> getTheSessions(){
		return theSessions;
	}
	
	public void addSession(Session s){
		String sessionName=s.getMetadata("filename");
		theSessions.put(sessionName,s);
		numberOfQueries+=s.getQueryList().size();
		orderedSessions.add(s);
		tabSessionSize.add(s.getQueryList().size());
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
		
		history=new UserHistory(); //needs to be iteratively done
	
		
		Iterator<Session> its=theSessions.values().iterator();
		boolean reached=false;
		int count=1;
		int nbSession=1;
		while(its.hasNext() && !reached){
			Session s =its.next();
			int positionInCurrent=0;
			Iterator<Query> itq= s.getQueryList().iterator();
			while(itq.hasNext() && !reached){
				Query q =itq.next();
				if(count<numberOfPastQueries){
					history.add(q.getResult());
					count ++;
					positionInCurrent++;
				}
				else{
					currentSession=s;
					positionInCurrentSession=positionInCurrent;
					currentQuery=q;
					currentQueryLabel=theLabels.get(q);
					currentSessionNumber=nbSession;
					currentQueryNumber=count;
					reached=true;
				}
			}
			nbSession++;
		}
		
		
	}
	
	
	public void computeHistoryIterative(int numberOfPastQueries){
		
		if(history==null){
			history=new UserHistory(); 
			currentQueryNumber=0;
			currentSession=orderedSessions.get(0);
			positionInCurrentSession=0;
			currentQuery=orderedSessions.get(0).getFirstQuery();
			currentSessionNumber=0;
		}
		// compute session nb
		
		/*
		int count=0, i=0;
		while(count<numberOfPastQueries){
			count+=tabSessionSize.get(i);
			i++;
		}
		if(count==numberOfPastQueries){
			i++;
		}
		// i is session containing the next current query
		 
		 */
		
		int j=currentQueryNumber;
		Session s=currentSession;
		Query q=currentQuery;
		while(j<numberOfPastQueries){
			if(positionInCurrentSession==s.getNumberOfQueries()){
				currentSessionNumber=currentSessionNumber+1;
				s=orderedSessions.get(currentSessionNumber);
				currentSession=s;
				positionInCurrentSession=0;
			}
			q = s.getQueryByPosition(positionInCurrentSession);
			history.add(q.getResult());
			currentQueryNumber=currentQueryNumber+1;
			positionInCurrentSession=positionInCurrentSession+1;
			j++;
		}
		currentSession=s;
		//positionInCurrentSession=positionInCurrentSession+1;
		currentQuery=q;
		if(theLabels.containsKey(q)){
			currentQueryLabel=theLabels.get(q);
		}
		else{
			currentQueryLabel=999;
		}
		//currentSessionNumber=nbSession;
		history.computeBelief();
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
	
	
	public String toString(){
		String result ="I am user " + name + "\n";
		result = result + "I did a total of " + numberOfQueries + " queries\n";
		for(Session s : theSessions.values()){
			result = result + "I did session " + s.getMetadata("filename") + " with " + s.getQueryList().size()+ " queries\n";
			
		}
		
		return result;
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
