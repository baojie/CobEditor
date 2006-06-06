package edu.iastate.utils.string;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

public class HTMLHandler
{
    public HTMLHandler()
    {
    }

    /**
     * return the content of the highest <tag>...</tag> region
     *
     * @param inputStr
     * @return null if not found, tag and content if found
     */
    public static Vector getTopNestdBlock(CharSequence inputStr)
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
     * return all tags begin with 'tag'
     * no repeated nesting is detected, like <tag><tag></tag></tag>
     * if this case happens, only the top tag nesting is added into list.
     * @param tag
     * @param input
     * @return
     *
     * NOT implemented yet
     */
    public static Vector getNestdBlock(String tag, CharSequence input,
                                       boolean greedy)
    {
        String head = "<" + tag + ".*?>";
        String tail = "</" + tag + ">";
        return getNestdBlock(head, tail, input, greedy);
    }

    /**
     * get all block with given head and tail
     * head and tail are not included
     *
     * @param head
     * @param tail
     * @param input
     * @param greedy
     * @return
     */
    public static Vector getNestdBlock(String head, String tail,
                                       CharSequence input, boolean greedy)
    {
        String mid = greedy ? "(.*)" : "(.*?)";
        String patternStr = head + mid + tail;

        Vector list = new Vector();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find())
        {
            list.add(matcher.group(1));
        }
        return list;
    }

    /**
     * Getting the Text in an HTML Document
     *
     * to reaf from a URI [ a filename (e.g. file://c:/dir/file.html)
     *                     or a URL (e.g. http://host.com/page.html) ]
     *      URL url = new URI( uriStr ).toURL() ;
     *      URLConnection conn = url.openConnection() ;
     *      Html2Txt( conn.getInputStream() ) ;
     * for String strHTML
     *      InputStream stream = new StringBufferInputStream(strHTML);
     *      Html2Txt(stream);
     *
     * @param uriStr
     * @return
     */
    public static String Html2Txt(URI uri)
    {
        try
        {
            URL url = uri.toURL();
            URLConnection conn = url.openConnection();
            return Html2Txt(conn.getInputStream());
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return "";
    }

    public static String Html2Txt(String HTMLstr)
    {
        InputStream stream = new StringBufferInputStream(HTMLstr);
        return Html2Txt(stream);
    }

    public static String Html2Txt(InputStream stream)
    {
        final StringBuffer buf = new StringBuffer(1000);

        try
        {
            // Create an HTML document that appends all text to buf
            HTMLDocument doc = new HTMLDocument()
            {
                public HTMLEditorKit.ParserCallback getReader(int pos)
                {
                    return new HTMLEditorKit.ParserCallback()
                    {
                        // This method is whenever text is encountered in the HTML file
                        public void handleText(char[] data, int pos)
                        {
                            buf.append(data);
                            buf.append('\n');
                        }
                    };
                }
            };

            // Create a reader on the HTML content
            Reader rd = new InputStreamReader(stream);

            // Parse the HTML
            EditorKit kit = new HTMLEditorKit();
            kit.read(rd, doc, 0);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace(System.err);
        }

        // Return the text
        return buf.toString();
    }

    /**
     * Remove the tags "<" between  ">"
     *
     * @param strHTML String
     * @return String
     *
     * @author Jie Bao
     * @version 2004-04-01
     */
    public static String TagRemover(String strHTML)
    {
        String patternStr = "<(.*?)>";
        return strHTML.replaceAll(patternStr, "\n");
    }

    public static boolean isEmailValid(String inputString)
    {
        String EMAILVALIDATIONPATTERN =
            "m/^[\\w-_\\.]*[\\w-_\\.]@([\\w-_]+\\.)*[\\w_-]+$/s";
        boolean returnVal = false;

        returnVal = inputString.matches(EMAILVALIDATIONPATTERN);
        return returnVal;
    }

    /**
     * Getting the Links in an HTML Document
     * This method takes a URI which can be either a filename
     * (e.g. file://c:/dir/file.html)or a URL (e.g. http://host.com/page.html)
     * and returns all HREF links in the document.
     *
     * @param uriStr
     * @return
     */
    public static String[] getLinksInHTML(String uriStr)
    {
        List result = new ArrayList();

        try
        {
            // Create a reader on the HTML content
            URL url = new URI(uriStr).toURL();
            URLConnection conn = url.openConnection();
            Reader rd = new InputStreamReader(conn.getInputStream());

            // Parse the HTML
            EditorKit kit = new HTMLEditorKit();
            HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
            kit.read(rd, doc, 0);

            // Find all the A elements in the HTML document
            HTMLDocument.Iterator it = doc.getIterator(HTML.Tag.A);
            while (it.isValid())
            {
                SimpleAttributeSet s = (SimpleAttributeSet) it.getAttributes();

                String link = (String) s.getAttribute(HTML.Attribute.HREF);
                if (link != null)
                {
                    // Add the link to the result list
                    result.add(link);
                }
                it.next();
            }
        }
        catch (MalformedURLException e)
        {
        }
        catch (URISyntaxException e)
        {
        }
        catch (BadLocationException e)
        {
        }
        catch (IOException e)
        {
        }

        // Return all found links
        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
     *protected boolean isUrlValid(String url)
     *----------------------------------------
     * Checks whether the URL is valid; really basic for now;
     */

    public static boolean isUrlValid(String url)
    {

        if (url == null)
        {
            return false;
        }
        if (!url.startsWith("http://")
            && !url.startsWith("https://")
            && !url.startsWith("nntps://")
            && !url.startsWith("news://")
            && !url.startsWith("ftps://")
            && !url.startsWith("rtsp://"))
        {
            return false;
        }

        int slashPos = url.indexOf("://");
        int dotPos = url.indexOf(".");
        boolean b = slashPos != -1 && dotPos != -1 && dotPos > slashPos + 3;

        return b;
    }

    /**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
     * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
     * their HTML escape sequences.
     *
     * @param in the text to be converted.
     * @return the input string with the characters '&lt;' and '&gt;' replaced
     *  with their HTML escape sequences.
     */

    public static final String escapeHTMLTags(String in)
    {
        final char[] LT_ENCODE = "&lt;".toCharArray();
        final char[] GT_ENCODE = "&gt;".toCharArray();
        if (in == null)
        {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuffer out = new StringBuffer( (int) (len * 1.3));
        for (; i < len; i++)
        {
            ch = input[i];
            if (ch > '>')
            {
                continue;
            }
            else if (ch == '<')
            {
                if (i > last)
                {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            }
            else if (ch == '>')
            {
                if (i > last)
                {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            }
        }
        if (last == 0)
        {
            return in;
        }
        if (i > last)
        {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

}
