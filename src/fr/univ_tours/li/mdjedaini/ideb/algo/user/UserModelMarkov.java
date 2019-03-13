/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.user;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.RandomRecommander;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.ext.jung.EAB_Edge_Weight;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Pair;

/**
 *
 * @author mahfoud
 */
public class UserModelMarkov extends UserModel {
    
    //
    private TaskBundle tb;    
    
    //
    private Map<Pair<Query, Query>, Integer> pairToCount;
    
    // number of "edges" starting by this query
    private Map<Query, Integer> seedToCount;
    private Map<Pair<Query, Query>, Double> pairToWeight;
    
    Double probabilityToPlay;   // en pourcentage
    
    //
    Graph<Query, EAB_Edge_Weight> g;
    PageRank pr;
    
    /**
     * 
     */
    public UserModelMarkov() {
        this.pairToCount    = new HashMap<>();
        this.seedToCount    = new HashMap<>();
        this.pairToWeight   = new HashMap<>();
        this.probabilityToPlay  = 100.;
    }

    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        this.tb         = arg_tb;
        this.user       = arg_tb.getCurrentUser();
        
        // update will create the model (the graph) from the user log
        if(this.g == null) {
            this.update();
        }
        
    }
    
    /**
     * Returns the graph representing the Markov chain.
     * @return 
     */
    public Graph getGraph() {
        return this.g;
    }
    
    /**
     * Returns a random query from the user log.
     * @return 
     */
    @Override
    public List<Query> playQueryList() {
        List<Query> result  = new ArrayList<>();
        
//        System.out.println("I am the Markov based on page rank, and I am playing a list of queries...");
        
        
        
        return result;
    }

    /**
     * 
     * @param arg_tb
     * @return 
     */
    @Override
    public List<Query> playQueryListForTask(TaskBundle arg_tb) {
        List<Query> result  = new ArrayList<>();
        
        // play nothing depending on probability
        Random r_tmp    = new Random();
        Integer proba   = r_tmp.nextInt(100);
        
        if(proba >= this.probabilityToPlay) {
            return result;
        }
        
        // here we play something...
        DirectedSparseGraph<Query, EAB_Edge_Weight> g_tmp    = (DirectedSparseGraph<Query, EAB_Edge_Weight>)this.g;
        
        boolean playedFromGraph = false;
        
        if(arg_tb.getCurrentSession().getNumberOfQueries() > 0) {
            
            Query currentQuery      = arg_tb.getCurrentSession().getLastQuery();
        
            // if the query is contained in the graph
            if(this.g.containsVertex(currentQuery)) {

                // if there are possibilites to play...
                if(this.g.getOutEdges(currentQuery).size() > 0) {
                    playedFromGraph   = true;
                    return this.playFromGraph(currentQuery);
                }
            }
            
        } 
        
        // Above, we played only if the current query has been found in the graph
        // In all the other cases, play randomly a query from the graph
        
        // If user model is not empty, play from it
        if(g_tmp.getVertexCount() > 0) {
            Random rg               = new Random();
            Integer index           = rg.nextInt(g_tmp.getVertexCount());
            ArrayList<Query> v_list = new ArrayList<>(g_tmp.getVertices());
            result.add(v_list.get(index));
        }
        
        // if user model is empty, play random query
        else {
            RandomRecommander rr    = new RandomRecommander();
            rr.readTaskBundle(this.tb);
            result.addAll(rr.recommand());
        }
        
        return result;
        
    }
    
    /**
     * PLays a query from the graph
     * @param arg_q
     * @return 
     */
    private List<Query> playFromGraph(Query arg_q) {
        List<Query> result  = new ArrayList<>();
        
        List<Query> listWithOccurences  = new ArrayList<>();
        
        Query seed  = this.lookForMatchingQuery(arg_q);
        
        for(Query q_tmp : this.g.getSuccessors(seed)) {

            EAB_Edge_Weight weight   = this.g.findEdge(seed, q_tmp);
            Double nbOccur          = weight.value * 100;
            
            Integer nbOccurInt  = nbOccur.intValue();
            
            for(int i = 0; i < nbOccurInt; i++) {
                listWithOccurences.add(q_tmp);
            }
            
        }
        
//        Random rg       = new Random();
//        Integer index   = rg.nextInt(listWithOccurences.size());
//
//        result.add(listWithOccurences.get(index));
        
        List<Query> successors  = new ArrayList<>(this.g.getSuccessors(seed));
        Collections.shuffle(successors);
        
        if(!successors.isEmpty()) {
            result.add(successors.get(0));
        }
        
        System.out.println("I played from graph!");
        
        return result;
        
    }
    
    /**
     * 
     * @param arg_q 
     */
    private Query lookForMatchingQuery(Query arg_q) {
        
        // we extract the exact query in the model that matches the current query
        for(Query q_tmp : this.g.getVertices()) {
            
            if(q_tmp.equals(arg_q)) {
                return q_tmp;
            }
            
        }
        
        // normalement, on ne devrait jamais atteindre ce point!
        return null;
    }
    
    /**
     * Creates the graphs with probabilities on edges...
     * 
     * @return 
     */
    private Graph createGraph() {
        this.g  = new DirectedSparseGraph<>();
        
        List<Session> us    = this.tb.getCurrentUserSessions();
        
        // add each session independtly to keep ordering coherence
        for(Session s_tmp : us) {
            
            Iterator<Query> it_q    = s_tmp.getQueryList().iterator();
            
            // @todo faire gaffe ici si l'iterateur a 0 queries...
            // We add the first query of the session
            Query previousQuery = it_q.next();
            
            // add the rest of the queries
            while(it_q.hasNext()) {
                // current query
                Query currentQuery              = it_q.next();
                
                // if the edge exists, we increment its value...
                // finds edge returns null if no edge
                if(this.g.findEdge(previousQuery, currentQuery) != null) {
                    EAB_Edge_Weight d_ref    = this.g.findEdge(previousQuery, currentQuery);
                    d_ref.value++;
                } else {
                    EAB_Edge_Weight ea_ew    = new EAB_Edge_Weight(previousQuery, currentQuery);
                    ea_ew.value = 1.0;
                    this.g.addEdge(ea_ew, previousQuery, currentQuery);
                }
                
                // current query becomes previous query...
                previousQuery   = currentQuery;
            }
        }
        
        return g;
    }
    
    /**
     * Normalize edge weights.
     * Edges have to be a probability...
     * 
     */
    private void normalizeWeights() {
        
        for(Query q_tmp : this.g.getVertices()) {
            
            // nb of out edges...
            Integer nbOut   = this.g.outDegree(q_tmp);
            
            // here no problem, why? nbOut will never be null below
            // If there is no out edges, we will not make the d_tmp / nbOut
            for(EAB_Edge_Weight d_tmp : this.g.getOutEdges(q_tmp)) {
                d_tmp.value = d_tmp.value / nbOut;
            }
            
        }
        
    }
    
    /**
     * Updates the user model.
     * The user model has to be implemented after too many queries have been played.
     * The updating frequency is a parameter for the benchmark...
     */
    public void update() {
        this.createGraph();
        this.normalizeWeights();
        //this.displayGraph();
    }
    
    /**
     * 
     * @param arg_queryList
     * @return 
     */
    @Override
    public boolean followRecommandation(List<Query> arg_queryList) {
        return true;
    }
    
    /**
     * 
     */
    public void displayGraph() {
        Dimension dim   = new Dimension(1200, 950);
        
        Layout<Query, Pair<Query, Query>> layout           = new ISOMLayout(g);
        VisualizationViewer<Query, Pair<Query, Query>> vv  = new VisualizationViewer<>(layout);
        
        // set the sizes
        layout.setSize(dim);
        vv.setPreferredSize(dim);

        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
 
        JFrame frame = new JFrame("User Model for user: " + this.user.getUid());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
    
}
