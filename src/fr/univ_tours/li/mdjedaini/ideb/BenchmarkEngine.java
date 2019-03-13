/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mondrian.olap.Cube;
import mondrian.olap.SchemaReader;
import fr.univ_tours.li.mdjedaini.ideb.algo.FocusOnFirstQuery;
import fr.univ_tours.li.mdjedaini.ideb.algo.I_FocusDetector;
import fr.univ_tours.li.mdjedaini.ideb.algo.clustering.I_SessionClusteringAlgorithm;
import fr.univ_tours.li.mdjedaini.ideb.algo.clustering.NicolasSessionClustering;
import fr.univ_tours.li.mdjedaini.ideb.algo.discovery.DiscoveryManager;
import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.AligonSessionSimilarity;
import fr.univ_tours.li.mdjedaini.ideb.algo.similarity.I_SessionSimilarity;
import fr.univ_tours.li.mdjedaini.ideb.algo.suts.I_SUT;
import fr.univ_tours.li.mdjedaini.ideb.algo.user.I_UserSimulator;
import fr.univ_tours.li.mdjedaini.ideb.algo.user.UserModelMarkov;
import fr.univ_tours.li.mdjedaini.ideb.eval.SUT_Evaluator;
import fr.univ_tours.li.mdjedaini.ideb.eval.metric.Metric;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.I_SUTScorer;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.SUTScorer;
import fr.univ_tours.li.mdjedaini.ideb.eval.scoring.SutResolutionScore;
import fr.univ_tours.li.mdjedaini.ideb.io.I_LogLoader;
import fr.univ_tours.li.mdjedaini.ideb.io.XMLLogLoader;
import fr.univ_tours.li.mdjedaini.ideb.olap.EAB_Cube;
import fr.univ_tours.li.mdjedaini.ideb.params.Parameters;
import fr.univ_tours.li.mdjedaini.ideb.struct.AbstractDiscovery;
import fr.univ_tours.li.mdjedaini.ideb.struct.CellList;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.UserLog;
import fr.univ_tours.li.mdjedaini.ideb.user.User;

/**
 *
 * @author mahfoud
 */
public class BenchmarkEngine {
    
    // 
    Integer bid;
    
    //private static final Logger LOGGER = LogManager.getLogger(BenchmarkEngine.class);
    
    private static Set<Class<? extends Metric>> registeredMetrics   = new HashSet<>();
    private static Set<Class<? extends I_SUT>> registeredSUTs       = new HashSet<>();
    
    //

    /**
     *
     */
    public Parameters parameters;
    OlapSystem benchmarkData;
    
    I_LogLoader i_logLoader;                // module loading the log
    I_SessionClusteringAlgorithm i_sca;     // module for clustering algorithms
    I_SessionSimilarity i_ss;               // module for similarity between sessions
    I_UserSimulator i_us;                   // module for simulating a user
    
    SUT_Evaluator evaluator;
    I_SUTScorer sutScorer;                  // module for handling the scoring process
    I_FocusDetector i_focusDetector;       // module for detecting focus zones
    
    Integer uid = 0;
    
    /**
     * Creates a benchmark engine with default modules and parameters.
     * 
     */
    public BenchmarkEngine() {
        // default parameters
        this.parameters = new Parameters();
        this.benchmarkData = new OlapSystem(this);
    }
    
    /**
     * Creates a benchmark engine with default modules but provided parameters.
     * 
     * @param arg_parameters 
     */
    
    public BenchmarkEngine(Parameters arg_parameters) {
        this();
        this.parameters = arg_parameters;
    }
    
    /**
     * Initializes data source.
     * Initializes the Mondrian connection, and load the cube list, with their data, in memory...
     */
    public void initDatasource() {
        System.out.println("Intializing Mondrian connection...");
        this.initMondrianConnection();
        
        System.out.println("Intializing cube list..");
        this.initCubeList();

    }
    
    /**
     * Initializes the benchmark.
     * Initializes the log from the file given as parameter, and create users...
     */
    public void initBenchmark() {
        
        this.initDefaultModules();
        
        this.initData();
        
    }
    
    /**
     * Initialize the log and the users.
     * Connection, cube, log, etc...
     */
    private void initData() {
        
        this.initLog();
        
        this.initUsers();
        
    }
    
    /**
     * Initializes default modules.
     * Initializes only modules that have not been initialized so far...
     */
    public void initDefaultModules() {
        // default modules
        if(this.i_logLoader == null) this.i_logLoader           = new XMLLogLoader(this, this.getParameters().logFilePath);
        if (this.i_ss == null)      this.i_ss                   = new AligonSessionSimilarity(this);
        if (this.i_sca == null)     this.i_sca                  = new NicolasSessionClustering(this);
        if (this.i_us == null)      this.i_us                   = new UserModelMarkov();
        if (this.evaluator == null) this.evaluator              = new SUT_Evaluator(this);
        if (this.i_focusDetector == null) this.i_focusDetector  = new FocusOnFirstQuery();
        if (this.sutScorer == null) this.sutScorer              = new SUTScorer(this);
    }
    
