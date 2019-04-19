package model;

import olap.CellSet;

import java.util.List;

public abstract class AbstractModel {

    /**
     * A list of the model components. A component is a boolean membership matrix, one for each label the model has predict
     *
     * @see #fit(CellSet)
     */
    private List<Boolean[][]> modelComponentList;

    /**
     * <p>Fit the model to the given <code>cellSet</code></p>
     * <p>When implementing your own model, be sure to store the model prediction into the <code>modelComponentList</code>
     * field. Each component of the list act as a boolean membership matrix. For example, if your model predict 5 target
     * (like a K-Mean with k = 5) you should store 5 membership matrix, one for each label</p>
     *
     * @param cellSet cellSet to fit your model on
     * @see #modelComponentList
     */
    public abstract void fit(CellSet cellSet);

    /**
     * Fit the model to the given <code>cellSet</code> and return a list of the different model component built
     *
     * @param cellSet cellSet to fit your model on
     * @return list of model component
     */
    public List<Boolean[][]> fitAndPredict(CellSet cellSet) {
        fit(cellSet);
        return this.modelComponentList;
    }
}
