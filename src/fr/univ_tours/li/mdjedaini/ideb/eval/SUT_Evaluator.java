/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.FocusZone;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.I_SUT;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.ExplorationScore;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.ExplorationScorer;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.SUTScorer;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.SutResolutionScore;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.TaskResolutionScore;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.params.EvaluationParameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 * todo
 * Evaluator must not contain scoring tasks.
 * Evaluator should only contain the logic of the evaluation protocol.
 * It should take as input a SUT, the data and evaluate the SUT against a protocol.
 * @author mahfoud
 */
public class SUT_Evaluator implements I_Evaluator {
    
    // 
    BenchmarkEngine benchmarkEngine;
    EvaluationParameters params;
    
    I_LogSplitter logSplitter;
    I_TaskGenerator taskGenerator;
    
    Log visibleLog;
    Log hiddenLog;
    
    Integer numberOfTasks;
    Integer numberOfChances;
    
    Boolean executeQueries;         // @todo not used yet
    Boolean userModelActive;        // @todo not used yet
    Boolean parallelEvaluation;     // @todo not used yet
    Boolean addWorkSessionInLog;    // @todo not used yet
    Boolean storeResultsDuringEvaluation;         // @todo not used yet
    
    /**
     * ensure the tasks are exactly the same for each SUT
     */
    Boolean ensureSameTasks;
    
    /**
     * 
     * @param arg_be
     */
    public SUT_Evaluator(BenchmarkEngine arg_be) {
        this.benchmarkEngine    = arg_be;
        
        this.ensureSameTasks    = true;
        
        //this.logSplitter            = new VerticalLogSplitter(arg_be);
        this.logSplitter            = new HorizontalLogSplitter(arg_be);
        this.taskGenerator          = new TaskGenerator2(this, this.ensureSameTasks);
        
        this.numberOfTasks          = 10;
        this.numberOfChances        = 5;
        
        this.executeQueries         = true;
        this.userModelActive        = true;
        
        this.parallelEvaluation     = false;
        this.addWorkSessionInLog    = false;
        
        // do not initalize here...
        //this.init();
    }
    
    /**
     * This function evaluates a SUT.
     * It takes as input a SUT and returns a score, which contains a detailed score for a SUT.
     * @param arg_sut
     * @return 
     */
    public SutResolutionScore evaluateSut(I_SUT arg_sut) {
        SutResolutionScore result;
        
        this.init();
        
        if(this.parallelEvaluation) {
            SUTResolution sr    = this.undergoSut(arg_sut);
            result              = this.getBenchmarkEngine().getSUTScorer().score(sr);
        } else {
            result  = new SutResolutionScore(arg_sut, this.getBenchmarkEngine().getSUTScorer());
            
            SUTScorer ss            = (SUTScorer)this.getBenchmarkEngine().getSUTScorer();
            ExplorationScorer es    = ss.getExplorationScorer();
            
            // Tasks are generated from the hidden log...
            for(Task t_tmp : this.taskGenerator.generateTaskList(this.numberOfTasks)) {
                TaskBundle tb       = new TaskBundle(t_tmp);
            
                tb.setVisibleLog(this.visibleLog);
                tb.setHiddenLog(this.hiddenLog);
            
                System.out.println("Solving task number " + t_tmp.tid);
                TaskResolution tr_tmp   = this.undergoSutForTask(arg_sut, t_tmp, tb);
                tr_tmp.prepare();
                
                // display the task resolution
//                System.out.println(tr_tmp.getSummary());
                
                System.out.println("Task number " + t_tmp.tid + " is finished");
                ExplorationScore e_score    = es.score(tr_tmp);
                TaskResolutionScore trs     = new TaskResolutionScore(e_score, tr_tmp);
                result.registerTaskScore(tr_tmp, trs);
            }
        
        }
        
        return result;
        
    }
    
    /**
     * Initialize the evaluator.
     * So far, we only split the log here...
     */
    private void init() {
        // we split the log
        ArrayList<Log> logs = (ArrayList)this.logSplitter.splitLog(this.benchmarkEngine.getLog());
        
        while(logs.get(0).getNumberOfSessions().equals(0) || logs.get(1).getNumberOfSessions().equals(0)) {
            System.out.println("One log (training or test log) is empty, so we split again...");
            logs    = (ArrayList)this.logSplitter.splitLog(this.benchmarkEngine.getLog());
        }
        
        // the logs belong to the SUT resolution
        this.visibleLog   = logs.get(0);
        this.hiddenLog    = logs.get(1);
    }
    