    /**
     * Create users and their models.
     * Creates users, users logs and models...
     */
    public void initUsers() {
        
         // create users
        System.out.println("Creating users using session clustering...");
        this.getBenchmarkData().createUsers();
        System.out.println("Users created!");
        
        // create users logs
        System.out.println("Extracting user logs from the complete log...");
        this.getBenchmarkData().computeUserLogsListByUser();
        System.out.println("User logs extracted!");
        
        // detect discoveries
        System.out.println("Assigning user model to each user...");
        this.assignUserModel();
        System.out.println("User models assigned!");

    }
    
    /**
     * Initializing the Mondrian connection.
     * 
     */
    public void initMondrianConnection() {
        // creates and set connection
        EAB_Connection c_tmp    = new EAB_Connection(this);
        c_tmp.open();
        this.benchmarkData.setC(c_tmp);
    }
    
    /**
     * Returns the list of all available cubes.
     * @return 
     */
    public List<String> getCubeNameList() {
        List<String> result = new ArrayList<>();
        
        EAB_Connection connection   = this.getConnection();
        connection.open();
        
        for(mondrian.olap.Cube c_tmp : connection.getMondrianConnection().getSchema().getCubes()) {
            result.add(c_tmp.getName());
        }
        connection.close();
        
        return result;
    }
    
    /**
     * Retrieves the default cube.
     * The default cube is retrieved because we store it with the same name as the Mondrian cube...
     * @return 
     */
    public EAB_Cube getDefaultCube() {
        EAB_Connection connection   = this.getConnection();
        connection.open();
        EAB_Cube result = this.getInternalCubeByName(connection.getDefaultCube().getName());
        connection.close();
        return result;
    }
    
    /**
     * Initializes all available cubes...
     */
    public void initCubeList() {
        EAB_Connection connection   = new EAB_Connection(this);
        connection.open();
        
        for(Cube c_tmp : connection.getMondrianConnection().getSchema().getCubes()) {
            System.out.println("Cube " + c_tmp.getName() + " - Loading cube data in memory...");
            EAB_Cube ic_tmp   = new EAB_Cube(this, c_tmp);
            ic_tmp.loadDataFromMondrianCube();
            this.benchmarkData.setInternalCube(ic_tmp);
            System.out.println("Cube loaded!");
        }
    }
    
    /**
     * 
     */
    public void initLog() {
        // load the log from the file
        System.out.println("Loading log...");
        Log log_tmp         = this.i_logLoader.loadLog();
        this.getBenchmarkData().setLog(log_tmp);
        System.out.println("Log loaded!");
    }
    
    /**
     * Bootstraps the complete benchmark.
     * Used in the case of benchmark oriented use of the system.
     */
    public void init() {
        
        this.initDatasource();
        
        this.initDefaultModules();
        
        this.initData();
          
    }
    
    /**
     * 
     * @param arg_user 
     */
    public void addUser(User arg_user) {
        arg_user.setUid(this.uid);
        this.uid++;
        this.getBenchmarkData().userList.put(arg_user.getUid(), arg_user);
    }
        
    /**
     * 
     */
    public void assignUserModel() {
        for(User u_tmp : this.getBenchmarkData().getUserList()) {
            try {
                I_UserSimulator um_tmp  = this.i_us.getClass().newInstance();
                u_tmp.setUserModel(um_tmp);
            } catch(Exception arg_e) {
                
            }
        }
    }
    
    /**
     * Create discoveries in the current log. 
     */
    public void createDiscoveryListByUser() {
        
        // create discoveries for each user
        for(User u_tmp : this.getBenchmarkData().getUserList()) {
            UserLog u_log   = this.getBenchmarkData().getUserLog(u_tmp);
            this.getBenchmarkData().discoveryList.put(u_tmp, this.createDiscoveriesInLog(u_log));
        }

    }
    
    /**
     * 
     * @param arg_u
     * @return 
     */
    public Collection<AbstractDiscovery> getDiscoveryListByUser(User arg_u) {
        return this.getBenchmarkData().discoveryList.get(arg_u);
    }
    
    /**
     * 
     * @return 
     */
    public Collection<AbstractDiscovery> getDiscoveryList() {
        Collection<AbstractDiscovery> result    = new HashSet<>();
        
        for(User u_tmp : this.getBenchmarkData().getUserList()) {
            result.addAll(this.getDiscoveryListByUser(u_tmp));
        }
        
        return result;
    }
    
    /**
     * Create discoveries in a given log.
     * @param arg_log
     * @return 
     */
    public Collection<AbstractDiscovery> createDiscoveriesInLog(Log arg_log) {
        DiscoveryManager dm = new DiscoveryManager(this);
        return dm.createDiscoveries(arg_log.computeCellList().getCellCollection());
    }
    
