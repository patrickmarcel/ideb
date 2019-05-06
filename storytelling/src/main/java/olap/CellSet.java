package olap;

import org.olap4j.*;
import org.olap4j.metadata.Member;
import result.HeaderTree;

import java.util.Arrays;
import java.util.List;

/**
 * <p>This class act like a wrapper for the {@link org.olap4j.CellSet CellSet} interface. The main purpose is to store
 * the cell set values into a <code>Double[][]</code> matrix for a simpler accessing, and provide utility methods</p>
 * <p><b><i>"But why just not extends/implement CellSet ?"</i></b> you may ask.</p>
 * <p>When executing an MDX query, a {@link org.olap4j.CellSet CellSet} implementation is retrieved. The implementation
 * depends of which driver was used for the connection. Mondrian for example return an <code>MondrianOlap4jCellSet</code>
 * which is a private class ! That means we can't extends it (curse you Mondrian !).</p>
 * <p>This class is also named CellSet, that may be stupid. We could change it if we find a better name</p>
 */
public class CellSet {
    /**
     * The {@link org.olap4j.CellSet CellSet} cell set object to wrap
     */
    private org.olap4j.CellSet olap4JCellSet;

    /**
     * Cells values matrix. Empty cells are null
     */
    private Double[][] data;

    private int nbOfRows;
    private int nbOfColumns;
    private int nbOfCells;

    /**
     * <p>Constructor used for testing. You should not build your cell set with this constructor.
     * Instead use {@link CellSet#CellSet(org.olap4j.CellSet)}</p>
     */
    public CellSet() {
    }

    public CellSet(org.olap4j.CellSet olap4JCellSet) {
        //TODO: check if olap4JCellSet is 2D, else throw an exception
        this.olap4JCellSet = olap4JCellSet;
        nbOfRows = olap4JCellSet.getAxes().get(Axis.ROWS.axisOrdinal()).getPositionCount();
        nbOfColumns = olap4JCellSet.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositionCount();
        nbOfCells = nbOfColumns * nbOfRows;

        data = new Double[nbOfRows][nbOfColumns];
        for (int row = 0; row < nbOfRows; row++) {
            for (int col = 0; col < nbOfColumns; col++) {
                Cell cell = olap4JCellSet.getCell(Arrays.asList(col, row));
                if (cell != null && !cell.isError() && !cell.isNull() && !cell.isEmpty()) {
                    try {
                        data[row][col] = cell.getDoubleValue(); //TODO: find something prettier than replace method
                    } catch (OlapException e) {
                        e.printStackTrace();
                    }
                } else {
                    data[row][col] = null;
                }
            }
        }
    }

    public Double[][] getData() {
        return data;
    }

    public void setData(Double[][] data) {
        this.data = data;
    }

    public Double[] getFlatData() {
        Double[] res = new Double[nbOfCells];
        for (int ordinal = 0; ordinal < nbOfCells; ordinal++) {
            res[ordinal] = data[ordinal / nbOfColumns][ordinal % nbOfColumns];
        }
        return res;
    }

    public HeaderTree getHeaderTree(int axisOrdinal) {
        CellSetAxis rowAxis = olap4JCellSet.getAxes().get(axisOrdinal);
        HeaderTree root = new HeaderTree("root");
        for (Position position : rowAxis) {
            HeaderTree parent = root;
            for (Member member : position.getMembers()) {
                HeaderTree child = parent.getChildNamed(member.getName());
                if (child == null) {
                    child = new HeaderTree(member.getName());
                    parent.getChildren().add(child);
                }
                parent = child;
            }
        }
        root.updateSpanAndTrimChildren();
        return root;
    }

    public List<CellSetAxis> getAxes() {
        return this.olap4JCellSet.getAxes();
    }

    public Cell getCell(List<Integer> list) {
        return this.olap4JCellSet.getCell(list);
    }

    public Cell getCell(int ordinal) {
        return this.olap4JCellSet.getCell(ordinal);
    }

    public List<Integer> ordinalToCoordinates(int ordinal) {
        return olap4JCellSet.ordinalToCoordinates(ordinal);
    }

    public int getNbOfRows() {
        return nbOfRows;
    }

    public void setNbOfRows(int nbOfRows) {
        this.nbOfRows = nbOfRows;
    }

    public int getNbOfColumns() {
        return nbOfColumns;
    }

    public void setNbOfColumns(int nbOfColumns) {
        this.nbOfColumns = nbOfColumns;
    }

    public int getNbOfCells() {
        return nbOfCells;
    }

    public void setNbOfCells(int nbOfCells) {
        this.nbOfCells = nbOfCells;
    }
}
