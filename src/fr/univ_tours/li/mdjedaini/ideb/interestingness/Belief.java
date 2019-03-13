package fr.univ_tours.li.mdjedaini.ideb.interestingness;

import java.util.HashMap;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

public class Belief {
	// double is probability that member is used
	HashMap<EAB_Cell, Double> cellMap;
	HashMap<EAB_Member, Double> memberMap;
	
	public Belief(UserHistory h, Log l, EAB_Cube c){
		// belief comes from user history, global log a,d cube schema
		// see Alex&Ben code : https://github.com/AlexChanson/IntrestingnessForOLAP
		// belief is given under ND4J INDArray output by page rank
		// whose link to cells is given by a Map<Object, Integer>
		// where object is the representation of a cell (well ony query parts are supported so far)
	
	}
	
	public Belief(UserHistory uh){
		memberMap=new HashMap<EAB_Member,Double>();
		//int size=h.theMembers.keySet().size();
		int count=0;
		for(EAB_Member m : uh.theMembers.keySet()){
			count= count + uh.theMembers.get(m);
		}	
		for(EAB_Member m : uh.theMembers.keySet()){
			memberMap.put(m, (double) uh.theMembers.get(m)/count);
		}	
		
		/* not sure what to do here
		// populate cellMap;
		cellMap= new HashMap<EAB_Cell, Double>();

		for(EAB_Cell c : uh.theCells.keySet()){
			
			double acc=0;
			for(EAB_Hierarchy h  : c.getCube().getHierarchyList()){ 
				EAB_Member m = c.getMemberByHierarchy(h);
				double proba_m= memberMap.get(m); // uh.getBelief(m);
				double log_m = Math.log(proba_m);
				acc=acc+log_m;
				
			}
			cellMap.put(c, acc)   ;
		}
		*/
		
	}
	
	
	
	public double beliefOf(EAB_Cell c){
		return cellMap.get(c);	
	}
	
	public String toString(){
		String result="";
		if(memberMap!=null){
			for(EAB_Member c : memberMap.keySet()){
				result+= "member: " + c.toString() + " belief: " + memberMap.get(c) + "\n";
			}
		}
		return result;
	}
	
	/*
	public void importBelief(INDArray vectorOfBelief, Map<Object,Integer>){
		// where object is a cell representation 
		// new EAB_cell
		// put in hashmap
	}*/
}