    /**
     * 
     * @param arg_sut
     * @return 
     */
    @Override
    public SUTResolution undergoSut(I_SUT arg_sut) {
        SUTResolution result  = new SUTResolution(arg_sut);

        // Tasks are generated over the hidden log...
        for(Task t_tmp : this.taskGenerator.generateTaskList(numberOfTasks)) {
            
            // Task bundle contains data to provide to the SUT...
            TaskBundle tb       = new TaskBundle(t_tmp);
            
            tb.setVisibleLog(this.visibleLog);
            tb.setHiddenLog(this.hiddenLog);
            
            
            System.out.println("Solving task number " + t_tmp.tid);
            TaskResolution tr_tmp   = this.undergoSutForTask(arg_sut, t_tmp, tb);
            
            System.out.println("Task number " + t_tmp.tid + " is finished");
            result.addTaskResolution(tr_tmp);
            
        }
        
        return result;
    }
    
    /**
     * This function submits a task to a SUT.
     * It takes as input a SUT and returns a score, which contains a detailed score for a SUT.
     * @param arg_sut
     * @param arg_task
     * @param arg_tb
     * @return 
     */
    public TaskResolution undergoSutForTask(I_SUT arg_sut, Task arg_task, TaskBundle arg_tb) {
        
        // TR records how a SUT solved a task with a given task bundle
        TaskResolution tr   = new TaskResolution(this.benchmarkEngine, arg_sut, arg_task, arg_tb);
        tr.init();

        // computes the focus zone which is made of the coverage zones
        tr.computeFocusZone(this.benchmarkEngine.getFocusDetector());
        System.out.println("FOCUS ZONE");
        System.out.println(tr.getFocusZoneList().get(0).getCellList());
        
        User u              = arg_task.getUser();
        
        // SUT and users can read task bundle
        arg_sut.readTaskBundle(arg_tb);
        u.getUserModel().readTaskBundle(arg_tb);
        
        // 
        boolean continueEvaluation      = true;
        Integer consumedChances         = 0;
        
        // for the moment I skip the "end of task" module...
        while(continueEvaluation) {
            
            // 
            System.out.println("The SUT " + arg_sut.getName() + " recommends...");
            
            List<List<Long>> sutTimestampList  = new ArrayList<>();

            // sut recommends
            Long timeStampBeforeRecommendation  = tr.getCurrentTimestamp();
            List<Query> lq_tmp                  = arg_sut.recommand();
            Long timeStampAfterRecommendation   = tr.getCurrentTimestamp();
            
            for(Query q_tmp : lq_tmp) {
                List<Long> timestampPerQuery    = new ArrayList<>();
                    
                timestampPerQuery.add(timeStampBeforeRecommendation);
                timestampPerQuery.add(timeStampAfterRecommendation);
                
                // we execute the queries if needed
                if(this.executeQueries) {
                    Long timeStampBeforeExecution   = tr.getCurrentTimestamp();
                    q_tmp.execute(true);
                    Long timeStampAfterExecution    = tr.getCurrentTimestamp();
                
                    timestampPerQuery.add(timeStampBeforeExecution);
                    timestampPerQuery.add(timeStampAfterExecution);
                }
                
                sutTimestampList.add(timestampPerQuery);
            }
            
            boolean somethingNewDiscovered  = this.somethingNewDiscovered(tr, lq_tmp);
            Integer maxQueries              = 50;
            
//            boolean somethingNewDiscovered  = false;
            continueEvaluation              = (somethingNewDiscovered || consumedChances < this.numberOfChances) && tr.getWorkSession().getNumberOfQueries() < maxQueries;

            // I add the query list AFTER (!!!) checking whether something new has been discovered
            tr.addQueryList(lq_tmp, sutTimestampList, "SUT");
            
            // 
            if(!somethingNewDiscovered) {
                System.out.println("Je me suis trompe, j'ai encore " + (numberOfChances - consumedChances) + " chances... Cool!");
                consumedChances++;
                if(consumedChances.equals(numberOfChances)) {
                    System.out.println("Oh punaise! j'ai epuise mes chances... :S");
                }
            }
        
            // we stop the evaluation...
            if(!continueEvaluation) {
                break;
            }
            
            List<List<Long>> userTimestampList  = new ArrayList<>();

            if(this.userModelActive) {
                // user is playing
                System.out.println("The user plays...");
            
                Long userTimeStampBeforeRecommendation  = tr.getCurrentTimestamp();
                List<Query> uq_tmp  = u.playQueryListForTask(arg_tb);
                Long userTimeStampAfterRecommendation   = tr.getCurrentTimestamp();
            
                for(Query q_tmp : uq_tmp) {
                    List<Long> timestampPerQuery    = new ArrayList<>();
                    
                    timestampPerQuery.add(userTimeStampBeforeRecommendation);
                    timestampPerQuery.add(userTimeStampAfterRecommendation);    
                
                    // we execute the queries if needed
                    if(this.executeQueries) {
                        Long timeStampBeforeExecution   = tr.getCurrentTimestamp();
                        q_tmp.execute(true);
                        Long timeStampAfterExecution    = tr.getCurrentTimestamp();
                
                        timestampPerQuery.add(timeStampBeforeExecution);
                        timestampPerQuery.add(timeStampAfterExecution);
                    }
                
                    userTimestampList.add(timestampPerQuery);
                }
            
                // add queries from user model
            
                tr.addQueryList(uq_tmp, userTimestampList, "USER");
            }
            

        }
        
        return tr;
    }
    
