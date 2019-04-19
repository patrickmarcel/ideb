import builder.*;
import model.AbstractModel;
import olap.CellSet;

import java.util.ArrayList;
import java.util.List;

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

    public void compute(){
        Double[][] oldSignificance = significanceBuilder.computeScore(oldCellSet);
        Double[][] newSignificance = significanceBuilder.computeScore(newCellSet);
        Double[][] proxies = proxyBuilder.computeProxyMatrix(oldCellSet, newCellSet);

        Double[][] surprise = surpriseBuilder.computeScore(oldSignificance, newSignificance);
        List<Double> modelScoreList = new ArrayList<>();
        for (AbstractModel model : modelList) {
            List<Double> modelComponentScoreList = new ArrayList<>();
            for(Boolean[][] modelComponent : model.fitAndPredict(newCellSet)){
                modelComponentScoreList.add(componentScoreBuilder.computeScore(modelComponent, surprise));
            }
            modelScoreList.add(modelScoreBuilder.computeScore(modelComponentScoreList));
        }


    }
}
