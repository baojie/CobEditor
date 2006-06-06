package edu.iastate.utils.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * <p>Title: ProgressBarWin</p>
 * <p>Description: progress bar</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 2003-10-30
 *          2004-01-14 add step()
 *          2004-10-15 add two examples
 */

public class ProgressBarWin
    extends JPanel
//    implements ActionListener
{
    private JLabel labelInfo;
    private JProgressBar progressBar;
    private Timer timer;
    private int timerInterval;
    private boolean indeterminate;
    private String progressInfo = null;
    private JFrame mainFrame;
    private JDialog dlg;

    int porgressIndex = 0;

    public int getPorgressIndex()
    {
        return porgressIndex;
    }

    public void setPorgressIndex(int porgressIndex)
    {
        this.porgressIndex = porgressIndex;
    }

    public void step()
    {
        this.porgressIndex++;
    }

    public void setProgressInfo(String progressInfo)
    {
        this.progressInfo = progressInfo;
    }

    public void start()
    {
//        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) ) ;
//        progressBar.setIndeterminate( true ) ;
        showWindow();
        timer.start();
    }

    public void stop()
    {
        Toolkit.getDefaultToolkit().beep();
        timer.stop();
        progressBar.setValue(progressBar.getMinimum());
        progressBar.setString(""); //hide % string
        dlg.dispose();
    }

    /**
     * To create a progressbar
     *
     * @param infoText : text shown as  help label
     * @param interval : interval to check change (ms)
     * @param isIndeterminate :if the step is not known
     * @param min :when isIndeterminate is true, it's ignored
     * @param max :when isIndeterminate is true, it's ignored
     *
     * @author Jie Bao
     * @version 2003-10-30
     */
    public ProgressBarWin(JFrame frame, String infoText, int interval,
                          boolean isIndeterminate, int min,
                          final int max)
    {
        super(new BorderLayout());

        mainFrame = frame;
        timerInterval = interval;
        indeterminate = isIndeterminate;

        labelInfo = new JLabel();
        labelInfo.setText(infoText);

        progressBar = new JProgressBar(min, max);
        progressBar.setValue(0);
        progressBar.setIndeterminate(indeterminate);

        //We call setStringPainted, even though we don't want the
        //string to show up until we switch to determinate mode,
        //so that the progress bar height stays the same whether
        //or not the string is shown.
        progressBar.setStringPainted(true); //get space for the string
        progressBar.setString(""); //but don't paint it

        JPanel panel = new JPanel();
        panel.add(labelInfo);
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Create a timer.
        timer = new Timer(timerInterval, new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                if (!progressBar.isIndeterminate())
                {
                    progressBar.setValue(getPorgressIndex());
                    if (getPorgressIndex() == max)
                    {
                        stop();
                    }

                    progressBar.setString(null); //display % string
                }

                if (progressInfo != null)
                {
                    progressBar.setString(progressInfo);
                }
            }
        });
    }

    private void showWindow()
    {
        try
        {
            //Create and set up the window.
            dlg = new JDialog(mainFrame);
            dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            //Create and set up the content pane.
//        this.setOpaque( true ) ; //content panes must be opaque
            dlg.setContentPane(this);

            //Display the window.
            dlg.pack();
            centerWindows(dlg);
            dlg.setVisible(true);
        }
        catch (HeadlessException ex)
        {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    private void centerWindows(JDialog frame)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height)
        {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width)
        {
            frameSize.width = screenSize.width;
        }
        frame.setLocation( (screenSize.width - frameSize.width) / 2,
                          (screenSize.height - frameSize.height) / 2);

    }

    /**
     * @since 2004-10-15
     */
    static void DeterminateExample()
    {
        final int max = 30;
        final int interval = 100;

        System.out.println("start");
        final ProgressBarWin win = new ProgressBarWin(new JFrame(), "Test:",
            interval, false,
            0,
            max);
        win.start();

        // run 3 seconds
        new Timer(interval, new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                win.step();
            }
        }).start();

        System.out.println("done");
    }

    /**
     * @since 2004-10-15
     */
    static void IndeterminateExample()
    {
        final int interval = 100;

        System.out.println("start");
        final ProgressBarWin win = new ProgressBarWin(new JFrame(), "Test:",
            interval, true, 0, 0);
        win.start();

        new Timer(interval, new ActionListener()
        {
            float count = 0;
            public void actionPerformed(ActionEvent evt)
            {
                count++;
                win.setProgressInfo(count / 10 + " seconds past");
                if (count >= 30)
                {
                    win.stop();
                }
            }
        }).start(); ;

        System.out.println("done");
    }

    // for test purpose
    public static void main(String[] args)
    {
        ProgressBarWin.DeterminateExample();
        //ProgressBarWin.IndeterminateExample();
    }
}
