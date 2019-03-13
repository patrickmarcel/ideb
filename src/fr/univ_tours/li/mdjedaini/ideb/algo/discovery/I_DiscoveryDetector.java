/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.discovery;

import java.util.Collection;

import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;

/**
 *
 * @author mahfoud
 */
public interface I_DiscoveryDetector {
        
    /**
     * 
     * @return 
     */
    public Collection<AbstractDiscovery> detectDiscoveries();
    
}
