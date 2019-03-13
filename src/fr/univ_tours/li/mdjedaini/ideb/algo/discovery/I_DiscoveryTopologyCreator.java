/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.discovery;

import java.util.Collection;

import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryTopology;

/**
 *
 * @author mahfoud
 */
public interface I_DiscoveryTopologyCreator {
    
    /**
     * // @todo should be removed, to let the place to the second function
     * @param arg_cellList
     * @return 
     */
    public Collection<Collection<EAB_Cell>> determineTopology(Collection<EAB_Cell> arg_cellList);
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    public Collection<DiscoveryTopology> extractTopology(Collection<EAB_Cell> arg_cellList);
    
}
