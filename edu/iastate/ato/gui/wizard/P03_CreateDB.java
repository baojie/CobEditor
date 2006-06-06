package edu.iastate.ato.gui.wizard ;

import java.sql.Connection ;
import java.util.List ;

import java.awt.BorderLayout ;
import javax.swing.BorderFactory ;
import javax.swing.JCheckBox ;
import javax.swing.JLabel ;
import javax.swing.JPasswordField ;
import javax.swing.JTextField ;

import edu.iastate.ato.po.OntologyServerBuilder ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.LabelledItemPanel ;
import edu.iastate.utils.gui.wizard.WizardPanel ;
import edu.iastate.utils.sql.JDBCUtils ;

/**
 * Create the ontology server database and public access account,
 *
 * @author Jie Bao
 * @since 2005-08-21
 */
public class P03_CreateDB extends WizardPanel
{
    SeverBuilder parent ;
    // the connection created by the super user
    // we will use this connection to create the database and users
    Connection superUserConn ;

    public P03_CreateDB(SeverBuilder parent, Connection db)
    {
        try
        {
            this.parent = parent ;
            this.superUserConn = db ;
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public void update(Connection db)
    {
        this.superUserConn = db ;
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
        // database and user name should be string of [a-zA-Z0-9]+, start with [a-zA-Z]
        String pattern = "[a-zA-Z][\\s\\w\\-._]*" ;

        String on = ontologyServerName.getText() ;
        String user = publicUser.getText() ;
        String p1 = new String(password1.getPassword()) ;
        String p2 = new String(password2.getPassword()) ;

        boolean oldDB = existedDB.isSelected() ;
        boolean oldUser = existedUser.isSelected() ;

        boolean good = true ;

        if(on == null || !on.matches(pattern))
        {
            list.add("Ontology server name is not legal") ;
            good = false ;
        }

        if(user == null || !user.matches(pattern))
        {
            list.add("User is not legal") ;
            good = false ;
        }

        if(p1 == null || p1.trim().length() == 0)
        {
            list.add("Password cannot be null") ;
            good = false ;
        }
        else if(!p1.equals(p2))
        {
            list.add("Password and its retype don't match") ;
            good = false ;
        }

        if(good)
        {
            // create the user
            if(!oldUser)
            {
                String message = OntologyServerBuilder.createUser(superUserConn,
                    user, p1) ;
                if(!JDBCUtils.isOK(message))
                { // failed
                    list.add(message) ;
                    good = false ;
                }
                else
                {
                    Debug.trace("User '" + user + "' is successfully created") ;
                }
            }
            // create database
            if(good)
            {
                if(!oldDB)
                {
                    String message = OntologyServerBuilder.createDatabase(
                        superUserConn, on, user) ;
                    if(!JDBCUtils.isOK(message))
                    { // failed
                        list.add(message) ;
                        good = false ;
                        if(!oldUser)
                        {
                            OntologyServerBuilder.deleteUser(superUserConn,
                                user) ; // delete the user
                        }
                    }
                    else
                    {
                        Debug.trace("Database '" + on +
                            "' is successfully created") ;
                    }
                }
            }

        }

        if(good)
        {
            next = new P04_Initialize(parent, on, user, p1) ;
        }

        return good ;
    }

    P04_Initialize next ;

    public WizardPanel next()
    {
        parent.p4_init = next ;
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
        jLabel1.setText(
            "Step 2: Select ontology server name and public access setting") ;
        this.add(jLabel1, java.awt.BorderLayout.NORTH) ;

        this.add(myContentPane, java.awt.BorderLayout.CENTER) ;
        myContentPane.setBorder(BorderFactory.createEtchedBorder()) ;
        myContentPane.addItem("Ontology Server Name", ontologyServerName) ;
        myContentPane.addItem(" Use existed database", existedDB) ;
        myContentPane.addItem("Public User", publicUser) ;
        myContentPane.addItem("Use existed user", existedUser) ;
        myContentPane.addItem("", new JLabel(
            "Editor will use this account to access the ontology")) ;
        myContentPane.addItem("Public User Password", password1) ;
        myContentPane.addItem("Retype Password", password2) ;

    }

    LabelledItemPanel myContentPane = new LabelledItemPanel() ;
    BorderLayout borderLayout1 = new BorderLayout() ;
    JLabel jLabel1 = new JLabel() ;

    private JTextField ontologyServerName = new JTextField() ;
    private JTextField publicUser = new JTextField() ;
    private JPasswordField password1 = new JPasswordField() ;
    private JPasswordField password2 = new JPasswordField() ;
    private JCheckBox existedDB = new JCheckBox() ;
    private JCheckBox existedUser = new JCheckBox() ;

    public boolean isExistedDB()
    {
        return existedDB.isSelected() ;
    }

    public boolean isExistedUser()
    {
        return existedUser.isSelected() ;
    }

    public String getUser()
    {
        return publicUser.getText() ;
    }

    public String getOntDB()
    {
        return ontologyServerName.getText() ;
    }

}
