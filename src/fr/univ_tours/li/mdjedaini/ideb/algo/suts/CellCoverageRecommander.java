/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.suts;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.TaskBundle;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryAsCellWithOlapNeighbors;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 * The perfect recommender knows the hidden session.
 * It acts like this: It takes the hidden session and split it into different 
 * sequences of successive queries.
 * Each time, the algorithm recommends the next sequence of queries, until it has 
 * nothing to recommend.
 * @author mahfoud
 */
public class CellCoverageRecommander implements I_SUT {

    //
    EAB_Cube cube;
    Log log;
    
    TaskBundle tb;
    
//    Session hiddenSession;
    List<List<Query>> recommandationsList;
    
    Integer nbMaxOfQueriesToRecommand;
    
    /**
     * 
     */
    public CellCoverageRecommander() {
        this.recommandationsList    = new ArrayList<>();
    }
    
    /**
     * 
     */
    private void init() {
        Query currentQuery  = this.tb.getCurrentSession().getQueryByPosition(0);
        EAB_Cell cell       = currentQuery.getResult().getCellList().getCellCollection().iterator().next();
        
        AbstractDiscovery ad    = new DiscoveryAsCellWithOlapNeighbors(cell);
        
        for(EAB_Cell c_tmp : ad.getCellList().getCellCollection()) {
            
            Query qForCell  = c_tmp.getQueryForCell();
            
            ArrayList<Query> queryListToRecommand   = new ArrayList<>();
            queryListToRecommand.add(qForCell);
            
            this.recommandationsList.add(queryListToRecommand);
        }
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public List<Query> recommand() {
        return this.nextQuerySequence();
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getName() {
        return "SUT-CellCoverage";
    }
    
    /**
     * 
     * @param arg_tb 
     */
    @Override
    public void readTaskBundle(TaskBundle arg_tb) {
        this.tb     = arg_tb;
        this.cube   = arg_tb.getCube();
        this.log    = arg_tb.getLog();
        
        // computes the hidden session
        this.init();
    }
    
    /**
     * Recommends the next query sequence, and deletes it so it is not recommended twice.
     * 
     * @return 
     */
    public List<Query> nextQuerySequence() {
        List<Query> result  = new ArrayList<>();

        if(this.recommandationsList.size() > 0) {
            result  = this.recommandationsList.get(0);
            this.recommandationsList.remove(0);
        }
        
        return result;
    }
    
}
