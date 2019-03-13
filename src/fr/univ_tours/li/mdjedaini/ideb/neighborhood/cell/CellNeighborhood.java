/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.neighborhood.cell;

import java.util.Set;

import fr.univ_tours.li.mdjedaini.ideb.neighborhood.member.MemberNeighborhood;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;

/**
 * This is the base neighborhood for cells.
 * Returns true if two cells differ only on one hierarchy.
 * Returns false if they differ on more than one hierarchy.
 * For more constrained neighborhoods, this class has to be inherited...
 * @author mahfoud
 */
public class CellNeighborhood implements I_CellNeighborhood {

    //
    MemberNeighborhood mn;
    
    /**
     * 
     */
    public CellNeighborhood() {
        this.mn = new MemberNeighborhood();
    }
    
    /**
     * 
     * @param arg_mn 
     */
    public CellNeighborhood(MemberNeighborhood arg_mn) {
        this.mn = arg_mn;
    }
    
    /**
     * 
     * @param arg_c1
     * @param arg_c2
     * @return 
     */
    @Override
    public boolean areNeigbours(EAB_Cell arg_c1, EAB_Cell arg_c2) {

        Set<EAB_Hierarchy> h_differential    = arg_c1.getDifferentialHierarchyList(arg_c2);
        
        // if the cells are the same or if they differ on more than on hierarchy we return false
        if(h_differential.isEmpty() || h_differential.size() > 1) {
            return false;
        }
        
        return true;
        
    }

}
