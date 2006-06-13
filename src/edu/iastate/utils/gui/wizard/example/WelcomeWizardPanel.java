package edu.iastate.utils.gui.wizard.example;

import java.util.List;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

import edu.iastate.utils.gui.wizard.WizardPanel;

/** An implementation of the base class used for implementing a panel that is
 * displayed in a Wizard.  Shows some sample license data. Feel free to
 * re-use this panel if it happens to meet your needs.
 *
 * @author Christopher Brind
 */
public class WelcomeWizardPanel extends WizardPanel
{

    private String welcome = "Welcome to the jwf example Wizard.\n"
        + "Press next to continue.";

    private final WizardPanel license = new LicenseWizardPanel();

    public WelcomeWizardPanel()
    {

        setLayout(new BorderLayout());
        setBorder(new TitledBorder("welcome"));
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setText(welcome);
        add(new JScrollPane(pane), BorderLayout.CENTER);
    }

    /** Called when the panel is set. */
    public void display()
    {
    }

    /** Is there be a next panel?
     * @return true if there is a panel to move to next
     */
    public boolean hasNext()
    {
        return true;
    }

    /** Called to validate the panel before moving to next panel.
     * @param list a List of error messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List list)
    {
        boolean valid = true;
        return valid;
    }

    /** Get the next panel to go to. */
    public WizardPanel next()
    {
        return license;
    }

    /** Can this panel finish the wizard?
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish()
    {
        return false;
    }

    /** Called to validate the panel before finishing the wizard. Should
     * return false if canFinish returns false.
     * @param list a List of error messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List list)
    {
        return false;
    }

    /** Handle finishing the wizard. */
    public void finish()
    {
    }

}
