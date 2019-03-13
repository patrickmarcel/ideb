/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mondrian.olap.Schema;
import mondrian.olap.SchemaReader;
import fr.univ_tours.li.jaligon.falseto.Generics.Connection;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.struct.UserLog;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class OlapSystem {
    
    // engine these data are related to
    BenchmarkEngine benchmarkEngine;
    EAB_Connection c;                           // Connection that encloses mondrian connection
    Connection falsetoConnection;

    Log log;    // the initial complete log
    CellList cellList;
        
    Map<String, EAB_Cube> internalCubeList;     // my own representation of the cubes
    Map<Integer, User> userList;
    Map<User, UserLog> userLogList;
    Map<User, Collection<AbstractDiscovery>> discoveryList;
    
    /**
     * 
     */
    OlapSystem(BenchmarkEngine arg_be) {
        this.benchmarkEngine    = arg_be;
        this.internalCubeList   = new HashMap<>();
        this.userList           = new HashMap<>();
        this.userLogList        = new HashMap<>();
        this.discoveryList      = new HashMap<>();
    }
    
    /**
     * 
     */
    public void computeUserLogsListByUser() {
        
        for(User u_tmp : this.getUserList()) {
            UserLog ul_tmp  = new UserLog(this.getLog(), u_tmp.getUid());
            this.userLogList.put(u_tmp, ul_tmp);
        }
        
    }

    /**
     * 
     * @return 
     */
    public Connection getFalsetoConnection() {
        return falsetoConnection;
    }
    
    /**
     * 
     * @return 
     */
    public Map<User, Collection<AbstractDiscovery>> getDiscoveryList() {
        return discoveryList;
    }
    
    Parameters getParameters() {
        return this.benchmarkEngine.getParameters();
    }
    
    /**
     * 
     */
    public void createUsers() {
        for(Collection<Session> l_tmp : this.benchmarkEngine.i_sca.clusterizeSessions(this.log.getSessionList())) {
            User u_tmp  = new User(this.benchmarkEngine);
            
            // add the user through engine for ID coherence
            this.benchmarkEngine.addUser(u_tmp);
            
            for(Session s_tmp : l_tmp) {
                s_tmp.setUser(u_tmp);
            }
            
        }
    }

    /**
     * 
     */
    public void deleteUsers() {
        this.userList.clear();
        this.userLogList.clear();
    }
    
    /**
     * 
     * @param arg_uid 
     * @return  
     */
    public User getUserById(Integer arg_uid) {
        
        for(User u_tmp : this.getUserList()) {
            if(u_tmp.getUid().equals(arg_uid)) {
                return u_tmp;
            }
        }
        
        return null;
    }
    
    /**
     * 
     * @return 
     */
    public Collection<User> getUserList() {
        return this.userList.values();
    }
    
    /**
     * 
     * @return 
     */
    public Collection<User> getUsersWithNonEmptyLog() {
        Set<User> result    = new HashSet<>();
        
        for(User u_tmp : this.getUserList()) {
            if(this.getUserLog(u_tmp).getSessionList().size() > 0) {
                result.add(u_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_user
     * @return 
     */
    public UserLog getUserLog(User arg_user) {
        return this.userLogList.get(arg_user);
    }
    
    /**
     * 
     * @return 
     */
    public EAB_Connection getConnection() {
        //return this.c;
        EAB_Connection c_tmp    = new EAB_Connection(this.getBenchmarkEngine());
        return c_tmp;
    }
    
    /**
     * 
     */
    void setConnection(EAB_Connection arg_c) {
        this.c  = arg_c;
    }
    
    // @todo
    /**
     * 
     * @return cube 0 so far
     */
    public Map<String, EAB_Cube> getInternalCubeList() {
        return this.internalCubeList;
    }
    
    /**
     * 
     * @param arg_measureName
     * @return 
     */
//    public Member getMeasureMemberByName(String arg_measureName) {
//        return this.internalCube.getMeasureMemberByName(arg_measureName);
//    }
    
    /**
     * 
     * @return 
     */
    public Schema getSchema() {
        return this.getConnection().getMondrianConnection().getSchema();
    }
    
    /**
     * 
     * @return 
     */
    public SchemaReader getSchemaReader() {
        // withLocus is important, to avoid emptyStack exceptions...
        return this.getConnection().getMondrianConnection().getSchemaReader().withLocus();
    }
    
    /**
     * 
     * @return 
     */
    public Log getLog() {
        return log;
    }

    /**
     * 
     * @param log 
     */
    public void setLog(Log log) {
        this.log        = log;
        //this.log.computeCellList();
        //this.cellList   = log.getCellList();
    }
    
    /**
     * 
     * @param c 
     */
    public void setC(EAB_Connection c) {
        this.c = c;
    }

    /**
     * 
     * @param arg_ic 
     */
    public void setInternalCube(EAB_Cube arg_ic) {
        this.internalCubeList.put(arg_ic.getName(), arg_ic);
    }

    /**
     *
     * @return
     */
    public CellList getCellList() {
        return cellList;
    }

    /**
     *
     * @param cellList
     */
    public void setCellList(CellList cellList) {
        this.cellList = cellList;
    }

    /**
     *
     * @return
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return benchmarkEngine;
    }
    
    /**
     * 
     */
    public void printSummary() {
        System.out.println("User summary:");
        System.out.println("-------------");
        System.out.println("There are " + this.getUserList().size() + " users.");
        
        for(User u_tmp : this.getUserList()) {
            System.out.println("User " + u_tmp.getUid() + " has " + this.getUserLog(u_tmp).getSessionList().size() + " sessions...");
        }
    }
    
}
