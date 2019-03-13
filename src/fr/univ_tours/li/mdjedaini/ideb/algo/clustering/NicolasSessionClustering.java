/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import clustering.FCMd;

import comparator.Comparator;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.I_SessionSimilarity;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 *
 * @author mahfoud
 */
public class NicolasSessionClustering extends GenericSessionClustering {

    BenchmarkEngine benchmarkEngine;
    
    //
    Integer nbClusters;     // number of clusters wanted
    Integer nbMedoids;      // must be < nb of sessions
    
    /**
     * 
     * @param arg_be 
     */
    public NicolasSessionClustering(BenchmarkEngine arg_be) {
        super();
        this.benchmarkEngine    = arg_be;
        
        this.iss        = arg_be.getSessionSimilarity();
        this.nbClusters = this.benchmarkEngine.getParameters().nbOfUsers;
    }
    
    /**
     * 
     * @param arg_be
     * @param arg_iss 
     */
    public NicolasSessionClustering(BenchmarkEngine arg_be, I_SessionSimilarity arg_iss) {
        this(arg_be);
        this.iss    = arg_iss;
    }
    
    /**
     * 
     * @param arg_sessionList
     * @return 
     */
    @Override
    public Collection<Collection<Session>> clusterizeSessions(Collection<Session> arg_sessionList) {
        
        // important! update parameters because parameters may have changed meantime
        this.iss        = this.benchmarkEngine.getSessionSimilarity();
        this.nbClusters = this.benchmarkEngine.getParameters().nbOfUsers;
        
        Collection<Collection<Session>> result  = new ArrayList<>();
        ArrayList<ArrayList<Session>> res       = (ArrayList)result;
        
        // number of medoids must be < number of sessions
        Random rg       = new Random();
        this.nbMedoids  = rg.nextInt(arg_sessionList.size() - 1) + 1;
        
        // we initialize the clusters containers
        for(int i = 0; i < this.nbClusters; i++) {
            res.add(new ArrayList<>());
        }
        
        // create list from collection
        List<Session> sessionList   = new ArrayList<>(arg_sessionList);
        SessionComparator sc        = new SessionComparator();
        // Comparator<Session> cp      = new SessionComparator();
            
        FCMd<Session> algo = new FCMd();
        
        // args: 3 -> nombre de clusters, 4 -> nombre de medoids
        algo.cluster(sessionList, sc, this.nbClusters, this.nbMedoids);
        int[] resultat = algo.createPartition();
            
        int i = 0;
//        System.out.println("Session : Cluster");
        
        for(int res_tmp : resultat) {
            // System.out.println(sessionList.get(i).getSid() + " : " + res_tmp);
            // ajoute au cluster number res_tmp la session courante
            res.get(res_tmp).add(sessionList.get(i));
            i++;
        }
        
        return result;
    }    
    
    /**
     * 
     * @param arg_nbClusters 
     */
    public void setNumberOfClusters(Integer arg_nbClusters) {
        this.nbClusters = arg_nbClusters;
    }
    
    /**
     * 
     */
    public class SessionComparator implements Comparator<Session> {
	
	/**
         * Compares two sessions and returns a dissimilarity as a double value.
         * It uses an interface that has to be implemented.
         * Be careful, the Nicolas clustering algorithm is based on dissimilarity,
         * and NOT on similarity.
         * @param qs1
         * @param qs2
         * @return 
         */
        @Override
	public double compare(Session qs1, Session qs2) {
            return 1 - NicolasSessionClustering.this.iss.similarity(qs1, qs2);
	}
    }
    
}
