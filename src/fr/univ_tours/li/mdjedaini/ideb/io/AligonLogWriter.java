/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univ_tours.li.mdjedaini.ideb.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author mahfoud
 */
public class AligonLogWriter implements I_LogWriter {

    /**
     * 
     * @param arg_inFilePath
     * @param arg_outFilePath
     * @param arg_xslFilePath
     * @return 
     */
    @Override
    public String writeLog(String arg_inFilePath, String arg_outFilePath, String arg_xslFilePath) {
        
        String result   = "";
        
         try {
            // Create transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Use the factory to create a template containing the xsl file
            StreamSource ss     = new StreamSource(new FileInputStream(arg_xslFilePath));
            Templates template  = factory.newTemplates(ss);
            
            // Use the template to create a transformer
            Transformer xformer = template.newTransformer();

            // Prepare the input and output files
            Source s    = new StreamSource(new FileInputStream(arg_inFilePath));
            Result r    = new StreamResult(new FileOutputStream(arg_outFilePath));

            // Apply the xsl file to the source file and write the result to the output file
            xformer.transform(s, r);
        } catch (FileNotFoundException e) {
            
        } catch (TransformerConfigurationException e) {
            // An error occurred in the XSL file
        } catch (TransformerException e) {
            // An error occurred while applying the XSL file
            // Get location of error in input file
            SourceLocator locator = e.getLocator();
            int col = locator.getColumnNumber();
            int line = locator.getLineNumber();
            String publicId = locator.getPublicId();
            String systemId = locator.getSystemId();
        }
        
        return result;
        
    }
    
}
