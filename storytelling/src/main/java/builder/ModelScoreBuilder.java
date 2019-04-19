package builder;

import java.util.List;

public interface ModelScoreBuilder {
    public double computeScore(List<Double> modelComponentScoreList);
}
