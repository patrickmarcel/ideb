package builder;

import olap.CellSet;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;


public class ZScoreSignificanceBuilder implements SignificanceBuilder {
    @Override
    public Double[][] computeScore(CellSet cellSet) {
        Double[][] significanceScores = new Double[cellSet.getNbOfRows()][cellSet.getNbOfColumns()];
        SummaryStatistics stats = new SummaryStatistics();
        Double[][] data = cellSet.getData();

        for (Double[] row : data) {
            for (Double val : row) {
                if (val != null) stats.addValue(val);
            }
        }

        for (int row = 0; row < significanceScores.length; row++) {
            for (int col = 0; col < significanceScores[row].length; col++) {
                if (data[row][col] != null)
                    significanceScores[row][col] = (data[row][col] - stats.getMean()) / stats.getStandardDeviation();
            }
        }

        return significanceScores;
    }
}
