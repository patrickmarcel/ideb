package olap;

import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSetAxis;

import java.util.Arrays;
import java.util.List;

/**
 * <p>This class act like a wrapper for the {@link org.olap4j.CellSet CellSet} interface. The main purpose is to store
 * the cell set values into a <code>Double[][]</code> matrix for a simpler accessing</p>
 * <p><b><i>"But why just not extends/implement CellSet ?"</i></b></p>
 * <p>When executing an MDX query, a {@link org.olap4j.CellSet CellSet} implementation is retrieved. The implementation
 * depends of which driver was used for the connection. Mondrian for example return an <code>MondrianOlap4jCellSet</code>
 * which is a private class ! That means we can't extends it (curse you Mondrian !).</p>
 * <p>This class is also named CellSet, that may be stupid. We could change it if we find a better name</p>
 */
public class CellSet {
    /**
     * The {@link org.olap4j.CellSet CellSet} object to wrap
     */
    private org.olap4j.CellSet cellSet;

    private int nbOfRows;
    private int nbOfColumns;
    private int nbOfCells;

    public CellSet(org.olap4j.CellSet cellSet) {
        //TODO: check if cellSet is 2D, else throw an exception
        this.cellSet = cellSet;
        nbOfRows = cellSet.getAxes().get(Axis.ROWS.axisOrdinal()).getPositionCount();
        nbOfColumns = cellSet.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositionCount();
        nbOfCells = nbOfColumns * nbOfRows;
    }

    public Double[][] getValues() {
        if (this.cellSet == null) {
            return null;
        } else if (cellSet.getAxes().size() != 2) {
            //throw new UtilException("CellSet must be 2 dimensional (CellSet provided is " + cellSet.getAxes().size() + "d");
        }
        int rowCount = this.cellSet.getAxes().get(Axis.ROWS.axisOrdinal()).getPositionCount();
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

    public List<CellSetAxis> getAxes() {
        return this.cellSet.getAxes();
    }

    public Cell getCell(List<Integer> list) {
        return this.cellSet.getCell(list);
    }

    public Cell getCell(int ordinal) {
        return this.cellSet.getCell(ordinal);
    }

    public List<Integer> ordinalToCoordinates(int ordinal) {
        return cellSet.ordinalToCoordinates(ordinal);
    }

    public int getNbOfRows() {
        return nbOfRows;
    }

    public int getNbOfColumns() {
        return nbOfColumns;
    }

    public int getNbOfCells() {
        return nbOfCells;
    }
}
