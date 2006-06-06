package edu.iastate.ato.gui.wizard ;

import java.sql.Connection ;
import java.sql.SQLException ;
import java.util.List ;
import java.util.Set ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.awt.SystemColor ;
import javax.swing.BorderFactory ;
import javax.swing.JLabel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.po.OntologyServerBuilder ;
import edu.iastate.ato.shared.AtoConstent ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.wizard.WizardPanel ;

/**
 * @author Jie Bao
 * @since 2005-08-21
 */
public class P05_Publish extends WizardPanel
{
    SeverBuilder parent ;
    Connection ontConn ;

    public P05_Publish(SeverBuilder parent, Connection ontConn)
    {
        try
        {
            this.parent = parent ;
            this.ontConn = ontConn ;
            next = new PX_Final(parent) ;
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

    public boolean validateNext(List list)
    {
        try
        {
            if(ontConn == null || ontConn.isClosed())
            {
                list.add("Ontology server is not connected") ;
                return false ;
            }
            // check if the database is correctly created
            boolean good = OntologyServerBuilder.checkIntegrity(ontConn) ;
            if(!good)
            {
                list.add(
                    "Ontology database has missing table!, please rebuild them") ;
                return false ;
            }

            // add it into registered set
            Set<OntologyServerInfo> ontologies = MOEditor.theInstance.
                serverList ;
            boolean found = false ;
            for(OntologyServerInfo ont : ontologies)
            {
                if(ont.url.equals(ontConn.getMetaData().getURL()))
                {
                    Debug.trace(
                        "The given ontology server is registered before") ;
                    found = true ;
                    break ;
                }
            }
            if(!found)
            {
                OntologyServerInfo newInfo = new OntologyServerInfo() ;
                newInfo.driver = AtoConstent.JDBC_DRIVER ;
                newInfo.name = parent.p4_init.dbName + "@" +
                    parent.p2_host.getHost() ;
                newInfo.url = ontConn.getMetaData().getURL() ;
                newInfo.user = parent.p4_init.user ;
                newInfo.password = parent.p4_init.pass ;
                newInfo.type = "POSTGRE" ;
                ontologies.add(newInfo) ;

                // show information to the user
                String str =
                    "The Ontology server is registered, please click menu " +
                    "'" + MOEditor.theInstance.menuConfig.getText() + "'\n" +
                    "to view the setting or load the ontology \n\n" +
                    "You may publish the ontolgy server as \n" +
                    "\n   URL: " + newInfo.url +
                    "\n   JDBC driver:" + newInfo.driver +
                    "\n   User: " + newInfo.user +
                    "\n   Password: " + newInfo.password +
                    "\n\nYou may login with user 'admin' and password 'admin'\n" +
                    "Please go to User Management Center to change your password!" ;
                next.info.setText(str) ;
            }
            return true ;

        }
        catch(SQLException ex)
        {
            list.add("Ontology server is not connected") ;
        }
        return false ;
    }

    PX_Final next ;
    public WizardPanel next()
    {
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
        jLabel1.setText("Step 4: Register Ontology Server Setting") ;
        jTextArea1.setBackground(SystemColor.menu) ;
        jTextArea1.setFont(new java.awt.Font("Dialog", Font.PLAIN, 15)) ;
        jTextArea1.setBorder(BorderFactory.createEtchedBorder()) ;
        jTextArea1.setEditable(false) ;
        jTextArea1.setText("Click Next to register the ontology server") ;
        jScrollPane1.getViewport().add(jTextArea1) ;
        this.add(jScrollPane1, java.awt.BorderLayout.CENTER) ;
        this.add(jLabel1, java.awt.BorderLayout.NORTH) ;
    }

    BorderLayout borderLayout1 = new BorderLayout() ;
    JLabel jLabel1 = new JLabel() ;
    JTextArea jTextArea1 = new JTextArea() ;
    JScrollPane jScrollPane1 = new JScrollPane() ;
}
