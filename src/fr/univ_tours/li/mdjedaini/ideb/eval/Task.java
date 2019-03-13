/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.Collection;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class Task {
    
    //
    private static Integer tid_count  = 0;
    Integer tid;
    
    User user;
    Session seedSession;
    Session targetSession;
    
    /**
     * important! this allows to use several models of target discoveries.
     * target could be discoveries: discoveries in end of session, complete discoveries in session,
     * discoveries in complete user log...
     */
    Collection<AbstractDiscovery> targetDiscoveryList;
    
    /**
     * 
     * @param arg_user
     * @param arg_seedSession
     * @param arg_targetSession
     * @param arg_targetDiscoveryList 
     */
    public Task(User arg_user, Session arg_seedSession, Session arg_targetSession, Collection<AbstractDiscovery> arg_targetDiscoveryList) {
        this.tid            = tid_count;
        Task.tid_count++;
        
        this.user           = arg_user;
        this.seedSession    = arg_seedSession;
        this.targetSession  = arg_targetSession;
        
        this.targetDiscoveryList    = arg_targetDiscoveryList;
        
//        for(AbstractDiscovery ad_tmp : this.targetDiscoveryList) {
//            System.out.println("Discovery:");
//            System.out.println(ad_tmp.getCellList().getCellCollection());
//        }
    }
    
    /**
     * 
     * @return 
     */
    public Integer getTid() {
        return this.tid;
    }

    /**
     * 
     * @return 
     */
    public User getUser() {
        return this.user;
    }

    /**
     * 
     * @return 
     */
    public Session getSeedSession() {
        return this.seedSession;
    }

    /**
     * 
     * @return 
     */
    public Collection<AbstractDiscovery> getTargetDiscoveryList() {
        return this.targetDiscoveryList;
    }

    /**
     * 
     * @param arg_ad 
     */
    public void addTargetDiscovery(AbstractDiscovery arg_ad) {
        this.targetDiscoveryList.add(arg_ad);
    }
    
    /**
     * 
     * @return 
     */
    public Session getTargetSession() {
        return this.targetSession;
    }
    
    /**
     * 
     * @return 
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.user.getBenchmarkEngine();
    }
    
}
