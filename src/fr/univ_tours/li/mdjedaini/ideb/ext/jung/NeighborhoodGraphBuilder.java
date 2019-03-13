/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.ext.jung;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import fr.univ_tours.li.mdjedaini.ideb.neighborhood.cell.CellNeighborhoodSimple;
import fr.univ_tours.li.mdjedaini.ideb.neighborhood.cell.I_CellNeighborhood;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;

/**
 *
 * @author mahfoud
 */
public class NeighborhoodGraphBuilder extends GraphBuilder {
    
    /**
     * 
     */
    I_CellNeighborhood i_neighborhood;
    
    /**
     * 
     */
    public NeighborhoodGraphBuilder() {
        this.i_neighborhood = new CellNeighborhoodSimple();
    }
    
    /**
     * 
     * @param arg_neighborhood 
     */
    public NeighborhoodGraphBuilder(I_CellNeighborhood arg_neighborhood) {
        this.i_neighborhood = arg_neighborhood;
    }
    
    /**
     * Creates the connection graph from a list of cells.
     * Cells are connected only if and only if they have a distance of 1.
     * @param arg_cellList
     * @return 
     */
    @Override
    public Graph<EAB_Vertex, EAB_Edge> buildGraphFromCells(Collection<EAB_Cell> arg_cellList) {
        
        Graph<EAB_Vertex, EAB_Edge> g  = new UndirectedSparseGraph();

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
                if(this.i_neighborhood.areNeigbours(eav_list.get(i).c, eav_list.get(j).c)) {
                    g.addEdge(new EAB_Edge(eav_list.get(i).c, eav_list.get(j).c), eav_list.get(i), eav_list.get(j));
                }
                
            }
            
        }
        
        return g;
    }   
    
}
