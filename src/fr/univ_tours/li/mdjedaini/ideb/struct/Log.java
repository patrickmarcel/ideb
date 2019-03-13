/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author Mahfoud
 */
public class Log {

    //
    private Map<Integer, Query> queryList;
    private Map<Integer, Session> sessionList;
    private Map<Integer, Integer> qidToSid;
    Map<Query, Result> resultByQuery;
    
    private CellList cellList;

    Integer sid = 0;
    Integer qid = 0;
    Integer cid = 0;
    
    /**
     * 
     */
    public Log() {
        this.queryList      = new HashMap<>();
        this.sessionList    = new HashMap<>();
        this.qidToSid       = new HashMap<>();
        this.resultByQuery  = new HashMap<>();
        
        //this.cellList       = new CellList();
        
        this.sid    = 0;
        this.qid    = 0;
        this.cid    = 0;
    }

    /**
     * Copy constructor.
     * Copy only the list of sessions from the log in argument.
     * Queries are not copied, but referred to.
     * @param arg_log 
     */
    public Log(Log arg_log) {
        // call the other constructor
        this();
                
        for(Session s_tmp : arg_log.getSessionList()) {
            this.addSession(s_tmp);
        }
    }
    
    /**
     * 
     * @return 
     */
    public Collection<Session> getSessionList() {
        return this.sessionList.values();
    }

    /**
     * 
     * @param arg_session 
     */
    public void addSession(Session arg_session) {
        
        // convert queries of the session before adding it to the log
        arg_session.convertQueries();
        
        arg_session.setSid(this.sid);
        this.sid++;
        
        // 
        this.sessionList.put(arg_session.getSid(), arg_session);
        
        // add each query of the session
        for(Query q : arg_session.getQueryList()) {
            q.setQid(this.qid);
            this.qid++;
        
            this.queryList.put(q.getQid(), q);
            this.qidToSid.put(q.getQid(), arg_session.getSid());
            //this.addCellsForQuery(q);
        }
        
        
    }
    
    /**
     * 
     * @param arg_user
     * @return 
     */
    public List<Session> extractUserSessionList(User arg_user) {
        List<Session> result    = new ArrayList();
        
        for(Session s_tmp : this.getSessionList()) {
            if(s_tmp.getUser().equals(arg_user)) {
                result.add(s_tmp);
            }
        }
        
        return result;
    }
    
    /**
     * TODO reecrire cette fonction
     * @param arg_q
     * @param arg_sid 
     */
    public void addQuery(Query arg_q, Integer arg_sid) {
        
        if(!this.sessionList.containsKey(arg_sid)) {
            System.out.println("Fais gaffe, le session ID n n existe pas...");
            System.out.println("Mais faut que tu reecrives cette fonction hein...");
        }
        
        arg_q.setQid(this.qid);
        this.qid++;
        
        this.queryList.put(arg_q.getQid(), arg_q);
        this.qidToSid.put(arg_q.getQid(), arg_sid);
        
        //this.addCellsForQuery(arg_q);
        
        this.sessionList.get(arg_sid).addQuery(arg_q);
        
    }

    /**
     * 
     * @param arg_sid
     * @return 
     */
    public Session getSessionById(Integer arg_sid) {
        // @todo faire des verifications ici pour s'assurer que le sid existe bien
        return this.sessionList.get(arg_sid);
    }
    
    /**
     * 
     * @param arg_qid
     * @return 
     */
    public Session getSessionByQueryId(Integer arg_qid) {
        // @todo faire des verifications ici pour s'assurer que le sid existe bien
        return this.getSessionById(this.qidToSid.get(arg_qid));
    }
    
    /**
     * 
     * @param arg_qid
     * @return 
     */
    public Query getQueryById(Integer arg_qid) {
        return this.getSessionByQueryId(arg_qid).getQueryById(arg_qid);
    }
    
    /**
     * Executes the queries in the log.
     * 
     * @param arg_store
     * @return TRUE to store the results inside queries objects, FALSE otherwise.
     */
    public List<Result> execute(Boolean arg_store) {
        List<Result> result = new ArrayList<>();
        
        for(Query q_tmp : this.getQueryList()) {
            result.add(q_tmp.execute(arg_store));
        }
        
        return result;
    }
    
    /**
     * Computes and returns the list of cells in the log...
     * @return 
     */
    public CellList computeCellList() {
        
        CellList result = new CellList();
        
        for(Query q_tmp : this.getQueryList()) {
            QueryTriplet qt = (QueryTriplet)q_tmp;
            
            for(EAB_Cell c_tmp : qt.getResult().computeCellList().getCellCollection()) {
                result.addCell(c_tmp);
            }
        }
        
        return result;
        
    }
    
