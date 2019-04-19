package builder;

import olap.CellSet;

public interface SignificanceBuilder {

    public Double[][] computeScore(CellSet cellSet);
}
