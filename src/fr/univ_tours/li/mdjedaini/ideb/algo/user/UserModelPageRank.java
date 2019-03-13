/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.user;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.struct.UserLog;
import fr.univ_tours.li.mdjedaini.ideb.tools.Pair;

/**
 *
 * @author mahfoud
 */
public class UserModelPageRank extends UserModel {
    
    //
    private TaskBundle tb;
    private UserLog userLog;
    
    //
    private Map<Pair<Query, Query>, Integer> pairToCount;
    
    // number of "edges" starting by this query
    private Map<Query, Integer> seedToCount;
    private Map<Pair<Query, Query>, Double> pairToWeight;
    
    //
    Graph<Query, Pair<Query, Query>> g;
    PageRank pr;
    
    /**
     * 
     */
    public UserModelPageRank() {
        this.pairToCount    = new HashMap<>();
        this.seedToCount    = new HashMap<>();
        this.pairToWeight   = new HashMap<>();
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
        
        DirectedSparseGraph<Query, Pair<Query, Query>> g_tmp    = (DirectedSparseGraph<Query, Pair<Query, Query>>)this.g;

        Query currentQuery  = arg_tb.getCurrentSession().getLastQuery();
        
        boolean playedFromGraph = false;
        
        // we base the user on the last query of the current session
        for(Query q_tmp : g_tmp.getVertices()) {
            if(q_tmp.equals(currentQuery)) {
                // we played from the graph
                playedFromGraph = true;
                
                // we select the most probable query from the neighborhood
                Double i = -1.;
                Query q_toAdd;
                
                for(Query q_nb : g_tmp.getNeighbors(q_tmp)) {
                    Double v_score  = (Double)this.pr.getVertexScore(q_nb);
                    
                    if(v_score >= i) {
                        i       = v_score;
                        q_toAdd = q_nb;
                    }   
                }
            }
        }
        
        // @todo
        // here, if I cannot find the previous query in the model, I play a random query
        // one good idea would be to find the query in the model that is the closer to the last query
        // to investigate...
        
        // if we have not played from the graph
        // we check that there are queries in the user model... (vertx count > 0)
        if(!playedFromGraph) {
            if(g_tmp.getVertexCount() > 0) {
                Random rg   = new Random();
                Integer index   = rg.nextInt(g_tmp.getVertexCount());
                ArrayList<Query> v_list = new ArrayList<>(g_tmp.getVertices());
                result.add(v_list.get(index));
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    private Graph createGraph() {
        this.g  = new DirectedSparseGraph<>();
        
        for(Pair<Query, Query> p_tmp : this.pairToCount.keySet()) {
            
            if(!g.containsVertex(p_tmp.getKey())) {
                g.addVertex(p_tmp.getKey());
            }
            
            if(!g.containsVertex(p_tmp.getValue())) {
                g.addVertex(p_tmp.getValue());
            }
            
            g.addEdge(p_tmp, p_tmp.getKey(), p_tmp.getValue());
            
        }
        
        // transforme un edge en poids
        Transformer<Pair<Query, Query>, Double> edgeWeight = new Transformer<Pair<Query, Query>, Double>() {
            public Double transform(Pair<Query, Query> arg_edge) {
                return pairToWeight.get(arg_edge);
            }
        };
        
        Double alpha    = 0.15;
        this.pr         = new PageRank(g, edgeWeight, alpha);        
        this.pr.initialize();
        this.pr.evaluate();
        
        // faire 10 iterations
        for(int i = 0; i < 10; i++) {
            this.pr.step();
        }
        
        return g;
    }
    
    /**
     * Creates a temporary structure for building the graph.
     * 
     */
    private void gatherStatistics() {
        
        // add each session independtly to keep ordering coherence
        for(Session s_tmp : this.userLog.getSessionList()) {
            
            Iterator<Query> it_q    = s_tmp.getQueryList().iterator();
            
            // @todo faire gaffe ici si l'iterateur a 0 queries...
            // We add the first query of the session
            Query previousQuery = it_q.next();
            
            // add the rest of the queries
            while(it_q.hasNext()) {
                Query currentQuery              = it_q.next();
                Pair<Query, Query> currentEdge  = new Pair<>(previousQuery, currentQuery);
                
                // seeds queries count
                if(this.seedToCount.containsKey(previousQuery)) {
                    Integer i_tmp   = this.seedToCount.get(previousQuery);
                    i_tmp++;
                } else {
                    this.seedToCount.put(previousQuery, 1);
                }
                
                // edges count
                if(this.pairToCount.containsKey(currentEdge)) {
                    Integer i_tmp   = this.pairToCount.get(currentEdge);
                    i_tmp++;
                } else {
                    this.pairToCount.put(currentEdge, 1);
                }
                
                // update the previous query
                previousQuery   = currentQuery;
            }
            
        }
    }
    
    /**
     * 
     */
    public void computeWeights() {
        
        for(Pair<Query, Query> p_tmp : this.pairToCount.keySet()) {
            
            Integer seedCount   = this.seedToCount.get(p_tmp.getKey());
            Double weight       = this.pairToCount.get(p_tmp).doubleValue() / (Double)seedCount.doubleValue();
            this.pairToWeight.put(p_tmp, weight);
            
        }
        
    }
    
    /**
     * Updates the user model.
     * The user model has to be implemented after too many queries have been played.
     * The updating frequency is a parameter for the benchmark...
     */
    public void update() {
        this.gatherStatistics();
        this.computeWeights();
        this.createGraph();
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
