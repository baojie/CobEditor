package edu.iastate.utils.string;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.transform.*;

/**
 * A simple XML parser
 *
 * @author Jie Bao
 * @since 1.0 2004-10-17
 */
public class SimpleXMLParser
    extends ParserUtils
{
    /**
     * return the content of the highest <tag>...</tag> region
     *
     * @param inputStr
     * @return null if not found, tag and content if found
     */
    public static Vector getTopNestedBlock(CharSequence inputStr)
    {
        // Compile regular expression with a back reference to group 1
        String patternStr = "(?s)<(\\S+?).*?>(.*)</\\1>";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);

// Get tagname and contents of tag
        boolean matchFound = matcher.find(); // true
        if (matchFound)
        {
            Vector list = new Vector();
            list.add(matcher.group(1)); // tag
            list.add(matcher.group(2)); //  content
            return list;
        }
        else
        {
            return null;
        }
    }

    /**
     * Print XML in text
     * @param inputStr CharSequence
     * @return CharSequence
     * @author Jie Bao
     * @since 2004-10-17
     */
    public static CharSequence printXMLSkeleton(CharSequence inputStr)
    {
        String str = (String) removeCRLF(inputStr);
        //str = (String) removeDuplicateBlanks(str);
        TaggedText tt = new TaggedText();
        tt.fromXML(str);
        return tt.print(0);
    }

    /**
     * Return a value on certain XPath
     * @param doc Document
     * @param xpath String
     * @return int
     * @since 2005-02-24
     */
    public static int parseSingleValueInt(Node doc, String xpath) throws
        Exception
    {
        try
        {
            NodeList nodelist = XPathAPI.selectNodeList(doc, xpath);
            Text elem = (Text) nodelist.item(0).getFirstChild();
            int i = Integer.parseInt(elem.getData());
            return i;
        }
        catch (DOMException ex)
        {
            throw new DOMException(ex.code, ex.getMessage());
        }
        catch (NumberFormatException ex)
        {
            throw new NumberFormatException(ex.getMessage());
        }
        catch (TransformerException ex)
        {
            throw new TransformerException(ex.getMessage());
        }
    }

    public static String parseSingleValueString(Node doc, String xpath) throws
        Exception
    {
        try
        {
            NodeList nodelist = XPathAPI.selectNodeList(doc, xpath);
            //System.out.println(xpath + " : "+ nodelist);
            Text elem = (Text) nodelist.item(0).getFirstChild();
            //System.out.println(xpath + " : "+ elem);
            String result = elem.getData().toString();
            //System.out.println(xpath + " : "+ result);
            return result;
        }
        catch (DOMException ex)
        {
            System.err.println(ex);
            throw new DOMException(ex.code, ex.getMessage());
        }
        catch (NumberFormatException ex)
        {
            System.err.println(ex);
            throw new NumberFormatException(ex.getMessage());
        }
        catch (TransformerException ex)
        {
            System.err.println(ex);
            throw new TransformerException(ex.getMessage());
        }
    }

    /**
     * Parses a string containing XML and returns a DocumentFragment
     * containing the nodes of the parsed XML.
     *
     * @param fragment String
     * @return Document
     * @since 2005-02-24
     */
    public static Document parseXmlString(String fragment)
    {
        try
        {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Create the builder and parse the file
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new
                StringReader(fragment)));
            return doc;
        }
        catch (SAXException e)
        {
            // A parsing error occurred; the xml input is not valid
        }
        catch (ParserConfigurationException e)
        {
        }
        catch (IOException e)
        {
        }
        return null;
    }

    // This method writes a DOM document to a file
    // 2005-03-02
    public static void writeXmlFile(Document doc, String filename)
    {
        try
        {
            // Prepare the DOM document for writing
            Source source = new DOMSource(doc);

            // Prepare the output file
            File file = new File(filename);
            file.createNewFile();

            Result result = new StreamResult(file);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().
                newTransformer();
            xformer.transform(source, result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Transform a document to a string
     * @param doc Document
     * @return String
     * @since 2005-03-22
     */
    public static String documentToString(Document doc, String charset)
    {
        try
        {
            Source source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);

            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().
                newTransformer();
            xformer.setOutputProperty(OutputKeys.ENCODING, charset);
            xformer.transform(source, result);
            String str = writer.toString();
            return str.replaceAll("[\\n|\\r|\\f]", "");
        }
        catch (TransformerException ex)
        {
        }
        catch (TransformerFactoryConfigurationError ex)
        {
        }
        return null;
    }

    // Parses an XML file and returns a DOM document.
    // If validating is true, the contents is validated against the DTD
    // specified in the file.
    // 2005-03-02 Jie Bao
    public static Document parseXmlFile(String filename, boolean validating)
    {
        try
        {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validating);

            // Create the builder and parse the file
            Document doc = factory.newDocumentBuilder().parse(new File(filename));
            return doc;
        }
        catch (SAXException e)
        {
            // A parsing error occurred; the xml input is not valid
        }
        catch (ParserConfigurationException e)
        {
        }
        catch (IOException e)
        {
        }
        return null;
    }

    public static boolean find(Node doc, String xpath)
    {
        try
        {
            NodeList nodelist = XPathAPI.selectNodeList(doc, xpath);
            return (nodelist.getLength() != 0);
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}

/**
 * @author Jie Bao
 * @since 1.0 2005-02-27
 */
class SimpleXMLParserTest
{
    public static void main(String[] args)
    {
        try
        {
            String str = "<head><mam>M</mam><dad>F</dad></head>";
            Document doc = SimpleXMLParser.parseXmlString(str);
            String r = SimpleXMLParser.parseSingleValueString(doc,
                "/head/mam");
            System.out.println(r);
        }
        catch (Exception ex)
        {
        }

    }
}
