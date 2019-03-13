/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.query;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.ext.falseto.FalsetoQueryConverter;
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
 * @author 21408782t
 */
public class QueryConverter {
    
    BenchmarkEngine be;
    
    /**
     * 
     * @param be 
     */
    public QueryConverter(BenchmarkEngine be) {
        this.be = be;
    }
    
    /**
     * Converts a query to MDX.
     * It will call a precise 
     * @param arg_q
     * @return 
     */
    public QueryMdx toMdx(Query arg_q) {
        if(arg_q instanceof QueryMdx) {
            return (QueryMdx)arg_q;
        }
        
        // in priority, we return the original query if it exists
        // if not, we compute the conversion ourselves
        if(arg_q instanceof QueryTriplet) {
            TripletToMdxQueryConverter ttmqc    = new TripletToMdxQueryConverter();
            QueryTriplet qt = (QueryTriplet)arg_q;
            if(qt.getOriginalQuery() != null) {
                return (QueryMdx)qt.getOriginalQuery();
            } else {
                return (QueryMdx)ttmqc.convert(arg_q);
                //return this.convertToMdxWithFalseto((QueryTriplet)arg_q);
            }
        }
        
        //throw new Exception("Conversion to MDX not possible");
        return null;
    }
    
    /**
     * Converts any query to a query triplet
     * Calls the correct function for conversion. Each class must provide a converter.
     * Original query is recorder within the query triplet object and is 
     * accessible through .getOriginalQuery() method
     * @param arg_q
     * @return 
     */
    public QueryTriplet toQueryTriplet(Query arg_q) {
        QueryTriplet result = null;
        
        if(arg_q instanceof QueryTriplet) {
            result  = (QueryTriplet)arg_q; 
            //result.setOriginalQuery(arg_q); // set original query
        }
        
        if(arg_q instanceof QueryMdx) {
            result  = this.convertToQueryTriplet((QueryMdx)arg_q);
            result.setOriginalQuery(arg_q); // set original query
        }
        
        return result;
        //throw new Exception("Conversion to MDX not possible");
    }
    
    /**
     * 
     * @param arg_q
     * @return 
     */
    public QueryTriplet convertToQueryTriplet(QueryMdx arg_q) {
         MdxToTripletQueryConverter mttqc   = new MdxToTripletQueryConverter();
         
         return (QueryTriplet)mttqc.convert(arg_q);
     }
     
//    /**
//     * 
//     * @param arg_q
//     * @return 
//     */
//    public QueryTriplet convertToQueryTriplet(QueryMdx arg_q) {
//        EAB_Cube cube       = arg_q.getCube();
//        
//        // keep track of members per level
//        Map<EAB_Level, List<EAB_Member>> recordList;
//        
//        Result r    = arg_q.execute(Boolean.FALSE);
//        
//        QueryTriplet result                 = new QueryTriplet(cube);
//        mondrian.olap.Result mondrianResult = r.getMondrianResult();
//        
//        Axis slicerAxis = mondrianResult.getSlicerAxis();
//        Axis columns    = mondrianResult.getAxes()[0];
//        
////        System.out.println("En colonnes...");
//        Position p_column   = columns.getPositions().iterator().next();
//        for(Member m_tmp : p_column) {
////            System.out.println("Member: " + m_tmp);
//            if(m_tmp.getHierarchy().getDimension().isMeasures()) {
//                this.addMeasure(result, m_tmp);
//            } else {
//                this.addProjection(result, m_tmp);
//            }
//        }
//        
//        // if there is anothe axis (i.e. line axis)
//        if (mondrianResult.getAxes().length == 2) {
////            System.out.println("En lignes...");
//            Axis rows       = mondrianResult.getAxes()[1];
//            Position p_row  = rows.getPositions().iterator().next();
//            for(Member m_tmp : p_row) {
////                System.out.println("Member: " + m_tmp);
//                if(m_tmp.getHierarchy().getDimension().isMeasures()) {
//                    this.addMeasure(result, m_tmp);
//                } else {
//                    this.addProjection(result, m_tmp);
//                }
//            }
//        }
//        
//        // Manage the selections now
//        for(Position p_tmp : slicerAxis.getPositions()) {
//            for(Member m_tmp : p_tmp) {
//                this.addSelection(result, m_tmp);
//                //System.out.println("Member in the slicer axis: " + m_tmp);
//            }
//        }
//        
//        //System.out.println("QueryTriplet: ");
//        //System.out.println(result);
//        
//        return result;
//    }

    /**
     * todo to write
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
    
     
     /**
     * todo to write
     * @param arg_q
     * @return 
     */
     public QueryMdx convertToMdxWithFalseto(QueryTriplet arg_q) {

        FalsetoQueryConverter fqc   = new FalsetoQueryConverter(arg_q.getCube().getBencharkEngine());

        // Falseto will now create the mdx query using the correct cube, updated above
        String mdxStringFromFalseto  = fqc.convertQuery(arg_q).toMDX().toString();
        
        QueryMdx result = new QueryMdx(arg_q.getCube(), mdxStringFromFalseto);
        
        return result;
        
    }
     
    /**
     * 
     * @param arg_qt
     * @param arg_mondrianMember 
     */
    public void addProjection(QueryTriplet arg_qt, mondrian.olap.Member arg_mondrianMember) {
        ProjectionFragment pf   = new ProjectionFragment(arg_qt, arg_qt.getCube().getLevelByMondrianLevel(arg_mondrianMember.getLevel()));
        arg_qt.addProjection(pf);
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
        EAB_Measure measure = new EAB_Measure(arg_mondrianMember, arg_qt.getCube().getLevelByMondrianLevel(arg_mondrianMember.getLevel()));
        MeasureFragment mf  = new MeasureFragment(arg_qt, measure);
        arg_qt.addMeasure(mf);
    }
    
}
