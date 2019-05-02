package main;

import olap.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.metadata.NamedList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Playground {

    private static final String DRIVER_CLASS = "mondrian.olap4j.MondrianOlap4jDriver";
    private static final String PROVIDER = "mondrian";
    private static final String JDBC_URL = "jdbc:sqlserver://10.195.25.10\\db_dopan:54437;databaseName=db_dopan;user=dopan;password=diblois";
    private static final String CATALOG = "res/cubeSchemas/DOPAN_DW3-agg.xml";
    private static final String USER = "patrick";
    private static final String PASSWORD = "oopi7taing7shahD";

    private static OlapConnection connectToServer() {
        OlapConnection olapConnection = null;
        try {
            Class.forName(Playground.DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(
                    "jdbc:" + Playground.PROVIDER + ":" +
                            "Jdbc=" + Playground.JDBC_URL + ";" +
                            "Catalog=" + Playground.CATALOG + ";" +
                            "JdbcUser=" + Playground.USER + ";" +
                            "JdbcPassword=" + Playground.PASSWORD + ";"
            );
            olapConnection = connection.unwrap(OlapConnection.class);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver class not found, you should try with \"mondrian.olap4j.MondiranOlap4jDriver\" for connecting to a Mondrian server");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return olapConnection;
    }

    public static void main(String[] args) {

        String query1 = "select NON EMPTY Crossjoin([Indicateur de lieu de travail.ILOCC_Hierarchie_1].[Indicateur de lieu].Members, [Annee du recensement.Annee_rencesement_Hierarchie_1].[Annee].Members) ON COLUMNS, NON EMPTY Hierarchize(Union(Crossjoin({[Measures].[Distance trajet domicile - travail (moyenne)]}, [Sexe.Sexe_Hierarchie].[Sexe].Members), Crossjoin({[Measures].[Nombre total d'individus]}, [Sexe.Sexe_Hierarchie].[Sexe].Members))) ON ROWS from [Cube1MobProInd]";
        String query2 = "SELECT NON EMPTY CrossJoin([Commune de residence.CNERES_Hierarchie_intercommunale].[Pays].Members, {[Measures].[Duree trajet domicile - travail (moyenne)]}) ON COLUMNS FROM [Cube1MobProInd]";

        String queryBig = "SELECT NON EMPTY {Hierarchize({[Indicateur de lieu de travail.ILOCC_Hierarchie_1].[Indicateur de lieu].Members})} ON COLUMNS, NON EMPTY {Hierarchize({[Lien avec la personne de reference.LPRM_Hierarchie_1].[Lien avec la personne de reference].Members})} ON ROWS FROM [Cube1MobProInd]";
        String querySmall = "SELECT NON EMPTY {Hierarchize({[Indicateur de lieu de travail.ILOCC_Hierarchie_1].[Categorie].Members})} ON COLUMNS, NON EMPTY {Hierarchize({[Lien avec la personne de reference.LPRM_Hierarchie_1].[Categorie].Members})} ON ROWS FROM [Cube1MobProInd]";

        OlapConnection connection = connectToServer();
//        OlapConnection connection = null;
        if (connection != null) {
            try {

                OlapStatement statement = connection.createStatement();
                CellSet cellSetBig = new CellSet(statement.executeOlapQuery(queryBig));
                CellSet cellSetSmall = new CellSet(statement.executeOlapQuery(querySmall));

                System.out.println(cellSetBig.getHeaderTree(0));

                /*List<AbstractModel> models = new ArrayList<>();
                models.add(new AutoClustering());
                ProxyBuilder proxyBuilder = new AncestorOrDescendantProxyBuilder();
                SignificanceBuilder significanceBuilder = new ZScoreSignificanceBuilder();
                SurpriseBuilder surpriseBuilder = new DifferenceSurpriseBuilder();
                ComponentScoreBuilder componentScoreBuilder = new AverageComponentScoreBuilder();
                AlgorithmOne algo = new AlgorithmOne(cellSetBig, cellSetSmall, models,proxyBuilder, significanceBuilder, surpriseBuilder, componentScoreBuilder, null);
                Boolean[][] modelComponent = algo.compute();
                for (Boolean[] booleans : modelComponent) {
                    System.out.println(Arrays.toString(booleans));
                }*/

                /*AncestorOrDescendantProxyBuilder proxyBuilder = new AncestorOrDescendantProxyBuilder();
                Map<List<Integer>, Set<List<Integer>>> proxy = proxyBuilder.computeProxyMatrix(cellSetBig, cellSetSmall);
                for (Map.Entry<List<Integer>, Set<List<Integer>>> entry : proxy.entrySet()){
                    System.out.print(coordinatesToString(entry.getKey()) + " -> ");
                    String[] res = new String[entry.getValue().size()];
                    int i = 0;
                    for (List<Integer> coordinates : entry.getValue()) {
                        res[i] = coordinatesToString(coordinates);
                        i++;
                    }
                    System.out.println(String.join(", ", res));
                }*/

                /*for (CellSetAxis cellSetAxis : cellSet.getAxes()) {
                    System.out.println(cellSetAxis.getAxisOrdinal().name());
                }*/

                // Print schema and cubes names
                /*System.out.println("# SCHEMA: " + connection.getSchema());
                Schema schema = connection.getOlapSchema();
                System.out.println(namedListToString(schema.getCubes()));*/

                /*Cube cube = connection.getOlapSchema().getCubes().get(0);
                System.out.println("# CUBE: " + cube.getName() + " (" + cube.getUniqueName() + ")");
                System.out.println("## DIMENSIONS:");
                System.out.println(namedListToString(cube.getDimensions()));
                System.out.println("## HIERARCHIES:");
                System.out.println(namedListToString(cube.getHierarchies()));

                for (int i = 0; i < 10; i++) {
                    Hierarchy hierarchy = cube.getHierarchies().get(i);
                    System.out.println("## " + hierarchy.getName());
                    System.out.println(namedListToString(hierarchy.getLevels()));
                }*/


                /*Hierarchy hierarchy = cube.getHierarchies().get(2);
                System.out.println(hierarchy.getName());

                Level regionsLevel = hierarchy.getLevels().get("Region");
                Level paysLevel = hierarchy.getLevels().get("Pays");
                Level communesLevel = hierarchy.getLevels().get("Communaute de communes");

                Member m1 = regionsLevel.getMembers().get(0);
                Member m2 = paysLevel.getMembers().get(0);
                Member m3 = communesLevel.getMembers().get(0);
                System.out.println(m1.getChildMembers().contains(m2));*/

                /*Set<String> s1 = new HashSet<>();
                Set<String> s2 = new HashSet<>();

                s1.add("A");
                s1.add("B");
                s1.add("C");

                s2.add("B");
                s2.add("C");
                s2.add("A");

                System.out.println(s1.equals(s2));*/

            } catch (OlapException e) {
                e.printStackTrace();
            }
        }


    }

    private static String namedListToString(NamedList<?> namedList) {
        if (namedList == null) {
            return "- List is null";
        } else {
            StringBuilder res = new StringBuilder();
            for (Map.Entry<String, ?> entry : namedList.asMap().entrySet()) {
                res.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            if (res.length() == 0) return "- List is empty";
            else return res.deleteCharAt(res.length() - 1).toString();
        }
    }

    private static String coordinatesToString(List<Integer> list) {
        if (list == null) return "coordinates are null";
        List<String> res = new ArrayList<>();
        for (int val : list) res.add(String.valueOf(val));
        return "(" + String.join(", ", res) + ")";
    }
}
