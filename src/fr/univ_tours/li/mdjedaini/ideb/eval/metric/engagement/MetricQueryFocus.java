/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.engagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.neighborhood.I_QueryNeighborhood;
import fr.univ_tours.li.mdjedaini.ideb.neighborhood.QueryNeighborhood;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricQueryFocus extends Metric {
    
    I_QueryNeighborhood qn;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricQueryFocus(BenchmarkEngine arg_be) {
        super(arg_be);
        this.qn             = new QueryNeighborhood();
        this.name           = "Metric - Query Focus...";
        this.description    = "Evaluates how focused is a session...";
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

        // get the work session
        Session workSession = arg_tr.getWorkSession();
        
        List<Integer> focusSizeList = new ArrayList<>();
        Query currentQuery          = null;
        
        Iterator<Query> it_q    = workSession.getQueryList().iterator();
        if(it_q.hasNext()) {
            currentQuery    = it_q.next();
        }
        
        Integer focusSize_tmp   = 0;
        
        while(it_q.hasNext()) {
            QueryTriplet qt_tmp = (QueryTriplet)it_q.next();
            
            // /!\ gerer le cas ou on a que des voisins!
            if(qt_tmp.isNeighborOf((QueryTriplet)currentQuery)) {
                focusSize_tmp++;
            } else {
                focusSizeList.add(focusSize_tmp);
                focusSize_tmp   = 0;
            }
            
            currentQuery    = qt_tmp;
            
        }
        
        // ici on gere le cas ou toute la session est voisine
        focusSizeList.add(focusSize_tmp);
        
        result.score    = this.extractFromFocusSizeList(focusSizeList) / arg_tr.getWorkSession().getNumberOfQueries();
        
        return result;
    }
    
    /**
     * 
     * @param arg_focusSizeList
     * @return 
     */
    Double extractFromFocusSizeList(List<Integer> arg_focusSizeList) {
       return Collections.max(arg_focusSizeList).doubleValue();
    }
    
}
