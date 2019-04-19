package builder;

import olap.CellSet;

public interface SurpriseBuilder {

    public abstract Double[][] computeScore(Double[][] significanceScoresOld, Double[][] significanceScoresNew);
}
