package edu.iastate.ato.gui.dialog ;

import java.util.Vector ;

import java.awt.event.ActionEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JButton ;
import javax.swing.JFrame ;
import javax.swing.JOptionPane ;

import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.po.User ;
import edu.iastate.ato.po.UserManager ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.sql.DBViewer ;

import com.borland.dbswing.JdbNavToolBar ;
import com.borland.dx.dataset.Column ;
import com.borland.dx.dataset.MetaDataUpdate ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-27</p>
 */
public class PrivilegeEditor extends DBViewer
{
    String package_oid ;
    String packageName ;

    Vector<String> alluser = UserManager.getAllUsers(db, User.PACKAGE_ADMIN) ;

    public PrivilegeEditor(JFrame parent, OntologyServerInfo info,
        String package_oid, String packageName)
    {
        super(parent, false, "Who can edit package '" + packageName + "'?",
            info.url, info.user, info.password, false, info.driver) ;
        this.package_oid = package_oid ;
        this.packageName = packageName ;
        query.setMetaDataUpdate(MetaDataUpdate.NONE) ;

        jbInit() ;
    }

    public void jbInit()
    {
        /*
         SELECT package.pid AS from, privilege.user_id AS user, privilege.rights
                 FROM privilege, package
                 WHERE (package_oid IN
                 (SELECT p2 FROM bj_super_pkg('117550', 'nest_in'))
                 OR package_oid = '117550' )
                  AND package.oid = privilege.package_oid
         */

        String sql =
            "SELECT pid AS from, user_id AS user, rights " +
            "FROM privilege, package  WHERE (package_oid IN " +
            "(SELECT p2 FROM bj_super_pkg('" + package_oid + "', 'nest_in')) " +
            "OR package_oid = '" + package_oid +
            "') AND package.oid = privilege.package_oid" ;
        System.out.println(sql) ;
        setSQL(sql) ;
        query.executeQuery() ;

        Column[] cols = query.getColumns() ;
        System.out.println(query.getColumnCount()) ;
        for(int i = 0 ; i < cols.length ; i++)
        {
            System.out.println(cols[i].getColumnName()) ;
            query.setRowId(cols[i].getColumnName(), true) ;
        }
        //query.setReadOnly(true);


        dbNavToolbar.setButtonStateDitto(JdbNavToolBar.HIDDEN) ;

        datasourceList.setVisible(false) ;

        dataTable.setEditable(true) ;
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

        // get the col name
        String colName = dataTable.getColumnName(col).toLowerCase() ;
        //System.out.println("col - " + colName);

        if(colName.equals("rights"))
        {
            String roles[] =
                {"r", "w", "rw"} ;

            Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Privileges", JOptionPane.INFORMATION_MESSAGE,
                null, roles, value) ;

            if(selectedValue != null)
            {
                dataTable.setValueAt(selectedValue, row, col) ;
            }
        }
        else if(colName.equals("user"))
        {
            // list all user who [have no rights so far]
            Object values[] = alluser.toArray() ;
            Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose User", "Users", JOptionPane.INFORMATION_MESSAGE,
                null, values, value) ;

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
            //System.out.println(dataTable.getRowCount()) ;
            int row = dataTable.getSelectedRow() ;
            int col = dataTable.getColumnModel().getColumnIndex("rights") ;
            dataTable.setValueAt("rw", row, col) ;

            col = dataTable.getColumnModel().getColumnIndex("from") ;
            dataTable.setValueAt(packageName, row, col) ;
        }
    }

    public boolean beforeToolebarAction(ActionEvent e)
    {
        int rightscol = dataTable.getColumnModel().getColumnIndex("rights") ;
        int usercol = dataTable.getColumnModel().getColumnIndex("user") ;
        int pkgcol = dataTable.getColumnModel().getColumnIndex("from") ;

        JButton btnPost = dbNavToolbar.getPostButton() ;
        JButton btnSave = dbNavToolbar.getSaveButton() ;
        if(e.getSource() == btnPost || e.getSource() == btnSave)
        {
            // save
            for(int row = 0 ; row < dataTable.getRowCount() ; row++)
            {
                String pkg = (String)dataTable.getValueAt(row, pkgcol) ;
                if(pkg.equals(packageName))
                {
                    String user = (String)dataTable.getValueAt(row, usercol) ;
                    String rights = (String)dataTable.getValueAt(row, rightscol) ;
                    UserManager.addPrivilege(db, user, package_oid, rights) ;
                }
            }
            return false ;
        }
        else if(e.getSource() == dbNavToolbar.getDeleteButton())
        {
            // delete selected
            int row = dataTable.getSelectedRow() ;
            String user = (String)dataTable.getValueAt(row, usercol) ;
            String rights = (String)dataTable.getValueAt(row, rightscol) ;
            String pkg = (String)dataTable.getValueAt(row, pkgcol) ;
            if(!pkg.equals(this.packageName))
            {
                Debug.trace("You can delete user rights for package '" +
                    packageName + "' only") ;
                return false ;
            }
            UserManager.deletePrivilege(db, user, package_oid, rights) ;
            refresh() ;

            return false ;
        }
        else if(e.getSource() == dbNavToolbar.getRefreshButton())
        {
            refresh() ;
            return false ;
        }
        return true ;
    }

    public boolean isTableCellEditable(int row, int col)
    {
        int pkgcol = dataTable.getColumnModel().getColumnIndex("from") ;
        if(col == pkgcol)
        {
            return false ;
        }
        return true ;
    } ;

}
