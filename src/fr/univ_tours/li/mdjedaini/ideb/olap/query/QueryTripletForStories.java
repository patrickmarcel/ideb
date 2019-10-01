package fr.univ_tours.li.mdjedaini.ideb.olap.query;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;

public class QueryTripletForStories extends QueryTriplet {
	
	
	
	//int cost;
	//int interest;
	boolean executed=false;

	public QueryTripletForStories(EAB_Cube arg_cube) {
		super(arg_cube);
		
	}

	public QueryTripletForStories(QueryTriplet arg_qt) {
		super(arg_qt);
	}

	

	/**
	 * @return the cost
	 */
	public int getCost() {
		// for now, interest=number of fragments
		int cost = this.projectionList.size()+this.measureList.size()+this.selectionList.size();
		//System.out.println("cost: "+cost);
		return cost;
	}

	
	/**
	 * @return the interest
	 */
	public int getInterest() {
		// for now, interest=number of cells		
		int interest = (int) this.computeNumberOfCellsD();
		//System.out.println("interest: " + interest);
		return interest;
	}

	public Result execute(Boolean arg_store) {
		// TODO need actual execution cost and actual interest
		Result  r=this.execute(arg_store);
		return r;
	}
	
	public String toString() {
		String fragments=super.toString();
		String result = fragments + " ";
		return result;
	}

}
