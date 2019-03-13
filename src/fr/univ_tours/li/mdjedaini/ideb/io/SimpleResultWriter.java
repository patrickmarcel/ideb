/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.io.PrintWriter;
import java.io.StringWriter;

import mondrian.olap.Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;

/**
 *
 * @author mahfoud
 */
public class SimpleResultWriter implements I_ResultWriter {

    Result r;
    
    /**
     *
     */
    public SimpleResultWriter() {
    }
    
    /**
     * 
     * @param arg_result
     * @return 
     */
    public String writeResult(Result arg_result) {
        String result   = "";
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        // on affiche le resultat grace au rectangular formatter
        //new RectangularCellSetFormatter(true).format(arg_result, pw);
//            new TraditionalCellSetFormatter().format(result, pw);
        
        result  = sw.toString();
        return result;
    }

    /**
     * 
     * @param out
     * @param cell 
     */
    private void showCell(StringBuilder out, Cell cell) {
        //System.out.println("Cell to show: " + cell);
        //System.out.println("Cells in the result:");
        //System.out.println(this.r.cellMapping.keySet());
        EAB_Cell eab_c  = r.cellMapping.get(cell.getCoordinateList());
        out.append("<td class='cell-" + eab_c.cellId + "'>");
        out.append(cell.getFormattedValue());
        out.append("</td>");
    }

}