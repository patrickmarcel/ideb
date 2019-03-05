/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.struct;

import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mahfoud
 */
public class CellList {
    
    // 
    Collection<EAB_Cell> cellList;
    
    /**
     * 
     */
    public CellList() {
        this.cellList   = new HashSet<>();
        //this.cellList   = new ArrayList<>();
    }
    
    /**
     * 
     * @param arg_cellList 
     */
    public CellList(Collection<EAB_Cell> arg_cellList) {
    	this();
        for(EAB_Cell c_tmp : arg_cellList) {
            this.addCell(c_tmp);
        }
    }
    
    /**
     * 
     * @param arg_cell
     * @return 
     */
    public boolean contains(EAB_Cell arg_cell) {
        return this.cellList.contains(arg_cell);
    }
    
        
    /**
     * 
     * @return 
     */
    public Collection<EAB_Cell> getCellCollection() {
        return this.cellList;
    }

    /**
     * 
     * @param arg_c
     * @return 
     */
    public Set<Query> getQueryListByCell(EAB_Cell arg_c) {
        Set<Query> result   = new HashSet<>();
        
        for(EAB_Cell c_tmp : this.cellList) {
            result.add(c_tmp.getResult().getQuery());
        }
        
        return result;
    }
    
    /**
     * Manages the add of a cell.
     * If the cell is already present, we just add an occurrence.
     * If the cell is not present, we effectively add the cell and its occurrence.
     * @param arg_c
     */
    public void addCell(EAB_Cell arg_c) {
        this.cellList.add(arg_c);
    }

    /**
     * 
     * @param arg_cellList 
     */
    public void addCellCollection(Collection<EAB_Cell> arg_cellList) {
        for(EAB_Cell c_tmp : arg_cellList) {
            this.addCell(c_tmp);
        }
    }
    
    /**
     * 
     * @param arg_criteria
     * @return 
     */
    public Set<EAB_Cell> searchCell(Map<EAB_Hierarchy, EAB_Member> arg_criteria) {
        Set<EAB_Cell> result    = new HashSet<>();
        
        for(EAB_Cell c_tmp : this.cellList) {
            boolean ok  = true;
            for(EAB_Hierarchy h_tmp : arg_criteria.keySet()) {
                ok  = ok && (arg_criteria.containsKey(h_tmp) && c_tmp.getMemberByHierarchy(h_tmp).equals(arg_criteria.get(h_tmp)));
            }
            
            if(ok) {
                result.add(c_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Integer nbOfCells() {
        return this.getCellCollection().size();
    }
    
    /**
     * 
     * @return 
     */
    public CellList distinct() {
        CellList result = new CellList();
        
        for(EAB_Cell c_tmp : this.getCellCollection()) {
            if(!result.contains(c_tmp)) {
                result.addCell(c_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    public CellList union(CellList arg_cellList) {
        CellList result = new CellList();
        
        // add cells from this collection
        for(EAB_Cell c_tmp : this.getCellCollection()) {
            result.addCell(c_tmp);
        }
        
        // add cells from the other collection
        for(EAB_Cell c_tmp : arg_cellList.getCellCollection()) {
            result.addCell(c_tmp);
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    public CellList intersection(CellList arg_cellList) {
        CellList result = new CellList();
        
        // add cells from this collection
        for(EAB_Cell c_tmp : this.getCellCollection()) {
            if(arg_cellList.contains(c_tmp)) {
                result.addCell(c_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_cellList
     * @return 
     */
    public CellList minus(CellList arg_cellList) {
        CellList result = new CellList();
        
        // add cells from this collection
        for(EAB_Cell c_tmp : this.getCellCollection()) {
            if(!arg_cellList.contains(c_tmp)) {
                result.addCell(c_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String summary  = "";
        
        summary += "There are " + this.nbOfCells() + " cells..." + System.lineSeparator();
        
        for(EAB_Cell c_tmp : this.cellList) {
            summary += c_tmp.toString();
            summary += System.lineSeparator();
        }

        return summary;
    }
    
}
