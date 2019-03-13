/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.stats;

import java.util.HashMap;
import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 *
 * @author mahfoud
 */
public class LogStatistics {

    //
    Log log;
    
    /**
     * 
     * @param arg_log 
     */
    public LogStatistics(Log arg_log) {
        this.log    = arg_log;
    }
    
    /**
     * 
     * @return 
     */
    public Map<EAB_Hierarchy, Map<EAB_Member, Integer>> selectionFrequency() {
        Map<EAB_Hierarchy, Map<EAB_Member, Integer>> result = new HashMap<>();
        
        // for all the hierarchies of the cube
        for(EAB_Hierarchy h_tmp : this.log.pickRandomQueryList(1).get(0).getCube().getHierarchyList()) {
        
            Map<EAB_Member, Integer> memberToFrequency    = new HashMap<>();
        
            // for all queries in the log
            for(Query q_tmp : this.log.getQueryList()) {
                QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            
                // for each selection fragment
                for(SelectionFragment sf_tmp : qt_tmp.getSelectionFragmentByHierarchy(h_tmp)) {
                    EAB_Level l_tmp     = sf_tmp.getLevel();
                    EAB_Member m_tmp    = sf_tmp.getMemberValue();
                    
                    // only if the level is child of level All
                    if(l_tmp.isDirectChildOf(h_tmp.getAllLevel())) {
                    
                        // increment frequency if present
                        if(memberToFrequency.containsKey(m_tmp)) {
                            Integer int_ref = memberToFrequency.get(m_tmp) + 1;
                            memberToFrequency.replace(m_tmp, int_ref);
                        } else {
                            memberToFrequency.put(m_tmp, 1);
                        }
                    
                    }
                }
            }
        
            // we add the frequency for the current hierarchy
            result.put(h_tmp, memberToFrequency);
        }
        
        return result;
    }
    
}
