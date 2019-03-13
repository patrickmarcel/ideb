package fr.univ_tours.li.mdjedaini.ideb.ext.falseto;



import fr.univ_tours.li.jaligon.falseto.QueryStructure.Qfset;
import fr.univ_tours.li.jaligon.falseto.QueryStructure.QuerySession;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mahfoud
 */
public class FalsetoSessionConverter {

    // 
    FalsetoQueryConverter fqc;

    /**
     * 
     * @param arg_be 
     */
    public FalsetoSessionConverter(BenchmarkEngine arg_be) {
        this.fqc    = new FalsetoQueryConverter(arg_be);
    }
    
    /**
     * 
     * @param arg_fqc 
     */
    public FalsetoSessionConverter(FalsetoQueryConverter arg_fqc) {
        this.fqc    = arg_fqc;
    }
    
    /**
     * 
     * @param arg_session
     * @return 
     */
    public QuerySession convert(Session arg_session) {
        
        // l'identifiant de Julien doit etre une string
        // QuerySession result = new QuerySession(arg_session.getSid().toString());
        QuerySession result = new QuerySession("_0");
        
        QueryConverter qc   = new QueryConverter(this.fqc.be);
        
        for(Query q_tmp : arg_session.getQueryList()) {
            try {
                QueryTriplet qt = qc.toQueryTriplet(q_tmp);
                result.add(this.fqc.convertQuery(qt));
            } catch(Exception arg_e) {
                arg_e.printStackTrace();
            }
            
        }
        
        return result;
    }
    
    /**
     * 
     * @param arg_session
     * @return 
     */
    public Session convert(QuerySession arg_session) {
       
        Session result = new Session();
        
        for(Qfset q_tmp : arg_session.getQueries()) {
            
            result.addQuery(this.fqc.convertQfset(q_tmp));
        }
        
        return result;
    }
    
}
