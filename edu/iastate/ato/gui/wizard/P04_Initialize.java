package edu.iastate.ato.gui.wizard ;

import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.SQLException ;
import java.util.List ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import javax.swing.JCheckBox ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;

import edu.iastate.ato.po.OntologyServerBuilder ;
import edu.iastate.ato.shared.AtoConstent ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.wizard.WizardPanel ;
import edu.iastate.utils.sql.JDBCUtils ;

/**
 * @author Jie Bao
 * @since 2005-08-21
 */
public class P04_Initialize extends WizardPanel
{
    SeverBuilder parent ;
    String dbName ;
    String user ;
    String pass ;

    public P04_Initialize(SeverBuilder parent, String dbName, String user,
        String pass)
    {
        try
        {
            this.parent = parent ;
            this.dbName = dbName ;
            this.user = user ;
            this.pass = pass ;
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

    public Connection db = null ;

    public boolean validateNext(List list)
    {
        parent.wizard.enableNext(false) ;

        try
        {
            String url = "jdbc:postgresql://" + parent.p2_host.getHost() +
                "/" + dbName ;
            Class.forName(AtoConstent.JDBC_DRIVER) ;
            db = DriverManager.getConnection(url, user, pass) ;
        }
        catch(SQLException ex)
        {
            list.add(ex.getMessage()) ;
        }
        catch(ClassNotFoundException ex)
        {
            list.add(ex.getMessage()) ;
        }

        if(db == null)
        {
            String str = "Cannot create connection to the ontology database\n" ;
            str += "\nYou may need to change the database configuration to enable internet visiting\n" +
                "of new created database. Please Do: \n" +
                "1. Go to PostgreSQL data folder, e.g. C:\\PostgreSQL8.0\\data\n" +
                "2. Open pg_hba.conf, add a line as \n" +
                "   host    " + dbName +
                "       all          0.0.0.0          0.0.0.0           trust\n" +
                "3. Reload the PostgreSQL server\n\n" +
                "Contact your database administrator if the database is not local" ;
            Debug.trace(str) ;
            parent.wizard.enableNext(true) ;
            return false ;
        }
        else
        {
            if(checkBoxSkip.isSelected())
            {
                parent.wizard.enableNext(true) ;
                next = new P05_Publish(parent, db) ;
                return true ;
            }
            String msg = OntologyServerBuilder.createTables(db, user) ;
            if(!JDBCUtils.isOK(msg))
            {
                list.add(msg) ;
                parent.wizard.enableNext(true) ;
                return false ;
            }
            msg = OntologyServerBuilder.createFunctions(db, user) ;
            if(!JDBCUtils.isOK(msg))
            {
                list.add(msg) ;
                parent.wizard.enableNext(true) ;
                return false ;
            }
            OntologyServerBuilder.initData(db) ;
        }

        parent.wizard.enableNext(true) ;
        info.setText("Ontology Server Initialized") ;
        Debug.trace("Ontology Server Initialized") ;
        next = new P05_Publish(parent, db) ;
        return true ;
    }

    P05_Publish next ;
    public WizardPanel next()
    {

        parent.p5_publish = next ;
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
        title.setText("Step 3: Initialize the ontology database server") ;
        info.setFont(new java.awt.Font("Dialog", Font.PLAIN, 15)) ;
        info.setHorizontalAlignment(SwingConstants.CENTER) ;
        info.setText("Click Next to create all tables and funtions") ;
        checkBoxSkip.setHorizontalAlignment(SwingConstants.CENTER) ;
        checkBoxSkip.setText("Skip") ;
        this.add(info, java.awt.BorderLayout.CENTER) ;
        jPanel1.add(checkBoxSkip) ;
        this.add(jPanel1, java.awt.BorderLayout.SOUTH) ;
        this.add(title, java.awt.BorderLayout.NORTH) ; }

    BorderLayout borderLayout1 = new BorderLayout() ;
    JLabel title = new JLabel() ;
    JLabel info = new JLabel() ;
    JCheckBox checkBoxSkip = new JCheckBox() ;
    JPanel jPanel1 = new JPanel() ;
}
