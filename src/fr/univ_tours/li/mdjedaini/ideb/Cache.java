/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb;

import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;

/**
 *
 * @author mahfoud
 */
public class Cache {
    
    /**
     * 
     */
    public static Map<Result, CellList> cellListPerResult;
    
    /**
     * 
     */
    public Cache() {
        
    }
    
    /**
     * Clear the cache...
     */
    public static void clear() {
        Cache.cellListPerResult.clear();
    }
    
}
