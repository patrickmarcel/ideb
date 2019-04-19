package utils;

import errors.MatrixException;
import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;

import java.util.Arrays;

public class CellSetUtil {

    /**
     * Retrieve cell values from a CellSet into a Double matrix of the same size. Null, Empty or Error cell
     * are set to null. If the cell set is null, the method also return null.
     * Warnings: The cell set must have 2 axis, otherwise it will throw an MatrixException
     *
     * @param cellSet The cell set to extract
     * @return The Double matrix extracted, or null is cellSet was null
     * @throws MatrixException The cell set must be two dimensional
     */
    public static Double[][] fromCellSetToDoubleMatrix(CellSet cellSet) throws MatrixException {
        if (cellSet == null) {
            return null;
        } else if (cellSet.getAxes().size() != 2) {
            throw new MatrixException("CellSet must be 2 dimensional (CellSet provided is " + cellSet.getAxes().size() + "d");
        }
        int rowCount = cellSet.getAxes().get(Axis.ROWS.axisOrdinal()).getPositionCount();
        int colCount = cellSet.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositionCount();
        Double[][] res = new Double[rowCount][colCount];
        for (int m = 0; m < rowCount; m++) {
            for (int n = 0; n < colCount; n++) {
                Cell cell = cellSet.getCell(Arrays.asList(n, m));
                if (cell != null && !cell.isError() && !cell.isNull() && !cell.isEmpty()) {
                    res[m][n] = Double.valueOf(cell.getFormattedValue());
                } else {
                    res[m][n] = null;
                }
            }
        }
        return res;
    }
}
