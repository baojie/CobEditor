package edu.iastate.utils.gui.wizard.example;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

import edu.iastate.utils.gui.wizard.Wizard;
import edu.iastate.utils.gui.wizard.WizardListener;

/** Run this class to see the example Wizard.
 * @author Christopher Brind
 */
public class Main  implements WizardListener
{

    private static JFrame frame;

    /** The entry point in to the application. */
    public static void main(String args[])
    {
        frame = new JFrame("jwf example");
        frame.addWindowListener(createAppCloser());
        Wizard wizard = new Wizard();
        wizard.addWizardListener(new Main());
        frame.setContentPane(wizard);
        frame.pack();
        frame.setVisible(true);
        wizard.start(new WelcomeWizardPanel());
    }

    private static WindowListener createAppCloser()
    {
        return new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
                System.exit(0);
            }
        };
    }

    /** Called when the wizard finishes.
     * @param wizard the wizard that finished.
     */
    public void wizardFinished(Wizard wizard)
    {
        System.out.println("wizard finished");
        System.exit(0);
    }

    /** Called when the wizard is cancelled.
     * @param wizard the wizard that was cancelled.
     */
    public void wizardCancelled(Wizard wizard)
    {
        System.out.println("wizard cancelled");
        System.exit(0);
    }

    /** Called when a new panel has been displayed in the wizard.
     * @param wizard the wizard that was updated
     */
    public void wizardPanelChanged(Wizard wizard)
    {
        System.out.println("wizard new panel");
    }

}
