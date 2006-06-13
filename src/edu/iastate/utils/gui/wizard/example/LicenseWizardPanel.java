package edu.iastate.utils.gui.wizard.example;

import java.util.List;

import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
public class LicenseWizardPanel extends WizardPanel
{

    private String license = "if you agree to this license, please "
        + "choose 'I agree'\n"
        + "if you don't agree, then you won't be able to continue";

    private JCheckBox accept = new JCheckBox();

    private final WizardPanel chooseDirectory =
        new ChooseDirectoryWizardPanel();

    public LicenseWizardPanel()
    {

        setLayout(new BorderLayout());
        setBorder(new TitledBorder("license agreement"));
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setText(license);
        add(new JScrollPane(pane), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.add(new JLabel("I agree"));
        panel.add(accept);
        add(panel, BorderLayout.SOUTH);
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
        if (!accept.isSelected())
        {
            list.add("Please accept the license to continue");
            valid = false;
        }
        return valid;
    }

    /** Get the next panel to go to. */
    public WizardPanel next()
    {
        return chooseDirectory;
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
