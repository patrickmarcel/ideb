/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.struct;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;

/**
 *
 * @author mahfoud
 */
public class DiscoveryAsCellWithOlapNeighbors extends AbstractDiscovery {

    
    EAB_Cell centralCell;
    
    CellList neighborHood;
    
    /**
     * 
     * @param arg_cell 
     */
    public DiscoveryAsCellWithOlapNeighbors(EAB_Cell arg_cell) {
       this.centralCell     = arg_cell; 
       this.neighborHood    = new CellList();
       this.computeOlapNeighbors();
    }
    
    /**
     * 
     */
    private void computeOlapNeighbors() {
        EAB_Cube cube   = this.centralCell.getResult().getCube();
        
        for(EAB_Hierarchy h_tmp : cube.getHierarchyList()) {
            
            // if level is ALL we do not consider the hierarchy
//            if(this.centralCell.getMemberByHierarchy(h_tmp).getLevel().isAllLevel()) {
//                continue;
//            }
            
            // for the moment, I drill on each hierarchy, even All...
            
            this.neighborHood.addCellCollection(this.centralCell.rollOnHierarchy(h_tmp));
            this.neighborHood.addCellCollection(this.centralCell.drillOnHierarchy(h_tmp));
            this.neighborHood.addCellCollection(this.centralCell.switchOnHierarchy(h_tmp));
            
        }
        
    }
        
    /**
     * Returns the cell list of the discovery.
     * @return 
     */
    @Override
    public CellList getCellList() {
        return this.neighborHood;
    }
    
}