    /**
     * Checks if the provided query list contains some new discovery of discoveries.
     * @param arg_tr
     * @param arg_queryList
     * @return 
     */
    public boolean somethingNewDiscovered(TaskResolution arg_tr, List<Query> arg_queryList) {
        FocusZone focusZone  = arg_tr.getFocusZoneList().iterator().next();
        
        // check if at least one cell in arg_queryList is in the focus zone
        for(Query q_tmp : arg_queryList) {
            for(EAB_Cell c_tmp : q_tmp.getResult().getCellList().getCellCollection()) {
                //System.out.println("Searched cell: " + c_tmp);
                if(focusZone.getCellList().contains(c_tmp)) {
                    return true;
                }
            }
        }
        
        System.out.println(":S NOTHING NEW DISCOVERED :S");
        
        return false;

//        boolean somethingNewDiscovered  = false;
//        for(AbstractDiscovery ad_tmp : arg_tr.getTask().getTargetDiscoveryList()) {
//            //DiscoveryAsCellTopology dact    = (DiscoveryAsCellTopology)ad_tmp;
//            
//            CellList workSessionCells       = arg_tr.getWorkSession().getCellList();
//            
//            // verify whether the cell is present in a discovery or not
//            for(Query q_tmp : arg_queryList) {
//                QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
//                
//                // for each cell of the query
//                for(EAB_Cell c_tmp : qt_tmp.getResult().computeCellList().getCellCollection()) {
//                    
//                    somethingNewDiscovered  = ad_tmp.getCellList().contains(c_tmp) && !workSessionCells.contains(c_tmp);
//                    
//                    if(somethingNewDiscovered) {
//                        return somethingNewDiscovered;
//                    }
//                }
//                
//            }
//            
//        }
//        
//        return somethingNewDiscovered;
    }
    
    /**
     * The evaluator knows when it is time to stop the evaluation.
     * It stops when the SUT does not recommend anything more or when the recommendation
     * has not found anything new in the discoveries...
     * @param arg_tr
     * @param arg_queryList
     * @return 
     */
    public boolean endOfTaskEvaluation(TaskResolution arg_tr, List<Query> arg_queryList) {
        
        boolean emptyRecommendation = arg_queryList.isEmpty();
        boolean somethingNewDiscovered  = this.somethingNewDiscovered(arg_tr, arg_queryList);
        
        // stop the evaluation if recommendation is empty
        // or if there is no new under sun
        return (emptyRecommendation || !somethingNewDiscovered);
        
    }
    
    /**
     * 
     * @return 
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.benchmarkEngine;
    }

    /**
     * 
     * @param arg_benchmarkEngine 
     */
    public void setBenchmarkEngine(BenchmarkEngine arg_benchmarkEngine) {
        this.benchmarkEngine    = arg_benchmarkEngine;
    }

    /**
     *
     * @return
     */
    public Integer getNumberOfTasks() {
        return numberOfTasks;
    }

    /**
     *
     * @param numberOfTasks
     */
    public void setNumberOfTasks(Integer numberOfTasks) {
        this.numberOfTasks = numberOfTasks;
    }

    /**
     *
     * @return
     */
    public Integer getNumberOfChances() {
        return numberOfChances;
    }

    /**
     *
     * @param numberOfChances
     */
    public void setNumberOfChances(Integer numberOfChances) {
        this.numberOfChances = numberOfChances;
    }
    
    
    
}
