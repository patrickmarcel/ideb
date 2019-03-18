package fr.univ_tours.li.mdjedaini.ideb.interestingness;

import org.apache.commons.math.MathException;

import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;

public interface Metrics {
	
		
	/**
	 * 
	 * @return Number of ALLs of this
	 */
	public int nbAll();

	public double ratioAll();

	/**
	 * @param h a user history
	 * @return True if this is in h false otherwise
	 */
	public boolean binaryNovelty(UserHistory h);
	
	/**
	 * @param q
	 * @return z-score of this among the cells of q
	 */
	public double outlierness(CellList cl) throws MathException;
	
	
	public boolean matchRelevance(Query q);
	
	public int numberOfRelatives(CellList cl);
	
	public long sizeOfDetailedArea();
	
	public double simpleRelevance(UserHistory h);
}