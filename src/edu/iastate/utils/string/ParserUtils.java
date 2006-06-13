package edu.iastate.utils.string;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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

/**
 * <p>Title: ParserUtils</p>
 * <p>Description: Some regular expression routines </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 1.0 - 2003-11-07
 *
 * note:
 * CharBuffer, String, StringBuffer are all implement of CharSequence interface
 *
 */

public class ParserUtils
    extends PatternEx
{
    // Private constructor to avoid accidental instantiation.
    protected ParserUtils()
    {}

    /** Converts the contents of a file into a CharSequence
        suitable for use by the regex package.
      example
        Matcher matcher = pattern.matcher(fromFile("infile.txt"));
     */
    public static CharSequence fromFile(String filename) throws IOException
    {
        FileInputStream fis = new FileInputStream(filename);
        FileChannel fc = fis.getChannel();

        // Create a read-only CharBuffer on the file
        ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                                 (int) fc.size());
        CharBuffer cbuf = Charset.forName("8859_1").newDecoder().
            decode(bbuf);
        return cbuf;
    }

    /**
       Determining If a String Matches a Pattern Exactly
     * @param patternStr
     * @param inputStr
     * @return
     */
    public static boolean isFound(String patternStr,
                                  CharSequence inputStr)
    {
        // Compile regular expression
        Pattern pattern = Pattern.compile(patternStr);

        // Determine if there is an exact match
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches(); //
    }

    /**
         // Returns a version of the input where all contiguous
         // whitespace characters are replaced with a single
         // space. Line terminators are treated like whitespace.
     * @param inputStr
     * @return
     */
    public static CharSequence removeDuplicateBlanks(CharSequence
        inputStr)
    {
        String patternStr = BLANKS;
        String replaceStr = " ";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.replaceAll(replaceStr);
    }

    /**
     * Returns the first substring in input that matches the pattern.
     * Returns null if no match found.
     *
     * @param patternStr
     * @param input
     * @return
     */
    public static String findFirst(String patternStr, CharSequence input)
    {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find())
        {
            return matcher.group();
        }
        return null;
    }

    public static String findFirst(String head, String tail,
                                   CharSequence input,
                                   boolean greedy)
    {
        String mid = greedy ? ".*" : ".*?";
        return findFirst(head + mid + tail, input);
    }

    /**
     * Returns all substrings in input that matches the pattern.
     * @param patternStr
     * @param input
     * @return
     */
    public static Vector findAll(String patternStr, CharSequence input)
    {
        Vector list = new Vector();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find())
        {
            list.add(matcher.group());
        }
        return list;
    }

    public static Vector findAll(String head, String tail,
                                 CharSequence input,
                                 boolean greedy)
    {
        String mid = greedy ? ".*" : ".*?";
        return findAll(head + mid + tail, input);
    }

    public static String replaceAll(String patternStr, String replacementStr,
                                    CharSequence input)
    {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(replacementStr);
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
    public static String[] getLinks(String uriStr)
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
         // Getting the Text in an HTML Document
         // This method takes a URI which can be either a filename (e.g. file://c:/dir/file.html)
         // or a URL (e.g. http://host.com/page.html) and returns all text in the document.
     * @param uriStr
     * @return
     */
    public static String getTextFromHTML(String uriStr)
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
            URL url = new URI(uriStr).toURL();
            URLConnection conn = url.openConnection();
            Reader rd = new InputStreamReader(conn.getInputStream());

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

    // Returns a pattern where all punctuation characters are escaped.
    /**
         String patternStr = "i.e.";
         boolean matchFound = Pattern.matches(patternStr, "i.e.");// true
         matchFound = Pattern.matches(patternStr, "ibex");        // true
         // Escape the pattern
         patternStr = escapeRE(patternStr);                       // i\.e\.
         matchFound = Pattern.matches(patternStr, "i.e.");        // true
         matchFound = Pattern.matches(patternStr, "ibex");        // false
     */
    public static String escapeRE(String str)
    {
        Pattern escaper = Pattern.compile("([^a-zA-z0-9])");
        return escaper.matcher(str).replaceAll("\\\\$1");
    }

    /**
     * Reading Lines from a String Using a Regular Expression
     * The lines can be terminated with any of the legal line termination
     *  character sequences: \r, \r\n, or \n.
     *
     * @return
     */
    public static Vector readLines(CharSequence inputStr,
                                   boolean keepLineTerminator)
    {
        Vector list = new Vector();
        // Compile the pattern
        String patternStr = "^(.*)$";
        Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(inputStr);

        // Read the lines
        while (matcher.find())
        {
            if (keepLineTerminator)
            {
                // Get the line with the line termination character sequence
                list.add(matcher.group(0));
            }
            else
            {
                // Get the line without the line termination character sequence
                list.add(matcher.group(1));
            }
        }
        return list;
    }

    // Returns a version of the input where all line terminators
    // are replaced with a space.
    public static CharSequence removeLineTerminators(CharSequence inputStr)
    {
        String patternStr = "(?m)$^|[\\r\\n]+\\z";
        String replaceStr = " ";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.replaceAll(replaceStr);
    }

    /**
     * Remove all carriage return and line feed
     * @param inputStr CharSequence
     * @return CharSequence
     * @since 2004-10-17
     * @author Jie Bao
     */
    public static CharSequence removeCRLF(CharSequence inputStr)
    {
        String str = inputStr.toString();
        return str.replaceAll("[\\r|\\n]", " ");
    }

    public static Vector convertToParagraphs(CharSequence inputStr)
    {
        Vector list = new Vector();
        String patternStr = "(?<=(\r\n|\r|\n))([ \\t]*$)+";

        // Parse the input into paragraphs
        String[] paras = Pattern.compile(patternStr,
                                         Pattern.MULTILINE).split(inputStr);

        // Get paragraphs
        for (int i = 0; i < paras.length; i++)
        {
            list.add(paras[i]);
        }
        return list;
    }

    /**
     * get all blocks with given head and tail
     * head and tail are not included
     *
     * @param head
     * @param tail
     * @param input
     * @param greedy
     * @return
     */
    public static Vector getNestedBlock(String head, String tail,
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
     * get the first block with given head and tail
     * head and tail are not included
     *
     * @param head
     * @param tail
     * @param input
     * @param greedy
     * @return
     * @since 2005-03-03
     * @author Jie Bao
     */
    public static String getFirstNestedBlock(String head, String tail,
                                             CharSequence input, boolean greedy)
    {
        String mid = greedy ? "(.*)" : "(.*?)";
        String patternStr = head + mid + tail;

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find())
        {
            return matcher.group(1);
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
     */
    public static Vector getNestedBlock(String tag, CharSequence input,
                                        boolean greedy)
    {
        String head = "<" + tag + ".*?>";
        String tail = "</" + tag + ">";
        return getNestedBlock(head, tail, input, greedy);
    }

}
