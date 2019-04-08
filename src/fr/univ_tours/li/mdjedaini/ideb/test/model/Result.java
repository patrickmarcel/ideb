package fr.univ_tours.li.mdjedaini.ideb.test.model;

import org.olap4j.*;
import org.olap4j.metadata.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Result {
    private Double[][] values;
    private String[][] colNames;
    private String[][] rowNames;
    private Integer[][] colSpans;
    private Integer[][] rowSpans;

    public Result(Double[][] values, String[][] columnNames, String[][] rowNames, Integer[][] columnSpans, Integer[][] rowSpans) {
        this.values = values;
        this.colNames = columnNames;
        this.rowNames = rowNames;
        this.colSpans = columnSpans;
        this.rowSpans = rowSpans;
    }

    public Double[][] getValues() {
        return values;
    }

    public void setValues(Double[][] values) {
        this.values = values;
    }

    public String[][] getColNames() {
        return colNames;
    }

    public void setColNames(String[][] colNames) {
        this.colNames = colNames;
    }

    public String[][] getRowNames() {
        return rowNames;
    }

    public void setRowNames(String[][] rowNames) {
        this.rowNames = rowNames;
    }

    public Integer[][] getColSpans() {
        return colSpans;
    }

    public void setColSpans(Integer[][] colSpans) {
        this.colSpans = colSpans;
    }

    public Integer[][] getRowSpans() {
        return rowSpans;
    }

    public void setRowSpans(Integer[][] rowSpans) {
        this.rowSpans = rowSpans;
    }

    public static Result buildResult(CellSet cellSet) {
        if (cellSet.getAxes().size() != 2) {
            throw new IndexOutOfBoundsException("The result is " + cellSet.getAxes().size() + " dimensional, and cannot be parse into a 2D array");
        }

        // Gather row names
        CellSetAxis rowAxis = cellSet.getAxes().get(Axis.ROWS.axisOrdinal());
        String[][] rowNamesRaw = new String[rowAxis.getPositionCount()][];
        int i = 0;
        for (Position position : rowAxis) {
            rowNamesRaw[i] = new String[position.getMembers().size()];
            int j = 0;
            for (Member member : position.getMembers()) {
                rowNamesRaw[i][j] = member.getName();
                j++;
            }
            i++;
        }

        // Build row spans
        String[][] rowNames = new String[rowNamesRaw.length][];
        Integer[][] rowSpans = new Integer[rowNamesRaw.length][];
        if (rowNamesRaw.length == 0) {
            System.out.println("What the what!");
        }
        int[][] rowIndexes = new int[rowNamesRaw[0].length][];
        for (i = 0; i < rowIndexes.length; i++) {
            rowIndexes[i] = new int[]{0, i};
        }

        rowNames[0] = rowNamesRaw[0];
        rowSpans[0] = new Integer[rowNamesRaw[0].length];
        Arrays.fill(rowSpans[0], 1);

        for (i = 1; i < rowNamesRaw.length; i++) {
            List<String> rowNameList = new ArrayList<>();
            List<Integer> rowSpanList = new ArrayList<>();
            for (int j = 0; j < rowNamesRaw[i].length; j++) {
                if (rowNamesRaw[i][j].equals(rowNames[rowIndexes[j][0]][rowIndexes[j][1]]) && j != rowNamesRaw[i].length -1) {
                    rowSpans[rowIndexes[j][0]][rowIndexes[j][1]]++;
                } else {
                    rowNameList.add(rowNamesRaw[i][j]);
                    rowSpanList.add(1);
                    rowIndexes[j][0] = i;
                    rowIndexes[j][1] = rowNameList.size() - 1;
                }
            }
            rowNames[i] = rowNameList.toArray(new String[0]);
            rowSpans[i] = rowSpanList.toArray(new Integer[0]);
        }

        // Gather column names
        CellSetAxis colAxis = cellSet.getAxes().get(Axis.COLUMNS.axisOrdinal());
        String[][] colNamesRaw = new String[colAxis.getPositionCount()][];
        i = 0;
        for (Position position : colAxis) {
            colNamesRaw[i] = new String[position.getMembers().size()];
            int j = 0;
            for (Member member : position.getMembers()) {
                colNamesRaw[i][j] = member.getName();
                j++;
            }
            i++;
        }

        // Build column spans
        String[][] colNames = new String[colNamesRaw[0].length][];
        Integer[][] colSpans = new Integer[colNamesRaw[0].length][];
        for (int j = 0; j < colNamesRaw[0].length; j++) {
            List<String> rowNameList = new ArrayList<>();
            rowNameList.add(colNamesRaw[0][j]);
            List<Integer> rowSpanList = new ArrayList<>();
            int span = 1;
            for (i = 1; i < colNamesRaw.length; i++) {
                if (rowNameList.get(rowNameList.size() - 1).equals(colNamesRaw[i][j])) {
                    span++;
                } else {
                    rowNameList.add(colNamesRaw[i][j]);
                    rowSpanList.add(span);
                    span = 1;
                }
            }
            rowSpanList.add(span);
            colNames[j] = rowNameList.toArray(new String[0]);
            colSpans[j] = rowSpanList.toArray(new Integer[0]);
        }

        Double[][] values = new Double[rowNames.length][colNames[colNames.length - 1].length];
        for (int row = 0; row < values.length; row++) {
            for (int col = 0; col < values[row].length; col++) {
                Cell cell = cellSet.getCell(Arrays.asList(col, row));
                if (cell.isError() || cell.isNull() || cell.isEmpty()) {
                    values[row][col] = null;
                } else {
                    String val = cell.getValue().toString();
                    values[row][col] = Double.valueOf(val);
                }
            }
        }

        return new Result(values, colNames, rowNames, colSpans, rowSpans);
    }
}
