package edu.iastate.utils.io;

/**
 * Jie Bao 2003-11-07
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * Provides some useful methods when working with URLs.
 * @author <a href="mailto:jsanleandro@yahoo.es"
           >Jose San Leandro Armendáriz</a>
 * @version $Revision: 1.1 $
 * @testcase unittests.org.acmsl.utils.net.URLUtilsTest
 */
public abstract class URLUtils
//    implements  Utils
{

    /**
     * Private constructor to avoid accidental instantiation.
     */
    private URLUtils()
    {};

    /**
     * Safely appends extra information to given url.
     * @param url the first part of the url.
     * @param extraInfo the new information to add.
     * @return the updated url.
     */
    public static String append(String url, String extraInfo)
    {
        String result = "";

        if ( (url != null)
            && (extraInfo != null))
        {
            int t_iQuestionPosition = url.indexOf("?");

            result =
                (t_iQuestionPosition == -1)
                ? "?"
                : "&";

            t_iQuestionPosition = extraInfo.indexOf("?");

            extraInfo =
                (t_iQuestionPosition == -1)
                ? extraInfo
                : (t_iQuestionPosition == extraInfo.length() - 1)
                ? extraInfo.substring(0, t_iQuestionPosition)
                : extraInfo.substring(0, t_iQuestionPosition)
                + "&"
                + extraInfo.substring(t_iQuestionPosition + 1);

            result = url + result + extraInfo;
        }

        return result;
    }

    /**
     *
     * Show Information in a popup window
     *
     * @param info
     * @author Jie Bao
     * @version 2003-10-29
     */
// TODO- debug
    public static void showHTML(String type, String htmlsource)
    {
        JFrame frame = new JFrame();

        JEditorPane text = new JEditorPane(type, htmlsource);
        JScrollPane panel = new JScrollPane(text);
        frame.getContentPane().add(panel);
        frame.setSize(800, 600);
        int len = 20 < htmlsource.length() ? 20 : htmlsource.length();
        frame.setTitle(htmlsource.substring(0, len));
        frame.show();
    }

    /**
     *
     * @param type
     * @param htmlsource
     * @author Jie Bao
     * @version 2003-11-06
     */
    public static void showHTML(String url)
    {
        JEditorPane jep = new JEditorPane();
        jep.setEditable(false);

        try
        {
            jep.setPage(url);
        }
        catch (IOException e)
        {
            jep.setContentType("text/html");
            jep.setText("<html>Could not load " + url + "</html>");
        }

        JScrollPane scrollPane = new JScrollPane(jep);
        JFrame f = new JFrame(url);

//      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(scrollPane);
        f.setSize(800, 600);
        f.show();
    }

    /**
     * To test if a given URL is accessible
     *
     * @author Jie Bao
     * @version 2003-10-28
     * @param urlStr - the URL string
     * @return true or false
     */
    public static boolean isURLValidate(String urlStr)
    {
        try
        {
            // Create a URL for the desired page
            URL url = new URL(urlStr);
            url.openStream();
        }
        catch (MalformedURLException e)
        {
            return false;
        }
        catch (IOException e)
        {
            return false;
        }
        return true;
    }

    // for test purpose
    public static void main(String[] args)
    {
        showHTML("http://www.iastate.edu");
    }
}
