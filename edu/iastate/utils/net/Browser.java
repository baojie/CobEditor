package edu.iastate.utils.net;

import java.io.IOException;
import java.net.URL;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Simple illustration of GUI construction for a rudimentary
 * web-browser.
 * <P>
 * Illustrates Layouts, Buttons, Menus, and simple GUI construction.
 * Exercises for the reader include making the buttons functional,
 * adding bookmarks, and more. This can be done via extension
 * or by modifying the existing class.
 *
 * <P>
 * When creating a browser, client code should set the size of
 * of the frame (note that Browser extends JFrame).
 * <P>
 * <PRE>
 *   Browser b = new Browser();
 *   b.setSize(400,400);
 *   b.setVisible(true);
 * </PRE>
 *
 *  Jie Bao Modified 2003-10-21
 */

public class Browser
    extends JFrame
{
    private JEditorPane myEditor; // displays web page
    private JLabel myNextURL; // if link clicked, go here
    private JTextField myURLDisplay; // user-entered url
    private JButton myGoButton; // goes to file located in myURLDisplay
    private String myHome; // url of user selected homepage

    /**
     * Construct the web browser, calling code should set size of frame.
     */
    public Browser()
    {
        myEditor = new JEditorPane();
        myEditor.setEditable(false); // allows links to be followed
        myHome = ""; // no home page, but not null
        initGui();
    }

    public Browser(String url)
    {
        this();
        showPage(url);
    }

    /**
     * Make user-entered URL/text field and back/next/home/go buttons
     * @return returns JPanel containing buttons
     */

    private JPanel makeTopPanel()
    {

        myGoButton = new JButton("Go");
        myGoButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                showPage(myURLDisplay.getText());
            }
        });
        myURLDisplay = new JTextField(35);

        JLabel topLabel = new JLabel("Address ");
        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.add(topLabel, BorderLayout.WEST);
        urlPanel.add(myURLDisplay, BorderLayout.CENTER);
        urlPanel.add(myGoButton, BorderLayout.EAST);

        // if user presses return, load/show the URL
        myURLDisplay.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                showPage(e.getActionCommand());
            }
        });

        return urlPanel;
    }

    /**
     * Make the panel where "would-be" clicked URL is displayed
     * and the
     * @return returns a JPanel containing the HTML window and the
     * panel at the bottom that
     * lists the URL of a rolled-over link
     */
    private JPanel makeBottomPanel()
    {
        // bottomLabel is a hack. Because the myNextURL label
        // is initially empty it doesn't show up, the bottomLabel does

        myNextURL = new JLabel();
        JLabel bottomLabel = new JLabel(" ");
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bottomLabel, BorderLayout.WEST);
        bottomPanel.add(myNextURL, BorderLayout.CENTER);
        return bottomPanel;
    }

    /**
     * make all UI components, lay them out, ready to use
     */
    private void initGui()
    {
        JPanel topPanel = makeTopPanel();
        JPanel bottomPanel = makeBottomPanel();

        // make editor respond to link-clicks/mouse-overs, make it scroll
        myEditor.addHyperlinkListener(new LinkFollower());
        JScrollPane scroller = new JScrollPane(myEditor);

        // add components to frame, make it exit when closed
        // default for contentPane is BorderLayout, use this

//    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(scroller, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

//    setTitle("Duke/JETT Wowser/Browser");
    }

    /**
     * displays the url the user enters and echo
     * the url in the textfield (even if it came from there!)
     * the echo makes it so link-clicking shows the URL
     */
    public void showPage(String url)
    {
        try
        {
            myEditor.setPage(url);
            myURLDisplay.setText(url);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "could not load " + url);
        }
    }

    /** Show where link would take us if clicked
     * @param s URL of link
     */

    private void showNextURL(String s)
    {
        myNextURL.setText(s);
    }

    /**
     * Inner class to deal with link-clicks and mouse-overs
     */

    private class LinkFollower
        implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent evt)
        {
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
                // user clicked a link, load it and show it
                URL url = null;
                try
                {
                    url = evt.getURL();
                    showPage(url.toString());
                }
                catch (Exception e)
                {
                    String s = evt.getURL().toString();
                    JOptionPane.showMessageDialog(
                        Browser.this,
                        "loading problem for " + s + " " + e,
                        "Load Problem",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED)
            {
                // user moused-into a link, show what would load

                try
                {
                    showNextURL(evt.getURL().toString());
                }
                catch (Exception e)
                {
                    // nothing to do, if URL fails, don't pre-announce
                }
            }
            else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED)
            {
                // user moused-out of a link, erase what was shown

                showNextURL("");
            }
        }
    }
    /**
     * Open a web page in windows default browser
     * @param pageURL String
     * @author JieBao
     * @since 2005-03-14
     */
    public static void openInWindowsDefaultBrowser(String pageURL)
    {
        try
        {
            Runtime.getRuntime().exec("cmd.exe  /c  start " + pageURL);
        }
        catch (IOException ex)
        {
        }
    }

    /** opens a new browser window */
    public static void main(String args[])
    {
        Browser b = new Browser();
        b.setSize(600, 600);
        b.setLocation(10, 20);
        b.setVisible(true);
    }
}