    /**
     * 
     * @param arg_qid
     * @return 
     */
    public CellList getCellListByQueryId(Integer arg_qid) {
        CellList result = new CellList();
        
        for(EAB_Cell c_tmp : this.cellList.getCellCollection()) {
            if(c_tmp.getResult().getQuery().getQid().equals(arg_qid)) {
                result.addCell(c_tmp);
            }
        }
        
        return result;
    }

    /**
     * Executes the queries in the log.
     * 
     * @return TRUE to store the results inside queries objects, FALSE otherwise.
     */
    public CellList getCellList() {
        CellList result = new CellList();
        
        for(Query q_tmp : this.getQueryList()) {
            for(EAB_Cell c_tmp : q_tmp.getResult().getCellList().getCellCollection()) {
                result.addCell(c_tmp);
            }
        }
        
        return result;
    }

    /**
     * Picks a random cell from the log.
     * It first picks a random query, and then pick a random cell from this query.
     * @return 
     */
    public EAB_Cell pickRandomCell() {
        EAB_Cell result;
        
        Query q_tmp = this.pickRandomQueryList(1).get(0);
        Result r    = q_tmp.execute(Boolean.TRUE);
        
        // we redo a draw is the query result has no cell(s)
        if(r.getNumberOfCells() < 0) {
            q_tmp = this.pickRandomQueryList(1).get(0);
            r    = q_tmp.execute(Boolean.TRUE);
        }
        
        List<EAB_Cell> cells    = new ArrayList<>(r.getCellList().getCellCollection());
        Collections.shuffle(cells);

        result  = cells.get(0);
        
        return result;
    }
    
    /**
     * Picks one random session from the log.
     * @return 
     */
    public Session pickRandomSession() {
        
        if(this.sessionList.size() > 0) {
            Random rg       = new Random();
            //System.out.println("Nombre de sessions: " + this.sessionList.size());
            
            Integer r_int   = rg.nextInt(this.sessionList.size());
            return this.sessionList.get(r_int);
        }
        
        return null;
    }
    
    /**
     * Picks a random number of sessions from the log
     * @param arg_nb
     * @return 
     */
    public HashSet<Session> pickRandomSession(Integer arg_nb) {
        HashSet<Session> result   = new HashSet<Session>();
        
        for(int i = 0; i < arg_nb; i++) {
            result.add(this.pickRandomSession());
        }
        
        return result;
    }
    
    /**
     * Picks a random number of queries from the log
     * @param arg_nb
     * @return 
     */
    public ArrayList<Query> pickRandomQueryList(Integer arg_nb) {
        ArrayList<Query> result     = new ArrayList<>();

        if(this.getQueryList().size() > 0) {
            for(int i = 0; i < arg_nb; i++) {
                Random rg       = new Random();
                Integer r_int   = rg.nextInt(this.sessionList.size());
                result.add(this.getQueryList().get(r_int));
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public Integer getNumberOfSessions() {
        return this.sessionList.size();
    }
    
    /**
     * 
     * @return 
     */
    public Integer getNumberOfQueries() {
        Integer result  = 0;
        for(Session s_tmp : this.getSessionList()) {
            result  += s_tmp.getNumberOfQueries();
        }
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public ArrayList<Query> getQueryList() {
        ArrayList<Query> result = new ArrayList<Query>();
        for(Session s : this.getSessionList()) {
            result.addAll(s.getQueryList());
        }
        return result;
    }
    
    /**
     * 
     * @param arg_qid 
     */
    public void removeQueryById(Integer arg_qid) {
        this.getSessionByQueryId(arg_qid).remove(arg_qid);
        this.qidToSid.remove(arg_qid);
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String summary  = "";

        summary += this.logSummary();
        
        summary += "Log details:";
        for(Session s_tmp : this.getSessionList()) {
            summary += s_tmp.getSummary();
            summary += "---";
        }
        
        return summary;
    }
    
    /**
     * 
     * @return 
     */
    public String logSummary() {
        String summary  = "";
        
        summary += "Log summary:" + System.lineSeparator();
        summary += "There are " + this.getNumberOfSessions() + " sessions..." + System.lineSeparator();
        summary += "There are " + this.getNumberOfQueries() + " queries..." + System.lineSeparator();
        //summary += "There are " + this.getCellList().nbOfCells() + " cells...";
        //summary += 
        
        return summary;
    }
    
    /**
     * 
     * @return 
     */
    public String querySizeSummary() {
        String summary  = "";
        
        for(Session s : this.getSessionList()) {
            for(Query q : s.queryList) {
                QueryTriplet q_tmp  = (QueryTriplet)q;
                summary += q.toString();
            }
        }
        
        return summary;
    }
    
    /**
     * Merges a log with another one.
     * This function is useful to merge the current log with a log generated using a SUT.
     * @param arg_log
     * @return 
     */
    public Log merge(Log arg_log) {
        
        for(Session s_tmp : arg_log.getSessionList()) {
            this.addSession(s_tmp);
        }
        
        return this;
    }
    
}
