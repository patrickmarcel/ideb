/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.algo.gen.log;

import it.unibo.csr.big.cubeload.generator.OlapGenerator;
import it.unibo.csr.big.cubeload.generator.Profile;
import it.unibo.csr.big.cubeload.io.XMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.io.XMLLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;


/**
 *
 * @author mahfoud
 */
public class CubeloadGenerator implements I_LogGenerator {
    
    private BenchmarkEngine be;
            
    /**
     *
     */
    private String schemaFilePath;

    /**
     *
     */
    private String profileFilePath;

    /**
     *
     */
    private String dataDirectoryPath;
    
    /**
     *
     */
    private String cubeName;
    
    // todo try to add profile parameters here instead of having them in a file...

    /**
     *
     */
    private Integer numberOfProfiles; // Number of profiles to be generated.

    /**
     *
     */
    private Integer maxMeasures; // Maximum number of measures in a query

    /**
     *
     */
    private Integer minReportSize; // Minimum size of the report for starting queries.

    /**
     *
     */
    private Integer maxReportSize; // Maximum size of the report for starting queries.

    /**
     *
     */
    private Integer numberOfSurprisingQueries; // Number of surprising queries.

    /**
     * 
     * @param arg_be 
     */
    public CubeloadGenerator(BenchmarkEngine arg_be) {
        this.be                 = arg_be;
        
        this.schemaFilePath     = "/home/mahfoud/projects/ea-benchmark/schema.xml";
        this.profileFilePath    = "/home/mahfoud/Documents/phd/mdrepo/md_export/ssb_for_cubeload/profile.xml";
        this.dataDirectoryPath  = "/home/mahfoud/Documents/phd/mdrepo/md_export/ssb_for_cubeload";
        
        this.numberOfProfiles           = 1;
        this.maxMeasures                = 2;
        this.minReportSize              = 5;
        this.maxReportSize              = 7;
        this.numberOfSurprisingQueries  = 1;
        
        this.cubeName           = "SSB";
        
    }

    /**
     * 
     * @return 
     */
    @Override
    public Log generateLog() {
        Log log             = new Log() ;
        File profileFile    = new File(this.profileFilePath);
        
        List<Profile> listOfProfiles    = new ArrayList<>();
        
        try {
            
            listOfProfiles.add(new XMLReader().getProfile(profileFile.getCanonicalPath()));
            OlapGenerator olapGen   = new OlapGenerator(
                    this.numberOfProfiles,
                    this.maxMeasures,
                    this.minReportSize,
                    this.maxReportSize,
                    this.numberOfSurprisingQueries,
                    this.cubeName, this.schemaFilePath, this.dataDirectoryPath, listOfProfiles);
            
            long unixTimestamp  = Instant.now().getEpochSecond();
            String outputFile   = "Workload-" + unixTimestamp + ".xml";
            
            olapGen.generateWorkload(outputFile);
            System.out.println("Fin de la creation du workload...");
            
            XMLLogLoader xml_reader = new XMLLogLoader(this.be, outputFile);
            return xml_reader.loadLog();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return log;
    }

    public BenchmarkEngine getBe() {
        return be;
    }

    public void setBe(BenchmarkEngine be) {
        this.be = be;
    }

    public String getSchemaFilePath() {
        return schemaFilePath;
    }

    public void setSchemaFilePath(String schemaFilePath) {
        this.schemaFilePath = schemaFilePath;
    }

    public String getProfileFilePath() {
        return profileFilePath;
    }

    public void setProfileFilePath(String profileFilePath) {
        this.profileFilePath = profileFilePath;
    }

    public String getDataDirectoryPath() {
        return dataDirectoryPath;
    }

    public void setDataDirectoryPath(String dataDirectoryPath) {
        this.dataDirectoryPath = dataDirectoryPath;
    }

    public String getCubeName() {
        return cubeName;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    public Integer getNumberOfProfiles() {
        return numberOfProfiles;
    }

    public void setNumberOfProfiles(Integer numberOfProfiles) {
        this.numberOfProfiles = numberOfProfiles;
    }

    public Integer getMaxMeasures() {
        return maxMeasures;
    }

    public void setMaxMeasures(Integer maxMeasures) {
        this.maxMeasures = maxMeasures;
    }

    public Integer getMinReportSize() {
        return minReportSize;
    }

    public void setMinReportSize(Integer minReportSize) {
        this.minReportSize = minReportSize;
    }

    public Integer getMaxReportSize() {
        return maxReportSize;
    }

    public void setMaxReportSize(Integer maxReportSize) {
        this.maxReportSize = maxReportSize;
    }

    public Integer getNumberOfSurprisingQueries() {
        return numberOfSurprisingQueries;
    }

    public void setNumberOfSurprisingQueries(Integer numberOfSurprisingQueries) {
        this.numberOfSurprisingQueries = numberOfSurprisingQueries;
    }
 
    
    
}
