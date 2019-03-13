/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

/**
 *
 * @author mahfoud
 * @param <V>
 * @param <E>
 */
public class GraphViewer<V,E> {

    /**
     *
     */
    public GraphViewer() {
    }
    
    /**
     *
     * @param arg_graph
     */
    public void viewGraph(Graph<V,E> arg_graph) {
        Dimension dim   = new Dimension(1200, 950);
        
        Layout<V,E> layout          = new ISOMLayout(arg_graph);
        VisualizationViewer<V,E> vv = new VisualizationViewer<>(layout);
        
        // set the sizes
        layout.setSize(dim);
        vv.setPreferredSize(dim);
                
        // set the transformers
//        Transformer<EAB_Vertex, Paint> vertexPaint = new Transformer<EAB_Vertex, Paint>() {
//            public Paint transform(EAB_Vertex arg_c) {
//                return Color.RED;
//            }
//        };
//        
//        Transformer<EAB_Vertex, String> vertexLabel   = new Transformer<EAB_Vertex, String>() {
//            public String transform(EAB_Vertex cell) {
//                return cell.c.cellId.toString();
//            }
//        };
//        
//        Transformer<EAB_Edge, Paint> edgePaint = new Transformer<EAB_Edge, Paint>() {
//            public Paint transform(EAB_Edge edge) {
//                return Color.BLACK;
//            }
//        };
        
//        Transformer<EA_Edge, String> edgeLabel  = new Transformer<EA_Edge, String>() {
//            public String transform(EA_Edge edge) {
//            }
//        };
        
//        Transformer<EA_Edge, Stroke> edgeStroke = new Transformer<EA_Edge, Stroke>() {
//            float dash[] = { 10.0f };
//        
//            public Stroke transform(EA_Edge edge) {
//                
//            }
//        };
        
//        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
//        vv.getRenderContext().setVertexLabelTransformer(vertexLabel);
//
//        vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
//        vv.getRenderContext().setEdgeLabelTransformer(edgeLabel);
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStroke);
  
        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        vv.setGraphMouse(gm);
 
        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
    
}
