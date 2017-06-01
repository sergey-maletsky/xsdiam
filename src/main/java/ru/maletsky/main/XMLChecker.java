package ru.maletsky.main;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class XMLChecker {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse(new File("/home/sergey/Documents/work/Projects/FNS_Audit/testing/docs/XSD_ZI/fromdb/311_hemofarm.xml"));

// create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

// load a WXS schema, represented by a Schema instance

        Source documentsSchema = new StreamSource(new File("/home/sergey/Documents/work/Projects/FNS_Audit/testing/docs/XSD_ZI/main/documents.xsd"));
        Source schemaFile = new StreamSource(new File("/home/sergey/Documents/work/Projects/FNS_Audit/testing/docs/XSD_ZI/main/311-register_end_packing.xsd"));

        Schema schema = factory.newSchema(new Source[] {
                documentsSchema,
                schemaFile,
        });

// create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();

// validate the DOM tree
        try {
            validator.validate(new DOMSource(document));
        } catch (SAXException e) {
            // instance document is invalid!
        }
    }
}
