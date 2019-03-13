/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval.metric.learning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.user.UserModelMarkov;
import fr.univ_tours.li.mdjedaini.ideb.eval.Exploration;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.ext.jung.EAB_Vertex;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import fr.univ_tours.li.mdjedaini.ideb.tools.Utils;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 * This metric evaluates the evolution of the access area.
 * Access area is defined as all the primary cells (tuples) accessed.
 * It evaluates how rich is the access area provided by the SUT.
 * @author mahfoud
 */
public class MetricLearningUtils extends Metric {
    
    // 
    Double p_l0;    // probability to master the skill before starting
    Double p_t;     // probability to master the skill after an exercice
    Double p_g;     // probability to guess even we do not master the skill
    Double p_s;     // probability to fail even if we master the skill
    
    Exploration tr;
    
    /**
     * 
     * @param arg_be 
     */
    public MetricLearningUtils(BenchmarkEngine arg_be) {
        super(arg_be);
        this.name           = "Metric: Knowledge Tracing";
        this.description    = "Evaluates the ability of a system to help users in mastering skills...";
    }
    
    /**
     * 
     * @param arg_tr
     * @param arg_ad
     * @return 
     */
    public static List<Boolean> computeSuccessList(Exploration arg_tr, AbstractDiscovery arg_ad) {
        List<Boolean> successList   = new ArrayList<>();
        
        // accumulateur...
        CellList cl_acc         = new CellList();
        CellList discoveryCells = arg_ad.getCellList();
        
        for(Query q_tmp : arg_tr.getWorkSession().getQueryList()) {
            QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            CellList cl_tmp     = qt_tmp.getResult().getCellList();
            
            Boolean success = false;
            
            for(EAB_Cell c_tmp : discoveryCells.getCellCollection()) {
                
                // success if at least one cell is newly discovered
                if(cl_tmp.contains(c_tmp) && !cl_acc.contains(c_tmp)) {
                    success = true;
                    break;
                }
            }
            
            // ajoouter les cellules de la requete courante dans l'accumulateur
            cl_acc.addCellCollection(cl_tmp.getCellCollection());
            
            // add the success to the success list...
            successList.add(success);
            
        }
        
        return successList;
    }

    /**
     * 
     * @param arg_tr
     * @param arg_ad
     * @return 
     */
    public static Map<String, Double> initializeParameters(Exploration arg_tr, AbstractDiscovery arg_ad) {
        Map<String, Double> result  = new HashMap<>();
        
        result.put("pl0", MetricLearningUtils.initializePL0(arg_tr, arg_ad));
        result.put("pt", MetricLearningUtils.initializePT(arg_tr, arg_ad));
        result.put("pg", MetricLearningUtils.initializePG(arg_tr, arg_ad));
        result.put("ps", MetricLearningUtils.initializePS(arg_tr, arg_ad));
        
        return result;
    }
    
