/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;

/**
 *
 * @author mahfoud
 */
public class Utils {
 
    /**
     * 
     * @param arg_r
     * @return 
     */
    public static CellList extractCellListFromResultList(List<fr.univ_tours.li.mdjedaini.ideb.olap.result.Result> arg_r) {
        CellList result   = new CellList();
        
        for(fr.univ_tours.li.mdjedaini.ideb.olap.result.Result r_tmp : arg_r) {
            result.addCellCollection(r_tmp.getCellList().getCellCollection());
        }
      
        return result;
    }
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    public static Double computeInterest(Collection<EAB_Cell> arg_cellList) {
        return 1. - Utils.computeEntropy(arg_cellList);
    }
    
    /**
     * Computes the entropty of a list of cells.
     * Cells in the set must not be null, neither error. They are only "good"
     * existing cells, with a value.
     * @param arg_cellList
     * @return 
     */
    public static Double computeEntropy(Collection<EAB_Cell> arg_cellList) {
        Double result = 0.;
        
        List<EAB_Cell> list = new ArrayList<>(arg_cellList);

        // IF ONLY ON CELL, RETURN 1.0
        if(arg_cellList.size() <= 1) {
//            System.out.println("Moins d'1 cellule: entropy = 1.");
            result  = 1.;
            return result;
        }
        
        // IF THE SUM OF THE CELLS IS EQUAL TO 0, RETURN 1.0
        Double sum  = 0.;
        for(EAB_Cell c_tmp : arg_cellList) {
            sum += c_tmp.getValueAsDouble();
        }

        // if sum equals 0, we return 1 (max entropy)
        // it means that all the cells have a value of 0.
        if(sum == 0) {
//            System.out.println("Somme nulle: entropy = 1.");
            return 1.;
        }
        
        // REMOVE CELLS EQUAL TO 0
        List<EAB_Cell> listWithoutNulls = new ArrayList<>(Utils.removeNulls(arg_cellList));

        for(EAB_Cell c_tmp : listWithoutNulls) {
            
            // here, value of c_tmp != 0 and sum != 0
            Double proba    = c_tmp.getValueAsDouble() / sum;

            // purée faire gaffe ici à pas donnr 0 à la fonction log!
            // System.out.println("Proba de: " + proba);
            result          = result + proba * Math.log10(proba);
                
        }
        
        // /!\ nbcells must not be 1
        // BE CAREFUL following line is an error! because it can happen that listWithoutNulls contain 1 element, and then log(1) = 0!!!
        //result  = - result / Math.log10(listWithoutNulls.size());
        result  = - result / Math.log10(arg_cellList.size());
        
        if(result.isNaN()) {
            System.out.println("Je suis NaN...");
        }
        
//        System.out.println("Entropy = " + result);
        return result;
    }
  
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    private static Collection<EAB_Cell> removeNulls(Collection<EAB_Cell> arg_cellList) {
        List<EAB_Cell> result   = new ArrayList<>();
        
        // remove null cells...
        for(EAB_Cell c_tmp : arg_cellList) {
            if(c_tmp.getValueAsDouble() != 0.) {
                result.add(c_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    public static double computeKL(Collection<EAB_Cell> arg_cellList) {
        double result   = 0.;
        
        List<EAB_Cell> list = new ArrayList<>();
        
        // remove null cells...
        for(EAB_Cell c_tmp : arg_cellList) {
            if(c_tmp.isOK()) {
                list.add(c_tmp);
            }
        }
        
        Double sum  = 0.;
        for(EAB_Cell c_tmp : list) {
            sum += c_tmp.getValueAsDouble();
        }
        
        Double avg  = sum / list.size();
        
        double[] p1 = new double[arg_cellList.size()];
        double[] p2 = new double[arg_cellList.size()];
        
        for(int i = 0; i < list.size(); i++) {
            p1[i]   = list.get(i).getValueAsDouble();
            p2[i]   = avg;
        }
        
        double kl1  = Utils.KLDivergence(p1, p2);
        double kl2  = Utils.KLDivergence(p1, p2);
        
        result  = (kl1 + kl2) / 2;
        
        result  = 1 - Math.exp(-result);
        
        return result;
    }
    
    /**
     * Computes the Kullback Leilbler divergence
     * @param p1
     * @param p2
     * @return 
     */
    public static double KLDivergence(double[] p1, double[] p2) {
        
        double klDiv = 0.0;

        for (int i = 0; i < p1.length; ++i) {
            if (p1[i] == 0) { continue; }
            if (p2[i] == 0.0) { continue; } // Limin

            klDiv += p1[i] * Math.log( p1[i] / p2[i] );
        }
        
        return klDiv / Math.log(2); // moved this division out of the loop -DM
    
    }
    
}
