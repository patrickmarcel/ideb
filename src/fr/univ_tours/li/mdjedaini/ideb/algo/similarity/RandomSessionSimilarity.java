/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.similarity;

import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 *
 * @author mahfoud
 */
public class RandomSessionSimilarity implements I_SessionSimilarity {

    /**
     * 
     */
    public RandomSessionSimilarity() {
    }
    
    /**
     * 
     * @param arg_s1
     * @param arg_s2
     * @return 
     */
    @Override
    public Double similarity(Session arg_s1, Session arg_s2) {
        Double result   = 0.5;
        
        Random rg   = new Random();
        result      = (rg.nextDouble() + result) / 2.0;
        
        return result;
    }
    
    
    
}
