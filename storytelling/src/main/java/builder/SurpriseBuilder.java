package builder;

import java.util.List;

public interface SurpriseBuilder {

    public Double computeScore(double significanceScoreNew, List<Double> significanceScoreProxyList);
}
