import builder.*;
import model.AbstractModel;
import olap.CellSet;
import org.olap4j.Axis;

import java.util.*;

public class AlgorithmOne {

    private CellSet oldCellSet;
    private CellSet newCellSet;
    private List<AbstractModel> modelList;
    private ProxyBuilder proxyBuilder;
    private SignificanceBuilder significanceBuilder;
    private SurpriseBuilder surpriseBuilder;
    private ComponentScoreBuilder componentScoreBuilder;
    private ModelScoreBuilder modelScoreBuilder;


    public AlgorithmOne(CellSet oldCellSet, CellSet newCellSet, List<AbstractModel> modelList, ProxyBuilder proxyBuilder, SignificanceBuilder significanceBuilder, SurpriseBuilder surpriseBuilder, ComponentScoreBuilder componentScoreBuilder, ModelScoreBuilder modelScoreBuilder) {
        this.oldCellSet = oldCellSet;
        this.newCellSet = newCellSet;
        this.modelList = modelList;
        this.proxyBuilder = proxyBuilder;
        this.significanceBuilder = significanceBuilder;
        this.surpriseBuilder = surpriseBuilder;
        this.componentScoreBuilder = componentScoreBuilder;
        this.modelScoreBuilder = modelScoreBuilder;
    }

    public void compute() {
        int col = newCellSet.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositionCount();
        int row = newCellSet.getAxes().get(Axis.ROWS.axisOrdinal()).getPositionCount();
        Double[][] oldSignificance = significanceBuilder.computeScore(oldCellSet);
        Double[][] newSignificance = significanceBuilder.computeScore(newCellSet);
        Map<List<Integer>, Set<List<Integer>>> proxies = proxyBuilder.computeProxyMatrix(newCellSet, oldCellSet);

        Double[][] surprise = new Double[row][col];
        for (int m = 0; m < surprise.length; m++) {
            for (int n = 0; n < surprise[m].length; n++) {
                List<Double> proxySignificance = new ArrayList<>();
                List<Integer> coordinates = Arrays.asList(m, n);
                for (List<Integer> proxyIndexes : proxies.get(coordinates)) {
                    proxySignificance.add(oldSignificance[proxyIndexes.get(0)][proxyIndexes.get(1)]);
                }
                surprise[m][n] = surpriseBuilder.computeScore(newSignificance[m][n], proxySignificance);
            }
        }
        List<Double> modelScoreList = new ArrayList<>();
        for (AbstractModel model : modelList) {
            List<Double> modelComponentScoreList = new ArrayList<>();
            for (Boolean[][] modelComponent : model.fitAndPredict(newCellSet)) {
                modelComponentScoreList.add(componentScoreBuilder.computeScore(modelComponent, surprise));
            }
            modelScoreList.add(modelScoreBuilder.computeScore(modelComponentScoreList));
        }


    }
}
