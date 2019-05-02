package main;

import builder.*;
import model.AbstractModel;
import olap.CellSet;

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

    public Boolean[][] compute() {
        int col = newCellSet.getNbOfColumns();
        int row = newCellSet.getNbOfRows();
        Double[][] oldSignificance = significanceBuilder.computeScore(oldCellSet);
        Double[][] newSignificance = significanceBuilder.computeScore(newCellSet);
        Map<List<Integer>, Set<List<Integer>>> proxies = proxyBuilder.computeProxyMatrix(newCellSet, oldCellSet);

        Double[][] surprise = new Double[row][col];
        for (int m = 0; m < surprise.length; m++) {
            for (int n = 0; n < surprise[m].length; n++) {
                Double significance = newSignificance[m][n];
                if (significance != null) {
                    List<Double> proxySignificance = new ArrayList<>();
                    List<Integer> coordinates = Arrays.asList(n, m);
                    Set<List<Integer>> proxiesSet = proxies.get(coordinates);
                    if (proxiesSet != null) {
                        for (List<Integer> proxyIndexes : proxiesSet) {
                            proxySignificance.add(oldSignificance[proxyIndexes.get(1)][proxyIndexes.get(0)]);
                        }
                        surprise[m][n] = surpriseBuilder.computeScore(significance, proxySignificance);
                    }
                }
            }
        }
        // List<Double> modelScoreList = new ArrayList<>();
        TreeMap<Double, Boolean[][]> modelComponentScoreMap = new TreeMap<>();
        for (AbstractModel model : modelList) {
            for (Boolean[][] modelComponent : model.fitAndPredict(newCellSet)) {
                modelComponentScoreMap.put(componentScoreBuilder.computeScore(modelComponent, surprise), modelComponent);
            }
            // modelScoreList.add(modelScoreBuilder.computeScore(modelComponentScoreList));
        }
        return modelComponentScoreMap.lastEntry().getValue();
    }
}