    /**
     * Initializes the P_T parameter for the knowledge tracing model.
     * Initialization is done by using the user log.
     * It is computed for each discovery, as the proportion of cells already discovered...
     * @param arg_tr
     * @param arg_ad
     * @return 
     */
    public static Double initializePT(Exploration arg_tr, AbstractDiscovery arg_ad) {
        Double result   = 0.;
        
        List<Session> us    = arg_tr.getCurrentUserSessions();
        List<Double> ptList = new ArrayList<>();
        
        for(Session s_tmp : us) {
            
            Integer nbq = s_tmp.getNumberOfQueries();
            
            Double num_tmp      = 0.;
            Integer denum_tmp   = nbq * (nbq + 1) / 2;
            
            // we compute and store the interest list
            List<Double> interestList    = new ArrayList<>();
            for(Query q_tmp : s_tmp.getQueryList()) {
                
                CellList cl_tmp = MetricLearningUtils.extractDiscoveryCellsFromQuery(q_tmp, arg_ad);
                Double i_tmp    = Utils.computeInterest(cl_tmp.getCellCollection());
                interestList.add(i_tmp);
                
            }
            
            List<Integer> positionList  = new ArrayList<>();
            
            //
            for(int i = 1; i < nbq; i++) {
                
                Double delta    = interestList.get(i) - interestList.get(i-1);

                if(delta > 0) {
                    Integer position    = nbq - i;
                    positionList.add(position);
                    num_tmp           += position;
                }
                
            }
            
            Double result_tmp   = num_tmp / denum_tmp;
            ptList.add(result_tmp);
            
        }
        
        // compute the average for the pts
        Double sum  = 0.;
        for(Double d_tmp : ptList) {
            sum += d_tmp;
        }
        
        result  = sum / ptList.size();
        
        return result;
    }
    
//    /**
//     * Initializes the P_T parameter for the knowledge tracing model.
//     * Initialization is done by using the user log.
//     * It is computed for each discovery, as the proportion of cells already discovered...
//     * @param arg_tr
//     * @param arg_ad
//     * @return 
//     */
//    public static Double initializePT(Exploration arg_tr, AbstractDiscovery arg_ad) {
//        Double result   = 0.5;
//        
//        // gather discovered cell lists
//        CellList cl_tmp = new CellList();
//        
//        // for all the steps so far...
//        for(Query q_tmp : arg_tr.getTaskBundle().getCurrentUserLog().getQueryList()) {
//            
//            CellList q_cl   = q_tmp.getResult().getCellList();
//                    
//            // get all the cells discovered at each step
//            for(EAB_Cell c_tmp : q_cl.getCellCollection()) {
//                cl_tmp.addCell(c_tmp);
//            }
//            
//        }
//        
//        // we have the cells, we compute the ratio...
//        Integer denum   = arg_ad.getCellList().nbOfCells();
//        Integer num     = 0;
//        
//        for(EAB_Cell c_tmp : cl_tmp.getCellCollection()) {
//            if(arg_ad.getCellList().contains(c_tmp)) {
//                num++;
//            }
//        }
//        
//        // result is the ratio #found cells / # cells
//        result  = num.doubleValue() / denum.doubleValue();
//        
//        return result;
//    }
    
