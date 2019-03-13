/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author Mahfoud
 */
public class Session {
    
    //

    /**
     *
     */
    public Integer sid;

    /**
     *
     */
    public User user;
    
    /**
     *
     */
    public ArrayList<Query> queryList;

    /**
     *
     */
    public ArrayList<Long> timeBeforeQueryExecutionList;    // in milliseconds

    /**
     *
     */
    public ArrayList<Long> timeAfterQueryExecutionList;     // in milliseconds
    
    /**
     *
     */
    public Map<String, String> metadata;
    
    /**
     * 
     */
    public Session() {
        this.queryList                      = new ArrayList<>();
        this.timeBeforeQueryExecutionList   = new ArrayList<>();
        this.timeAfterQueryExecutionList    = new ArrayList<>();
        this.metadata                       = new HashMap<>();
    }
    
    /**
     * 
     * @param arg_session 
     */
    public Session(Session arg_session) {
        this();
        this.user   = arg_session.getUser();
        this.addQueryList(arg_session.getQueryList());
    }
    
    /**
     * 
     * @param arg_sid 
     */
    public Session(Integer arg_sid) {
        this.queryList                      = new ArrayList<>();
        this.timeBeforeQueryExecutionList   = new ArrayList<>();
        this.timeAfterQueryExecutionList    = new ArrayList<>();
        this.metadata                       = new HashMap<>();
    }
    
    /**
     * 
     * @param arg_qid 
     */
    public void remove(Integer arg_qid) {
        for(Query q : this.queryList) {
            if(q.getQid().equals(arg_qid)) {
                this.queryList.remove(q);
                return;
            }
        }
    }

    /**
     * 
     * @param arg_q 
     */
    public void remove(Query arg_q) {
        queryList.remove(arg_q);
    }
    
    /**
     * 
     * @return 
     */
    public Integer getSid() {
        return sid;
    }

    /**
     * 
     * @param sid 
     */
    public void setSid(Integer sid) {
        this.sid = sid;
    }

    /**
     * 
     * @return 
     */
    public User getUser() {
        return this.user;
    }
    
    /**
     * 
     * @param arg_user 
     */
    public void setUser(User arg_user) {
        this.user   = arg_user;
    }
    
    /**
     * Registers metadata with the session.
     * @param arg_key
     * @param arg_value 
     */
    public void addMetaData(String arg_key, String arg_value) {
        this.metadata.put(arg_key, arg_value);
    }
    
    /**
     * Adds a query to the session.
     * Query is added as is. Conversion is performed after by a specific function.
     * @param arg_q 
     */
    public void addQuery(Query arg_q) {
        
//        ParallelQueryLoader pql = new ParallelQueryLoader(this, arg_q);
//        pql.start();
        
        arg_q.setPosition(this.getNumberOfQueries());
        this.queryList.add(arg_q);
        
//        try { pql.join(); } catch(Exception arg_e) { arg_e.printStackTrace(); }
        
    }
    
    /**
     * 
     * @param arg_q
     * @param arg_tsBefore
     * @param arg_tsAfter 
     */
    public void addQuery(Query arg_q, Long arg_tsBefore, Long arg_tsAfter) {
        this.addQuery(arg_q);
        this.timeBeforeQueryExecutionList.add(arg_tsBefore);
        this.timeAfterQueryExecutionList.add(arg_tsAfter);
    }
    
    /**
     * 
     * @param arg_qlist 
     */
    public void addQueryList(List<Query> arg_qlist) {
        for(Query q_tmp : arg_qlist) {
            this.addQuery(q_tmp);
        }
    }
    
    /**
     * 
     * @param arg_key
     * @return 
     */
    public String getMetadata(String arg_key) {
        return this.metadata.get(arg_key);
    }
    
    /**
     * 
     * @return the size of the session, i.e. the number of queries
     */
    public Integer getNumberOfQueries() {
        return this.queryList.size();
    }
    
    /**
     * 
     * @param arg_qid
     * @return 
     */
    public Query getQueryById(Integer arg_qid) {
        for(Query q : this.queryList) {
            if(q.getQid().equals(arg_qid)) {
                return q;
            }
        }
        return null;
    }
    
    /**
     * 
     * @param arg_position
     * @return 
     */
    public Query getQueryByPosition(Integer arg_position) {
        return this.getQueryList().get(arg_position);
    }
    
    /**
     * 
     * @return 
     */
    public ArrayList<Query> getQueryList() {
        return this.queryList;
    }
    
    /**
     * 
     * @return 
     */
    public Query getFirstQuery() {
        return this.queryList.get(0);
    }
    
    /**
     * 
     * @return 
     */
    public Query getLastQuery() {
        return this.queryList.get(this.getNumberOfQueries() - 1);
    }
    
    /**
     * 
     * @return 
     */
    public Query pickRandomQuery() {
        Random rg   = new Random();
        //System.out.println("There are " + this.queryList.size() + " queries...") ;
        return this.queryList.get(rg.nextInt(this.queryList.size()));
    }
    
    /**
     * 
     * @param arg_nb
     * @return 
     */
    public HashSet<Query> pickRandomQuery(Integer arg_nb) {
        HashSet<Query> result   = new HashSet<>();
        
        for(int i = 0; i < arg_nb; i++) {
            result.add(this.pickRandomQuery());
        }
        
        return result;
    }
    
    /**
     * Executes the current session queries.
     * @param arg_store true to store the results in memory, false otherwise...
     * @return 
     */
    public List<Result> execute(Boolean arg_store) {
        List<Result> res    = new ArrayList<>();
        
        for(Query q_tmp : this.getQueryList()) {
            res.add(q_tmp.execute(arg_store));
        }

        return res;
    }
    
