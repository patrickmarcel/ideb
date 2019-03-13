/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.awt.Color;
import java.awt.Paint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLWriter;
import fr.univ_tours.li.mdjedaini.ideb.ext.jung.EAB_Edge;
import fr.univ_tours.li.mdjedaini.ideb.ext.jung.EAB_Vertex;

/**
 *
 * @author 21408782t
 */
public class GraphWriter {

    /**
     * 
     */
    public GraphWriter() {
    }
    
    /**
     * 
     * @param arg_g
     * @param arg_fileName 
     */
    public void writeGraph(Graph<EAB_Vertex, EAB_Edge> arg_g, String arg_fileName) {
        GraphMLWriter<EAB_Vertex, EAB_Edge> graphWriter = new GraphMLWriter<>();
 
        // set the transformers
        Transformer<EAB_Vertex, String> vertexID    = new Transformer<EAB_Vertex, String>() {
            public String transform(EAB_Vertex arg_c) {
                return arg_c.c.cellId.toString();
            }
        };
        
        Transformer<EAB_Vertex, String> vertexLabel = new Transformer<EAB_Vertex, String>() {
            public String transform(EAB_Vertex cell) {
                return cell.c.cellId.toString();
                //return cell.c.getMondrianCell().getCoordinateList();
            }
        };
        
        Transformer<EAB_Edge, Paint> edgePaint = new Transformer<EAB_Edge, Paint>() {
            public Paint transform(EAB_Edge edge) {
                return Color.BLACK;
            }
        };
        
        try {
            // this is for creating directory structure if it does not exist
            File file = new File(arg_fileName);
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);

            graphWriter.setVertexIDs(vertexID);
            graphWriter.setVertexDescriptions(vertexLabel);
            //graphWriter.setvsetVertexIDs(vertexID);
            
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(arg_fileName)));
            graphWriter.save(arg_g, out);
        } catch(Exception arg_e) {
            
        }
        
    }
    
}
