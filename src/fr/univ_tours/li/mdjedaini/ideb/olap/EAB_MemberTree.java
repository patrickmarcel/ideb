/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap;

import java.util.Map;
import java.util.Set;

import mondrian.olap.Member;
import mondrian.olap.SchemaReader;
import edu.uci.ics.jung.graph.DelegateTree;

/**
 *
 * @author mahfoud
 */
public class EAB_MemberTree {
    
    // 
    Map<EAB_Member, Set<EAB_Member>> memberToChildren;
    
    DelegateTree<EAB_Member, String> jungTree;
    
    /**
     * 
     * @param arg_hierarchy 
     */
    EAB_MemberTree(EAB_Hierarchy arg_hierarchy) {
        this.jungTree   = new DelegateTree<>();
        this.loadHierarchyMembers(arg_hierarchy);
        //jungTree.
    }
        
    /**
     * 
     * @param arg_hierarchy 
     */
    private void loadHierarchyMembers(EAB_Hierarchy arg_hierarchy) {

        EAB_Cube cube   = arg_hierarchy.getCube();
        SchemaReader sr = cube.getMondrianCube().getSchemaReader(null).withLocus();
        
        for(Member m_tmp : arg_hierarchy.getCube().memberMapping.keySet()) {
            
            if(cube.getHierarchyByMondrianHierarchy(m_tmp.getHierarchy()).equals(arg_hierarchy)) {
                EAB_Member currentMember    = cube.getMemberByMondrianMember(m_tmp);
                EAB_Member parentMember     = cube.getMemberByMondrianMember(m_tmp.getParentMember());
                
                this.jungTree.addChild("chaine", parentMember, currentMember);
            }
            
        }
            
    }
    
}
