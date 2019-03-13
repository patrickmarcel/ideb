/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.query;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Measure;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;

/**
 *
 * @author mahfoud
 */
public class TripletToMdxQueryConverter implements I_QueryConverter {

    /**
     * 
     * @param arg_q
     * @return 
     */
    @Override
    public Query convert(Query arg_q) {
        if(!(arg_q instanceof QueryTriplet)) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        EAB_Cube cube       = arg_q.getCube();
        BenchmarkEngine be  = cube.getBencharkEngine();
        
        QueryTriplet qt     = (QueryTriplet)arg_q;
        
        String rows     = this.onRows(qt);
        String columns  = this.onColumns(qt);
        String where    = this.where(qt);
        
        String mdx  = "SELECT " + columns + " ON COLUMNS ";
        
        if(!rows.isEmpty()) {
            mdx         += ",";
            mdx         += System.lineSeparator();
            mdx         += rows + " ON ROWS ";
            mdx         += System.lineSeparator();
        }
        
        mdx         += "FROM [" + qt.getCube().getName() + "] ";
        
        if(!where.isEmpty()) {
            mdx         += System.lineSeparator();
            mdx         += "WHERE " + where;
        }
        
        QueryMdx q_mdx  = new QueryMdx(qt.getCube(), mdx);
        
        return q_mdx;
    }

    /**
     * Builds the where clause of the mdx query.
     * The where clause contains the selections for which the query has no 
     * projection.
     * If the query has a projection on the selection, mdx will not accept.
     * In this latter case, filter has to be considered within a crossjoin.
     * @param arg_qt
     * @return 
     */
    private String where(QueryTriplet arg_qt) {
        String where    = "";
        
        List<SelectionFragment> sf_list = new ArrayList<>();
        
        // get selection fragments that will be on the where clause
        // these are selections whose hierarchy has no projection
        for(SelectionFragment sf_tmp : arg_qt.getSelectionFragments()) {
            EAB_Level l_tmp = arg_qt.getProjectionFragmentByHierarchy(sf_tmp.getLevel().getHierarchy()).getLevel();
            if(l_tmp.isAllLevel()) {
                sf_list.add(sf_tmp);
            }
        }
        
        // WHERE CLAUSE
        if(!sf_list.isEmpty()) {
            where   += "{(";
            for(int i = 0; i < sf_list.size(); i++) {
                where += sf_list.get(i).getMemberValue().getUniqueName();
                if(i + 1 < sf_list.size()) where += ", ";
            }
            where += ")}";
        }
        
        return where;
    }
    
    /**
     * 
     * @param arg_qt
     * @return 
     */
    private String onColumns(QueryTriplet arg_qt) {
        String mdxColumns   = "";

        List<EAB_Measure> columns       = new ArrayList<>();
        List<EAB_Level> rows            = new ArrayList<>();
        List<SelectionFragment> sf_list = new ArrayList<>();
        
        // put measures on columns
        for(MeasureFragment m_tmp : arg_qt.getMeasures()) {
            columns.add(m_tmp.getMeasure());
        }
        
        // put all other hierarchies in rows (only non All levels)
        for(SelectionFragment sf_tmp : arg_qt.getSelectionFragments()) {
            sf_list.add(sf_tmp);
        }
        
        // ON COLUMNS
        mdxColumns += "{";
        for(int i = 0; i < columns.size(); i++) {
            mdxColumns  += columns.get(i).getUniqueName();
            if(i +1 < columns.size()) {
                mdxColumns   += ", ";
            }
        }
        mdxColumns += "}";
        
        return mdxColumns;
    }
    
