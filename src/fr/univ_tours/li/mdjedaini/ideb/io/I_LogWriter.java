/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;


/**
 *
 * @author mahfoud
 */
public interface I_LogWriter {
    
    /**
     * Writes a log to a file of a given path.
     * @param arg_inFile
     * @param arg_outFilePath
     * @param arg_xslFilePath
     * @return 
     */
    public String writeLog(String arg_inFile, String arg_outFilePath, String arg_xslFilePath);
    
}
