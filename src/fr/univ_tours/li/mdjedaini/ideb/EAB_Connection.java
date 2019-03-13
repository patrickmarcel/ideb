package fr.univ_tours.li.mdjedaini.ideb;


import java.sql.DriverManager;

import mondrian.olap.Cube;

import org.olap4j.OlapConnection;

import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;


/**
 * todo CHANGER TOUT CA, PASSER A OLAP4J POUR RENDRE LE CODE + MODULAIRE...
 * @author mahfoud
 */
public class EAB_Connection {

    BenchmarkEngine be;
    
    //
    private java.sql.Connection jdbcConnection;
    private mondrian.olap.Connection mondrianConnection;
    private org.olap4j.OlapConnection olap4jConnection;    
    
    /**
     * 
     * @param arg_be 
     */
    public EAB_Connection(BenchmarkEngine arg_be) {
        this.be = arg_be;
    }
    
    /**
     * 
     */
    public void open() {
        Parameters params   = this.getBenchmarkEngine().getParameters();
        // ! we have to rebuild the connection string before connecting
        params.rebuildConnectionString();
        
        // open also the olap4j connection
        try {
            // on charge le driver necessaire pour jdbc
            Class.forName(params.driver);
            Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
            
            this.jdbcConnection     = DriverManager.getConnection(params.jdbcUrl, params.user, params.password);
            
            this.mondrianConnection = mondrian.olap.DriverManager.getConnection(params.getMondrianConnectionString(), null);

            //this.olap4jConnection   = this.jdbcConnection.unwrap(OlapConnection.class);
            
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
        
    }
    
    /**
     *
     * @return
     */
    public BenchmarkEngine getBenchmarkEngine() {
        return this.be;
    }
    
    /**
     *
     * @return the mondrian connection.
     */
    public mondrian.olap.Connection getMondrianConnection() {
        return this.mondrianConnection;
    }
    
     /**
      * Retrieves the Olap4j connection
      * @return 
      */
    public OlapConnection getOlap4jConnection() {
        return this.olap4jConnection;
    }

    /**
     * Retrieves the cube by its name.
     * 
     * @return 
     */
    public Cube getDefaultCube() {
        return this.getMondrianConnection().getSchema().getCubes()[0];
    }

    /**
     * Close the connection
     */
    public void close(){
        try {
            this.mondrianConnection.close();
            this.jdbcConnection.close();
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
        
    }
}
