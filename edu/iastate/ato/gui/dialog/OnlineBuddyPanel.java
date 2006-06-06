package edu.iastate.ato.gui.dialog ;

import java.util.Vector ;

import java.awt.event.ActionEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JButton ;
import javax.swing.JFrame ;
import javax.swing.JTable ;
import javax.swing.ListSelectionModel ;
import javax.swing.table.TableColumn ;

import edu.iastate.ato.agent.ChatPanel ;
import edu.iastate.ato.agent.MoAgent ;
import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.po.UserManager ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.lang.MessageHandler ;
import edu.iastate.utils.lang.MessageMap ;
import edu.iastate.utils.net.EmailTools ;
import edu.iastate.utils.sql.DBViewer ;
import javax.swing.table.* ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since2005-08-25 </p>
 */
public class OnlineBuddyPanel extends DBViewer implements MessageHandler
{
    public OnlineBuddyPanel(JFrame parent, OntologyServerInfo info)
    {
        super(parent, false, "Who is online?", info.url, info.user,
            info.password, false, info.driver) ;
        this.setModal(false) ;
        try
        {
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    JButton btnReload = new JButton("Reload") ;
    JButton btnEmail = new JButton("Send Email") ;
    JButton btnMessage = new JButton("Send Message") ;
    JButton btnClear = new JButton("Clear Inactive") ;

    private void jbInit() throws Exception
    {
        messageMap() ;
        this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE) ;

        toolbar.add(btnReload, null) ;
        toolbar.add(btnEmail, null) ;
        toolbar.add(btnMessage, null) ;
        if(MOEditor.user.isAdmin())
        {
            btnClear.setToolTipText("Delete users that already get off line") ;
            toolbar.add(btnClear, null) ;
        }

        datasourceList.setVisible(false) ;
        dbNavToolbar.setVisible(false) ;

        String sql =
            " SELECT online.user_id, users.role, users.name, users.email, " +
            " users.institution, online.login_time, host, port " +
            " FROM online, users " +
            " WHERE online.user_id = users.id" ;
        this.setSQL(sql) ;

        dataTable.setEditable(false) ;
        dataTable.setToolTipText("Double click to see details") ;
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION) ;
        dataTable.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                if(evt.getClickCount() == 2)
                {
                    onTableMouseClick(evt) ;
                }
            }
        }) ;
    }

    public void setVisible(boolean v)
    {
        super.setVisible(v) ;

        // hide host and port
        if(v == true)
        {
            refresh() ;
            dataTable.getTableHeader().setReorderingAllowed(false) ;
            dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS) ;
            TableColumnModel t = dataTable.getColumnModel() ;
            System.out.println(dataTable.getColumnName(0)) ;
            idxHost = t.getColumnIndex("host") ;
            idxPort = t.getColumnIndex("port") ;

            dataTable.setHiddenColumns(new int[]
                {idxHost, idxPort}) ;
        }
    }

    int idxHost, idxPort ;

    private void onTableMouseClick(MouseEvent evt)
    {
        // left click to select row
        int row = dataTable.rowAtPoint(evt.getPoint()) ;
        int col = dataTable.getColumnModel().getColumnIndex("user_id") ;

        String user_id = (String)dataTable.getValueAt(row, col) ;
        Vector<String> allPackages = UserManager.getEditingPackages
            (CacheDB.getJdbcConnection(), user_id, false) ;

        String str = "User '" + user_id +
            "' is editing following package(s): \n" ;
        if(allPackages.size() == 0)
        {
            str += "<none>" ;
        }
        else
        {
            for(String pkg : allPackages)
            {
                str += pkg + "\n" ;
            }
        }

        Debug.trace(null, str) ;
    }

    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(this.btnReload, this, "onReload") ;
            MessageMap.mapAction(this.btnEmail, this, "onEmail") ;
            MessageMap.mapAction(this.btnMessage, this, "onMessage") ;
            MessageMap.mapAction(this.btnClear, this, "onClear") ;
        }
        catch(Exception ex)
        {
        }
    }

    public void onClear(ActionEvent e)
    {
        if(!MOEditor.user.isAdmin())
        {
            Debug.trace("You have no right to delete online information") ;
        }

        int colUser = dataTable.getColumnModel().getColumnIndex("user_id") ;
        int count = dataTable.getRowCount() ;
        for(int row = 0 ; row < count ; row++)
        {
            String host = (String)dataTable.getModel().getValueAt(row, idxHost) ;
            String port = (String)dataTable.getModel().getValueAt(row, idxPort) ;
            String user_id = (String)dataTable.getModel().getValueAt(row, colUser) ;

            if(user_id != null && host != null && port != null)
            {
                // test
                boolean suc = false ;
                try
                {
                    suc = MoAgent.isServerOn(host, port) ;
                }
                catch(Exception ex)
                {
                    ex.printStackTrace() ;
                }
                System.out.println(user_id + "@" + host + ":" + port + " - " +
                    suc) ;
                if(!suc)
                {
                    UserManager.logout(CacheDB.getJdbcConnection(), user_id,
                        host, port) ;
                }
            }
        }
        refresh() ;
    }

    public void onEmail(ActionEvent e)
    {
        if(MOEditor.user.isGuest())
        {
            Debug.trace("Guest cannot send email") ;
            return ;
        }

        int row = dataTable.getSelectedRow() ;
        if(row != -1)
        {
            int col = dataTable.getColumnModel().getColumnIndex("email") ;
            String email = (String)dataTable.getValueAt(row, col) ;

            String ont = MOEditor.theInstance.selectedServer.name ;
            String subject = "\"On Ontology Editing - " + ont + "\"" ;
            String body = "\"From editor " + MOEditor.user.name + "\"" ;

            boolean suc = EmailTools.sendWindowsEmail(email, subject, body) ;
            if(!suc)
            {
                Debug.trace("Cannot send email to " + email) ;
            }
        }
    }

    public void onMessage(ActionEvent e)
    {
        if(MOEditor.user.isGuest())
        {
            Debug.trace("Guest cannot send message") ;
            return ;
        }

        int row = dataTable.getSelectedRow() ;
        if(row != -1)
        {
            String host = (String)dataTable.getModel().getValueAt(row, idxHost) ;
            String port = (String)dataTable.getModel().getValueAt(row, idxPort) ;
            int col = dataTable.getColumnModel().getColumnIndex("user_id") ;
            String user = (String)dataTable.getModel().getValueAt(row, col) ;

            if(host != null && port != null)
            {

                MoAgent myServer = MOEditor.theInstance.messenger ;

                ChatPanel window = myServer.startChat(user, host, port) ;

                if(window == null)
                {
                    Debug.trace("Connot connect to selected user") ;
                }
                else
                {
                    window.showMe() ;
                }
            }
        }
    }

    public void onReload(ActionEvent e)
    {
        refresh() ;
    }
}
