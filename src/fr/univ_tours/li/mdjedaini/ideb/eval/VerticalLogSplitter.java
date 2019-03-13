/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 *
 * @author mahfoud
 */
public class VerticalLogSplitter implements I_LogSplitter {

    BenchmarkEngine be;
    
    /**
     * 
     * @param arg_be 
     */
    public VerticalLogSplitter(BenchmarkEngine arg_be) {
        this.be = arg_be;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Log> splitLog(Log arg_log) {
        List<Log> result    = new ArrayList<>();
        
        Log v_log   = new Log();
        Log h_log   = new Log();
        
        // pb: comment gerer si l'utilisateur n'a que une session?
        // et si il a 0 sessions?
        
        // voir avec l'algo de clustering, si le cluster donne 0 sessions on le
        // considere ou pas???
        
        for(Session s_tmp : arg_log.getSessionList()) {
            Random rg                   = new Random();
            Double hiddenProbability    = 0.5;

            // we hide a session based on a probability
            if(rg.nextDouble() < hiddenProbability) {
                h_log.addSession(s_tmp);
            } else {
                v_log.addSession(s_tmp);
            }
        }
        
        result.add(v_log);
        result.add(h_log);
        
        System.out.println("Complete log:");
        System.out.println(arg_log.logSummary());
        
        System.out.println("Visible log:");
        System.out.println(v_log.logSummary());
        
        System.out.println("Hidden log:");
        System.out.println(h_log.logSummary());
        
        return result;
    }
    
}
