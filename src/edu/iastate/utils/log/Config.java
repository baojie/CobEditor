package edu.iastate.utils.log;

import java.io.File;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import edu.iastate.utils.io.FileUtils;
import edu.iastate.utils.string.SimpleXMLParser;

/**
 * @author Jie Bao
 * @since  2005-03-07
 */
abstract public class Config
{
    public Config(String configFileName, boolean isSave)
    {
        try
        {
            configFile = configFileName;

            if (isSave)
            {
                createNew();
            }
            else if (!FileUtils.isFileExists(configFile))
            {
                // create a new config file
                createNew();
            }
            else
            {
                // read the config file
                DocumentBuilderFactory factory = DocumentBuilderFactory.
                    newInstance();
                // Create the builder and parse the file
                configXML = factory.newDocumentBuilder().parse(new File(
                    configFile));
            }

//            KeyGenerator kg = KeyGenerator.getInstance("DES");
//            key = kg.generateKey();
//            cipher = Cipher.getInstance("DES");

        }
        catch (Exception ex)
        {
        }
    }

//    static Key key;
//    static Cipher cipher;

    /**
     *
     * @param str String
     * @return String
     * @since 2005-03-14
     */
    public static String Encode(String str)
    {
        try
        {
            Deflater compressor = new Deflater();

            byte[] plainText = str.getBytes("UTF-8");
            //Debug.trace("plainText.length=" + plainText.length);

            byte[] output = new byte[1024];
            compressor.setInput(plainText);
            compressor.finish();
            int compressedDataLength = compressor.deflate(output);
            //Debug.trace("compressedDataLength=" + compressedDataLength);

            // Get the compressed data
            StringBuffer ss = new StringBuffer();
            for (int i = 0; i < compressedDataLength; i++)
            {
                ss.append(new Byte(output[i]) + "_");
            }
            //Debug.trace(ss.toString());
            return ss.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *
     * @param str String
     * @return String
     * @since 2005-03-14
     */
    public static String Decode(String str)
    {
        try
        {
            Inflater decompressor = new Inflater();
            if (str == null || str.length() == 0)
            {
                return "";
            }

            String bb[] = str.split("_");
            //Debug.trace(bb.length);
            byte[] plainText = new byte[bb.length - 1];
            for (int i = 0; i < plainText.length; i++)
            {
                plainText[i] = Byte.parseByte(bb[i]);
            }
            //byte[] plainText = str.getBytes();

            decompressor.setInput(plainText);

            byte[] result = new byte[1024];
            int resultLength = decompressor.inflate(result);
            decompressor.end();

            // Decode the bytes into a String
            String outputString = new String(result, 0, resultLength, "UTF-8");
            //Debug.trace(outputString);

            return outputString;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    static public void setPropertyCipher(Element parent, String tag,
                                         String value)
    {
        setProperty(parent, tag, Encode(value));
    }

    // 2005-03-07
    static public void setProperty(Element parent, String tag, String value)
    {
        try
        {
            Document doc = parent.getOwnerDocument();
            // if the tag already exists, delete it
            Element n = (Element) XPathAPI.selectSingleNode(parent, tag);
            if (n != null)
            {
                parent.removeChild(n);
            }

            // create a new one
            Element tagNode = doc.createElement(tag);
            tagNode.appendChild(doc.createTextNode(value));
            parent.appendChild(tagNode);
        }
        catch (DOMException ex)
        {
        }
        catch (TransformerException ex)
        {
        }
    }

    static public String getPropertyCipher(Node parent, String tag)
    {
        return Decode(getProperty(parent, tag));
    }

    static public String getProperty(Node parent, String tag)
    {
        try
        {
            Node n = XPathAPI.selectSingleNode(parent, tag);
            if (n != null)
            {
                Text elem = (Text) n.getFirstChild();
                if (elem != null)
                {
                    return elem.getData().toString();
                }
            }
        }
        catch (DOMException ex)
        {
        }
        catch (TransformerException ex)
        {
        }
        return "";
    }

    static public int getPropertyInt(Node parent, String tag)
    {
        try
        {
            return Integer.parseInt(getProperty(parent, tag));
        }
        catch (NumberFormatException ex)
        {
            return 0;
        }
    }

    // 2005-03-07
    static public boolean getPropertyBoolean(Node parent, String tag)
    {
        return Boolean.getBoolean(getProperty(parent, tag));
    }

    /**
     *
     * @param parent Node
     * @param newTag String
     * @return Node
     * @since 2005-03-08
     */
    protected Node addChildTag(Node parent, String newTag)
    {
        Element newChild = configXML.createElement(newTag);
        return parent.appendChild(newChild);
    }

    public Document configXML;
    public String configFile = "config.xml";

    protected void createBlankNew()
    {
        try
        {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().
                newDocumentBuilder();
            configXML = builder.newDocument();
        }
        catch (FactoryConfigurationError ex)
        {
        }
        catch (ParserConfigurationException ex)
        {
        }
    }

    public void save(Object obj)
    {
        // clear old XML
        //createNew();

        objToXML(obj);

        String xml = getXML();
        //SimpleXMLParser.writeXmlFile(configXML, configFile);
        //System.out.println(xml);
        try
        {
            FileUtils.writeFile(configFile, xml);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void load(Object obj)
    {
        xmlToObj(obj);
    }

    public String getXML()
    {
        //System.out.println(charset);
        return SimpleXMLParser.documentToString(configXML, charset);
    }

    String charset = "UTF-8";

    /**
     *
     * @param xpath String
     * @return Element
     * @author Jie Bao
     * @since 2005-04-01
     */
    public Element findNode(Node contextNode, String xpath)
    {
        Element node = null;
        try
        {
            if (contextNode == null)
            {
                node = (Element) XPathAPI.selectSingleNode(
                    configXML, xpath);
            }
            else
            {
                node = (Element) XPathAPI.selectSingleNode(
                    contextNode, xpath);
            }
        }
        catch (TransformerException ex)
        {
        }
        return node;
    }

    abstract protected void createNew();

    abstract protected void objToXML(Object obj);

    abstract protected void xmlToObj(Object obj);

    public void setCharset(String charset)
    {
        this.charset = charset;
    }

}
