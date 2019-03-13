/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mondrian.olap.Axis;
import mondrian.olap.Member;
import mondrian.olap.Position;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Hierarchy;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Measure;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;

/**
 *
 * @author mahfoud
 */
public class MondrianResultToArray implements I_QueryConverter {

    Position[] positionTable    = null;
    
    @Override
    public Query convert(Query arg_q) {
        if(!(arg_q instanceof QueryMdx)) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        EAB_Cube cube       = arg_q.getCube();
        BenchmarkEngine be  = cube.getBencharkEngine();
        
        QueryMdx q_mdx      = (QueryMdx)arg_q;
        QueryTriplet result = new QueryTriplet(cube);
        
        this.extractProjectionFragments(q_mdx, result);
        //this.extractProjectionFragmentsWithoutExecution(q_mdx, result); // only based on query text
        this.extractSelectionFragments(q_mdx, result);
        
        // add the default measure if not specified
        if(result.getMeasures().isEmpty()) {
            EAB_Measure m_tmp       = cube.getMeasureList().get(0);
            MeasureFragment mf_tmp  = new MeasureFragment(result, m_tmp);
            result.addMeasure(mf_tmp);
        }
        
        Map<EAB_Cell, Set<EAB_Cell>> myMap  = new HashMap<>();
        
        Result r    = q_mdx.execute(Boolean.TRUE);
        
        for(EAB_Cell c_tmp : r.getCellList().getCellCollection()) {
            
            Set<EAB_Cell> set_tmp   = new HashSet<>();
            
            for(EAB_Hierarchy h_tmp : r.getCube().getHierarchyList()) {
                
                if(!c_tmp.getMemberByHierarchy(h_tmp).isAll()) {
                    
                    Collection<EAB_Cell> new_cell   = new HashSet<>(c_tmp.rollOnHierarchy(h_tmp));
                    set_tmp.addAll(new_cell);
                    
                }
                
            }
            
            myMap.put(c_tmp, set_tmp);
         
            c_tmp.getQueryForCell().execute(Boolean.TRUE).getCellList().getCellCollection().iterator().next().getValue();
            
            
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_qmdx
     * @param arg_qt 
     */
    public void extractProjectionFragments(QueryMdx arg_qmdx, QueryTriplet arg_qt) {
        
        mondrian.olap.Result mondrianResult = arg_qmdx.execute(Boolean.TRUE).getMondrianResult();
        
        Axis slicerAxis = mondrianResult.getSlicerAxis();
        Axis columns    = mondrianResult.getAxes()[0];
        
        // if columns has no position, it means result is empty
        if(columns.getPositions().isEmpty()) {
            return;
        }

//        System.out.println("En colonnes...");
        Position p_column   = columns.getPositions().iterator().next();
        
        for(Member m_tmp : p_column) {
//            System.out.println("Member: " + m_tmp);
            if(m_tmp.getHierarchy().getDimension().isMeasures()) {
                this.addMeasure(arg_qt, m_tmp);
            } else {
                this.addProjection(arg_qt, m_tmp);
            }
        }
        
        // if there is anothe axis (i.e. line axis)
        if (mondrianResult.getAxes().length == 2) {
//            System.out.println("En lignes...");
            Axis rows       = mondrianResult.getAxes()[1];
            Position p_row  = rows.getPositions().iterator().next();
            for(Member m_tmp : p_row) {
//                System.out.println("Member: " + m_tmp);
                if(m_tmp.getHierarchy().getDimension().isMeasures()) {
                    this.addMeasure(arg_qt, m_tmp);
                } else {
                    this.addProjection(arg_qt, m_tmp);
                }
            }
        }
    }
    
    /**
     * 
     * @param arg_qmdx
     * @return 
     */
    public Set<String> extractPatterns(QueryMdx arg_qmdx) {
        Set<String> result = new HashSet<>();
        
        String mdx  = arg_qmdx.toString();
        
        // pattern for extracting cube name
        Pattern p = Pattern.compile("((\\[.*?\\])(\\.\\[.*?\\]){0,})");
//        System.out.println("Pattern: " + p.pattern());
        
        // Normally, the pattern is always found!
        Matcher m = p.matcher(mdx);
        
        while (m.find()) {
            for (int i = 0; i < m.groupCount(); i++) {
                result.add(m.group(i));
            }
        }
        
//        System.out.println("Hash set:");
//        System.out.println(result);
        
        return result;
    }
    
    /**
     * 
     * @param arg_qmdx
     * @param arg_qt 
     */
    public void extractSelectionFragments(QueryMdx arg_qmdx, QueryTriplet arg_qt) {
        EAB_Cube cube   = arg_qmdx.getCube();
        
        Set<String> patterns   = this.extractPatterns(arg_qmdx);
        
        for(String p_tmp : patterns) {
            // if parsed string represents a member
            if(cube.getMemberByName(p_tmp) != null) {
                EAB_Member m_tmp        = cube.getMemberByName(p_tmp);
                SelectionFragment sf    = new SelectionFragment(arg_qt, m_tmp);
                arg_qt.addSelection(sf);
            }
        }
        
    }
    
    /**
     * 
     * @param arg_qt
     * @param arg_mondrianMember 
     */
    public void addProjection(QueryTriplet arg_qt, mondrian.olap.Member arg_mondrianMember) {
        ProjectionFragment pf   = new ProjectionFragment(arg_qt, arg_qt.getCube().getLevelByMondrianLevel(arg_mondrianMember.getLevel()));
        // only add the projection if it is more detailed
        arg_qt.addProjectionIfMoreDetailed(pf);
    }
    
    /**
     * 
     * @param arg_qt
     * @param arg_mondrianMember 
     */
    public void addSelection(QueryTriplet arg_qt, mondrian.olap.Member arg_mondrianMember) {
        //System.out.println("Adding selection...");
        SelectionFragment sf    = new SelectionFragment(arg_qt, arg_qt.getCube().getMemberByMondrianMember(arg_mondrianMember));
        arg_qt.addSelection(sf);
    }
    
    /**
     * 
     * @param arg_qt
     * @param arg_mondrianMember 
     */
    public void addMeasure(QueryTriplet arg_qt, mondrian.olap.Member arg_mondrianMember) {
        //EAB_Measure measure = new EAB_Measure(arg_mondrianMember, arg_qt.getCube().getLevelByMondrianLevel(arg_mondrianMember.getLevel()));
        EAB_Measure measure = new EAB_Measure(arg_mondrianMember, arg_qt.getCube().getMeasureByMondrianMember(arg_mondrianMember).getLevel());
        MeasureFragment mf  = new MeasureFragment(arg_qt, measure);
        arg_qt.addMeasure(mf);
    }
    
}
