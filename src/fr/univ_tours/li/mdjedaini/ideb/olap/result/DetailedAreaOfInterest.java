package fr.univ_tours.li.mdjedaini.ideb.olap.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.univ_tours.li.mdjedaini.ideb.interestingness.UserHistory;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;

/**
 * @author patrick
 *
 */
public class DetailedAreaOfInterest {

	HashMap<EAB_Cell, Integer> theCells;
	
	public DetailedAreaOfInterest(){
		theCells=new HashMap<EAB_Cell, Integer> ();
	}
	
	
	/**
	 * 
	 * @param col is already a detailed area
	 */
	public DetailedAreaOfInterest(Collection<EAB_Cell> col){
		this();
		Iterator<EAB_Cell> it = col.iterator();
		while(it.hasNext()){
			theCells.put(it.next(), 1);
		}	
	}
	
	
	/**
	 * the detailed area of c
	 * @param c 
	 */
	public DetailedAreaOfInterest(EAB_Cell c){
		this(c.detailedAreaOfInterest());
	}
	
	
	public DetailedAreaOfInterest(UserHistory uh){
		this(new CellList(uh.getCollection()));
	}
	

	public DetailedAreaOfInterest(Result r){		
		this(r.getCellList());
	}
	
	public DetailedAreaOfInterest(CellList cl){
		this();
		Collection<EAB_Cell> col = cl.getCellCollection();
		Iterator<EAB_Cell> it = col.iterator();
		while(it.hasNext()){
			Collection<EAB_Cell> currentDetail=it.next().detailedAreaOfInterest();
			for(EAB_Cell c : currentDetail){
				if(theCells.containsKey(c)){
					theCells.put(c, theCells.get(c)+1);
				}
				else{
					theCells.put(c, 1);
				}
				
			}
		}
	}
	
	public CellList getCellList(){
		Set<EAB_Cell> s=theCells.keySet();
		return new CellList(s);
	}
}
