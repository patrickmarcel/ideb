package builder;

import olap.CellSet;

public interface ProxyBuilder {

    public Double[][] computeProxyMatrix(CellSet cellSetOld, CellSet cellSetNew);
}
