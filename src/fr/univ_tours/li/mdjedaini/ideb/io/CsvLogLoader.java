/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.algo.query.QueryConverter;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Level;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 * We represent a Saiku log as a directory containing a file for each session.
 * Loading the log consists in loading each session contained in this directory...
 * @author mahfoud
 */
public class CsvLogLoader implements I_LogLoader {

    BenchmarkEngine be;
    
    //
    String logDirectoryPath;
    ArrayList<String> sessionList;
    
    // do not load queries with number of cells > querySizeLimit
    Integer querySizeLimit  = 500;
    
    /**
     * 
     * @param arg_be
     * @param arg_filePath 
     */
    public CsvLogLoader(BenchmarkEngine arg_be, String arg_filePath) {
        this.be                 = arg_be;
        this.logDirectoryPath   = arg_filePath;
        this.sessionList        = new ArrayList();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Log loadLog() {
        Log log = new Log();
        
        // gather the session elements
        this.loadSessions();
        
        for(String sessionFilePath : this.sessionList) {
            // chargement de la session
            Session s   = this.loadSession(sessionFilePath);
            log.addSession(s);
        }
        
        return log;
    }
    
    
    /**
     * 
     */
    public void loadSessions() {
        File file   = new File(this.logDirectoryPath);
        
        if(file.isDirectory()) {
            for(String fileName : file.list()) {
                String absoluteFileName = file.getAbsolutePath() + "/" + fileName;
                File f_tmp              = new File(absoluteFileName);    
                this.sessionList.add(absoluteFileName);
            }
        } else {
            // if its not a directory, its a file...
            this.sessionList.add(logDirectoryPath);
        }
        
        
        
    }
    
    /**
     * 
     * @param arg_sessionFilePath
     * @return 
     */
    public Session loadSession(String arg_sessionFilePath) {
        
        Session result  = new Session();
        // add the name as metadata of the session
        result.addMetaData("path", arg_sessionFilePath);
        result.addMetaData("filename", new File(arg_sessionFilePath).getName());
        System.out.println("Loading session: " + result.getMetadata("filename"));
        
        try {
            
            Reader in = new FileReader(arg_sessionFilePath);
            Iterable<CSVRecord> records = CSVFormat.newFormat(';').withFirstRecordAsHeader().parse(in);
            
            // each record is a query
            int nbRec=0;
            for (CSVRecord record : records) {
                nbRec++;
                
                System.out.println("I am parsing the line: " + record);
                
                String cubeName = record.get("cube");
                EAB_Cube cube   = this.be.getBenchmarkData().getInternalCubeList().get(cubeName);
                
                QueryTriplet q_tmp  = new QueryTriplet(cube);
                
                // extract measures
                String currentMeasure       = record.get("Measures");
                
                // only add measure if not empty
                if(!currentMeasure.equals("[]")) {
                    Pattern p   = Pattern.compile("([a-zA-Z_0-9][a-zA-Z_0-9 ]+)");
                    Matcher m   = p.matcher(currentMeasure);
                    
                    // manage multiple measures
                    while(m.find()) {
                        //System.out.println("Current measure: " + currentMeasure + " --- trouve: " + m.groupCount());
                        String measure  = m.group(1);
//                        System.out.println("Measure: " + measure);    
                        
                        // add the current measure to the current query
                        MeasureFragment mf  = new MeasureFragment(q_tmp, measure);
                        
                        if(null == mf.getMeasure()) {
                            int i = 2;
                        }
                        
                        q_tmp.addMeasure(mf);
                    }
                    
                }
                
                
                // extract GBS
                String currentProjection    = record.get("GroupBy");
                
                // only add projections if not empty
                if(!currentProjection.equals("[]")) {
                    Pattern p   = Pattern.compile("([a-zA-Z_0-9][a-zA-Z_0-9 ]+)");
                    Matcher m   = p.matcher(currentProjection);
                    
                    // manage multiple group by
                    while(m.find()) {
                        //System.out.println("Group " + i + ": " + m.group(i));
                        String level    = m.group(1);
//                        System.out.println("Level: " + level);
                        
                        EAB_Level l_tmp = cube.getLevelByAtomicName(level);
                        ProjectionFragment pf_tmp   = new ProjectionFragment(q_tmp, l_tmp);
                        
                        if(null == pf_tmp.getLevel()) {
                            int i = 2;
                        }
                        
                        q_tmp.addProjection(pf_tmp);
                    }
                    
                }
                
                
                // extract filters
                String currentSelection     = record.get("Filters");
                // only add projections if not empty
                if(!currentSelection.equals("[]")) {
                    Pattern p   = Pattern.compile("([a-zA-Z_0-9][a-zA-Z_0-9 ]+)=>\\[EQUAL ([a-zA-Z_0-9& ]+)\\]");
                    Matcher m   = p.matcher(currentSelection);
                    
                    // manage multiple occurrences
                    while(m.find()) {
//                        System.out.println("Current selection: " + currentSelection + " --- trouve: " + m.groupCount());
                        
                        String level    = m.group(1);
                        String member   = m.group(2);
                        
                        EAB_Level l_tmp = cube.getLevelByAtomicName(level);
                        
//                        System.out.println("Cube: " + cubeName);
//                        System.out.println("Level: " + level);
//                        System.out.println("Member: " + member);
                        
                        if(null == l_tmp) {
                            int i = 2;
                        }
                        
                        //System.out.println("nbRec:" +nbRec);
                        
                        String dimName  = l_tmp.getHierarchy().getDimension().getMondrianDimension().getName();
                        String hieName  = l_tmp.getHierarchy().getName();
                        
                        //hieName.spl
                        
                        SelectionFragment sf_tmp    = new SelectionFragment(q_tmp, dimName, hieName, level, member);
                        
                        if(null != sf_tmp.getMemberValue()) {
                            q_tmp.addSelection(sf_tmp);
                        }
                        
                        
                    }
                    
                }
                
                
                // add the query to the session
                result.addQuery(q_tmp);
                //System.out.println(q_tmp.toString());
                
                QueryConverter qc   = new QueryConverter(this.be);
                
                // moche, a changer...
                try{
                    //System.out.println("******************");
                    //System.out.println("Record:" + record);

                    QueryMdx q_mdx  = qc.toMdx(q_tmp);
                    //System.out.println("MDX with my converter:");
                    //System.out.println(q_mdx);
                    
                    //System.out.println(q_mdx.toString());
                   
                    q_mdx.execute(Boolean.TRUE);
                    //q_mdx.execute(Boolean.FALSE);
//                    System.out.println("-----");
//                    System.out.println("Query: " + q_tmp);
//                    System.out.println("-----");
//                    System.out.println("Mdx: " + qc.toMdx(q_tmp));
//                    System.out.println("******************");
                } catch(Exception arg_e) {
                    System.out.println("******************");
                    System.out.println("Exception: " + arg_e.getClass().getName());
                    System.out.println("Record:" + record);
//                    System.out.println("-----");
//                    System.out.println("Query: " + q_tmp);
//                    System.out.println("-----");
                    //qc.toMdx(q_tmp);
                    //System.out.println("******************");
                    //System.err.println("Exception avec: ");
                    //System.err.println("Record: " + record);
                }
                
            } // end foreach record
            
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
            System.out.println("FOR SESSION: " + result.getMetadata("filename"));
        }
        
   
        
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
//        
//        // add the name as metadata of the session
//        result.addMetaData("name", arg_sessionFilePath);
//        
//        System.out.println("I am parsing the file: " + arg_sessionFilePath);
//        
//        // pattern for extracting cube name
//        Pattern p = Pattern.compile("from \\[(.*?)\\].*");
//
//        File file   = new File(arg_sessionFilePath);
//        
//        
//        try {
//            //BufferedReader br = new BufferedReader(new FileReader(file));
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arg_sessionFilePath), "UTF-8"));
//            String line = null;
//            
//            String currentQuery = "";
//            
//            // pour parser une requete, je cherche "select"
//            // je prends toutes les lignes suivantes, jusqu'a rencontrer une ligne vide...
//            while ((line = br.readLine()) != null) {
//
//                if(line.contains("select")) {
//                    
//                    // look for the time before query execution
//                    String date     = line.substring(0, 23);
//                    Date d          = sdf.parse(date);
//                    Long tsBefore   = d.getTime();
//                    
//                    // je recupere la position du mot "select" dans la ligne
//                    Integer position    = line.indexOf("select");
//                    currentQuery        = line.substring(position, line.length());
//                    
//                    String line_tmp = br.readLine();
//                    while(!line_tmp.equals("")) {
//                        currentQuery    += System.lineSeparator();
//                        //currentQuery    += System.lineSeparator();
//                        currentQuery    += line_tmp;
//                        line_tmp    = br.readLine();
//                    }
//                    
//                    // extract cubename from the query text
//                    // Normally, the pattern is always found!
//                    Matcher m = p.matcher(currentQuery);
//                    m.find();
//                    String cubeName = m.group(1);
//                    
//                    //System.out.println(currentQuery);
//                    //System.out.println("cubeName: " + cubeName);
//                    //System.out.println("-------");
//                    
//                    // look for the execution time
//                    while(!line_tmp.contains("exec:")) {
//                        line_tmp    = br.readLine();
//                    }
//                    
//                    // here the line contains exec
//                    // look for the time before query execution
//                    date            = line_tmp.substring(0, 23);
//                    d               = sdf.parse(date);
//                    Long tsAfter    = d.getTime();
//                    
//                    Query q_tmp = new QueryMdx(this.be.getInternalCubeByName(cubeName), currentQuery);
//                    
//                    result.addQuery(q_tmp, tsBefore, tsAfter);
//                }
//                
//            }
// 
//            br.close();
//        } catch(Exception arg_e) {
//            arg_e.printStackTrace();
//        }
        
        return result;
    }
    
}
