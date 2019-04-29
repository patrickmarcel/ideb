package model;

import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.SingleLinkageStrategy;
import olap.CellSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AutoClustering extends AbstractModel {

    private ClusteringAlgorithm algo = new DefaultClusteringAlgorithm();

    @Override
    public void fit(CellSet cellSet) {

        Double[] data = cellSet.getFlatData();
        List<Double> cleanedData = new ArrayList<>();
        List<String> ordinalList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                cleanedData.add(data[i]);
                ordinalList.add(String.valueOf(i));
            }
        }
        String[] names = ordinalList.toArray(new String[0]);

        double[] distances = new double[cleanedData.size() * (cleanedData.size() + 1) / 2];
        double[][] distanceMatrix = new double[cleanedData.size()][cleanedData.size()];
        int c = 0;
        for (int i = 0; i < cleanedData.size(); i++) {
            for (int j = 0; j < cleanedData.size(); j++) {
                double dist = Math.abs(cleanedData.get(i) - cleanedData.get(j));
                distanceMatrix[i][j] = dist;
                if (j >= i) {
                    distances[c] = dist;
                    c++;
                }
            }
        }
        Arrays.sort(distances);
        double threshold = findThreshold(distances);

        List<Cluster> clusterList = algo.performFlatClustering(distanceMatrix, names, new SingleLinkageStrategy(), threshold);

        this.modelComponentList = new ArrayList<>();
        for (Cluster cluster : clusterList) {
            ordinalList = getAllLeaves(cluster);
            Boolean[][] modelComponent = new Boolean[cellSet.getNbOfRows()][cellSet.getNbOfColumns()];
            for (int i = 0; i < cellSet.getNbOfCells(); i++) {
                if (data[i] != null) {
                    modelComponent[i / cellSet.getNbOfColumns()][i % cellSet.getNbOfColumns()] = ordinalList.contains(String.valueOf(i));
                }
            }
            modelComponentList.add(modelComponent);
        }
    }

    private List<String> getAllLeaves(Cluster cluster) {
        List<String> list = new ArrayList<>();
        if (cluster.isLeaf()) {
            list.add(cluster.getName());
            return list;
        } else {
            for (Cluster child : cluster.getChildren()) {
                list.addAll(getAllLeaves(child));
            }
            return list;
        }
    }

    private double findThreshold(double[] d) {
        if (d.length == 0) return 0;
        if (d.length < 3) return d[0];
        List<Double> angleList = new ArrayList<>();
        for (int i = 1; i < d.length - 1; i++) {
            double angle = Math.PI - Math.atan(d[i + 1] - d[i]) + Math.atan(d[i] - d[i - 1]);
            angleList.add(angle);
        }
        double angleMin = Collections.min(angleList);
        return d[angleList.indexOf(angleMin) + 1];
    }
}
