/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryEditionDistance;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.MetricIterativeDistance;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.MetricScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;

/**
 *
 * @author mahfoud
 */
public class DistanceBasedFocusDetection implements I_FocusDetector {

    /**
     * 
     * @param arg_exp
     * @return 
     */
    @Override
    public List<FocusZone> detectFocusZones(Exploration arg_exp) {
        List<FocusZone> result  = new ArrayList<>();

        MetricIterativeDistance mid = new MetricIterativeDistance(arg_exp.getBenchmarkEngine());
        MetricScore ms              = mid.apply(arg_exp);
        List<Double> distanceList   = ms.queryScoreList;
        
        result  = this.extractFocusZoneList(arg_exp, distanceList);
//        result  = this.mergeFocusZoneList(result);
        
        return result;
    }
    
    /**
     * 
     * @param arg_exp
     * @param arg_scoreList
     * @return 
     */
    public List<FocusZone> extractFocusZoneList(Exploration arg_exp, List<Double> arg_scoreList) {
        List result = new ArrayList<FocusZone>();
        
        Double currentDistance  = arg_scoreList.get(0);
        
//        System.out.println("L'exploration a " + arg_scoreList.size() + " requetes.");
        System.out.println(arg_scoreList);
        
        // add the first query as a focus zone
        // this zone will be handled in the merge function
        FocusZone initialFocusZone  = new FocusZone(arg_exp, 0, 0);
        result.add(initialFocusZone);
        
        for(int i = 1; i < arg_scoreList.size(); i++) {
            
//            System.out.println("J'analyse la requete " + i + ":");
            
            if(arg_scoreList.get(i) <=1) {
                
//                System.out.println("Debut d'une nouvelle focus zone a la position " + (i -1) + ":");
                
                FocusZone fz_tmp        = new FocusZone(arg_exp, 0, 0);
                fz_tmp.startPosition    = (i-1);
                
                result.add(fz_tmp);
                
                for(int j = i; j < arg_scoreList.size(); j++) {
                    
                    if(arg_scoreList.get(j) > 1) {
//                        System.out.println("Fin de la focus zone a la position " + (j - 1) + ":");
                        fz_tmp.endPosition  = j-1;
                        
//                        System.out.println("On continue la recherche donc a partir de " + j + "...");
                        // FAIRE GAFFE, le i va etre incremente a nouveau au passage dans le for
                        // donc normalement on recommence la recherche a partir de j
                        // mais nous on met j-1 car la boucle for externe va lui ajouter 1 avant de rentrer dans la boucle!
                        i = j-1;
                        break;
                    } else {
                        fz_tmp.endPosition  = arg_scoreList.size()-1;
                        i   = arg_scoreList.size(); // pour ne plus entrer dans la boucle
                    }
                    
                }
                
//                // here compute focus zone cells!
//                for(int k = fz_tmp.startPosition; k <= fz_tmp.endPosition; k++) {
//                    fz_tmp.cellList.addCellCollection(arg_exp.getWorkSession().getQueryByPosition(k).execute(Boolean.TRUE).getCellList().getCellCollection());
//                }
                
            }
        }
        
        return result;
    }
    
