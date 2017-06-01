package ru.maletsky.main;

import org.apache.xmlbeans.*;
import org.apache.xmlbeans.impl.tool.CommandLine;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class XMLGenerator {
    public static void printUsage()
    {
        System.out.println("Generates a document based on the given Schema file");
        System.out.println("having the given element as root.");
        System.out.println("The tool makes reasonable attempts to create a valid document,");
        System.out.println("but this is not always possible since, for example, ");
        System.out.println("there are schemas for which no valid instance document ");
        System.out.println("can be produced.");
        System.out.println("Usage: xsd2inst [flags] schema.xsd -name element_name");
        System.out.println("Flags:");
        System.out.println("    -name    the name of the root element");
        System.out.println("    -dl      enable network downloads for imports and includes");
        System.out.println("    -nopvr   disable particle valid (restriction) rule");
        System.out.println("    -noupa   diable unique particle attributeion rule");
        System.out.println("    -license prints license information");
    }

    public static void main(String[] args) throws IOException {
        Set flags = new HashSet();
        Set opts = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("dl");
        flags.add("noupa");
        flags.add("nopvr");
        flags.add("partial");
        opts.add("name");

        CommandLine cl = new CommandLine(new String[]{"/home/sergey/Documents/work/Projects/FNS_Audit/testing/docs/XSD_ZI/main/201-ref_query_clients.xsd"}, flags, opts);

        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null)
        {
            printUsage();
            return;
        }

        String[] badOpts = cl.getBadOpts();
        if (badOpts.length > 0)
        {
            for (int i = 0; i < badOpts.length; i++)
                System.out.println("Unrecognized option: " + badOpts[i]);
            printUsage();
            return;
        }

        if (cl.getOpt("license") != null)
        {
            CommandLine.printLicense();
            System.exit(0);
            return;
        }

        if (cl.getOpt("version") != null)
        {
            CommandLine.printVersion();
            System.exit(0);
            return;
        }

        boolean dl = (cl.getOpt("dl") != null);
        boolean nopvr = (cl.getOpt("nopvr") != null);
        boolean noupa = (cl.getOpt("noupa") != null);

        File[] schemaFiles = cl.filesEndingWith(".xsd");
        String rootName = "ref_query_clients";

        List sdocs = new ArrayList();
        for (int i = 0; i < schemaFiles.length; i++)
        {
            try
            {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[i],
                        (new XmlOptions()).setLoadLineNumbers().setLoadMessageDigest()));
            }
            catch (Exception e)
            {
                System.err.println("Can not load schema file: " + schemaFiles[i] + ": ");
                e.printStackTrace();
            }
        }

        XmlObject[] schemas = (XmlObject[]) sdocs.toArray(new XmlObject[sdocs.size()]);

        SchemaTypeSystem sts = null;
        if (schemas.length > 0)
        {
            Collection errors = new ArrayList();
            XmlOptions compileOptions = new XmlOptions();
            if (dl)
                compileOptions.setCompileDownloadUrls();
            if (nopvr)
                compileOptions.setCompileNoPvrRule();
            if (noupa)
                compileOptions.setCompileNoUpaRule();

            try
            {
                sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
            }
            catch (Exception e)
            {
                if (errors.isEmpty() || !(e instanceof XmlException))
                    e.printStackTrace();

                System.out.println("Schema compilation errors: ");
                for (Iterator i = errors.iterator(); i.hasNext(); )
                    System.out.println(i.next());
            }
        }

        if (sts == null)
        {
            System.out.println("No Schemas to process.");
            return;
        }
        SchemaType[] globalElems = sts.documentTypes();
        SchemaType elem = null;
        for (int i = 0; i < globalElems.length; i++)
        {
            if (rootName.equals(globalElems[i].getDocumentElementName().getLocalPart()))
            {
                elem = globalElems[i];
                break;
            }
        }

        if (elem == null)
        {
            System.out.println("Could not find a global element with name \"" + rootName + "\"");
            return;
        }

        String result = SampleXmlUtil.createSampleForType(elem);
        System.out.println(result);

        File file = new File("/home/sergey/Documents/work/Projects/FNS_Audit/testing/xsd_test/xmls/xmlFromGenerator_v2");
        if (!file.exists()) {
            file.createNewFile();
        }

        try(FileWriter writer = new FileWriter(file, false)) {
            writer.append(result);
            writer.flush();
        } catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        return;
    }
}
