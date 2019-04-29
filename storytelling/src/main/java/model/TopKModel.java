package model;

import olap.CellSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class TopKModel extends AbstractModel {

    private int k;

    public TopKModel(int k) {
        this.k = k;
    }

    /**
     * @param cellSet cellSet to fit your model on
     * @see AbstractModel#fit(CellSet)
     */
    @Override
    public void fit(CellSet cellSet) {
        Double[][] values = cellSet.getData();

        TreeMap<Double, int[]> sortedIndexTreeMap = new TreeMap<>();
        List<int[]> nullIndexList = new ArrayList<>();
        // Note: The int[] act as a coordinate tuple and are used to build the model components.

        for (int row = 0; row < values.length; row++) {
            for (int col = 0; col < values[row].length; col++) {
                if (values[row][col] == null) {
                    nullIndexList.add(new int[]{row, col});
                } else {
                    sortedIndexTreeMap.put(values[row][col], new int[]{row, col});
                }
            }
        }

        Boolean[][] modelComponent = new Boolean[values.length][values[0].length]; //TODO: Check if values[0] will not throw NullPointerException
        Boolean[][] inverseModelComponent = new Boolean[values.length][values[0].length];
        for (int i = 0; i < values.length; i++) {
            Arrays.fill(modelComponent[i], Boolean.FALSE);
            Arrays.fill(inverseModelComponent[i], Boolean.TRUE);
        }
        for (int i = 0; i < this.k; i++) { // Fill model components
            int[] index = sortedIndexTreeMap.pollLastEntry().getValue();
            modelComponent[index[0]][index[1]] = true;
            inverseModelComponent[index[0]][index[1]] = false;
        }
        for (int[] index : nullIndexList) { // Fill null values
            modelComponent[index[0]][index[1]] = null;
            inverseModelComponent[index[0]][index[1]] = null;
        }

        this.modelComponentList = new ArrayList<>();
        this.modelComponentList.add(modelComponent);
        this.modelComponentList.add(inverseModelComponent);
    }

    public int getK() {
        return k;
    }
}