    /**
     * Merges focus zones if there is only one query separating them.
     * 
     * @param arg_focusZoneList
     * @return 
     */
    public List<FocusZone> mergeFocusZoneList(List<FocusZone> arg_focusZoneList) {
        List<FocusZone> result  = new ArrayList<>();
        
        // first focus zone is at 0 distance by default
        List<Integer> distanceBetweenFocusZone  = new ArrayList<>();
        distanceBetweenFocusZone.add(0);
        
        for(int i = 1; i < arg_focusZoneList.size(); i++) {
            FocusZone currentFZ     = arg_focusZoneList.get(i);
            FocusZone previousFZ    = arg_focusZoneList.get(i-1);
            
            Integer distance    = currentFZ.startPosition - previousFZ.endPosition;
            distanceBetweenFocusZone.add(distance);
        }
        
        for(int i = 1; i < distanceBetweenFocusZone.size(); i++) {
            
//            System.out.println("J'analyse la focus zone " + i + ":");
            
            // be careful; the associated else is very important to keep non mergeable zones
            if(distanceBetweenFocusZone.get(i) <=1) {
                
                System.out.println("Debut d'une zone de fusion a la position " + (i -1) + ":");
                
                FocusZone fz_tmp        = new FocusZone(arg_focusZoneList.iterator().next().exp, 0, 0);
                fz_tmp.startPosition    = arg_focusZoneList.get(i-1).startPosition;
                
                result.add(fz_tmp);
                
                for(int j = i; j < distanceBetweenFocusZone.size(); j++) {
                    
                    if(distanceBetweenFocusZone.get(j) > 1) {
                        System.out.println("Fin de la zone de fusion a la position " + (j - 1) + ":");
                        fz_tmp.endPosition  = arg_focusZoneList.get(j-1).endPosition;
                        
                        System.out.println("On continue la recherche donc a partir de " + j + "...");
                        // FAIRE GAFFE, le i va etre incremente a nouveau au passage dans le for
                        // donc normalement on recommence la recherche a partir de j
                        // mais nous on met j-1 car la boucle for externe va lui ajouter 1 avant de rentrer dans la boucle!
                        i = j-1;
                        break;
                    } else {
                        fz_tmp.endPosition  = arg_focusZoneList.get(arg_focusZoneList.size()-1).endPosition;
                        i   = distanceBetweenFocusZone.size(); // pour ne plus entrer dans la boucle
                    }
                    
                }
            } else {
                // if there is no merge, we just add the zone as it is
                result.add(arg_focusZoneList.get(i));
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_fz1
     * @param arg_fz2
     * @return 
     */
    public Integer minimumEditionDistance(FocusZone arg_fz1, FocusZone arg_fz2) {
        QueryEditionDistance qed    = new QueryEditionDistance();
        
        // mettre un grand nombre qui sera ecrase par la suite
        Integer minED   = 100;
        
        for(int i = arg_fz1.startPosition; i < arg_fz1.endPosition; i++) {
            
            for(int j = arg_fz2.startPosition; j < arg_fz2.endPosition; j++) {
                
                Query q1    = arg_fz1.exp.getWorkSession().getQueryByPosition(i);
                Query q2    = arg_fz2.exp.getWorkSession().getQueryByPosition(j);
                
                if(qed.distance(q1, q2) < 100) {
                    minED   = qed.distance(q1, q2);
                }
                
            }
            
        }
        
        return minED;
    }
    
    /**
     * Merge based on query edition distance.
     * @param arg_focusZoneList
     * @return 
     */
    public List<FocusZone> mergeBasedOnEditionDistance(List<FocusZone> arg_focusZoneList) {
        List<FocusZone> result  = new ArrayList<>();

        if(arg_focusZoneList.size() == 0) {
            return new ArrayList<>();
        } else if(arg_focusZoneList.size() == 1) {
            return new ArrayList<>(arg_focusZoneList);
        } else {
            
            FocusZone fz1   = arg_focusZoneList.get(0);
            FocusZone fz2   = arg_focusZoneList.get(1);
            
            if(this.mergeOrNot(fz1, fz2)) {
                List<FocusZone> _result = this.mergeBasedOnEditionDistance(arg_focusZoneList);
                FocusZone mergedZone    =this.merge(arg_focusZoneList.get(0), arg_focusZoneList.get(1));
            } else {
                List<FocusZone> _result = this.mergeBasedOnEditionDistance(arg_focusZoneList);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_fz1
     * @param arg_fz2
     * @return 
     */
    public Boolean mergeOrNot(FocusZone arg_fz1, FocusZone arg_fz2) {

        QueryEditionDistance qed    = new QueryEditionDistance();
        
        Boolean merge   = false;
        
        for(int i = arg_fz1.startPosition; i < arg_fz1.endPosition; i++) {
            
            for(int j = arg_fz2.startPosition; j < arg_fz2.endPosition; j++) {
                
                Query q1    = arg_fz1.exp.getWorkSession().getQueryByPosition(i);
                Query q2    = arg_fz2.exp.getWorkSession().getQueryByPosition(j);
                
                if(qed.distance(q1, q2) <= 1) {
                    merge   = true;
                    break;
                }
                
            }
            
            if(merge = true) {
                break;
            }
            
        }
        
        return merge;
    }

    /**
     * Merges two focus zones.
     * Given two focus zones, this function returns a unique focus zone that is
     * the result of merging them.
     * @param arg_fz1
     * @param arg_fz2
     * @return 
     */
    public FocusZone merge(FocusZone arg_fz1, FocusZone arg_fz2) {
        FocusZone result    = new FocusZone();
        
        result.exp              = arg_fz1.exp;
        result.startPosition    = arg_fz1.startPosition;
        result.endPosition      = arg_fz2.endPosition;
        
        return result;
    }
    
}

