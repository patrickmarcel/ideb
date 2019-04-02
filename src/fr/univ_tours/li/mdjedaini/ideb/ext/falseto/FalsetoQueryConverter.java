package fr.univ_tours.li.mdjedaini.ideb.ext.falseto;



import fr.univ_tours.li.jaligon.falseto.Generics.Connection;
import fr.univ_tours.li.jaligon.falseto.Generics.falseto_params;
import fr.univ_tours.li.jaligon.falseto.QueryStructure.Qfset;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Measure;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Member;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mahfoud
 */
public class FalsetoQueryConverter {
    
    BenchmarkEngine be;
    
    /**
     * 
     * @param arg_be 
     */
    public FalsetoQueryConverter(BenchmarkEngine arg_be) {
        this.be = arg_be;
        
        /***
        OPENING THE FALSETO CONNECTION
        ***/
        falseto_params fp   = new falseto_params(
                be.parameters.dbms,
                be.parameters.schemaFilePath,
                be.parameters.provider,
                be.parameters.driver,
                be.parameters.jdbcUrl,
                be.parameters.user,
                be.parameters.password);
        
        try {
            // ceci est une connexion Falseto...
            // Connection.connectionString = this.be.getParameters().connectionString;
            Connection falsetoConnection = new Connection(fp);
            falsetoConnection.open();
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
    }
    
    /**
     * Converts a query from IDE model to Falseto model...
     * @param arg_q
     * @return 
     */
    public Qfset convertQuery(Query arg_q) {
        
        QueryTriplet arg_qt = (QueryTriplet)arg_q;
        
        Qfset result    = new Qfset();
        
        for(MeasureFragment mf_tmp : arg_qt.getMeasures()) {
            result.addMeasure(mf_tmp.getMeasure().getMondrianMeasure());
        }
        
        // ! only add prjections different than All ! \\
        for(ProjectionFragment pf_tmp : arg_qt.getProjectionFragments()) {
            // Falseto Qfset doesnt like the all level projections
//            if(!pf_tmp.getLevel().isAllLevel()) {
                result.addProjection(pf_tmp.getLevel().getMondrianLevel());
//            }
        }
        
        for(SelectionFragment sf_tmp : arg_qt.getSelectionFragments()) {
            result.addSelection(sf_tmp.getMemberValue().getMondrianMember());
        }
        
        return result;
        
    }
    
    /**
     * Converts a Falseto query to an IDE query.
     * @param arg_qt
     * @return 
     */
    public QueryTriplet convertQfset(Qfset arg_qt) {
        mondrian.olap.Query mondrianQuery   = arg_qt.toMDX();
        String cubeName = mondrianQuery.getCube().getName();
        EAB_Cube cube   = this.be.getInternalCubeByName(cubeName);
        
        QueryTriplet result    = new QueryTriplet(cube);
       
        for(fr.univ_tours.li.jaligon.falseto.QueryStructure.MeasureFragment mf_tmp : arg_qt.getMeasures()) {
            EAB_Measure mm      = cube.getMeasureByMondrianMember(mf_tmp.getAttribute());
            MeasureFragment m   = new MeasureFragment(result, mm);
            result.addMeasure(m);
        }
        
        for(fr.univ_tours.li.jaligon.falseto.QueryStructure.ProjectionFragment pf_tmp : arg_qt.getAttributes()) {
            EAB_Level ll            = new EAB_Level(pf_tmp.getLevel(), cube.getHierarchyByMondrianHierarchy(pf_tmp.getHierarchy()));
            ProjectionFragment p    = new ProjectionFragment(result, ll);
            result.addProjection(p);
        }
        
        for(fr.univ_tours.li.jaligon.falseto.QueryStructure.SelectionFragment sf_tmp : arg_qt.getSelectionPredicates()) {
            EAB_Member mm       = new EAB_Member(sf_tmp.getValue(), cube.getLevelByMondrianLevel(sf_tmp.getLevel()));
            SelectionFragment s = new SelectionFragment(result, mm);
            result.addSelection(s);
        }
        
        return result;
        
    }
    
}
