/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.ext.jung;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import fr.univ_tours.li.mdjedaini.ideb.neighborhood.cell.I_CellNeighborhood;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;

/**
 *
 * @author mahfoud
 */
public class NeighborhoodDirectedGraphBuilder extends GraphBuilder {
    
    /**
     * 
     */
    I_CellNeighborhood neighbourhood;

    /**
     * 
     * @param neighbourhood
     */
    public NeighborhoodDirectedGraphBuilder(I_CellNeighborhood neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
    
    
    /**
     * Creates the connection graph from a list of cells.
     * Cells are connected only if and only if they have a distance of 1.
     * @param arg_cellList
     * @return 
     */
    @Override
    public DirectedGraph<EAB_Vertex, EAB_Edge> buildGraphFromCells(Collection<EAB_Cell> arg_cellList) {
        
        DirectedGraph<EAB_Vertex, EAB_Edge> g  = new DirectedSparseGraph();

        ArrayList<EAB_Vertex> eav_list   = new ArrayList(arg_cellList.size());
        
        // 
        for(EAB_Cell c_tmp : arg_cellList) {
            EAB_Vertex eav_tmp   = new EAB_Vertex(c_tmp);
            
            eav_list.add(eav_tmp);
            g.addVertex(eav_tmp);
        }
        
        // add vertices and adges
        for(int i = 0; i < eav_list.size(); i++) {
            
            for(int j = i+1; j < eav_list.size(); j++) {
                
                // add edge only for neighbours
                if(this.neighbourhood.areNeigbours(eav_list.get(i).c, eav_list.get(j).c)) {
                    g.addEdge(new EAB_Edge(eav_list.get(i).c, eav_list.get(j).c), eav_list.get(i), eav_list.get(j));
                }
                
            }
            
        }
        
        return g;
    }   
    
}
