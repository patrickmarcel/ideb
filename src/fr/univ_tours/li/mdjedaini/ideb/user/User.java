package fr.univ_tours.li.mdjedaini.ideb.user;

import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.user.I_UserSimulator;
import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mahfoud
 */
public class User {
    
    // 
    Integer uid;
    I_UserSimulator userModel;
    
    BenchmarkEngine be;
    
    /**
     * Default model picks random query from log.
     * We do not assign the user model in the constructor.
     * The user model will be assigned by the benchmark engine, when creating users...
     * @param arg_be 
     */
    public User(BenchmarkEngine arg_be) {
        this.be         = arg_be;
    }
    
    /**
     * 
     * @param arg_be
     * @param arg_userModel 
     */
    public User(BenchmarkEngine arg_be, I_UserSimulator arg_userModel) {
        this.be         = arg_be;
        this.userModel  = arg_userModel;
    }
    
    /**
     * Plays a list of queries according to the model of the user.
     * @return 
     */
    public List<Query> playQueryList() {
        return this.userModel.playQueryList();
    }

    /**
     * Plays a list of queries for a given task.
     * @param arg_tb
     * @return 
     */
    public List<Query> playQueryListForTask(TaskBundle arg_tb) {
        return this.userModel.playQueryListForTask(arg_tb);
    }
    
    /**
     * 
     * @return 
     */
    public Integer getUid() {
        return this.uid;
    }
    
    /**
     * 
     * @param arg_uid 
     */
    public void setUid(Integer arg_uid) {
        this.uid    = arg_uid;
    }

    /**
     *
     * @return
     */
    public I_UserSimulator getUserModel() {
        return this.userModel;
    }

    /**
     * 
     * @param arg_userModel
     */
    public void setUserModel(I_UserSimulator arg_userModel) {
        this.userModel  = arg_userModel;
    }
    
    /**
     *
     * @return
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.be;
    }
    
}
