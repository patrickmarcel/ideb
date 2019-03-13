/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.olap.result;

import java.util.Map;
import java.util.Set;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;

/**
 *
 * @author mahfoud
 */
public class CellGroup {
    
    //
    Result result;
    
    EAB_Hierarchy variant;
    Map<EAB_Hierarchy, EAB_Member> invariantList;
    
    // group data (list of cells)
    CellList cellList;
    
    /**
     * 
     * @param arg_r
     * @param arg_variant
     * @param arg_invariantList 
     */
    public CellGroup(Result arg_r, EAB_Hierarchy arg_variant, Map<EAB_Hierarchy, EAB_Member> arg_invariantList) {
        this.result         = arg_r;
        
        this.variant        = arg_variant;
        this.invariantList  = arg_invariantList;
        
        this.cellList       = new CellList();
    }
    
    /**
     * 
     * @param arg_c 
     */
    public void addCell(EAB_Cell arg_c) {
        this.cellList.addCell(arg_c);
    }
    
    /**
     * 
     * @param arg_member
     * @return 
     */
    public CellGroup sswitch(EAB_Member arg_member) {
        
        if(!this.invariantList.get(arg_member.getLevel().getHierarchy()).getLevel().equals(arg_member.getLevel())) {
            // ici y a un probleme...
        }
        
        return this;
    }
    
    /**
     * Computes the slice of a group for the given set of members.
     * The slice must be done on the variant hierarchy!
     * @param arg_memberList
     * @return 
     */
    public CellGroup slice(Set<EAB_Member> arg_memberList) {
        for(EAB_Member m_tmp : arg_memberList) {
            if(m_tmp.getLevel().getHierarchy().equals(this.variant)) {
                // ici probleme de slice car on veut un slice sur la bonne hierarchie
                return null;
            }
        }
        
        CellGroup result    = new CellGroup(this.result, this.variant, this.invariantList);
        for(EAB_Cell c_tmp : this.cellList.getCellCollection()) {
            if(arg_memberList.contains(c_tmp.getMemberByHierarchy(variant))) {
                result.addCell(c_tmp);
            }
        }
        
        return result;
    }
    
    /**
     *
     * @return
     */
    public CellGroup relax() {
        return this;
    }
    
    /**
     *
     * @return
     */
    public CellGroup drill() {
        return this;
    }
    
    /**
     *
     * @param arg_g
     * @return
     */
    public boolean isSliceOf(CellGroup arg_g) {
        return false;
    }
    
    /**
     *
     * @param arg_g
     * @return
     */
    public boolean isSwitchOf(CellGroup arg_g) {
        return false;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String result   = "";
        
        result  += "Invariants: ";
        for(EAB_Member m_tmp : this.invariantList.values()) {
            result  += m_tmp.getUniqueName() + ", ";
        }
        
        result  += System.lineSeparator();
        
        result  += "Variant: " + this.variant;
        
        result  += System.lineSeparator();
        
//        result  += this.cellList.getSummary();
        
        return result;
    }
    
}
