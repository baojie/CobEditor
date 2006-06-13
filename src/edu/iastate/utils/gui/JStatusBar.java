package edu.iastate.utils.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.GridLayout;

/**
 * A status bar with a text and multiple progress bars
 * @author Jie Bao
 * @since 1.0 2005-03-08
 */
public class JStatusBar extends JPanel
{
    static private Map progressBars = new HashMap();
    private JLabel textInfomation = new JLabel();

    private static int indexProgressBar = 0;
    GridLayout gridLayout1 = new GridLayout();
    synchronized public int addProgressBar(boolean isIndeterminate, int min,
                                           int max)
    {
        indexProgressBar++;
        JProgressBar progress = new JProgressBar(min, max);
        progress.setIndeterminate(isIndeterminate);
        progressBars.put(indexProgressBar + "", progress);
        progress.setStringPainted(!isIndeterminate);
        add(progress, null);
        validate();
        return indexProgressBar;
    }

    // 2005-08-22
    public JProgressBar getProgressBar(int index)
    {
        JProgressBar progress = (JProgressBar) progressBars.get(index + "");
        return progress;
    }

    public void removeProgressBar(int index)
    {
        JProgressBar progress = (JProgressBar) progressBars.get(index + "");
        remove(progress);
        progressBars.remove(index + "");
        validate();
    }

    public void updateProgessBar(int index, int newValue)
    {
        JProgressBar progress = (JProgressBar) progressBars.get(index + "");
        progress.setValue(newValue);
    }

    public void updateProgressBar(int index, String newInfo)
    {
        JProgressBar progress = (JProgressBar) progressBars.get(index + "");
        if (newInfo != null)
        {
            progress.setStringPainted(true);
        }
        progress.setString(newInfo);
        progress.validate();
        validate();
    }

    public JStatusBar(String initText)
    {
        try
        {
            jbInit();
            textInfomation.setText(initText);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        this.setLayout(gridLayout1);
        add(textInfomation, null);
    }

    public String getTextInfomation()
    {
        return textInfomation.getText();
    }

    public void setTextInfomation(String newinfo)
    {
        textInfomation.setText(newinfo);
    }
}
