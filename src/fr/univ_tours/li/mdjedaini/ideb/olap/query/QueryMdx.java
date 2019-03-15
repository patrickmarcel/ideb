package fr.univ_tours.li.mdjedaini.ideb.olap.query;

import mondrian.olap.Axis;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.EAB_Connection;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;

/**
 * This class represents a formal model of an OLAP query.
 * It is composed of three components. The measure, the group by set (GBS) and
 * the selection predicates...
 * @author mahfoud
 */
public class QueryMdx extends Query implements java.io.Serializable {

    //
    String mdx;
    
    /**
     * 
     * @param arg_cube
     * @param arg_mdx 
     */
    public QueryMdx(EAB_Cube arg_cube, String arg_mdx) {
        super(arg_cube);
        this.cube   = arg_cube;
        this.mdx    = arg_mdx;
    }
    
    /**
     * 
     * @param arg_store
     * @return 
     */
    @Override
    public Result execute(Boolean arg_store) {
        
        BenchmarkEngine be          = this.getCube().getBencharkEngine();
        EAB_Connection connection   = be.getConnection();
        connection.open();
        mondrian.olap.Connection mc = connection.getMondrianConnection();
        
        mondrian.olap.Query mq      = mc.parseQuery(this.mdx);
        mondrian.olap.Result r      = mc.execute(mq);
        
        //System.out.println("RESULT MONDRIAN: "+ r);
        
        
        connection.close();
        
        Result  res = new Result(this, r);
        //System.out.println("La requete a " + r.getAxes().length + " axes...");
        
        if(r.getAxes().length==1){
        	// if only one axis, it is the on column axis, where only measures can appear
        	// ie no "on rows" means only one cell (projection fragment is empty)
        	// we just generate one cell
        	 Axis columnAxis = r.getAxes()[0];
             
             Axis slicerAxis = r.getSlicerAxis();
             
                     int[] position  = {0};
                     
                     mondrian.olap.Cell c_tmp    = r.getCell(position);
                     
                     if(c_tmp == null ) {
                         Integer m = 1;
                     }
                     
                     EAB_Cell eab_c              = new EAB_Cell(res, c_tmp);
                     
                     //System.out.println("creating Cell: "+ eab_c.toString());
                     
                     res.addCell(eab_c);
                 
        }
        else{
        Axis columnAxis = r.getAxes()[0];
        Axis rowAxis    = r.getAxes()[1];
        
        Axis slicerAxis = r.getSlicerAxis();
        
        for(int i = 0; i < columnAxis.getPositions().size(); i++) {
            for(int j = 0; j < rowAxis.getPositions().size(); j++) {
                int[] position  = {i, j};
                
                mondrian.olap.Cell c_tmp    = r.getCell(position);
                
                if(c_tmp == null ) {
                    Integer m = 1;
                }
                
                EAB_Cell eab_c              = new EAB_Cell(res, c_tmp);
                
                //System.out.println("creating Cell: "+ eab_c.toString());
                
                res.addCell(eab_c);
            }
        }
        }
        
        // we store the result if ok...
        if(arg_store) {
            this.result = res;
        }
        
        // Closes the query and releases resources
        mq.close();
        
        //System.out.println("RESULT: "+ res.toString());
        
        return res;
    }

    @Override
    public String toString() {
        return this.mdx;
    }
    
}
