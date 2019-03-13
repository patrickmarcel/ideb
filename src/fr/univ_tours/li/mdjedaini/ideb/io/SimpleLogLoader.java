/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.univ_tours.li.jaligon.falseto.QueryStructure.QuerySession;
import fr.univ_tours.li.jaligon.falseto.QueryStructure.SelectionFragment;
import fr.univ_tours.li.jaligon.falseto.logParsing.gpsj.StatisticalStudentSessionLogParsing;
import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.ext.falseto.FalsetoSessionConverter;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;

/**
 *
 * @author mahfoud
 */
public class SimpleLogLoader implements I_LogLoader {

    //
    BenchmarkEngine be;
    
    //
    String filePath;
    
    /**
     * 
     * @param arg_be
     * @param arg_filePath 
     */
    public SimpleLogLoader(BenchmarkEngine arg_be, String arg_filePath) {
        this.be             = arg_be;
        this.filePath       = arg_filePath;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Log loadLog() {
        Log log = new Log();
        
        FalsetoSessionConverter fsc = new FalsetoSessionConverter(this.be);
        
        try {
            File f  = new File(this.filePath);
            StatisticalStudentSessionLogParsing ssslp   = new StatisticalStudentSessionLogParsing(f.getAbsolutePath());
            fr.univ_tours.li.jaligon.falseto.QueryStructure.Log f_log   = ssslp.ReadSessionLog();
            
            for (QuerySession qs_tmp : f_log.getSessions()) {
                log.addSession(fsc.convert(qs_tmp));
            }
                        
        } catch(Exception arg_e) {
            arg_e.printStackTrace();
        }

        return log;
    }
    
//    /**
//     * 
//     * @return 
//     */
//    public Log ReadSessionLog() {
//        Log result  = new Log();
//                
//        File file               = new File(this.filePath);
//        BufferedReader reader   = null;
//
//        try {
//            reader      = new BufferedReader(new FileReader(file));
//            String text = null;
//
//            // we read until there is no line
//            while ((text = reader.readLine()) != null) {
//                
//                // we read a query
//                if(!text.startsWith("#")) {
//                    
//                    String measureLine  = reader.readLine();
//                    List<MeasureFragment> mf_list   = this.extractMeasuresFromLine(measureLine);
//                    
//                    String gbsLine      = reader.readLine();
//                    
//                    
//                    String filtersLine  = reader.readLine();
//                    
//                    
//                    if (text.contains("Relevant queries:")) {
//                        //System.out.println(text+" "+text.substring(18, text.length()));
//                        String idQueries = text.substring(18, text.length());
//                        StringTokenizer st = new StringTokenizer(idQueries, ",");
//
//                        while (st.hasMoreTokens()) {
//                            String s = st.nextToken();
//                            relevantQueriesSt.add("#" + s);
//                        }
//                    } else if (text.contains("ID question:")) {
//                        idQuestion = text.substring(13, text.length());
//
//                    }
//                }
//
//                //create a new session
//                counter++;
//                QuerySession session = new QuerySession(Integer.toString(counter));
//                session.setIdQuestion(Integer.parseInt(idQuestion));
//
//                while (text.contains("#") && !text.contains("#endSession")) {
//                    //create a new query
//                    String idQuery = text;
//                    Qfset query = new Qfset();
//                    //Read measures
//                    text = reader.readLine();
//
//                    String[] measures = text.split(",");
//
//                    for (String element : measures) //Extract measure 
//                    {
//                        query.addMeasure(MondrianObject.getMeasure(element));
//                    }
//                    //Read group by
//
//                    text = reader.readLine();
//
//                    //bug = text;
//                    String[] attributes = text.split(",");
//
//                    for (String element : attributes) {
//                        // ! split with REGEX
//                        //String[] dhl_string = element.split("\\."); // dimension hierarchy level
//                        String hierarchy = element.substring(0, element.lastIndexOf("."));
//                        String attribute = element.substring(element.lastIndexOf(".") + 1, element.length());
//                        Level l = MondrianObject.getLevel(attribute, hierarchy);
//                        query.addProjection(l);
//                    }
//                    //Read selection criteria
//                    text = reader.readLine();
//
//                    String[] selCriteria = text.split("\",");
//                    if (!selCriteria[0].equals("NONE")) {
//                        for (int k = 0; k < selCriteria.length; k++) {
//                            String selection = selCriteria[k];
//                            String[] attHierachy_val = selection.split("\\=");
//
//                            String hierarchy = attHierachy_val[0].substring(0, attHierachy_val[0].lastIndexOf("."));
//                            String attr = attHierachy_val[0].substring(attHierachy_val[0].lastIndexOf(".") + 1, attHierachy_val[0].length());
//                            String value = attHierachy_val[1].replace("\"", "");
//                            //System.out.println(attr+" "+hierarchy+" "+value);
//                            Member sl = MondrianObject.getSelection(attr, hierarchy, value);
//                            query.addSelection(sl);
//                        }
//                    }
//
//                    //useful to remove selections predicates that correspond to a projection...
//                    for (Hierarchy h : fr.univ_tours.li.jaligon.falseto.Generics.Generics.getHierarchies()) {
//                        HashSet<SelectionFragment> selections = query.getSelectionsFromHierarchy(h);
//
//                        if (!selections.isEmpty()) {
//                            List<Member> members = fr.univ_tours.li.jaligon.falseto.Generics.Connection.getCube().getSchema().getSchemaReader().withLocus().getLevelMembers(selections.iterator().next().getLevel(), true);
//
//                            if (members.size() == selections.size()) {
//                                for (SelectionFragment sf : selections) {
//                                    query.removeSelection(sf);
//                                }
//                            }
//                        }
//                    }
//
//                    if (relevantQueriesSt.contains(idQuery)) {
//                        relevantQueries.add(query);
//                    }
//                    session.add(query);
//
//                    //read the last to create a new query or session
//                    text = reader.readLine();
//                }
//
//                session.setRelevantQueries(relevantQueries);
//                result.add(session);
//
//            }
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(StatisticalStudentSessionLogParsing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(StatisticalStudentSessionLogParsing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//
//        return result;
//    }
    
    /**
     * 
     * @param arg_line
     * @return 
     */
    private List<MeasureFragment> extractMeasuresFromLine(String arg_line) {
        List<MeasureFragment> result    = new ArrayList<>();
        
        
        
        return result;
    }
    
    /**
     * 
     * @param arg_line
     * @return 
     */
    private List<ProjectionFragment> extractProjectionListFromLine(String arg_line) {
        List<ProjectionFragment> result    = new ArrayList<>();
        
        
        
        return result;
    }
    
    /**
     * 
     * @param arg_line
     * @return 
     */
    private List<SelectionFragment> extractSelectionListFromLine(String arg_line) {
        List<SelectionFragment> result    = new ArrayList<>();
        
        
        
        return result;
    }
    
}
