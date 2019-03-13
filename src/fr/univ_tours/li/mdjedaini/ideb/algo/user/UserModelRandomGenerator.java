/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.user;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.algo.suts.RandomRecommander;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class UserModelRandomGenerator extends UserModel {

    RandomRecommander rr;
    
    /**
     * 
     */
    public UserModelRandomGenerator() {
        this.rr = new RandomRecommander();
    }
    
    /**
     * 
     * @param arg_user 
     */
    public UserModelRandomGenerator(User arg_user) {
        super(arg_user);
        this.rr = new RandomRecommander();
    }

    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        this.rr.readTaskBundle(arg_tb);
//        System.out.println("I do not need to read the Task Bundle, because I am Random...");
        
    }
    
    /**
     * Returns a random query from the user log.
     * @return 
     */
    public List<Query> playQueryList() {
//        System.out.println("La version playquerylist sans taskbundle du usermodel random n'est pas encore implementee...");
        return null;
    }

    /**
     * 
     * @param arg_tb
     * @return 
     */
    @Override
    public List<Query> playQueryListForTask(TaskBundle arg_tb) {
        List<Query> result  = new ArrayList<>();
        
//        System.out.println("I am Random User Model, I am playing queries...");
        
        result.addAll(this.rr.recommand());
        
        return result;
    }
    
    /**
     * 
     * @param arg_queryList
     * @return 
     */
    @Override
    public boolean followRecommandation(List<Query> arg_queryList) {
        return true;
    }
    
}
