/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb;


/**
 *
 * @author Elisa
 */
public class Globals {
    
    // 

    /**
     *
     */
    public enum FragmentType {

        /**
         *
         */
        MEASURE_FRAGMENT,

        /**
         *
         */
        PROJECTION_FRAGMENT,

        /**
         *
         */
        SELECTION_FRAGMENT
    }
    
    /**
     *
     */
    public enum NeighborhoodType {

        /**
         *
         */
        SAME_PARENT_NEIGHBORHOOD, 
    }

    /**
     * 
     */
    public static BenchmarkEngine benchmarkEngine;
    
    /**
     *
     */
    public static String connectionFilePath = "eab_connection.properties";

    /**
     *
     */
    public static String schemaFilePath     = "schema.xml";

    /**
     *
     */
    public static String logFilePath        = "Workload.xml";
    
}
