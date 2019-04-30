package model;

import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.SingleLinkageStrategy;
import olap.CellSet;

import java.util.ArrayList;
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

        double[][] distanceMatrix = new double[cleanedData.size()][cleanedData.size()];
        for (int i = 0; i < cleanedData.size(); i++) {
            for (int j = 0; j < cleanedData.size(); j++) {
                double dist = Math.abs(cleanedData.get(i) - cleanedData.get(j));
                distanceMatrix[i][j] = dist;
            }
        }

        Cluster root = algo.performClustering(distanceMatrix, names, new SingleLinkageStrategy());
        List<Double> linkageDistance = new ArrayList<>();
        List<Cluster> clusterList = new ArrayList<>();
        clusterList.add(root);
        while (!clusterList.isEmpty()) {
            Cluster c = clusterList.remove(0);
            if (!c.isLeaf()) {
                linkageDistance.add(c.getDistanceValue());
                clusterList.addAll(c.getChildren());
            }
        }
        Collections.sort(linkageDistance);
        double threshold = findThreshold(linkageDistance);

        clusterList = algo.performFlatClustering(distanceMatrix, names, new SingleLinkageStrategy(), threshold);


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

        System.out.println(modelComponentList.size() + " component.s");
        System.out.println();
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

    private double findThreshold(List<Double> d) {
        if (d.size() == 0) return 0;
        if (d.size() < 3) return d.get(0);
        List<Double> angleList = new ArrayList<>();
        for (int i = 1; i < d.size() - 1; i++) {
            double angle = Math.PI - Math.atan(d.get(i + 1) - d.get(i)) + Math.atan(d.get(i) - d.get(i - 1));
            angleList.add(angle);
        }
        double angleMin = Collections.min(angleList);
        return d.get(angleList.indexOf(angleMin) + 1);
    }
}
