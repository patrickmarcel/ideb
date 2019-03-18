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
	HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> memberScoreByHierarchy;

	long size;
	EAB_Cube cube;
	UserHistory uh; //needed?->remove
	
	public DetailedAreaOfInterest(){
		//theCells=new HashMap<EAB_Cell, Integer> ();
	}
	
	public DetailedAreaOfInterest(EAB_Cube cube){
		this.cube=cube;
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

		HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> memberScoreByHierarchy = new HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> ();;
		
		for(EAB_Hierarchy h : cube.getHierarchyList()){
			HashSet<EAB_Member> hm=new HashSet<EAB_Member>();
			memberListByHierarchy.put(h, hm);
			
			HashMap<EAB_Member,Integer> hmembfreq = new HashMap<EAB_Member,Integer>();
			memberScoreByHierarchy.put(h, hmembfreq);
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
					
			if(cube.equals(c.getCube())){
							
			Query q =c.getMostDetailedQueryForCell();
			//System.out.println(q.toString());
			ResultStructure currs=((QueryTriplet) q).executePartially(Boolean.FALSE);		
			
			for(EAB_Hierarchy h : cube.getHierarchyList()){
				
				
//				for(EAB_Member m : currs.computeMemberListByHierarchy(h)){
				//System.out.println(h.toString());
				//System.out.println(cube.getName());
				//System.out.println(currs.memberListByHierarchy.get(h));
				for(EAB_Member m : currs.memberListByHierarchy.get(h)){
					
					//list members
					if(!memberListByHierarchy.get(h).contains(m)){
						
						memberListByHierarchy.get(h).add(m);
						memberListByHierarchy.put(h, memberListByHierarchy.get(h));

					}
					
					//score members
					
					if(memberScoreByHierarchy.get(h).containsKey(m)){
						memberScoreByHierarchy.get(h).put(m, memberScoreByHierarchy.get(h).get(m)+1);
					}
					else{
						memberScoreByHierarchy.get(h).put(m, 1);
					}
				}
				//memberScoreByHierarchy.put(h, memberScoreByHierarchy.get(h));
				
			}			
			}
		}
		
		this.memberScoreByHierarchy=memberScoreByHierarchy;
		this.memberListByHierarchy=memberListByHierarchy;
		this.size=this.computeNumberOfCells();	
		
	}
	
	
	
	public DetailedAreaOfInterest (Map<EAB_Hierarchy,Set<EAB_Member>>  map, 
			HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> memberScoreByHierarchy,
			EAB_Cube c){
		this.memberScoreByHierarchy=memberScoreByHierarchy;
		this.memberListByHierarchy=map;
		this.cube=c;
		this.size=this.computeNumberOfCells();
	}
	
	
	
	// TOCHECK
	public void add(CellList cl, EAB_Cube cube){
		if(this.memberListByHierarchy==null){
			Map<EAB_Hierarchy,Set<EAB_Member>> memberListByHierarchy=new HashMap<EAB_Hierarchy,Set<EAB_Member>>();

			HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> memberScoreByHierarchy = new HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> ();;

			for(EAB_Hierarchy h : cube.getHierarchyList()){
				HashSet<EAB_Member> hm=new HashSet<EAB_Member>();
				memberListByHierarchy.put(h, hm);
				
				HashMap<EAB_Member,Integer> hmembfreq = new HashMap<EAB_Member,Integer>();
				memberScoreByHierarchy.put(h, hmembfreq);
			}
			
			this.memberScoreByHierarchy=memberScoreByHierarchy;
			this.memberListByHierarchy=memberListByHierarchy;
		}
			
		//this.cube=cube;
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
					
			if(cube.equals(c.getCube())){ // useless, remove
							
			Query q =c.getMostDetailedQueryForCell();
			//System.out.println(q.toString());
			ResultStructure currs=((QueryTriplet) q).executePartially(Boolean.TRUE);		
			
			for(EAB_Hierarchy h : cube.getHierarchyList()){
				
				
//				for(EAB_Member m : currs.computeMemberListByHierarchy(h)){
				//System.out.println(h.toString());
				//System.out.println(cube.getName());
				//System.out.println(currs.memberListByHierarchy.get(h));
				for(EAB_Member m : currs.memberListByHierarchy.get(h)){
					
					//list members
					if(!memberListByHierarchy.get(h).contains(m)){
						
						memberListByHierarchy.get(h).add(m);
						memberListByHierarchy.put(h, memberListByHierarchy.get(h));

					}
					
					//score members
					
					if(memberScoreByHierarchy.get(h).containsKey(m)){
						memberScoreByHierarchy.get(h).put(m, memberScoreByHierarchy.get(h).get(m)+1);
					}
					else{
						memberScoreByHierarchy.get(h).put(m, 1);
					}
				}
				//memberScoreByHierarchy.put(h, memberScoreByHierarchy.get(h));
				
			}			
			}
		}
		
		
		this.size=this.computeNumberOfCells();	
		
	}
	
	
	
	
	
	
	public DetailedAreaOfInterest intersect(DetailedAreaOfInterest other){
		Map<EAB_Hierarchy,Set<EAB_Member>> localMemberListByHierarchy=new HashMap<EAB_Hierarchy,Set<EAB_Member>>();
		
		HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> localMemberScoreByHierarchy = new HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> ();;


		// intersect members by hierarchy
		for(EAB_Hierarchy h : cube.getHierarchyList()){
			Set<EAB_Member> cur = new HashSet<EAB_Member>();
			if(!other.memberListByHierarchy.isEmpty()){
				cur.addAll(this.memberListByHierarchy.get(h));
				cur.retainAll(other.memberListByHierarchy.get(h));
			}
			
			localMemberListByHierarchy.put(h, cur);						
			
		}
	
		// 
		// TOCHECK
		HashMap<EAB_Hierarchy,HashMap<EAB_Member,Integer>> othersMembers = other.memberScoreByHierarchy;
		for(EAB_Hierarchy h : cube.getHierarchyList()){
			Set<EAB_Member> cur = localMemberListByHierarchy.get(h);
			HashMap<EAB_Member,Integer> hs = new HashMap<EAB_Member,Integer>();
			for(EAB_Member m : cur){
				int score=othersMembers.get(h).get(m);
				hs.put(m, score);
				localMemberScoreByHierarchy.put(h, hs);
			}
			
		}
		
		
		DetailedAreaOfInterest result = new DetailedAreaOfInterest(localMemberListByHierarchy,localMemberScoreByHierarchy,this.cube);
		return result;
	}
	
	
	
	public void setUH(UserHistory uh){
		this.uh=uh;
	}
	
	
	public long size(){
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
	
	public long computeNumberOfCells() {
        long result  = 1;
        
        //this.computeMemberListByHierarchy();
        //System.out.println(cube.getHierarchyList().size());
        
        for(EAB_Hierarchy eab_h : cube.getHierarchyList()) {
            result  = result * this.memberListByHierarchy.get(eab_h).size();
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
	
	public void printDimensionSize(){
		
		for(EAB_Hierarchy h : cube.getHierarchyList()){
			System.out.println(h.toString() + ": "+ this.memberListByHierarchy.get(h).size());
			
		}
		
	}
	
	
}
