/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.I_SessionSimilarity;
import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.RandomSessionSimilarity;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 *
 * @author mahfoud
 */
public class GenericSessionClustering implements I_SessionClusteringAlgorithm {
    
    //
    I_SessionSimilarity iss;
    
    /**
     * 
     */
    public GenericSessionClustering() {
        this.iss    = new RandomSessionSimilarity();
    }
    
    /**
     * 
     * @param arg_iss 
     */
    public GenericSessionClustering(I_SessionSimilarity arg_iss) {
        this.iss    = arg_iss;
    }
    
    /**
     * 
     * @param arg_sessionList
     * @return 
     */
    @Override
    public Collection<Collection<Session>> clusterizeSessions(Collection<Session> arg_sessionList) {
        Collection<Collection<Session>> result  = new ArrayList<>();
        ArrayList<ArrayList<Session>> res       = (ArrayList)result;
        
        for(int i = 0; i < 5; i++) {
            res.add(i, new ArrayList<>());
        }
        
        Random rg   = new Random();
        
        for(Session s_tmp : arg_sessionList) {
            Integer i_tmp   = rg.nextInt(5);
            res.get(i_tmp).add(s_tmp);
        }
        
        return result;
    }    
    
}
