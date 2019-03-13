/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.univ_tours.li.mdjedaini.ideb.BenchmarkEngine;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.MeasureFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.ProjectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.QueryTriplet;
import fr.univ_tours.li.mdjedaini.ideb.olap.query.SelectionFragment;
import fr.univ_tours.li.mdjedaini.ideb.struct.Log;
import fr.univ_tours.li.mdjedaini.ideb.struct.Session;

/**
 *
 * @author mahfoud
 */
public class XMLLogLoader implements I_LogLoader {

    //
    BenchmarkEngine be;
    
    //
    String filePath;
    ArrayList<Element> sessionList;
    
    // do not load queries with number of cells > querySizeLimit
    Integer querySizeLimit  = 500;
    
    Element root;
    
    /**
     * 
     * @param arg_be
     * @param arg_filePath 
     */
    public XMLLogLoader(BenchmarkEngine arg_be, String arg_filePath) {
        this.be             = arg_be;
        this.filePath       = arg_filePath;
        this.sessionList    = new ArrayList();
    }
    
    /**
     * 
     * @return 
     */
    public Log loadLog() {
        Log log = new Log();
        
        // gather the session elements
        this.loadSessions();
        
        for(Element session : this.sessionList) {
            // chargement de la session
            Session s   = this.loadSession(session);
            log.addSession(s);
        }
        
        return log;
    }
    
    /**
     * 
     */
    public void loadSessions() {
        // 
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            	
        try {
            // we create a parser
            final DocumentBuilder builder   = factory.newDocumentBuilder();
            final Document document         = builder.parse(new File(this.filePath));
            
	    /*
	     * Etape 4 : récupération de l'Element racine
	     */
	    final Element racine = document.getDocumentElement();
            this.root            = racine;
            
	    // get all the session tags
	    final NodeList sessionTags  = racine.getElementsByTagName("Session");
	    final int nbSessions        = sessionTags.getLength();
			
            // iterate over sessions
	    for (int i = 0; i < nbSessions; i++) {
                
	        if(sessionTags.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            final Element session = (Element)sessionTags.item(i);
                    
                    
                    
                    this.sessionList.add(session);
	        }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 
     * @param arg_sessionElement Element tag in the XML file
     * @return 
     */
    public Session loadSession(Element arg_sessionElement) {
        
        Session s   = new Session();
        
        s.addMetaData("template", arg_sessionElement.getAttribute("template"));
        s.addMetaData("name", this.root.getElementsByTagName("Date-Time").item(0).getTextContent() + "-" + arg_sessionElement.getAttribute("progressive") + "-" + arg_sessionElement.getAttribute("template"));
        
        // get all the session tags
        final NodeList queryTags    = arg_sessionElement.getElementsByTagName("Query");
        final int nbQueries         = queryTags.getLength();
        
        // iterate over sessions
	for (int i = 0; i < nbQueries; i++) {
                
	    if(queryTags.item(i).getNodeType() == Node.ELEMENT_NODE) {
	        final Element query = (Element)queryTags.item(i);

                // chargement de la requete
                QueryTriplet qt = this.loadQuery(query);
                
                Integer numberOfCells   = qt.computeNumberOfCells();
                //s.addQuery(qt);
                
                // @todo only add
                if(numberOfCells < this.querySizeLimit) {
                    s.addQuery(qt);
                } else {
                    System.out.println(arg_sessionElement.getAttribute("template") + " - Query too big! " + numberOfCells + " cells");
                }
            }
            
        }	
        
        return s;
        
    }
    
    /**
     * 
     * @param arg_queryElement
     * @return 
     */
    public QueryTriplet loadQuery(Element arg_queryElement) {
        QueryTriplet q  = new QueryTriplet(this.be.getDefaultCube());
          
        // Measures
        NodeList measureElements    = arg_queryElement.getElementsByTagName("Measures").item(0).getChildNodes();
        int nbMeasures              = measureElements.getLength();

        // iterate over sessions
	for (int i = 0; i < nbMeasures; i++) {
                
	    if(measureElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
	        final Element measureElement    = (Element)measureElements.item(i);

                MeasureFragment mf  = new MeasureFragment(q, measureElement.getAttribute("value"));
                q.addMeasure(mf);
                
                // chargement de la session
                //this.loadQuery(query);
            }
            
        }
            
        // GROUP BY SET
        // Measures
        Element gbsElement      = (Element)arg_queryElement.getElementsByTagName("GroupBy").item(0);
        NodeList gbsElements    = gbsElement.getElementsByTagName("Element");
        int nbGbs               = gbsElements.getLength();

        // iterate over GBS
	for (int i = 0; i < nbGbs; i++) {
                
	    if(gbsElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
	        
                // element <Element> contenant <Hierarchy> et <Level>
                Element hierarchyLevel  = (Element)gbsElements.item(i);

                Element hierarchyElement    = (Element)hierarchyLevel.getElementsByTagName("Hierarchy").item(0);
                Element levelNameElement    = (Element)hierarchyLevel.getElementsByTagName("Level").item(0);
                
                String hierarchyName    = hierarchyElement.getAttribute("Value");
                String levelName        = levelNameElement.getAttribute("value");
                
                ProjectionFragment pf   = new ProjectionFragment(q, hierarchyName, hierarchyName, levelName);
                q.addProjection(pf);
                
	        }
            
	    }
        
        // iterate over Selections
        NodeList selectionElements  = arg_queryElement.getElementsByTagName("SelectionPredicates").item(0).getChildNodes();
        int nbSelections            = selectionElements.getLength();

        // iterate over selection predicates
	for (int i = 0; i < nbSelections; i++) {
                
	    if(selectionElements.item(i).getNodeType() == Node.ELEMENT_NODE) {
	        final Element selectionElement    = (Element)selectionElements.item(i);
                
                Element hierarchyElement        = (Element)selectionElement.getElementsByTagName("Hierarchy").item(0);
                Element levelNameElement        = (Element)selectionElement.getElementsByTagName("Level").item(0);
                Element selectedValueElement    = (Element)selectionElement.getElementsByTagName("Predicate").item(0);
                //Element yearPromptElement   = (Element)selectionElement.getElementsByTagName("YearPrompt").item(0);
                //Element segregationElement  = (Element)selectionElement.getElementsByTagName("SegregationPredicate").item(0);
                                
                String hierarchyName    = hierarchyElement.getAttribute("value");
                String levelName        = levelNameElement.getAttribute("value");
                String selectedValue    = selectedValueElement.getAttribute("value");
                
                SelectionFragment sf    = new SelectionFragment(q, hierarchyName, hierarchyName, levelName, selectedValue);
                q.addSelection(sf);

                //MeasureFragment mf  = new MeasureFragment();
                // chargement de la session
                //this.loadQuery(query);
	        }
            
	    }
        
        return q;
    }
    
}
