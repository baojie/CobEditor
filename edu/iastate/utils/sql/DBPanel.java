package edu.iastate.utils.sql;

import java.sql.Connection;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.borland.dbswing.DBDisposeMonitor;
import com.borland.dbswing.JdbNavToolBar;
import com.borland.dbswing.JdbStatusLabel;
import com.borland.dbswing.JdbTable;
import com.borland.dbswing.TableScrollPane;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.sql.dataset.ConnectionDescriptor;
import com.borland.dx.sql.dataset.Database;
import com.borland.dx.sql.dataset.Load;
import com.borland.dx.sql.dataset.QueryDataSet;

/**
 * Panel to show database content
 * @author Jie Bao
 * @since 1.0 - 2005-03-02
 */
public class DBPanel
    extends JPanel
{
    // dbSwing
    TableScrollPane tableScrollPane1 = new TableScrollPane();
    JdbStatusLabel jStatusLabel = new JdbStatusLabel();
    JdbTable Table = new JdbTable();
    JdbNavToolBar jToolBar = new JdbNavToolBar();

    // DataExpress
    Database CacheDB = new Database();
    QueryDataSet query = new QueryDataSet();
    DBDisposeMonitor dbDisposeMonitor = new DBDisposeMonitor();

    // Swing

    // member
    String connectionURL;
    String userName;
    String password;
    boolean promptPassword;
    String driver;
    String sql = ""; //"SELECT * FROM go_header;";
    boolean navigateBar;

    // the oracle example
    /*DBPanel dlg = new DBPanel( "jdbc:oracle:thin:@boole.cs.iastate.edu:1521:indus" ,
              "indus" ,
              "indus" ,
              false ,
              "oracle.jdbc.driver.OracleDriver" ) ;
     */

    // constructor
    public DBPanel(String connectionURL,
                   String userName,
                   String password,
                   boolean promptPassword,
                   String driver,
                   boolean navigateBar
        ) throws HeadlessException
    {
        this.connectionURL = connectionURL;
        this.userName = userName;
        this.password = password;
        this.promptPassword = promptPassword;
        this.driver = driver;
        this.navigateBar = navigateBar;

        try
        {
            jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        jStatusLabel.setText("Status");

        try
        {
            CacheDB.setConnection(new ConnectionDescriptor(connectionURL,
                userName, password, promptPassword, driver));
            CacheDB.setDatabaseName("");
            //query.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(CacheDB, sql, null, true, Load.ALL));
            dbDisposeMonitor.setDataAwareComponentContainer(this);
            //query.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(CacheDB, sql, null, true, Load.ALL));
            Table.setDataSet(null);

        }
        catch (Exception ex)
        {
            ex.printStackTrace(System.err);
        }

        Table.setFont(new java.awt.Font("DialogInput", 0, 11));
        Table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Table.setEditable(false);
        tableScrollPane1.getViewport().add(Table, null);

        // setup the pane
        setLayout(new BorderLayout());
        add(tableScrollPane1, BorderLayout.CENTER);
        add(jStatusLabel, BorderLayout.SOUTH);
        if (navigateBar)
        {
            add(jToolBar, BorderLayout.NORTH);
        }
    }

    /**
     * Enable/Disable editing
     * @param editable boolean
     * @since 2005-03-15
     * @author Jie Bao
     */
    public void setEditable(boolean editable)
    {
        Table.setEditable(editable);
    }

    /**
     * setSQL
     *
     * @param newSQL String
     */
    public void setSQL(String newSQL)
    {
        if (newSQL == null)
        {
            query.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(
                CacheDB, sql, null, true, Load.ALL));
            Table.setDataSet(null);
        }
        else
        {
            try
            {
                query.closeStatement();
                if (Table.getDataSet() != null && Table.getDataSet().isOpen())
                {
                    Table.getDataSet().close();
                }
                query.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(
                    CacheDB, newSQL, null, true, Load.ALL));
                //query.executeQuery();
                Table.setDataSet(query);
                sql = newSQL;
            }
            catch (Exception ex)
            {
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * refresh
     */
    public void refresh()
    {
        DataSet ds = Table.getDataSet();
        if (ds != null && ds.isOpen())
        {
            ds.refresh();
        }
    }

    /**
     * getDB
     *
     * @return Connection
     */
    public Connection getDB()
    {
        return CacheDB.getJdbcConnection();
    }

    public JdbTable getTable()
    {
        return Table;
    }

}
