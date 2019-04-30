package builder;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class AverageComponentScoreBuilder implements ComponentScoreBuilder {
    @Override
    public double computeScore(Boolean[][] modelComponent, Double[][] surpriseScore) {
        SummaryStatistics stats = new SummaryStatistics();
        for (int row = 0; row < modelComponent.length; row++) {
            for (int col = 0; col < modelComponent[row].length; col++) {
                if (modelComponent[row][col] != null && modelComponent[row][col]) {
                    stats.addValue(surpriseScore[row][col]);
                }
            }
        }
        return stats.getMean();
    }
}