    /**
     * 
     */
    private static void updateParameters(AbstractDiscovery arg_ad, Integer arg_step) {
//        this.updatePT(arg_ad, arg_step);
//        this.updatePTWithNovelty(arg_ad, arg_step);
    }
    
    
    
    
    /**
     * 
     * @param arg_tr
     * @param arg_ad
     * @param arg_successList
     * @param arg_params
     * @return 
     */
    public static List<Double> computePliList(Exploration arg_tr, AbstractDiscovery arg_ad, List<Boolean> arg_successList, Map<String, Double> arg_params) {
        List<Double> result = new ArrayList<>();
        
        Double pl0  = arg_params.get("pl0");
        Double pt   = arg_params.get("pt");
        Double pg   = arg_params.get("pg");
        Double ps   = arg_params.get("ps");
        
        // current pl0, and pli does not exist
        Double currentPLI   = pl0;
        Double previousPLI  = null;
        
        result.add(currentPLI);
        
        for(int i = 1; i < arg_tr.getWorkSession().getQueryList().size(); i++) {
            
            // at each step, the current becomes the previous
            previousPLI     = currentPLI;
            Double proba;
            
            // proba is computed based on the previous
            if(arg_successList.get(i-1)) {
                // success at the previous exercice
                proba   = (previousPLI * (1 - ps)) / ((previousPLI * (1 - ps)) + ((1 - previousPLI) * pg));
            } else {
                // fail at the previous exercice
                proba   = (previousPLI * ps) / ( (previousPLI * ps) + ((1 - previousPLI) * (1 - pg)) );
            }
            
            // current PLI is either skill as mastered before (proba)
            // or it has been learned during this step (1-proba)...
            currentPLI  = proba + ((1 - proba) * pt);
        
            result.add(currentPLI);
            
            // update PT
            MetricLearningUtils.updateParameters(arg_ad, i);
            
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_tr
     * @return 
     */
    private static Double initializePG(Exploration arg_tr, AbstractDiscovery arg_ad) {
        Double result   = 0.;
        
        if(true) {
            return 0.25;
        }
        
        User u  = arg_tr.getCurrentUser();
        UserModelMarkov um  = (UserModelMarkov)u.getUserModel();
        
        Graph<Query, EAB_Vertex> g  = um.getGraph();
        
        // number of graphs
        Double num      = 0.;
        Integer denum   = g.getEdgeCount();
                
        for(Query q_tmp : g.getVertices()) {
            
            QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            
            CellList cl_tmp = MetricLearningUtils.extractDiscoveryCellsFromQuery(qt_tmp, arg_ad);
            Double interet  = Utils.computeInterest(cl_tmp.getCellCollection());
            
            for(Query q_bis : g.getSuccessors(q_tmp)) {
                
                QueryTriplet qt_bis = (QueryTriplet)q_bis;
                
                CellList cl_bis     = MetricLearningUtils.extractDiscoveryCellsFromQuery(qt_tmp, arg_ad);
                Double interet_bis  = Utils.computeInterest(cl_tmp.getCellCollection());
                
                if(interet_bis > interet) {
                    num++;
                }
            }
            
        }
        
        result  = num / denum;
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    private static CellList extractDiscoveryCellsFromQuery(Query q_tmp, AbstractDiscovery arg_ad) {
        CellList result = new CellList();
        
        CellList discoveryCells = arg_ad.getCellList();
        CellList queryCellList  = q_tmp.getResult().getCellList();
        
        // ATTENTION A L ORDRE!
        // si je fais l'inverse, les cellules seront les mêmes MAIS vu qu'elles seront extraites
        // dela discovery et non de la query, elles n'auront pas de valeur!
        // En effet, pour le moment, le voisinage extrait uniquement la topologie des cellules voisines (discovery),
        // mais on ne calcule pas la valeur.
        // ceci sera corrigé dans le futur @todo
        return queryCellList.intersection(queryCellList);
    }
    
    /**
     * 
     * @return 
     */
    private static Double initializePS(Exploration arg_tr, AbstractDiscovery arg_ad) {
        Double result   = 0.;
        
        if(true) {
            return 0.25;
        }
        
        User u  = arg_tr.getCurrentUser();
        UserModelMarkov um  = (UserModelMarkov)u.getUserModel();
        
        Graph<Query, EAB_Vertex> g  = um.getGraph();
        
        // number of graphs
        Double num      = 0.;
        Integer denum   = g.getEdgeCount();
                
        for(Query q_tmp : g.getVertices()) {
            
            QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            
            CellList cl_tmp = MetricLearningUtils.extractDiscoveryCellsFromQuery(qt_tmp, arg_ad);
            Double interet  = Utils.computeInterest(cl_tmp.getCellCollection());
            
            for(Query q_bis : g.getSuccessors(q_tmp)) {
                
                QueryTriplet qt_bis = (QueryTriplet)q_bis;
                
                CellList cl_bis     = MetricLearningUtils.extractDiscoveryCellsFromQuery(qt_tmp, arg_ad);
                Double interet_bis  = Utils.computeInterest(cl_tmp.getCellCollection());
                
                if(interet_bis < interet) {
                    num++;
                }
            }
            
        }
        
        result  = num / denum;
        
        return result;
    }
    
    /**
     * 
     * @param arg_tr
     * @param arg_ad
     * @return 
     */
    public static Double initializePL0(Exploration arg_tr, AbstractDiscovery arg_ad) {
        Double result   = 0.;
        
        User u  = arg_tr.getCurrentUser();
        UserModelMarkov um  = (UserModelMarkov)u.getUserModel();
        
        Graph<Query, EAB_Vertex> g  = um.getGraph();
        
        Double num      = 0.;
        Integer denum   = g.getVertices().size();
        
        for(Query q_tmp : g.getVertices()) {
            
            QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            
            //qt_tmp.execute(true);
            
            CellList cl_tmp = MetricLearningUtils.extractDiscoveryCellsFromQuery(qt_tmp, arg_ad);
            Double interet  = Utils.computeInterest(cl_tmp.getCellCollection());
            
            for(Query q_bis : g.getSuccessors(q_tmp)) {
                
                QueryTriplet qt_bis = (QueryTriplet)q_bis;
                
                CellList cl_bis     = MetricLearningUtils.extractDiscoveryCellsFromQuery(qt_bis, arg_ad);
                Double interet_bis  = Utils.computeInterest(cl_bis.getCellCollection());
                
                if(interet_bis > interet) {
                    num++;
                }
                
            }
            
        }
        
        // result is the proportion of "good" queries over the total number of queries
        result  = num / denum;
        
        return result;
    }
    
//    /**
//     * 
//     * @param arg_ad 
//     */
//    private static Double initializePL0(Exploration arg_tr, AbstractDiscovery arg_ad) {
//        Double result   = 0.;
//        
//        CellList discoveryCells     = arg_ad.getCellList();
//        CellList cellsInDiscovery   = new CellList();
//        
//        Integer nbCellsInUgm    = 0;
//        
//        for(Session s_tmp : arg_tr.getTaskBundle().getCurrentUserSessions()) {
//            
//            for(Query q_tmp : s_tmp.getQueryList()) {
//                QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
//                
//                // parcourir qt_tmp plutot que discoveryCells pour avoir la valeur!
//                for(EAB_Cell c_tmp : qt_tmp.getResult().getCellList().getCellCollection()) {
//                    nbCellsInUgm++;
//                    if(discoveryCells.contains(c_tmp)) {
//                        cellsInDiscovery.addCell(c_tmp);
//                    }
//                }
//            }
//        }
//        
//        // now compute the entropy for the cells
//        result   = fr.univ_tours.li.mdjedaini.eab.tools.Utils.computeInterest(cellsInDiscovery.getCellCollection()) / nbCellsInUgm;
//        
////        if(result.isNaN()) {
////            result  = fr.univ_tours.li.mdjedaini.eab.tools.Utils.computeInterest(cellsInDiscovery.getCellCollection()) / nbCellsInUgm;
////        }
//        
//        return result;
//    }
    
//    /**
//     * 
//     */
//    private void updatePTWithNovelty(Exploration arg_tr, AbstractDiscovery arg_ad, Integer arg_i) {
//        
//        Double result   = 0.5;
//        
//        // gather discovered cell lists
//        CellList cl_tmp = new CellList();
//        
//        Query previousQuery = this.tr.getWorkSession().getQueryByPosition(0);
//        
//        // entropy for only cells in the neighborhood
//        List<Double> entropyList    = new ArrayList<>();
//        for(int i = 0; i <= arg_i; i++) {
//            Query q_tmp     = this.tr.getWorkSession().getQueryByPosition(i);
//            CellList cl_ad  = q_tmp.getResult().getCellList().intersection(arg_ad.getCellList());
//            Double entropy  = Utils.computeInterest(cl_ad.getCellCollection());
//            entropyList.add(entropy);
//        }
//        
//        // we consider queries until this step...
//        Integer nbQueriesSoFar  = arg_i;
//        Integer nbIncrease      = 0;
//        
//        // for all the steps so far...
//        for(int i = 1; i <= arg_i; i++) {
//            
////            System.out.println("Complete query list size: " + this.tr.completeQueryList.size());
////            System.out.println("Cells discovered list size: " + this.tr.cellDiscovered.size());
//            if(entropyList.get(i) > entropyList.get(i-1)) {
//                nbIncrease++;
//            }
//            
//        }
//        
//        this.p_t    = (double)nbIncrease / (double)nbQueriesSoFar;
//        
//    }
    
}
