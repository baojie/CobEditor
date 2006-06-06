package edu.iastate.utils;

/**
 * <p>Title: Debug utilities </p>
 * <p>Description: Some Debug Routines </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 2003-09-19
 *          2004-10-16 add trace(Object), trace(integer), trace(boolean), trace(float)
 */

import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Debug
{
    static boolean debugMode = true;

    public Debug(boolean mode)
    {
        debugMode = mode;
    }

    public Debug()
    {
        debugMode = true;
    }

    /**
     * Set debug mode
     *
     * @param mode
     */
    public void setDebugMode(boolean mode)
    {
        debugMode = mode;
    }

    /**
     * If in debug mode
     *
     * @return the debug status
     */
    public boolean getDebugMode()
    {
        return debugMode;
    }

    /**
     * Show a string in a message box
     * example Debug.trace(this,"Info")
     *
     * @param str : the string to show
     * @author Jie Bao
     * @version 1.0 : use JOptionPane
     *          2.0 2004-10-02 :  use JFrame for long message
     */
    static public void trace(Object obj, Object str)
    {
        if (!debugMode)
        {
            return;
        }

        String location = "";
        if (obj != null)
        {
            location = obj.getClass().getName() + ": ";
        }
        JOptionPane.showMessageDialog(null, location + str);
    }

    /**
     * Simpified trace
     * @param str Object
     * @since 2004-10-16
     * @author Jie Bao
     */
    static public void trace(Object str)
    {
        if (!debugMode)
        {
            return;
        }
        JOptionPane.showMessageDialog(null, str);
    }

    //@since 2004-10-16
    static public void trace(int i)
    {
        JOptionPane.showMessageDialog(null, i + "");
    }

    //@since 2004-10-16
    static public void trace(boolean i)
    {
        JOptionPane.showMessageDialog(null, i + "");
    }

    //@since 2004-10-16
    static public void trace(float i)
    {
        JOptionPane.showMessageDialog(null, i + "");
    }

    /**
     * @param obj Object
     * @param str String
     * @version 2004-10-02
     */
    static public void traceWin(Object obj, String str)
    {
        if (!debugMode)
        {
            return;
        }

        String location = "";
        if (obj != null)
        {
            location = obj.getClass().getName() + ": ";
        }
        JTextArea text = new JTextArea(20,40);
        text.setText(location + "\n\n" + str);
        text.setLineWrap(true);
        showControl(new JScrollPane(text));
    }

    /**
     * Print debug information to system.out
     * @param obj Object
     * @param str String
     * @author Jie Bao
     * @version 2004-07-12
     */
    static public void systrace(Object obj, String str)
    {
        if (!debugMode)
        {
            return;
        }
        String location = "";
        if (obj != null)
        {
            location = obj.getClass().getName() + ": ";
        }

        System.out.println(location + str);
    }

    /**
     * Called when application is crashing . the function prints an error
     * message and exits with the error code specifed.
     * @param view The window from which the exit was called.
     * @param errormsg The error message to display.
     * @param errorcode The errorcode to exit with.
     */
    public static void exitError(Window frame, String errormsg,
                                 int errorcode)
    { //{{{
        if (frame != null)
        {
            JOptionPane.showMessageDialog(frame, errormsg,
                                          "Fatal Error",
                                          JOptionPane.WARNING_MESSAGE);
        }

        //print the error to the command line also.
        System.err.println(errormsg);
        System.exit(errorcode);
    } //}}}

    /**
     *
     * to Test a Component
     *
     * @param control
     * @author Jie Bao
     * @version 2003-11-01
     */
    public static void showControl(Component control)
    {
        if (!debugMode)
        {
            return;
        }
        JFrame frame = new JFrame();
        frame.getContentPane().add(control, BorderLayout.CENTER);
        //frame.setSize(control.getWidth() + 10, control.getHeight() + 10);
        System.out.println("width = " + control.getWidth() + " height = " +
                           control.getHeight());
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Stop for user until any key is hitted.
     * @author Jie Bao
     * @version 2005-02-23
     */
    public static void pause()
    {
        try
        {
            System.in.read();
        }
        catch (IOException ex)
        {
        }
    }
}
