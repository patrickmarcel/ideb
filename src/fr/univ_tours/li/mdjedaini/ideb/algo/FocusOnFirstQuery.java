/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.struct.DiscoveryAsCellWithOlapNeighbors;

/**
 *
 * @author mahfoud
 */
public class FocusOnFirstQuery implements I_FocusDetector {

    /**
     * 
     * @param arg_exp
     * @return 
     */
    @Override
    public List<FocusZone> detectFocusZones(Exploration arg_exp) {
        List<FocusZone> result  = new ArrayList<>();
        FocusZone fz        = new FocusZone(arg_exp, 0, 0);
        Query firstQuery    = arg_exp.getWorkSession().getQueryList().iterator().next();
        Result r            = firstQuery.execute(Boolean.FALSE);
        
        // extract the coverage for each cell of the first query result
        for(EAB_Cell c_tmp : r.getCellList().getCellCollection()) {
            // compute the coverage for the cell
            DiscoveryAsCellWithOlapNeighbors dacwon = new DiscoveryAsCellWithOlapNeighbors(c_tmp);
            
            // add the coverage to the focus zone
            fz.cellList.addCellCollection(dacwon.getCellList().getCellCollection());
        }
        
        result.add(fz);
        return result;
    }
        
}
