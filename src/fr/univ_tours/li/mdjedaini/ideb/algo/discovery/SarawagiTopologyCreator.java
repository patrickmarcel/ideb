/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.discovery;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;
import fr.univ_tours.li.mdjedaini.ideb.ext.jung.EAB_Edge;
import fr.univ_tours.li.mdjedaini.ideb.ext.jung.EAB_Vertex;
import fr.univ_tours.li.mdjedaini.ideb.ext.jung.GraphBuilder;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryTopology;
import fr.univ_tours.li.mdjedaini.ideb.tools.Pair;

/**
 *
 * @author mahfoud
 */
public class SarawagiTopologyCreator implements I_DiscoveryTopologyCreator {
    
    GraphBuilder graphBuilder;
    
    Graph<EAB_Vertex, EAB_Edge> g;
    BetweennessCentrality<EAB_Vertex, EAB_Edge> bc;
    PageRank<EAB_Vertex, EAB_Edge> pr;
    
    /**
     * 
     * @param arg_graphBuilder 
     */
    public SarawagiTopologyCreator(GraphBuilder arg_graphBuilder) {
        this.graphBuilder   = arg_graphBuilder;
    }
    
    /**
     * 
     */
    private void computeJungIndicators() {
        
        // rank vertices but not edges -> true, false
        this.bc = new BetweennessCentrality<>(g, true, false);
        this.bc.setRemoveRankScoresOnFinalize(false);
        this.bc.evaluate();
        
        this.pr = new PageRank(g, 0.15);
        this.pr.evaluate();
    }
    
    /**
     * The topology creator must return a topology of linked cells from a whole cell list.
     * @return 
     */
    @Override
    public Collection<DiscoveryTopology> extractTopology(Collection<EAB_Cell> arg_cellList) {
        Collection<DiscoveryTopology> result    = new ArrayList<>();
        
        // build the graph first
        this.g  = this.graphBuilder.buildGraphFromCells(arg_cellList);
        this.computeJungIndicators();
        
        ArrayList<Pair<EAB_Vertex, Double>> myRanking    = new ArrayList();
        
        for(Ranking r : bc.getRankings()) {
            myRanking.add(new Pair<>((EAB_Vertex)r.getRanked(), r.rankScore));
        }
        
//        System.out.println(this.getClass().getCanonicalName() + ": Creating 5 discoveries with");
        
        for(int i = 0; i < 5; i++) {
            Collection<EAB_Cell> cellList_tmp   = new ArrayList<>();
            EAB_Vertex bestVertex                = myRanking.get(i).getKey();
            
            KNeighborhoodFilter knf         = new KNeighborhoodFilter(bestVertex, 1, KNeighborhoodFilter.EdgeType.IN_OUT);
            Graph<EAB_Vertex, EAB_Edge> g_knf = knf.transform(this.g);
            DiscoveryTopology d_tmp         = new DiscoveryTopology(g_knf);
            
            result.add(d_tmp);
        }
        
        return result;
    }
    
     /**
     * The topology creator must return a topology of linked cells from a whole cell list.
     * @return 
     */
    public Collection<Collection<EAB_Cell>> determineTopology(Collection<EAB_Cell> arg_cellList) {
        Collection<Collection<EAB_Cell>> result = new ArrayList<>();

        // build the graph first
        this.g  = this.graphBuilder.buildGraphFromCells(arg_cellList);
        this.computeJungIndicators();
        
        ArrayList<Pair<EAB_Vertex, Double>> myRanking    = new ArrayList();
        
        for(Ranking r_tmp : bc.getRankings()) {
            myRanking.add(new Pair<>((EAB_Vertex)r_tmp.getRanked(), r_tmp.rankScore));
        }
        
        // number of discoveries to extract
        for(int i = 0; i < 10; i++) {
            Collection<EAB_Cell> cellList_tmp   = new ArrayList<>();
            EAB_Vertex bestVertex            = myRanking.get(i).getKey();
            cellList_tmp.add(bestVertex.c);
        
            for(EAB_Vertex eav : g.getNeighbors(bestVertex)) {
                cellList_tmp.add(eav.c);
            }
            
            result.add(cellList_tmp);
        }
        
        return result;
    }
        
}
