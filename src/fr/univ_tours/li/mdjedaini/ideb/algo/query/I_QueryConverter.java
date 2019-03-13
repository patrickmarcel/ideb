package fr.univ_tours.li.mdjedaini.ideb.algo.query;


import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mahfoud
 */
public interface I_QueryConverter {
    
    /**
     * Converts a query in a format to another format
     * @param arg_q
     * @return 
     */
    public Query convert(Query arg_q);
    
//    public QueryTriplet toQueryTriplet();
//    
//    public QueryMdx toMdx();
    
}
