/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.ext.jung;

import edu.uci.ics.jung.graph.Graph;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import java.util.Collection;

/**
 *
 * @author mahfoud
 */
public abstract class GraphBuilder implements I_GraphBuilder {
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    @Override
    public abstract Graph<EAB_Vertex, EAB_Edge> buildGraphFromCells(Collection<EAB_Cell> arg_cellList);
    
}
