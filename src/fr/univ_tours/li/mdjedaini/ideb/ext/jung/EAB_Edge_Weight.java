/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.ext.jung;

import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;

/**
 *
 * @author mahfoud
 */
public class EAB_Edge_Weight {
    
    /**
     * 
     */
    public String type;
    
    private Query source;
    private Query dest;
    
    /**
     * 
     */
    public Double value;
    
    /**
     * 
     * @param arg_q1
     * @param arg_q2 
     */
    public EAB_Edge_Weight(Query arg_q1, Query arg_q2) {
        this.source = arg_q1;
        this.dest   = arg_q2;
    }
    
    /**
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        EAB_Edge_Weight other   = (EAB_Edge_Weight)obj;
        
        return this.source.equals(other.source) && this.dest.equals(other.dest);
    }
    
}
