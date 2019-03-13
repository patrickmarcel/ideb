/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;

/**
 *
 * @author mahfoud
 */
public class ResultWriter implements I_ResultWriter {

    /**
     *
     */
    public Result r;
    
    Map<EAB_Hierarchy, String> hierarchyToLineOrColumn;
    
    /**
     *
     * @param arg_r
     */
    public ResultWriter(Result arg_r) {
        this.r                          = arg_r;
        this.hierarchyToLineOrColumn    = new HashMap<>();
        this.autoDisplay();
    }
    
    /**
     * 
     */
    public void autoDisplay() {
        this.r.getMultiMemberHierarchy().size();
        
        Iterator<EAB_Hierarchy> it_h    = this.r.getMultiMemberHierarchy().iterator();

        while(it_h.hasNext()) {
            this.hierarchyToLineOrColumn.put(it_h.next(), "column");
        } 
    }
    
    /**
     * Computes the list of hierarchies for which members will be displayed in rows
     * @return 
     */
    List<EAB_Hierarchy> getRowHierarchy() {
        List<EAB_Hierarchy> result  = new ArrayList<>();
        
        for(EAB_Hierarchy h_tmp : r.getMultiMemberHierarchy()) {
            if(this.hierarchyToLineOrColumn.get(h_tmp).equals("row")) {
                result.add(h_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * Computes the list of hierarchies for which members will be displayed in columns
     * @return 
     */
    List<EAB_Hierarchy> getColumnHierarchy() {
        List<EAB_Hierarchy> result  = new ArrayList<>();
        
        for(EAB_Hierarchy h_tmp : r.getMultiMemberHierarchy()) {
            if(this.hierarchyToLineOrColumn.get(h_tmp).equals("column")) {
                result.add(h_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_result
     * @return 
     */
    @Override
    public String writeResult(Result arg_result) {
        
        String result   = "";

        result  += "<h2>Query - " + this.r.getQuery().getQid() + "</h2>";
        for(EAB_Hierarchy h_tmp : this.r.getUniMemberHierarchy()) {
            result  += "<h2>" + h_tmp.getName() + ": " + this.r.getMemberListByHierarchy().get(h_tmp).iterator().next() + "</h2>";
            result  += System.lineSeparator();
        }
        
        result  += System.lineSeparator();
        
        result  += this.htmlTable(this.getColumnHierarchy(), this.getRowHierarchy());
        
        System.out.println(result);
        
        return result;
    }
    
    String printHeader(List<EAB_Hierarchy> arg_hList) {
        String result   = "";
        
        if(arg_hList.size() == 0 || arg_hList.size() == 1) {
            return result;
        }
        
        result  += printHeader(arg_hList.subList(1, arg_hList.size()-1));
        
        result  += "<tr>";
        
        Integer nbOfTimes   = 1;
        for(int i = 1; i < arg_hList.size(); i++) {
            nbOfTimes   *= this.r.getMemberListByHierarchy().get(arg_hList.get(i)).size();
        }
        
        // afficher effectivement la hierarchie
        for(int i = 0; i < nbOfTimes; i++) {
            for(EAB_Member m_tmp : this.r.getMemberListByHierarchy().get(arg_hList.get(0))) {
                result  += "<td>" + m_tmp.getName() + "</td>";
            }
        }
        
        result  += "</tr>";

        return result;
    }
    
    String htmlTable(List<EAB_Hierarchy> arg_columnHierarchy, List<EAB_Hierarchy> arg_lineHierarchy) {
        
        String result   = "";
        
        result  += "<table border=1>";
        result  += System.lineSeparator();

        result  += this.printHeader(arg_columnHierarchy);
        
        result  += "</table>";
        
        return result;
        
    }
    
}
