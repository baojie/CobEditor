package edu.iastate.ato.gui.wizard ;

import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.SQLException ;
import java.util.List ;

import java.awt.BorderLayout ;
import javax.swing.BorderFactory ;
import javax.swing.JLabel ;
import javax.swing.JPasswordField ;
import javax.swing.JTextField ;

import edu.iastate.ato.shared.AtoConstent ;

import edu.iastate.utils.gui.LabelledItemPanel ;
import edu.iastate.utils.gui.wizard.WizardPanel ;

/**
 * Ask for host address and super user id and password
 *
 * @author Jie Bao
 * @since 2005-08-21
 */
public class P02_Host extends WizardPanel
{
    SeverBuilder parent ;

    public P02_Host(SeverBuilder parent)
    {
        try
        {
            this.parent = parent ;
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public void display()
    {
    }

    public boolean hasNext()
    {
        return true ;
    }

    Connection db = null ;

    public String getHost()
    {
        return host.getText() ;
    }

    public boolean validateNext(List list)
    {
        String h = host.getText() ;
        String u = user.getText() ;
        String p = new String(password.getPassword()) ;
        // check if the database is connectable
        try
        {
            // Load the JDBC driver
            String driverName = AtoConstent.JDBC_DRIVER ;
            Class.forName(driverName) ;

            // Create a connection to the database
            String serverName = h ;
            String url = "jdbc:postgresql://" + serverName ; // + "/template1" ;
            db = DriverManager.getConnection(url, u, p) ;
            if(db != null)
            {
                if(next == null)
                {
                    next = new P03_CreateDB(parent, db) ;
                }
                else
                {
                    next.update(db) ;
                }
            }
        }
        catch(ClassNotFoundException e)
        {
            // Could not find the database driver
            list.add(e.getMessage()) ;
            e.printStackTrace() ;
            return false ;
        }
        catch(SQLException e)
        {
            // Could not connect to the database
            list.add(e.getMessage()) ;
            e.printStackTrace() ;
            return false ;
        }

        return true ;
    }

    private P03_CreateDB next ;

    public WizardPanel next()
    {
        parent.p3_createDB = next ;
        return next ;
    }

    public boolean canFinish()
    {
        return false ;
    }

    public boolean validateFinish(List list)
    {
        return false ;
    }

    public void finish()
    {
    }

    private void jbInit() throws Exception
    {
        this.setLayout(borderLayout1) ;
        title.setText("Step 1: Select Host and Superuser") ;
        this.add(title, java.awt.BorderLayout.NORTH) ;
        this.add(myContentPane, java.awt.BorderLayout.CENTER) ;
        myContentPane.setBorder(BorderFactory.createEtchedBorder()) ;
        myContentPane.addItem("Host", host) ;
        myContentPane.addItem("", new JLabel(
            "The host that runs a PostgreSQL database server")) ;
        myContentPane.addItem("", new JLabel(
            "Example: boole.cs.iastate.edu, localhost")) ;
        myContentPane.addItem("Default Table", template) ;
        myContentPane.addItem("", new JLabel(
            "List any database you can access on this host")) ;
        myContentPane.addItem("User", user) ;
        myContentPane.addItem("Password", password) ;

        myContentPane.addItem("", new JLabel(
            "The user should have privilege to create database and user")) ;
        myContentPane.addItem("", new JLabel(
            "Connect the database admin if you need the privilege")) ;
    }

    LabelledItemPanel myContentPane = new LabelledItemPanel() ;
    BorderLayout borderLayout1 = new BorderLayout() ;
    JLabel title = new JLabel() ;
    private JTextField host = new JTextField("localhost") ;
    private JTextField user = new JTextField() ;
    private JPasswordField password = new JPasswordField() ;
    private JTextField template = new JTextField("template1") ;
}
