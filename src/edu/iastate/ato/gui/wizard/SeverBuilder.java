package edu.iastate.ato.gui.wizard ;

import java.sql.SQLException ;

import java.awt.event.WindowAdapter ;
import java.awt.event.WindowEvent ;
import javax.swing.JFrame ;
import javax.swing.JOptionPane ;

import edu.iastate.ato.po.OntologyServerBuilder ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.GUIUtils ;
import edu.iastate.utils.gui.wizard.Wizard ;
import edu.iastate.utils.gui.wizard.WizardListener ;

/**
 * @author Jie Bao
 * @since 2005-08-21
 */
public class SeverBuilder
    implements WizardListener
{
    final JFrame frame = new JFrame("Ontology Server Building Wizard") ;
    Wizard wizard = new Wizard() ;

    P01_Welcome p1_welcome = new P01_Welcome(this) ;
    P02_Host p2_host ;
    P03_CreateDB p3_createDB ;
    P04_Initialize p4_init ;
    P05_Publish p5_publish ;

    public SeverBuilder()
    {
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
                frame.dispose() ;
            }
        }) ;

        wizard.addWizardListener(this) ;

        frame.setContentPane(wizard) ;
        frame.setSize(500, 400) ;

        GUIUtils.centerWithinParent(frame) ;
        frame.setVisible(true) ;

        wizard.start(p1_welcome) ;
    }

    public void wizardFinished(Wizard wizard)
    {
        frame.setVisible(false) ;
        frame.dispose() ;
    }

    public void wizardCancelled(Wizard wizard)
    {
        try
        {
            // close connection to this db
            if(p4_init.db != null && !p4_init.db.isClosed())
            {
                p4_init.db.close() ;
            }
        }
        catch(SQLException ex)
        {
        }

        // delete new create user and db, if any

        if(p3_createDB != null)
        {
            if(!p3_createDB.isExistedDB())
            {
                int answer = JOptionPane.showConfirmDialog(frame,
                    "Delete new created database '" + p3_createDB.getOntDB() +
                    "'? ") ;
                if(answer == JOptionPane.YES_OPTION)
                {

                    String msg = OntologyServerBuilder.deleteDatabase(
                        p3_createDB.superUserConn, p3_createDB.getOntDB()) ;
                    Debug.trace("Delete database '" + p3_createDB.getOntDB() +
                        "'\n\n" + msg) ;
                }
            }
            if(!p3_createDB.isExistedUser())
            {
                int answer = JOptionPane.showConfirmDialog(frame,
                    "Delete new created user '" + p3_createDB.getUser() + "'? ") ;
                if(answer == JOptionPane.YES_OPTION)
                {
                    String msg = OntologyServerBuilder.deleteUser(
                        p3_createDB.superUserConn, p3_createDB.getUser()) ;
                    Debug.trace("Delete user '" + p3_createDB.getUser() +
                        "'\n\n" + msg) ;
                }
            }
        }

        // hide the frame
        frame.setVisible(false) ;
        frame.dispose() ;
    }

    public void wizardPanelChanged(Wizard wizard)
    {
    }

}
