/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.ext.jung;

import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;

/**
 *
 * @author mahfoud
 */
public interface I_GraphBuilder {
    
    /**
     * Build graph from cells
     * @param arg_cellList
     * @return 
     */
    public Graph<EAB_Vertex, EAB_Edge> buildGraphFromCells(Collection<EAB_Cell> arg_cellList);
    
}
