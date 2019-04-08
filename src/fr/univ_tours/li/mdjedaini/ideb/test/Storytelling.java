package fr.univ_tours.li.mdjedaini.ideb.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.univ_tours.li.mdjedaini.ideb.test.model.QueryRequest;
import fr.univ_tours.li.mdjedaini.ideb.test.model.Result;
import fr.univ_tours.li.mdjedaini.ideb.test.model.Session;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Storytelling {
    private static final String PROVIDER = "mondrian";
    private static final String JDBC = "jdbc:sqlserver://10.195.25.10\\db_dopan:54437;databaseName=db_dopan;user=dopan;password=diblois";
    private static final String CATALOG = "res/cubeSchemas/DOPAN_DW3-agg.xml";
    private static final String JDBC_USER = "patrick";
    private static final String JDBC_PASSWORD = "oopi7taing7shahD";

    private static final String JSON_DIR = "D:/Workspace/ideb/res/logs/dopan/json/";

    public static void main(String[] args) {
        try {
            // Init olap connection
            Class.forName("mondrian.olap4j.MondrianOlap4jDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mondrian:" +
                            "Jdbc=" + JDBC + ";" +
                            "Catalog=" + CATALOG + ";" +
                            "JdbcUser=" + JDBC_USER + ";" +
                            "JdbcPassword=" + JDBC_PASSWORD + ";"
            );
            OlapConnection olapConnection = connection.unwrap(OlapConnection.class);

            File jsonDir = new File(JSON_DIR);
            for (File file : Objects.requireNonNull(jsonDir.listFiles())) {
                if (file.isFile()) {
                    // Init Session
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
                    Gson gson = new GsonBuilder().setDateFormat(dateFormat.toPattern()).setPrettyPrinting().serializeNulls().create();
                    Session session = gson.fromJson(bufferedReader, Session.class);
                    bufferedReader.close();
                    System.out.println("File " + file.getName() + " loaded");

                    // Build results
                    for (QueryRequest queryRequest : session.getQueries()) {
                        OlapStatement statement = olapConnection.createStatement();
                        CellSet cellSet = statement.executeOlapQuery(queryRequest.getQuery());
                        queryRequest.setResult(Result.buildResult(cellSet));
                        statement.close();
                    }
                    System.out.println("All queries loaded for the session");

                    // Save json
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                    gson.toJson(session, bufferedWriter);
                    bufferedWriter.close();
                    System.out.println("Results saved in json");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
