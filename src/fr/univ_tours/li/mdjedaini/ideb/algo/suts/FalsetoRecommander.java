/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fr.univ_tours.li.jaligon.falseto.QueryStructure.QuerySession;
import fr.univ_tours.li.jaligon.falseto.Recommendation.FalsetoRecommenderSys;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.ext.falseto.FalsetoSessionConverter;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 *
 * @author Salim Igue
 */
public class FalsetoRecommander implements I_SUT {
    Log log;
    TaskBundle tb;
    //pour convertir la session de  Falseto en session de EAB
    FalsetoSessionConverter fsc;
    
    //pour convertir une QuerySession en Session
//    QuerySessionToSessionConverter qf_qc;
    
    //utilisable pour le découpage ( nécessaire pour l'algo ASRA )
    double percentage;
    
    EAB_Cube cube;
    HashSet<QuerySession> set_querysession_log;
    HashSet<QuerySession> set_querysession_user;
    
    FalsetoRecommenderSys falsetosut;
    
    /**
     * A default percentage of 0.8 is given to Falseto
     */
    public FalsetoRecommander() {
        this.percentage         = 0.8;
        set_querysession_log    = new HashSet<>();
        set_querysession_user   = new HashSet<>();
        falsetosut              = new FalsetoRecommenderSys();
    }
    
    /**
     * 
     * @param percentage percentage required by Falseto
     */
    public FalsetoRecommander(double percentage) {
        this();
        this.percentage = percentage;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        
        List<Query> result = new ArrayList();
        //conversion de la liste des sessions en une liste de querysessions pour le log et pour les sessions de l'utilisateur
        
        System.out.println("Taille du log fourni à falseto: " + this.set_querysession_log.size() + " sessions...");
        
        //conversion de la liste des sessions en une liste de querysessions pour les sessions de l'utilisateur
        Iterator it2    = tb.getCurrentUserSessions().iterator();
        while(it2.hasNext()) set_querysession_user.add(fsc.convert((Session) it2.next()));
         
        //On crée un falsetosut pour pouvoir utiliser le système de recommandation de Falseto
        this.falsetosut = new FalsetoRecommenderSys();
        falsetosut.setLog(set_querysession_log);
        falsetosut.setQuestion(set_querysession_user);

        // On convertit la liste des Qfset en liste de Query
        // List<Qfset> qs =  falsetosut.sessionGenerationRecommendationProposal(this.percentage).getQueries();
        
        // qf_qc        = new QuerySessionToSessionConverter(cube);
        // on convertit la QuerySession reçu en une session
        QuerySession q = falsetosut.sessionGenerationRecommendationProposal();
        
        Session s = fsc.convert(q);
        result.addAll(s.getQueryList());
        
        return result;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "SUT-Falseto";
    }

    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        
        this.tb     = arg_tb;
        this.cube   = arg_tb.getCube();
        this.log   = arg_tb.getLog();
        
        //FalsetoQueryConverter fqc   = new FalsetoQueryConverter(this.cube);
        this.fsc        = new FalsetoSessionConverter(this.cube.getBencharkEngine());
        this.falsetosut = new FalsetoRecommenderSys();
        
        //conversion de la liste des sessions en une liste de querysessions pour le log
        this.set_querysession_log.clear();
        Iterator it     = this.tb.getLog().getSessionList().iterator();
        while(it.hasNext()) set_querysession_log.add(fsc.convert((Session) it.next()));
        
    }
    
}
