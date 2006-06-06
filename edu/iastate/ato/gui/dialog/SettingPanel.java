package edu.iastate.ato.gui.dialog ;

import java.util.HashSet ;
import java.util.Set ;

import java.awt.event.ActionEvent ;
import java.awt.event.ItemEvent ;
import java.awt.event.ItemListener ;
import javax.swing.JButton ;
import javax.swing.JComboBox ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.OntologyServerInfo ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.lang.MessageHandler ;
import edu.iastate.utils.lang.MessageMap ;
import edu.iastate.utils.sql.JDBCConfigPanel ;
import javax.swing.*;
import java.awt.*;

/**
 * JDBC Setting
 *
 * @author Jie Bao
 * @since 2005-04-20
 */
public class SettingPanel extends JDBCConfigPanel implements MessageHandler
{
    protected JButton btnOK = new JButton("OK") ;
    protected JButton btnSave = new JButton("Save") ;
    protected JButton btnDelete = new JButton("Delete") ;
    protected JButton btnNew = new JButton("New") ;

    JComboBox ontList = new JComboBox() ;
    public OntologyServerInfo selectedOnt ;
    Window holder;

    public SettingPanel(Set<OntologyServerInfo> ontologies,
        OntologyServerInfo selected, Window holder)
    {
        try
        {
            this.holder = holder;
            useName = true ;
            for(OntologyServerInfo ont : ontologies)
            {
                ontList.addItem(ont) ;
            }
            if(selected != null)
            {
                this.selectedOnt = selected ;
                ontList.setSelectedItem(selectedOnt) ;
                //System.out.println("contains: " + ontologies.contains(selected));
                //System.out.println(selectedOnt + " is given");
            }
            else if(ontList.getItemCount() > 0)
            {
                ontList.setSelectedIndex(0) ;
                this.selectedOnt = (OntologyServerInfo)ontList.getItemAt(0) ;
                System.out.println(selectedOnt + " is self-given") ;
            }

            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    protected void jbInit() throws Exception
    {
        messageMap() ;

        myContentPane.addItem("Ontology list", ontList) ;

        MyItemListener actionListener = new MyItemListener() ;
        ontList.addItemListener(actionListener) ;

        updateUI(selectedOnt) ;

        paneButton.add(btnOK, null) ;
        paneButton.add(btnNew, null) ;
        paneButton.add(btnSave, null) ;
        paneButton.add(btnDelete, null) ;

        super.jbInit() ;
    }

    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(this.btnTest, this, "onTest") ;
            MessageMap.mapAction(this.btnSave, this, "onSave") ;
            MessageMap.mapAction(this.btnDelete, this, "onDelete") ;
            MessageMap.mapAction(this.btnNew, this, "onNew") ;
            MessageMap.mapAction(this.btnOK, this, "onOK") ;
        }
        catch(Exception ex)
        {
        }
    }

    public boolean isOK = false;

    public void onOK(ActionEvent evt)
    {
        holder.setVisible(false);
        isOK = true;
    }

    public void onNew(ActionEvent evt)
    {
        //add new
        clearUI() ;
        selectedOnt = new OntologyServerInfo() ;
        ontList.setSelectedItem(null) ;
    }

    public void onDelete(ActionEvent evt)
    {
        if(selectedOnt != null)
        {
            ontList.removeItem(selectedOnt) ;
        }
    }

    public Set<OntologyServerInfo> getOntologyConfigs()
    {
        Set s = new HashSet<OntologyServerInfo>() ;
        int size = ontList.getItemCount() ;
        for(int i = 0 ; i < size ; i++)
        {
            OntologyServerInfo ont = (OntologyServerInfo)ontList.getItemAt(i) ;
            s.add(ont) ;
        }
        return s ;
    }

    public void onSave(ActionEvent evt)
    {
        if(selectedOnt != null)
        {
            String name = dbName.getText() ;
            String url = dbMachineURL.getText() ;
            String user = dbUserID.getText() ;
            String password = new String(dbUserPwd.getPassword()) ;
            String driver = dbJdbcDriver.getText() ;
            String type = (String)dbType.getSelectedItem() ;

            // validate the name
            if(name == null || name.trim().length() == 0)
            {
                Debug.trace("Name cannot be empty") ;
                return ;
            }
            // check for duplicate names
            int size = ontList.getItemCount() ;
            for(int i = 0 ; i < size ; i++)
            {
                OntologyServerInfo ont = (OntologyServerInfo)ontList.getItemAt(
                    i) ;
                if(ont.name.equals(name) && ont != selectedOnt)
                {
                    Debug.trace("The name is already used") ;
                    return ;
                }
            }

            if(url == null || url.trim().length() == 0 ||
                user == null || user.trim().length() == 0 ||
                password == null || password.trim().length() == 0 ||
                driver == null || driver.trim().length() == 0 ||
                type == null || type.trim().length() == 0)
            {
                Debug.trace("You should fill out all fields") ;
                return ;
            }

            selectedOnt.name = name ;
            selectedOnt.url = url ;
            selectedOnt.user = user ;
            selectedOnt.password = password ;
            selectedOnt.driver = driver ;
            selectedOnt.type = type ;

            if(ontList.getSelectedItem() == null)
            {
                ontList.addItem(selectedOnt) ;
                ontList.setSelectedItem(selectedOnt) ;
            }
        }
        MOEditor.theInstance.saveConfig();
    }

    class MyItemListener
        implements ItemListener
    {
        // This method is called only if a new item has been selected.
        public void itemStateChanged(ItemEvent evt)
        {
            JComboBox cb = (JComboBox)evt.getSource() ;
            Object item = evt.getItem() ;

            if(cb == ontList && evt.getStateChange() == ItemEvent.SELECTED)
            {
                //Debug.trace("seleted: "+item);
                if(item instanceof OntologyServerInfo)
                {
                    // if same to currently selected bbs, do nothing
                    if(item == selectedOnt)
                    {
                        return ;
                    }

                    // select a registered bbs, change the post url
                    selectedOnt = (OntologyServerInfo)item ;
                    if(selectedOnt != null)
                    {
                        updateUI(selectedOnt) ;
                    }

                }
            }
        }

    }

    void clearUI()
    {
        dbName.setText(null) ;
        dbMachineURL.setText(null) ;
        dbUserID.setText(null) ;
        dbUserPwd.setText(null) ;
        dbJdbcDriver.setText(null) ;
        dbType.setSelectedItem(null) ;
    }

    void updateUI(OntologyServerInfo info)
    {
        if(info != null)
        {
            dbName.setText(info.name) ;
            dbMachineURL.setText(info.url) ;
            dbUserID.setText(info.user) ;
            dbUserPwd.setText(info.password) ;
            dbJdbcDriver.setText(info.driver) ;
            dbType.setSelectedItem(info.type) ;
        }
    }

}
