package builder;

import olap.CellSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProxyBuilder {

    public Map<List<Integer>, Set<List<Integer>>> computeProxyMatrix(CellSet cellSetNew, CellSet cellSetOld);
}
