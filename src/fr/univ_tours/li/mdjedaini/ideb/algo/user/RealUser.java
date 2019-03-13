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
import fr.univ_tours.li.mdjedaini.ideb.struct.UserLog;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class RealUser extends UserModel {
    
    //
    private UserLog userLog;
    
    /**
     * 
     * @param arg_user
     */
    public RealUser(User arg_user) {
        super(arg_user);
    }

    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    /**
     * Returns a random query from the user log.
     * @return 
     */
    @Override
    public List<Query> playQueryList() {
        List<Query> result  = new ArrayList<>();
        
        // TODO chercher une methode pour ajouter ca directement dans le constructeur...
        // we do it here because in the constructor the logs are not attributed to users yet
        if(this.userLog == null) {
            this.userLog    = this.user.getBenchmarkEngine().getBenchmarkData().getUserLog(this.user);
        }

        RandomRecommander rr    = new RandomRecommander();
        //rr.readData(this.user.getBenchmarkEngine().getBundle());

        result.addAll(rr.recommand());
        
        return result;
    }

    /**
     * 
     * @param arg_tb
     * @return 
     */
    @Override
    public List<Query> playQueryListForTask(TaskBundle arg_tb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    @Override
    public boolean followRecommandation(List<Query> arg_queryList) {
        return true;
    }
    
    
    
}
