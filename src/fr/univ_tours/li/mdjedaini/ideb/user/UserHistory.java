package fr.univ_tours.li.mdjedaini.ideb.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Measure;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;

public class UserHistory {
	//Set<EAB_Cell> theCells;
	// use hashmap where key is cell and value is number time seen

	HashMap<EAB_Cell, Integer> theCells;
	HashMap<EAB_Member, Integer> theMembers;
	HashMap<EAB_Measure, Integer> theMeasures;
	
	
	public UserHistory(){
		theCells=new HashMap<EAB_Cell, Integer> ();
		theMembers=new HashMap<EAB_Member, Integer> ();
		theMeasures=new HashMap<EAB_Measure, Integer> ();
	}
	
	public Collection<EAB_Cell> getCollection(){
		return this.theCells.keySet();
	}
	
	/**
	 * Add c to this user history. If c already there, increment number of views.
	 * @param c the cell to add
	 */
	public void add(EAB_Cell c){
		// just use hashmap contains, no need for the iterator stuff
		
		// check if exists
		Set<EAB_Cell> s= theCells.keySet();
		Iterator<EAB_Cell> it = s.iterator();
		boolean found=false;
		EAB_Cell theKey=null;
		while(it.hasNext() && found!=true){
			EAB_Cell n=it.next();
			if(c.equals(n)){
				found=true;
				theKey=n;
			}
		}
		// if exists add 1
		if(found){
			theCells.put(theKey, theCells.get(theKey)+1);
		}
		else{ // just put the new one
			theCells.put(c, 1);
		}
		analyze(c);
		
	}
	
	
	/**
	 * Analyzes c to maintain the sets of most seen members and measures
	 * @param c
	 */
	void analyze(EAB_Cell c){
		EAB_Measure meas=c.getMeasure();
				
		if(theMeasures.containsKey(meas)){
			theMeasures.put(meas, theMeasures.get(meas)+1);
		}
		else{
			theMeasures.put(meas, 1);
			
		}
		
	}

	public void printMeasures(){
		
		Set<EAB_Measure> sm = theMeasures.keySet();
		Iterator<EAB_Measure> it=sm.iterator();
		while(it.hasNext()){
			EAB_Measure meas=it.next();
			System.out.println(meas.getName() + ": " + theMeasures.get(meas));
			
		}
	}
	
	
	
	public void add(CellList cl){
		Collection<EAB_Cell> col= cl.getCellCollection();
		for(EAB_Cell c : col)
			this.add(c);
		
	}
	
	public void add(Result r){
		CellList cl = r.getCellList();
		this.add(cl);
		
	}
	
	
	
	public boolean contains(EAB_Cell c){
		Set<EAB_Cell> s= theCells.keySet();
		Iterator<EAB_Cell> it = s.iterator();
		boolean found=false;
		EAB_Cell theKey=null;
		while(it.hasNext() && found!=true){
			EAB_Cell n=it.next();
			if(c.equals(n)){
				found=true;
				theKey=n;
			}
		}
								
		if(found) 
			return true;
		else 
			return false;
	}
	
	
	
}
