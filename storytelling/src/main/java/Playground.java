import org.olap4j.*;
import org.olap4j.metadata.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        System.out.println(Math.sqrt(-5));
        String query1 = "select NON EMPTY Crossjoin([Indicateur de lieu de travail.ILOCC_Hierarchie_1].[Indicateur de lieu].Members, [Annee du recensement.Annee_rencesement_Hierarchie_1].[Annee].Members) ON COLUMNS, NON EMPTY Hierarchize(Union(Crossjoin({[Measures].[Distance trajet domicile - travail (moyenne)]}, [Sexe.Sexe_Hierarchie].[Sexe].Members), Crossjoin({[Measures].[Nombre total d'individus]}, [Sexe.Sexe_Hierarchie].[Sexe].Members))) ON ROWS from [Cube1MobProInd]";
        String query2 = "SELECT NON EMPTY CrossJoin([Commune de residence.CNERES_Hierarchie_intercommunale].[Pays].Members, {[Measures].[Duree trajet domicile - travail (moyenne)]}) ON COLUMNS FROM [Cube1MobProInd]";

        OlapConnection connection = connectToServer();
        if (connection != null) {
            try {
                OlapStatement statement = connection.createStatement();
                CellSet cellSet = statement.executeOlapQuery(query2);

                for(CellSetAxis cellSetAxis : cellSet.getAxes()){
                    System.out.println(cellSetAxis.getAxisOrdinal().name());
                }



                // Print schema and cubes names
                /*System.out.println("# SCHEMA: " + connection.getSchema());
                Schema schema = connection.getOlapSchema();
                System.out.println(namedListToString(schema.getCubes()));*/

                Cube cube = connection.getOlapSchema().getCubes().get(0);
                System.out.println("# CUBE: " + cube.getName() + " (" + cube.getUniqueName() + ")");
                System.out.println("## DIMENSIONS:");
                System.out.println(namedListToString(cube.getDimensions()));
                System.out.println("## HIERARCHIES:");
                System.out.println(namedListToString(cube.getHierarchies()));
                System.out.println("## SETS:");
                System.out.println(namedListToString(cube.getSets()));

                for (int i = 0; i < 10; i++) {
                    Hierarchy hierarchy = cube.getHierarchies().get(i);
                    System.out.println("## " + hierarchy.getName());
                    System.out.println(namedListToString(hierarchy.getLevels()));
                }

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
}