    /**
     * Prepares the session so it is fully configured.
     * Computes the timestamps if they have not been computed yet.
     */
    public void prepare() {
        
        if(!this.timeAfterQueryExecutionList.isEmpty()) {
            return;
        }
        
        for(Query q_tmp : this.getQueryList()) {
            
            Long tsBefore   = System.currentTimeMillis();
            q_tmp.execute(Boolean.FALSE);
            Long tsAfter    = System.currentTimeMillis();
            
            this.timeBeforeQueryExecutionList.add(tsBefore);
            this.timeAfterQueryExecutionList.add(tsAfter);
            
//            Random r        = new Random();
//            Double ttw_d    = r.nextDouble();
//            Double ttw      = ttw_d * q_tmp.getResult().getNumberOfCells();
//            ttw             = Math.max(ttw, 15000); // max consideration time of 15 seconds
//            System.out.println("Long genere: " + ttw_d.longValue());
//            System.out.println("Nombre de cellules: " + q_tmp.getResult().getNumberOfCells().longValue());
//            System.out.println("Repos force pour: " + ttw.longValue() + " millisecondes...");
//            try {
//                Thread.sleep(ttw.longValue());
//            } catch (Exception arg_e) {
//                System.err.println("Thread cannot wait!");
//                arg_e.printStackTrace();
//            }
            
        }

        return;
    }
    
    /**
     * Retrieves the cell list of a given session.
     * Executes each query of the session and retrieves its cell list, then 
     * compiles everything in a single collection.
     * @return 
     */
    public CellList getCellList() {
        CellList res    = new CellList();
        
        for(Query q_tmp : this.getQueryList()) {
            //QueryTriplet qt_tmp = (QueryTriplet)q_tmp;
            res.addCellCollection(q_tmp.execute(Boolean.FALSE).getCellList().getCellCollection());
        }
        
        return res;
    }
    
    /**
     * Clears the stored query results
     */
    public void clear() {
        for(Query q_tmp : this.getQueryList()) {
            q_tmp.clear();
        }
    }
    
    /**
     * Converts all the queries to the QueryTriplet format.
     */
    public void convertQueries() {
        for(int i = 0; i < this.getNumberOfQueries(); i++) {
            Query q_tmp = this.getQueryByPosition(i);
            QueryConverter qc   = new QueryConverter(q_tmp.getCube().getBencharkEngine());
            QueryTriplet qt     = qc.toQueryTriplet(q_tmp);
            qt.setPosition(i);
            this.getQueryList().set(i, qt);
        }
        
//        List<Thread> threadList = new ArrayList<>();
//        
//        for(int i = 0; i < this.getNumberOfQueries(); i++) {
//            Query q_tmp = this.getQueryByPosition(i);
//            ParallelQueryConverter pql = new ParallelQueryConverter(this, q_tmp);
//            threadList.add(pql);
//            pql.start();
//        }
//        
//        // *** waiting for all the converter threads to finish...
//        try {
//            for(Thread t_tmp : threadList) {
//                t_tmp.join();
//            }
//            System.out.println("Threads have finished...");
//        } catch(Exception arg_e) {
//            arg_e.printStackTrace();
//        }
//        // *** converter threads have finished...
        
    }
    
    /**
     * todo il faut optimiser cette fonction
     * le calcul des cellules d'un resultat ne doit etre fait que une seule fois
     * il faut gerer cela peut etre avec une memoire cache...
     * @return 
     */
    public CellList computeCellList() {
        CellList result = new CellList();
        
        for(Query q_tmp : this.getQueryList()) {
            //QueryTriplet qt = (QueryTriplet)q_tmp;
            
            for(EAB_Cell c_tmp : q_tmp.getResult().computeCellList().getCellCollection()) {
                result.addCell(c_tmp);
            }
            
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    public String getSummary() {
        String summary  = "";
        
        summary = "Session #" + this.sid;
        summary += System.getProperty("line.separator");
        summary += "#queries: " + this.getNumberOfQueries();
        summary += System.getProperty("line.separator");
        summary += "------------------------------------";
        summary += System.getProperty("line.separator");
        
        int i = 0;
//        for(Query q : this.getQueryList()) {
//            summary += "TS Before: " + this.timeBeforeQueryExecutionList.get(i);
//            summary += System.getProperty("line.separator");
//            summary += "TS After: " + this.timeAfterQueryExecutionList.get(i);
//            summary += System.getProperty("line.separator");
//            summary += q.toString();
//            summary += System.getProperty("line.separator");
//            i++;
//        }
        
        summary += "End Of Session #" + this.sid;
        
        return summary;
    }
 
    /**
     * Inner class for parallelizing the loading of sessions
     */
    public class ParallelQueryConverter extends Thread {

        Session arg_s;
        Query arg_q;
        
        /**
         * 
         * @param arg_s
         * @param arg_q 
         */
        public ParallelQueryConverter(Session arg_s, Query arg_q) {
            this.arg_s  = arg_s;
            this.arg_q  = arg_q;
        }
               
        /**
         * 
         */
        @Override
        public void run() {
            Integer i           = arg_q.getPosition();
            QueryConverter qc   = new QueryConverter(arg_q.getCube().getBencharkEngine());
            QueryTriplet qt     = qc.toQueryTriplet(arg_q);
            qt.setPosition(i);
            arg_s.getQueryList().set(i, qt);
        }
    }
    
    
}
