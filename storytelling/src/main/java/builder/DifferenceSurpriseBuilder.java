package builder;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.List;

public class DifferenceSurpriseBuilder implements SurpriseBuilder {
    @Override
    public Double computeScore(double significanceScoreNew, List<Double> significanceScoreProxyList) {
        SummaryStatistics stats = new SummaryStatistics();
        for (double val : significanceScoreProxyList) {
            stats.addValue(val);
        }
        return Math.abs(significanceScoreNew - stats.getMean());
    }
}
