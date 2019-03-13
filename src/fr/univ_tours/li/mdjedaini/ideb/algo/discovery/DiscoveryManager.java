/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.discovery;

import java.util.Collection;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;

/**
 *
 * @author mahfoud
 */
public class DiscoveryManager implements I_DiscoveryCreator, I_DiscoveryDetector {

    //
    BenchmarkEngine be;
    
    //
    I_DiscoveryCreator i_dc;
    I_DiscoveryDetector i_dd;
    
    /**
     * 
     * @param arg_be 
     */
    public DiscoveryManager(BenchmarkEngine arg_be) {
        this.be     = arg_be;
        this.i_dc   = new SimpleDiscoveryCreator();
    }

    /**
     * 
     * @param arg_be
     * @param i_dc
     * @param i_dd 
     */
    public DiscoveryManager(BenchmarkEngine arg_be, I_DiscoveryCreator i_dc, I_DiscoveryDetector i_dd) {
        this(arg_be);
        this.i_dc = i_dc;
        this.i_dd = i_dd;
    }

    /**
     * 
     * @param arg_cellList
     * @return 
     */
    @Override
    public Collection<AbstractDiscovery> createDiscoveries(Collection<EAB_Cell> arg_cellList) {
        return this.i_dc.createDiscoveries(arg_cellList);
    }

    /**
     * 
     * @return 
     */
    @Override
    public Collection<AbstractDiscovery> detectDiscoveries() {
        return this.i_dd.detectDiscoveries();
    }
    
    
}
