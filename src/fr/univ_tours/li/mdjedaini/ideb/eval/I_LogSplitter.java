/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.eval;

import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 *
 * @author mahfoud
 */
public interface I_LogSplitter {
    
    /**
     * Splits a log into a list of logs.
     * @param arg_log
     * @return 
     */
    public List<Log> splitLog(Log arg_log);
    
}
