/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.params;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import fr.univ_tours.li.mdjedaini.ideb.Globals;

/**
 *
 * @author mahfoud
 */
public class ConnectionParameters {
    
    String provider;
    
    String jdbc;
    
    String catalog;
    
    String driver;
    
    String user;
    
    String password;
    
    String connectionString;
    
    /*
    
    */

    /**
     *
     * @param arg_filePath
     * @return
     */

    public static ConnectionParameters loadParametersFromPropertiesFile(String arg_filePath) {
        
        ConnectionParameters cp = new ConnectionParameters();
        
        try {
            
            Properties p = new Properties();
            FileInputStream fs = new FileInputStream(arg_filePath);
            p.load(fs);

            cp.provider = p.getProperty("provider");
            cp.jdbc     = p.getProperty("jdbc");
            cp.catalog  = p.getProperty("schema");
            cp.driver   = p.getProperty("driver");
            cp.user     = p.getProperty("user");
            cp.password = p.getProperty("password");
            
            // Set the connection string for future reference
            cp.connectionString = "Provider=" + cp.provider
                    + ";Jdbc=" + cp.jdbc
                    //+ ";Catalog=" + cp.catalog
                    + ";Catalog=" + Globals.schemaFilePath
                    + ";JdbcDrivers=" + cp.driver
                    + ";JdbcUser=" + cp.user
                    + ";JdbcPassword=" + cp.password;
            
            // close the file stream
            fs.close();
            
        } catch (IOException e) {
            // handle the error
            System.err.println("OLAP connection failed. Please check olapConnection.properties exists or given information are correct.");
            
        }
        
        // return the ConnectionParameters instance
        return cp;
    }
            
}
