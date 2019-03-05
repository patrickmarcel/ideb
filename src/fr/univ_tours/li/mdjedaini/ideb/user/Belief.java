package fr.univ_tours.li.mdjedaini.ideb.user;

import java.util.HashMap;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

public class Belief {
	HashMap<EAB_Cell, Double> cellMap;
	
	public Belief(UserHistory h, Log l, EAB_Cube c){
		// belief comes from user history, global log a,d cube schema
		// see Alex&Ben code : https://github.com/AlexChanson/IntrestingnessForOLAP
		// belief is given under ND4J INDArray output by page rank
		// whose link to cells is given by a Map<Object, Integer>
		// where object is the representation of a cell (well ony query parts are supported so far)
	}
	
	public double beliefOf(EAB_Cell c){
		return cellMap.get(c);	
	}
	
	/*
	public void importBelief(INDArray vectorOfBelief, Map<Object,Integer>){
		// where object is a cell representation 
		// new EAB_cell
		// put in hashmap
	}*/
}
