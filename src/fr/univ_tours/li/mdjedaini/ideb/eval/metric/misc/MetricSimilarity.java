/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.misc;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.AligonSessionSimilarity;
import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.I_SessionSimilarity;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class MetricSimilarity extends Metric {
    
    // session similarity
    I_SessionSimilarity i_ss;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricSimilarity(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric Similarity";
        this.description    = "Evaluates the similarity of a generated session to previous user sessions...";
        this.i_ss           = new AligonSessionSimilarity(this.getBenchmarkEngine());
    }
    
    /**
     * 
     * @param arg_be
     * @param arg_ss 
     */
    public MetricSimilarity(BenchmarkEngine arg_be, I_SessionSimilarity arg_ss) {
        this(arg_be);
        this.i_ss   = arg_ss;
    }
    
    /**
     * Generates a random double, between 0.0 and 1.0.
     * 
     * @param arg_tr
     * @return 
     */
    @Override
    public MetricScore apply(Exploration arg_tr) {
        MetricScore result  = new MetricScore(this, arg_tr);
        
        User u_tmp      = arg_tr.getCurrentUser();

        List<Double> similarityArray    = new ArrayList<>();

        // @todo there is a problem if the user has no previous sessions...
        for(Session s_tmp : arg_tr.getCurrentUserSessions()) {
            similarityArray.add(this.i_ss.similarity(arg_tr.getWorkSession(), s_tmp));
        }
        
        Double sum  = 0.;
        for(Double d_tmp : similarityArray) {
            sum += d_tmp;
        }
        
        // average similarity between arg_tr and previous user sessions
        result.score  = sum / similarityArray.size();
        
        return result;
    }
    
}