    /**
     * 
     * @param arg_qt
     * @return 
     */
    private String onRows(QueryTriplet arg_qt) {
        String mdxRows  = "";
        
        List<EAB_Level> rows            = new ArrayList<>();
        List<SelectionFragment> sf_list = new ArrayList<>();
        
        // get selection fragments that will be on the where clause
        // these are selections whose hierarchy has no projection
        for(SelectionFragment sf_tmp : arg_qt.getSelectionFragments()) {
            EAB_Level l_tmp = arg_qt.getProjectionFragmentByHierarchy(sf_tmp.getLevel().getHierarchy()).getLevel();
            if(!l_tmp.isAllLevel()) {
                sf_list.add(sf_tmp);
            }
        }
        
        // put all other hierarchies in rows (only non All levels)
        // only add GBS for hierarchies that have no selection
        // if hierarchy has selection, it will be within DESCENDANTS function
        for(ProjectionFragment pf_tmp : arg_qt.getProjectionFragments()) {
            if(!pf_tmp.getLevel().isAllLevel()) {
                if(arg_qt.getSelectionFragmentByHierarchy(pf_tmp.getLevel().getHierarchy()).isEmpty()) {
                    rows.add(pf_tmp.getLevel());
                }
            }
        }

        // we transform the different parts into strings for easy crossjoin
        List<String> toCrossJoin    = new ArrayList<>();
        
        for(SelectionFragment sf_tmp : sf_list) {
            EAB_Level l_tmp = arg_qt.getProjectionFragmentByHierarchy(sf_tmp.getLevel().getHierarchy()).getLevel();
            String part     = "DESCENDANTS(" + sf_tmp.getMemberValue().getUniqueName() + ", " + l_tmp.getUniqueName() + ")";
            toCrossJoin.add(part);
        }
        
        for(EAB_Level l_tmp : rows) {
            String part     = "{" + l_tmp.getUniqueName() + ".Members}";
            toCrossJoin.add(part);
        }
        
        // ON ROWS
        switch(toCrossJoin.size()) {
            
            // to correct!
            case 0:
                // put a random hierarchy All member as a patch
                // @todo correct that and put the global All member!!!
                
//                EAB_Hierarchy h_tmp = arg_qt.getCube().getRandomHierarchy();
//                
//                // we need a hierarchy without selection for avoiding Mondrian errors!
//                while(!arg_qt.getSelectionFragmentByHierarchy(h_tmp).isEmpty()) {
//                    h_tmp = arg_qt.getCube().getRandomHierarchy();
//                }
                
                mdxRows += "{";
                //mdxRows += h_tmp.getAllLevel().getLevelMembers().get(0).getUniqueName();
                mdxRows += "} ";
                break;
                
            case 1:
                mdxRows += toCrossJoin.iterator().next();
                break;
                
            default:
                mdxRows += "{";
                
                String rowsText         = "";
                Integer currentPosition = 0;    // position, not index!
                
                for(int i = 0; i < toCrossJoin.size(); i++) {
                    if(i == 0) {
                        //rowsText    = "{" + rows.get(i).getUniqueName() + ".Members}";
                        rowsText    = toCrossJoin.get(i);
                    } else {
                        rowsText    = "NONEMPTYCROSSJOIN(" + rowsText + ", " + toCrossJoin.get(i) + ")";
                    }
                }
                
                mdxRows += rowsText;
                mdxRows += "} ";
                break;
            
        }
        
        return mdxRows;
    }
    
    /**
     * 
     * @param arg_q
     * @return 
     */
    public QueryMdx convertToMdx(QueryTriplet arg_q) {
        
        List<EAB_Measure> columns       = new ArrayList<>();
        List<EAB_Level> rows            = new ArrayList<>();
        List<SelectionFragment> sf_list = new ArrayList<>();
        
        // put measures on columns
        for(MeasureFragment m_tmp : arg_q.getMeasures()) {
            columns.add(m_tmp.getMeasure());
        }
        
        // put all other hierarchies in rows (only non All levels)
        for(ProjectionFragment pf_tmp : arg_q.getProjectionFragments()) {
            if(!pf_tmp.getLevel().isAllLevel()) {
                rows.add(pf_tmp.getLevel());
            }
        }
        
        // put all other hierarchies in rows (only non All levels)
        for(SelectionFragment sf_tmp : arg_q.getSelectionFragments()) {
            sf_list.add(sf_tmp);
        }
        
        String mdx  = "";
        
        // SELECT CLAUSE
        mdx += "SELECT ";
        mdx += System.lineSeparator();
        
        // ON COLUMNS
        mdx += "{";
        for(int i = 0; i < columns.size(); i++) {
            mdx += columns.get(i).getUniqueName();
            if(i +1 < columns.size()) mdx   += ", ";
        }
        mdx += "}";
        mdx += " ON COLUMNS,";
        mdx += System.lineSeparator();
        
        // ON ROWS
        switch(rows.size()) {
            
            // to correct!
            case 0:
                mdx += "{[COMMIT_TIME].[ALLCOMMIT_TIME].Members} ON ROWS";
                mdx += System.lineSeparator();
                break;
                
            case 1:
                mdx += "{" + rows.iterator().next().getUniqueName() + ".Members} ON ROWS";
                mdx += System.lineSeparator();
                break;
                
            default:
                mdx += "{";
                Integer size    = rows.size();
                
                String rowsText         = "";
                Integer currentPosition = 0;    // position, not index!
                
                for(int i = 0; i < size; i++) {
                    if(i == size - 1) {
                        //rowsText    = "{" + rows.get(i).getUniqueName() + ".Members}";
                        rowsText    = "CROSSJOIN({" + rows.get(i).getUniqueName() + ".Members}, " + rowsText + ")";
                    } else {
                        rowsText    = "CROSSJOIN({" + rows.get(i).getUniqueName() + ".Members}, " + rowsText + ")";
                    }
                }
                
                mdx += rowsText;
                mdx += "} ON ROWS";
                mdx += System.lineSeparator();
                break;
            
        }
        
        // FROM CLAUSE
        mdx += "FROM [" + arg_q.getCube().getName() + "]";
        mdx += System.lineSeparator();
        
        // WHERE CLAUSE
        if(!sf_list.isEmpty()) {
            mdx += "WHERE {( ";
            for(int i = 0; i < sf_list.size(); i++) {
                mdx += sf_list.get(i).getMemberValue().getUniqueName();
                if(i + 1 < sf_list.size()) mdx  += ", ";
            }
            mdx += ")}";
        }
        
        
        QueryMdx result = new QueryMdx(arg_q.getCube(), mdx);
        
        return result;
    }
    
}
