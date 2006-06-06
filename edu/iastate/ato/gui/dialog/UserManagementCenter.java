package edu.iastate.ato.gui.dialog ;

import java.awt.event.ActionEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JButton ;
import javax.swing.JFrame ;
import javax.swing.JOptionPane ;

import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.po.OntologyEdit ;
import edu.iastate.ato.po.User ;

import edu.iastate.utils.sql.DBViewer ;

import com.borland.dbswing.JdbNavToolBar ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since2005-08-23 </p>
 */
public class UserManagementCenter extends DBViewer
{
    public UserManagementCenter(JFrame parent, OntologyServerInfo info)
    {
        super(parent, false, "User Managament Center", info.url, info.user,
            info.password, false, info.driver) ;
        try
        {
            jbInit() ;
        }
        catch(Exception ex)
        {
        }
    }

    private void jbInit() throws Exception
    {
        datasourceList.setVisible(false) ;
        dbNavToolbar.setButtonStateDitto(JdbNavToolBar.HIDDEN) ;
        this.setSQL("SELECT * FROM users") ;
        dataTable.setEditable(true) ;
        dataTable.setToolTipText("id, role, email and pass cannot be empty") ;
        dataTable.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                onTableMouseClick(evt) ;
            }
        }) ;

    }

    private void onTableMouseClick(MouseEvent evt)
    {
        // left click to select row
        int row = dataTable.rowAtPoint(evt.getPoint()) ;
        int col = dataTable.columnAtPoint(evt.getPoint()) ;
        Object value = dataTable.getValueAt(row, col) ;
        //System.out.println("click on - " + value);

        // get the col name
        String colName = dataTable.getColumnName(col).toLowerCase() ;
        //System.out.println("col - " + colName);

        if(colName.equals("role"))
        {
            String roles[] =
                {User.ADMIN, User.GUEST, User.PACKAGE_ADMIN} ;

            Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "User Role", JOptionPane.INFORMATION_MESSAGE,
                null, roles, value) ;

            if(selectedValue != null)
            {
                dataTable.setValueAt(selectedValue, row, col) ;
            }
        }
    }

    public void afterToolebarAction(ActionEvent e)
    {
        JButton btnInsert = dbNavToolbar.getInsertButton() ;
        if(e.getSource() == btnInsert)
        {
            // add create date to the new row
            System.out.println(dataTable.getRowCount()) ;
            int row = dataTable.getSelectedRow() ;
            int col = dataTable.getColumnModel().getColumnIndex("create_date") ;
            dataTable.setValueAt(OntologyEdit.getTime(), row, col) ;
        }
    }
}
