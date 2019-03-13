/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.util.List;

import mondrian.olap.Cell;
import mondrian.olap.Member;
import mondrian.olap.Position;
import mondrian.olap.Util;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.EAB_Cell;
import fr.univ_tours.li.mdjedaini.ideb.olap.result.Result;

/**
 *
 * @author mahfoud
 */
public class MondrianResultWriter implements I_ResultWriter {

    Result r;
    
    /**
     *
     */
    public MondrianResultWriter() {
    }
    
    /**
     * 
     * @param arg_result
     * @return 
     */
    public String writeResult(Result arg_result) {
        // record the result being written
        this.r  = arg_result;
        mondrian.olap.Result result = arg_result.getMondrianResult();
        
        StringBuilder html = new StringBuilder();
        
        List<Position> slicers = result.getSlicerAxis().getPositions();
        html.append("<table class='resulttable' cellspacing=1 border=0>");
        html.append(Util.nl);

        List<Position> columns = result.getAxes()[0].getPositions();
        
//        System.out.println("Positions sur les colonnes");
        for(Position p_tmp : columns) {
//            System.out.println("Position: " + p_tmp);
        }
        
        List<Position> rows = null;
        if (result.getAxes().length == 2) {
            rows = result.getAxes()[1].getPositions();
        }
        int columnWidth = columns.get(0).size();
        int rowWidth = 0;
        if (result.getAxes().length == 2) {
            rowWidth = result.getAxes()[1].getPositions().get(0).size();
        }
        for (int j = 0; j < columnWidth; j++) {
            html.append("<tr>");
            // if it has more than 1 dimension
            if (j == 0 && result.getAxes().length > 1) {
                // Print the top-left cell, and fill it with slicer members.
                html.append("<td nowrap class='slicer' rowspan='")
                    .append(columnWidth)
                    .append("' colspan='")
                    .append(rowWidth)
                    .append("'>");
                for (Position position : slicers) {
                    int k = 0;
                    for (Member member : position) {
                        if (k > 0) {
                            html.append("<br/>");
                        }
                        html.append(member.getName());
                        k++;
                    }
                }
                html.append("&nbsp;</td>").append(Util.nl);
            }

            // Print the column headings.
            for (int i = 0; i < columns.size(); i++) {
                Position position = columns.get(i);
                //Member member = columns[i].getMember(j);
                Member member = position.get(j);
                int width = 1;
                while ((i + 1) < columns.size() && columns.get(i + 1).get(j) == member) {
                    i++;
                    width++;
                }
                
                html.append("<td nowrap class='columnheading' colspan='")
                    .append(width).append("'>")
                    .append(member.getName()).append("</td>");
            }
            html.append("</tr>").append(Util.nl);
        }
        
        //if is two axes, show
        if (result.getAxes().length > 1) {
            for (int i = 0; i < rows.size(); i++) {
                html.append("<tr>");
                final Position row = rows.get(i);
                for (Member member : row) {
                    html.append("<td nowrap class='rowheading'>")
                        .append(member.getName())
                        .append("</td>");
                }
                for (int j = 0; j < columns.size(); j++) {
                    showCell(html, result.getCell(new int[] {j, i}));
                }
                html.append("</tr>");
            }
        } else {
            html.append("<tr>");
            for (int i = 0; i < columns.size(); i++) {
                showCell(html, result.getCell(new int[] {i}));
            }
            html.append("</tr>");
        }
        html.append("</table>");

        return html.toString();
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