package edu.iastate.utils.gui.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * This class controls a wizard.<p>
 *  Add it to a frame or any other container then call start with your initial
 *  wizard panel.<p>
 * Listeners can also be added to trap when the wizard finishes and when
 *  the wizard is cancelled.
 *
 * @author Christopher Brind
 */
public class Wizard extends JPanel implements ActionListener
{
    public static final String BACK_I18N = "BACK_I18N";
    public static final String NEXT_I18N = "NEXT_I18N";
    public static final String FINISH_I18N = "FINISH_I18N";
    public static final String CANCEL_I18N = "CANCEL_I18N";
    public static final String HELP_I18N = "HELP_I18N";
    public static final Dimension WIZARD_WINDOW_SIZE = new Dimension(450, 200);

    private final JButton backButton = new JButton("<< back");
    private final JButton nextButton = new JButton("next >>");
    private final JButton finishButton = new JButton("finish");
    private final JButton cancelButton = new JButton("cancel");
    private final JButton helpButton = new JButton("help");

    private final HashMap listeners = new HashMap();

    private Stack previous = null;
    private WizardPanel current = null;
    private WizardContext ctx = null;
    private Map i18n = null;

    public Wizard(Map i18n)
    {
        this.i18n = i18n;
        init();
        this.applyI18N(this.i18n);
    }

    /** Creates a new wizard. */
    public Wizard()
    {
        init();
    }

    private void init()
    {
        nextButton.addActionListener(this);
        backButton.addActionListener(this);
        finishButton.addActionListener(this);
        cancelButton.addActionListener(this);
        helpButton.addActionListener(this);

        nextButton.setEnabled(false);
        backButton.setEnabled(false);
        finishButton.setEnabled(false);
        cancelButton.setEnabled(false);
        helpButton.setEnabled(false);

        setLayout(new BorderLayout());

        JPanel navButtons = new JPanel();
        navButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
        navButtons.add(backButton);
        navButtons.add(nextButton);
        navButtons.add(finishButton);

        JPanel cancelButtons = new JPanel();
        cancelButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
        cancelButtons.add(cancelButton);
        cancelButtons.add(helpButton);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BorderLayout());
        buttons.add(navButtons, BorderLayout.EAST);
        buttons.add(cancelButtons, BorderLayout.WEST);

        add(buttons, BorderLayout.SOUTH);

        setMinimumSize(WIZARD_WINDOW_SIZE);
        setPreferredSize(WIZARD_WINDOW_SIZE);
    }

    /* Sets a map of labels for changing the labels of the wizard buttons
     * The keys are the I18N constants of the Wizard class
     * @param map A Map object containing 5 key-value elements
     */
    public void setI18NMap(Map map)
    {
        i18n = map;
        applyI18N(i18n);
    }

    private void applyI18N(Map map)
    {
        if (map.size() > 0)
        {
            nextButton.setText( (String) map.get(NEXT_I18N));
            backButton.setText( (String) map.get(BACK_I18N));
            finishButton.setText( (String) map.get(FINISH_I18N));
            cancelButton.setText( (String) map.get(CANCEL_I18N));
            helpButton.setText( (String) map.get(HELP_I18N));

            backButton.setActionCommand("<< back");
            nextButton.setActionCommand("next >>");
            finishButton.setActionCommand("finish");
            cancelButton.setActionCommand("cancel");
            helpButton.setActionCommand("help");

        }
    }

    /** Add a listener to this wizard.
     * @param listener a WizardListener object
     */
    public void addWizardListener(WizardListener listener)
    {
        listeners.put(listener, listener);
    }

    /** Remove a listener from this wizard.
     * @param listener a WizardListener object
     */
    public void removeWizardListener(WizardListener listener)
    {
        listeners.remove(listener);
    }

    /** Start this wizard with this panel. */
    public void start(WizardPanel wp)
    {
        previous = new Stack();
        ctx = new WizardContext();
        wp.setWizardContext(ctx);
        setPanel(wp);
        updateButtons();
    }

    /** Handle's button presses.
     * param ae an ActionEvent object
     */
    public void actionPerformed(ActionEvent ae)
    {

        String ac = ae.getActionCommand();
        if ("<< back".equals(ac))
        {
            back();
        }
        else if ("next >>".equals(ac))
        {
            next();
        }
        else if ("finish".equals(ac))
        {
            finish();
        }
        else if ("cancel".equals(ac))
        {
            cancel();
        }
        else if ("help".equals(ac))
        {
            help();
        }

    }

    private void setPanel(WizardPanel wp)
    {
        if (null != current)
        {
            remove(current);
        }

        current = wp;
        if (null == current)
        {
            current = new NullWizardPanel();
        }
        add(current, BorderLayout.CENTER);

        Iterator iter = listeners.values().iterator();
        while (iter.hasNext())
        {
            WizardListener listener = (WizardListener) iter.next();
            listener.wizardPanelChanged(this);
        }
        setVisible(true);
        revalidate();
        updateUI();
        current.display();
    }

    private void updateButtons()
    {
        cancelButton.setEnabled(true);
        helpButton.setEnabled(current.hasHelp());
        backButton.setEnabled(previous.size() > 0);
        nextButton.setEnabled(current.hasNext());
        finishButton.setEnabled(current.canFinish());
    }

    private void back()
    {

        WizardPanel wp = (WizardPanel) previous.pop();
        setPanel(wp);
        updateButtons();

    }

    private void next()
    {
        ArrayList list = new ArrayList();
        if (current.validateNext(list))
        {
            previous.push(current);
            WizardPanel wp = current.next();
            if (null != wp)
            {
                wp.setWizardContext(ctx);
            }

            setPanel(wp);
            updateButtons();
        }
        else
        {
            showErrorMessages(list);
        }
    }

    private void finish()
    {

        ArrayList list = new ArrayList();
        if (current.validateFinish(list))
        {
            current.finish();
            Iterator iter = listeners.values().iterator();
            while (iter.hasNext())
            {
                WizardListener listener = (WizardListener) iter.next();
                listener.wizardFinished(this);
            }
        }
        else
        {
            showErrorMessages(list);
        }
    }

    private void cancel()
    {

        Iterator iter = listeners.values().iterator();
        while (iter.hasNext())
        {
            WizardListener listener = (WizardListener) iter.next();
            listener.wizardCancelled(this);
        }
    }

    private void help()
    {
        current.help();
    }

    private void showErrorMessages(ArrayList list)
    {
        Window w = SwingUtilities.windowForComponent(this);
        JFrame frame = null;
        ErrorMessageBox errorMsgBox = null;

        if (w instanceof Frame)
        {
            errorMsgBox = new ErrorMessageBox( (Frame) w);
        }
        else if (w instanceof Dialog)
        {
            errorMsgBox = new ErrorMessageBox( (Dialog) w);
        }
        else
        {
            errorMsgBox = new ErrorMessageBox();
        }

        errorMsgBox.showErrorMessages(list);
    }

    public void enableNext(boolean enabled)
    {
        nextButton.setEnabled(enabled);
    }
}
