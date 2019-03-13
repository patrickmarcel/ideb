/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.query;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;

/**
 *
 * @author mahfoud
 */
public class QueryEditionDistance {

    /**
     * Computes the edition distance between two olap queries.
     * todo est-ce que les requetes doivent etre sur le meme cube? oui je pense...
     * @param arg_q1
     * @param arg_q2
     * @return 
     */
    public Integer distance(Query arg_q1, Query arg_q2) {
        
        EAB_Cube cube   = arg_q1.getCube();
        
        QueryConverter qc   = new QueryConverter(cube.getBencharkEngine());

        QueryTriplet qt1    = qc.toQueryTriplet(arg_q1);
        QueryTriplet qt2    = qc.toQueryTriplet(arg_q2);
                
        // first compute distance between projection
        Integer projectionDistance  = 0;
        for(EAB_Hierarchy h_tmp : cube.getHierarchyList()) {
            EAB_Level l1    = qt1.getProjectionFragmentByHierarchy(h_tmp).getLevel();
            EAB_Level l2    = qt2.getProjectionFragmentByHierarchy(h_tmp).getLevel();
            projectionDistance  += this.distanceBetweenLevels(l1, l2);
        }
        
        // compute distance between selections
        Integer selectionDistance   = 0;
        for(EAB_Hierarchy h_tmp : cube.getHierarchyList()) {
            List<EAB_Member> l1    = new ArrayList<>();
            List<EAB_Member> l2    = new ArrayList<>();
            
            for(SelectionFragment sf_tmp : qt1.getSelectionFragmentByHierarchy(h_tmp)) {
                l1.add(sf_tmp.getMemberValue());
            }
            
            for(SelectionFragment sf_tmp : qt2.getSelectionFragmentByHierarchy(h_tmp)) {
                l2.add(sf_tmp.getMemberValue());
            }
            
            selectionDistance   += this.distanceBetweenMemberList(l1, l2);
        }
        
        // compute distance between measures
        // default measures are already handled in .getMeasures method!
        Integer measureDistance = 0;
        List<MeasureFragment> l1    = new ArrayList<>(qt1.getMeasures());
        List<MeasureFragment> l2    = new ArrayList<>(qt2.getMeasures());

        // PLUS BESOIN DES LIGNES SUIVANTES
        // LES MESURES PAR DEFAUT SONT GEREES DANS LA FONCTION GETMEASURES
//        // if there are no measures, we add the default measure
//        if(l1.isEmpty()) {
//            l1.add(new MeasureFragment(qt1, qt1.getCube().getDefaultMeasure()));
//        }
//        if(l2.isEmpty()) {
//            l2.add(new MeasureFragment(qt2, qt2.getCube().getDefaultMeasure()));
//        }
        
        measureDistance += this.distanceBetweenMeasureList(l1, l2);
        
        Integer distance    = projectionDistance + selectionDistance + measureDistance;
        
        return distance;
        
    }
    
    /**
     * 
     * @param arg_1
     * @param arg_2
     * @return 
     */
    public Integer distanceBetweenLevels(EAB_Level arg_1, EAB_Level arg_2) {
        Integer distance    = 0;
    
        Integer ld1 = arg_1.getLevelDepth();
        Integer ld2 = arg_2.getLevelDepth();
        
        distance    = Math.abs(ld2 - ld1);
        
        // en dessous j'avais fais un truc complique!
        // bizarre...
//        if(!arg_1.equals(arg_2)) {
//            EAB_Level father, child;
//            
//            if(arg_1.isChildOf(arg_2)) {
//                father  = arg_2;
//                child   = arg_1;
//            } else {
//                father  = arg_1;
//                child   = arg_2;
//            }
//            
//            distance++;
//            
//            while(child.getParentLevel() != father) {
//                child   = child.getParentLevel();
//                distance++;
//            }
//        }
        
        return distance;
    }
    
    /**
     * 
     * @param arg_l1
     * @param arg_l2
     * @return 
     */
    public Integer distanceBetweenMemberList(List<EAB_Member> arg_l1, List<EAB_Member> arg_l2) {
        Integer distance    = 0;
        
//        System.out.println("List l1: " + arg_l1);
//        System.out.println("List l2: " + arg_l2);
        
        if(arg_l1.isEmpty() && arg_l2.isEmpty()) {
            return distance;
        }
        if(arg_l1.isEmpty() && !arg_l2.isEmpty()) {
            distance    += arg_l2.size();
            return distance;
        } else if(!arg_l1.isEmpty() && arg_l2.isEmpty()) {
            distance    += arg_l1.size();
            return distance;
        }
        
        // in other cases, the edition distance is equal to the sum of the size of the two lists        
        // indeed, we need to remove all filters for one list, and add filters of the other list
        // before doing that, we need to remove all the elements that are in the two lists
        List<EAB_Member> l1_copy    = new ArrayList<>(arg_l1);
        List<EAB_Member> l2_copy    = new ArrayList<>(arg_l2);
        
        l1_copy.removeAll(arg_l2); // added members
        l2_copy.removeAll(arg_l1); // removed members
        
//        System.out.println("l1 copy: " + l1_copy);
//        System.out.println("l2 copy: " + l2_copy);
        
        distance    += l1_copy.size() + l2_copy.size();
        
        return distance;
    }
    
    /**
     * 
     * @param arg_l1
     * @param arg_l2
     * @return 
     */
    public Integer distanceBetweenMeasureList(List<MeasureFragment> arg_l1, List<MeasureFragment> arg_l2) {
        Integer distance    = 0;
        
        if(arg_l1.isEmpty() && !arg_l2.isEmpty()) {
            distance += arg_l2.size();
        } else if(!arg_l1.isEmpty() && arg_l2.isEmpty()) {
            distance += arg_l1.size();
        }
            
        // in other cases, the edition distance is equal to the sum of the size of the two lists
        // indeed, we need to remove all filters for one list, and add filters of the other list
        // before doing that, we need to remove all the elements that are in the two lists
        List<MeasureFragment> l1_copy    = new ArrayList<>(arg_l1);
        List<MeasureFragment> l2_copy    = new ArrayList<>(arg_l2);
        
        l1_copy.removeAll(arg_l2);  // added measures
        l2_copy.removeAll(arg_l1);  // removed measures
            
//          System.out.println("l1 copy: " + l1_copy);
//          System.out.println("l2 copy: " + l2_copy);
        
        distance += l1_copy.size() + l2_copy.size();
        
        return distance;
    }
    
}
