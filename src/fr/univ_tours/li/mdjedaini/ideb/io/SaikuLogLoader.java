/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.Query;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryMdx;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We represent a Saiku log as a directory containing a file for each session.
 * Loading the log consists in loading each session contained in this directory...
 * @author mahfoud
 */
public class SaikuLogLoader implements I_LogLoader {

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
    public SaikuLogLoader(BenchmarkEngine arg_be, String arg_filePath) {
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
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        
        // add the name as metadata of the session
        //result.addMetaData("name", arg_sessionFilePath);
        result.addMetaData("path", arg_sessionFilePath);
        result.addMetaData("filename", new File(arg_sessionFilePath).getName());
        
        
        System.out.println("I am parsing the file: " + arg_sessionFilePath);
        
        // pattern for extracting cube name
        Pattern p = Pattern.compile("from \\[(.*?)\\].*");

        File file   = new File(arg_sessionFilePath);
        
        
        try {
            //BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arg_sessionFilePath), "UTF-8"));
            String line = null;
            
            String currentQuery = "";
            
            // pour parser une requete, je cherche "select"
            // je prends toutes les lignes suivantes, jusqu'a rencontrer une ligne vide...
            while ((line = br.readLine()) != null) {

                if(line.contains("select")) {
                    
                    // look for the time before query execution
                    String date     = line.substring(0, 23);
                    Date d          = sdf.parse(date);
                    Long tsBefore   = d.getTime();
                    
                    // je recupere la position du mot "select" dans la ligne
                    Integer position    = line.indexOf("select");
                    currentQuery        = line.substring(position, line.length());
                    
                    String line_tmp = br.readLine();
                    while(!line_tmp.equals("")) {
                        currentQuery    += System.lineSeparator();
                        //currentQuery    += System.lineSeparator();
                        currentQuery    += line_tmp;
                        line_tmp    = br.readLine();
                    }
                    
                    // extract cubename from the query text
                    // Normally, the pattern is always found!
                    Matcher m = p.matcher(currentQuery);
                    m.find();
                    String cubeName = m.group(1);
                    
                    //System.out.println(currentQuery);
                    //System.out.println("cubeName: " + cubeName);
                    //System.out.println("-------");
                    
                    // look for the execution time
                    while(!line_tmp.contains("exec:")) {
                        line_tmp    = br.readLine();
                    }
                    
                    // here the line contains exec
                    // look for the time before query execution
                    date            = line_tmp.substring(0, 23);
                    d               = sdf.parse(date);
                    Long tsAfter    = d.getTime();
                    
                    Query q_tmp = new QueryMdx(this.be.getInternalCubeByName(cubeName), currentQuery);
                    
                    result.addQuery(q_tmp, tsBefore, tsAfter);
                }
                
            }
 
            br.close();
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }
        
        return result;
    }
    
}
