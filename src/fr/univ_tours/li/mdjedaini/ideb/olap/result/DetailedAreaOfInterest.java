package fr.univ_tours.li.mdjedaini.ideb.olap.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.univ_tours.li.mdjedaini.ideb.interestingness.UserHistory;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;


/**
 * @author patrick
 *
 */
public class DetailedAreaOfInterest {

	//HashMap<EAB_Cell, Integer> theCells;
	
	//Query mostDetailedQuery;
	//ResultStructure rs;
	Map<EAB_Hierarchy,Set<EAB_Member>> memberListByHierarchy;
	int size;
	EAB_Cube cube;
	
	public DetailedAreaOfInterest(){
		//theCells=new HashMap<EAB_Cell, Integer> ();
	}
	
	
	/**
	 * store the cells of q's result as this detailed area
	 * @param q
	 */
	public DetailedAreaOfInterest(Query q, EAB_Cube cube){
		this();
		//System.out.println("creating detailed area for: " + q.toString());
		//mostDetailedQuery=q;
		this.cube=cube;
		//System.out.println(cube.toString());
		
		ResultStructure rs=((QueryTriplet) q).executePartially(Boolean.FALSE);
		memberListByHierarchy = rs.getMemberListByHierarchy();
		size=this.computeNumberOfCells();
		
		
		//Result r = q.getResult();
		//System.out.println("mondrian: " + q.getMondrianQuery().toString());
		
		//CellList cl=r.getCellList();
		//System.out.println(cl.nbOfCells());
		//Collection<EAB_Cell> col=cl.getCellCollection();
	
		//Iterator<EAB_Cell> it = col.iterator();
		//while(it.hasNext()){
		//	theCells.put(it.next(), 1);
		//}	
	}
	
	
	/**
	 * the detailed area of c
	 * @param c 
	 */
	public DetailedAreaOfInterest(EAB_Cell c){
		this(c.getMostDetailedQueryForCell(), c.getCube());
		
	}
	
	
	/**
	 * the detailed area of all cells of uh
	 * @param uh
	 */
	public DetailedAreaOfInterest(UserHistory uh,  EAB_Cube cube){
		this(new CellList(uh.getCollection()),cube);
	}
	

	/**
	 * the detailed area of all cells of r
	 * @param r
	 */
	public DetailedAreaOfInterest(Result r){		
		this(r.getCellList(), r.getCube());
	}
	
	
	
	/**
	 * the detailed area of all cells of cl
	 * @param cl
	 */
	public DetailedAreaOfInterest(CellList cl, EAB_Cube cube){
		Map<EAB_Hierarchy,Set<EAB_Member>> memberListByHierarchy=new HashMap<EAB_Hierarchy,Set<EAB_Member>>();

		for(EAB_Hierarchy h : cube.getHierarchyList()){
			HashSet<EAB_Member> hm=new HashSet<EAB_Member>();
			memberListByHierarchy.put(h, hm);
		}
		//this();
		this.cube=cube;
		Collection<EAB_Cell> col = cl.getCellCollection();
		Iterator<EAB_Cell> it = col.iterator();
		while(it.hasNext()){
			/*
			Collection<EAB_Cell> currentDetail=it.next().getDetailedAreaCells();
			for(EAB_Cell c : currentDetail){
				if(theCells.containsKey(c)){
					theCells.put(c, theCells.get(c)+1);
				}
				else{
					theCells.put(c, 1);
				}
				
			}
			*/
			EAB_Cell c=it.next();
			
			Query q =c.getMostDetailedQueryForCell();
			ResultStructure currs=((QueryTriplet) q).executePartially(Boolean.FALSE);
		
			
			for(EAB_Hierarchy h : cube.getHierarchyList()){
				for(EAB_Member m : currs.computeMemberListByHierarchy(h)){
					if(!memberListByHierarchy.get(h).contains(m)){
						
						memberListByHierarchy.get(h).add(m);
						memberListByHierarchy.put(h, memberListByHierarchy.get(h));

					}
				}
			}			
			
		}
		this.memberListByHierarchy=memberListByHierarchy;
		this.size=this.computeNumberOfCells();
		
	}
	
	public DetailedAreaOfInterest (Map<EAB_Hierarchy,Set<EAB_Member>>  map, EAB_Cube c){
		this.memberListByHierarchy=map;
		this.cube=c;
		this.size=this.computeNumberOfCells();
	}
	
	
	public DetailedAreaOfInterest intersect(DetailedAreaOfInterest other){
		Map<EAB_Hierarchy,Set<EAB_Member>> memberListByHierarchy=new HashMap<EAB_Hierarchy,Set<EAB_Member>>();
		
		// intersect members by hierarchy
		for(EAB_Hierarchy h : cube.getHierarchyList()){
			Set<EAB_Member> cur = new HashSet<EAB_Member>();
			cur.addAll(this.memberListByHierarchy.get(h));
			cur.retainAll(other.memberListByHierarchy.get(h));
			memberListByHierarchy.put(h, cur);
			
		}
		
		DetailedAreaOfInterest result = new DetailedAreaOfInterest(memberListByHierarchy,this.cube);
		return result;
	}
	
	public int size(){
		//return theCells.size();
		//return rs.computeNumberOfCells();
		return size;
	}
	
	/*
	public CellList getCellList(){
		Set<EAB_Cell> s=theCells.keySet();
		return new CellList(s);
	}
	
	public String toString(){
		String result="";
		for(EAB_Cell c : theCells.keySet()){
			result=result + c.toString() + "\n";
		}
		return result;
	}
	*/
	
	public int computeNumberOfCells() {
        Integer result  = 1;
        
        //this.computeMemberListByHierarchy();
        //System.out.println(cube.getHierarchyList().size());
        
        for(EAB_Hierarchy eab_h : cube.getHierarchyList()) {
            result  = result * this.memberListByHierarchy.get(eab_h).size();
        }
        
        // result may be negative ?????
        if(result < 0) {
            return Integer.MAX_VALUE;
        }
        
        return result;
    }
	
	public String toString(){
		String result="";
		for(EAB_Hierarchy h : cube.getHierarchyList()){
			for(EAB_Member m : this.memberListByHierarchy.get(h)){
				result=result + h.toString() + ":" + m.toString() + "\n";
			}
			
		}
		return result;
	}
}
