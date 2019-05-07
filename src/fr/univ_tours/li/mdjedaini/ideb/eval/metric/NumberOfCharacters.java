/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Stats;

/**
 * This metric returns the number of characters of the text of the MDX queries in the exploration.
 * Respects Mahfoud's logic of processing only explorations.
 * @author patrick
 */
public class NumberOfCharacters extends Metric {
    
   
    /**
     * 
     * @param arg_be 
     */
    public NumberOfCharacters(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric - Number of characters";
        this.description    = "Number of characters in the current query text.";
    }
    
    /**
     * Computes the average recall for all the target discoveries for this task.
     * 
     * @param arg_tr
     * @return 
     */
    public MetricScore apply(Exploration arg_tr) {
        MetricScore result  = new MetricScore(this, arg_tr);
        
        List<Double> queryScoreList = new ArrayList<>();
        Session workSession         = arg_tr.getWorkSession();

        for(Query q_tmp : workSession.getQueryList()) {
            //QueryTriplet qt = (QueryTriplet)q_tmp;
        	String text=((QueryMdx) q_tmp).toString();
        	
        	System.out.println(text);
        	
            queryScoreList.add(new Double(text.length()));
        }
        
        result.score            = Stats.average(queryScoreList);
        result.addScoreList(queryScoreList);
        
        return result;
    }

}
