/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 * 
 * @author mahfoud
 */
public class CineCube implements I_SUT {

    // task bundle
    TaskBundle tb;
    
    //
    EAB_Cube cube;
    Log log;

    /**
     * 
     */
    public CineCube() {
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        List<Query> result  = new ArrayList<>();
        
        try {
            result  = this.internalRecommand();
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
        
        return result;
    }

    /**
     * 
     * @return
     * @throws Exception 
     */
    public List<Query> internalRecommand() throws Exception {
        List<Query> result  = new ArrayList<>();
        
        // we recommand next query if last query exists...
        if(this.tb.getCurrentSession().getNumberOfQueries() > 0) {
            result.addAll(this.generateNextQueryList(this.tb.getCurrentSession().getLastQuery()));
        } else {
            throw new Exception("La session actuelle n'a pas de requetes, donc je ne peux rien generer sorry...");
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "SUT-CineCube";
    }
    
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        this.tb     = arg_tb;
        this.cube   = arg_tb.getCube();
        this.log    = arg_tb.getLog();
    }
    
    /**
     * 
     * @param arg_q
     * @return 
     */
    public List<Query> generateNextQueryList(Query arg_q) {
        List<Query> result  = new ArrayList<>();
        
        // add context queries
        result.addAll(this.generateContextQueries(arg_q));
        
        // add explain queries
        result.addAll(this.generateExplainQueries(arg_q));
        
        // 
        Collections.shuffle(result);
        
        return result;
    }
    
    /**
     * 
     * @param arg_q
     * @return 
     */
    public List<Query> generateContextQueries(Query arg_q) {
        List<Query> result  = new ArrayList<>();
        QueryTriplet qt_tmp = (QueryTriplet)arg_q;

        // for each hierarchy other than all
        for(EAB_Hierarchy h_tmp : qt_tmp.getHierarchyWithProjectionOtherThanAll()) {
            
            QueryTriplet qt = new QueryTriplet(qt_tmp);
            
            EAB_Level l_tmp = qt.getProjectionFragmentByHierarchy(h_tmp).getLevel();
            
            if(l_tmp.getParentLevel() != null) {
                ProjectionFragment pf_tmp   = new ProjectionFragment(qt, l_tmp.getParentLevel());
                qt.addProjection(pf_tmp);
            
                result.add(qt);
            }
            
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_q
     * @return 
     */
    public List<Query> generateExplainQueries(Query arg_q) {
        List<Query> result  = new ArrayList<>();
        QueryTriplet qt_tmp = (QueryTriplet)arg_q;
        
        // for each hierarchy other than All, we drill...
        for(EAB_Hierarchy h_tmp : qt_tmp.getHierarchyWithProjectionOtherThanAll()) {
            
            QueryTriplet qt = new QueryTriplet(qt_tmp);
                                    
            EAB_Level l_tmp = qt_tmp.getProjectionFragmentByHierarchy(h_tmp).getLevel();
            EAB_Level child = l_tmp.getChildLevel();
            
            if(null != child) {
//                System.out.println("Child level: " + child);
                ProjectionFragment pf_tmp   = new ProjectionFragment(qt, child);
            
//                System.out.println("Projection: " + pf_tmp);
                qt.addProjection(pf_tmp);
            
                result.add(qt);
            }
            
        }
        
        return result;
    }
    
}
