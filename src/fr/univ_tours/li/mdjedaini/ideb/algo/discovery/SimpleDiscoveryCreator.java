/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.discovery;

import java.util.ArrayList;
import java.util.Collection;

import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryAsCellTopology;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryTopology;

/**
 *
 * @author mahfoud
 */
public class SimpleDiscoveryCreator implements I_DiscoveryCreator {

    //
    I_DiscoveryTopologyCreator i_dtc;
    I_DiscoveryDataModifier i_ddm;
    
    /**
     * 
     */
    public SimpleDiscoveryCreator() {
        // @TODO a revoir ce qui est fournir a chaque discovery creator
        this.i_dtc  = new DiscoveryKNeighborhoodTopologyCreator();
        this.i_ddm  = new RandomDataModifier();
    }
    
    /**
     * 
     * @param arg_idtc
     * @param arg_iddm 
     */
    public SimpleDiscoveryCreator(I_DiscoveryTopologyCreator arg_idtc, I_DiscoveryDataModifier arg_iddm) {
        super();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Collection<AbstractDiscovery> createDiscoveries(Collection<EAB_Cell> arg_cellList) {
        Collection<AbstractDiscovery> result = new ArrayList<>();
        
        Integer i = 0;
        
        for(DiscoveryTopology d_tmp : this.i_dtc.extractTopology(arg_cellList)) {
            DiscoveryAsCellTopology dact    = new DiscoveryAsCellTopology(d_tmp);
//            System.out.println("Discovery number " + i);
//            System.out.println(dact.toString());
            result.add(dact);
            i++;
        }
        
        return result;
    }
    
}