    /**
     * This function evaluates a SUT.
     * It takes as input a SUT and returns a score, which contains a detailed score for a SUT
     * @param arg_sut
     * @return 
     */
    public SutResolutionScore evaluateSut(I_SUT arg_sut) {
        
        return this.evaluator.evaluateSut(arg_sut);
    }

    /**
     * 
     * @return 
     */
    public OlapSystem getBenchmarkData() {
        return this.benchmarkData;
    }
    
    /**
     * 
     * @return 
     */
    public Log getLog() {
        return this.getBenchmarkData().getLog();
    }
    
    /**
     * 
     * @param arg_cubeName
     * @return 
     */
    public EAB_Cube getInternalCube(String arg_cubeName) {
        return this.getBenchmarkData().getInternalCubeList().get(arg_cubeName);
    }
    
    /**
     * Provides an internal cube given the cune name...
     * @param arg_cubeName
     * @return 
     */
    public EAB_Cube getInternalCubeByName(String arg_cubeName) {
        return this.getBenchmarkData().getInternalCubeList().get(arg_cubeName);
    }
    
    /**
     * Shortcut to this.getBenchmarkData().getSchemaReader().
     * 
     * @return 
     */
    public SchemaReader getSchemaReader() {
        return this.getBenchmarkData().getSchemaReader();
    }
    
    /**
     *
     * @return
     */
    public EAB_Connection getConnection() {
        return this.benchmarkData.getConnection();
    }
    
    /**
     *
     * @return
     */
    public CellList getCellList() {
        return this.benchmarkData.getCellList();
    }
    
    /**
     *
     * @return
     */
    public I_SUTScorer getSUTScorer() {
        return this.sutScorer;
    }
    
    /**
     *
     * @return
     */
    public Set<Class<? extends Metric>> getRegisteredMetrics() {
        return BenchmarkEngine.registeredMetrics;
    }
    
    /**
     *
     * @return
     */
    public Set<Class<? extends I_SUT>> getRegisteredSUTs() {
        return BenchmarkEngine.registeredSUTs;
    }
    
    /**
     *
     * @return
     */
    public Parameters getParameters() {
        return parameters;
    }
    
    /**
     * 
     */
    public void showDiscoveries() {
        for(AbstractDiscovery ad_tmp : this.getDiscoveryList()) {
            //DiscoveryAsCellTopology dact_tmp    = (DiscoveryAsCellTopology)ad_tmp;
            System.out.println("Discovery number " + ad_tmp.getDid());
            System.out.println("Number of cells: " + ad_tmp.getCellList().nbOfCells());
            System.out.println(ad_tmp);
        }
    }
    
    /**
     * 
     */
    public void connect() {
        this.benchmarkData.getConnection().open();
    }

    /**
     *
     * @return
     */
    public I_SessionClusteringAlgorithm getI_sca() {
        return i_sca;
    }

    /**
     *
     * @param i_sca
     */
    public void setI_sca(I_SessionClusteringAlgorithm i_sca) {
        this.i_sca = i_sca;
    }

    /**
     *
     * @return
     */
    public I_SessionSimilarity getSessionSimilarity() {
        return i_ss;
    }

    /**
     *
     * @param i_ss
     */
    public void setSessionSimilarity(I_SessionSimilarity i_ss) {
        this.i_ss = i_ss;
    }

    /**
     *
     * @return
     */
    public I_UserSimulator getUserSimulator() {
        return i_us;
    }

    /**
     *
     * @param i_us
     */
    public void setUserSimultor(I_UserSimulator i_us) {
        this.i_us = i_us;
    }

    /**
     *
     * @return
     */
    public SUT_Evaluator getEvaluator() {
        return evaluator;
    }

    /**
     *
     * @return
     */
    public I_FocusDetector getFocusDetector() {
        return this.i_focusDetector;
    }
    
    /**
     * Registers a new Metric implementation.
     * @param arg_class 
     */
    public void registerMetric(Class<? extends Metric> arg_class) {
        BenchmarkEngine.registeredMetrics.add(arg_class);
    }
    
    /**
     * Registers a new SUT implementation.
     * @param arg_class 
     */
    public void registerSUT(Class<? extends I_SUT> arg_class) {
        BenchmarkEngine.registeredSUTs.add(arg_class);
    }
    
    /**
     *
     * @param evaluator
     */
    public void setEvaluator(SUT_Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     *
     * @param i_focusDetector
     */
    public void setFocusDetector(I_FocusDetector i_focusDetector) {
        this.i_focusDetector = i_focusDetector;
    }
    
    /**
     *
     * @return
     */
    public I_SUTScorer getSutScorer() {
        return sutScorer;
    }

    /**
     *
     * @param sutScorer
     */
    public void setSutScorer(I_SUTScorer sutScorer) {
        this.sutScorer = sutScorer;
    }

    /**
     *
     * @return
     */
    public I_LogLoader getLogLoader() {
        return i_logLoader;
    }

    /**
     *
     * @param i_logLoader
     */
    public void setLogLoader(I_LogLoader i_logLoader) {
        this.i_logLoader = i_logLoader;
    }
    
}
