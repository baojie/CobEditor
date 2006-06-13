package edu.iastate.utils.sql ;

//dbSwing
// DataExpress
import java.util.Vector ;

import java.awt.BorderLayout ;
import java.awt.HeadlessException ;
import java.awt.event.ActionEvent ;
import java.awt.event.ItemEvent ;
import java.awt.event.ItemListener ;
import javax.swing.JButton ;
import javax.swing.JComboBox ;
import javax.swing.JDialog ;
import javax.swing.JFrame ;
import javax.swing.JPanel ;
import javax.swing.JTable ;

import edu.iastate.utils.gui.GUIUtils ;

import com.borland.dbswing.DBDisposeMonitor ;
import com.borland.dbswing.JdbNavToolBar ;
import com.borland.dbswing.JdbStatusLabel ;
import com.borland.dbswing.JdbTable ;
import com.borland.dbswing.TableScrollPane ;
import com.borland.dx.dataset.DataSet ;
import com.borland.dx.sql.dataset.ConnectionDescriptor ;
import com.borland.dx.sql.dataset.Database ;
import com.borland.dx.sql.dataset.Load ;
import com.borland.dx.sql.dataset.QueryDataSet ;
import com.borland.dx.sql.dataset.QueryDescriptor ;
import java.sql.* ;

/**
 * <p>Title: DBViewer</p>
 * <p>Description: Interface to view database</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 1.0 2003-11-10
 *          2.0 2005-03-18 Add table list selection
 * @todo rewrite it with DBPanel, cut all redundant code.
 */

public class DBViewer extends JDialog
{
    //{{ 2005-08-23 to intercept the toolbar action
    public class MyNavToolBar extends JdbNavToolBar
    {
        public void actionPerformed(ActionEvent e)
        {
            if(beforeToolebarAction(e))
            {
                super.actionPerformed(e) ;
                afterToolebarAction(e) ;
            }
        }
    }

    public boolean beforeToolebarAction(ActionEvent e)
    {
        return true ;
    }

    public void afterToolebarAction(ActionEvent e)
    {}

    //}} 2005-08-23

    // dbSwing
    TableScrollPane tableScrollPane1 = new TableScrollPane() ;
    protected JdbStatusLabel jStatusLabel = new JdbStatusLabel() ;
    protected JdbTable dataTable = new JdbTable()
    {
        public boolean isCellEditable(int row, int column)
        {
            return isTableCellEditable(row, column) ;
        }
    } ;

    public boolean isTableCellEditable(int row, int column)
    {
        return true ;
    } ;

    protected MyNavToolBar dbNavToolbar = new MyNavToolBar() ;

    // DataExpress
    protected Database CacheDB = new Database() ;
    protected QueryDataSet query = new QueryDataSet() ;
    protected DBDisposeMonitor dbDisposeMonitor = new DBDisposeMonitor() ;

    protected Connection db ;

    // Swing
    protected JPanel toolbar = new JPanel() ;
    protected JButton jButtonAdvanceSQL = new JButton() ;

    // member
    boolean showSQLButton = false ;
    String connectionURL ;
    String userName ;
    String password ;
    boolean promptPassword ;
    String driver ;
    String sql = "" ; //"SELECT * FROM go_header;";

    protected JComboBox datasourceList = new JComboBox() ;
    // constructor
    public DBViewer(JFrame aFrame,
        boolean showSQLButton,
        String title,
        String connectionURL,
        String userName,
        String password,
        boolean promptPassword,
        String driver
        ) throws HeadlessException
    {
        super(aFrame, true) ;
        this.showSQLButton = showSQLButton ;
        this.connectionURL = connectionURL ;
        this.userName = userName ;
        this.password = password ;
        this.promptPassword = promptPassword ;
        this.driver = driver ;

        if(title != null)
        {
            setTitle(title) ;

        }
        try
        {
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    private void jbInit() throws Exception
    {
        jStatusLabel.setText("Status") ;

        try
        {
            CacheDB.setConnection(new ConnectionDescriptor(connectionURL,
                userName, password, promptPassword, driver)) ;
            CacheDB.setDatabaseName("") ;
            dbDisposeMonitor.setDataAwareComponentContainer(this) ;
            db = CacheDB.getJdbcConnection() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.err) ;
        }

        dataTable.setFont(new java.awt.Font("DialogInput", 0, 11)) ;
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS) ;
        dataTable.setEditable(false) ;
        jButtonAdvanceSQL.setText("Advanced Query...") ;
        jButtonAdvanceSQL.setVisible(showSQLButton) ;
        jButtonAdvanceSQL.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonAdvancedQueryAction(e) ;
            }
        }) ;
        tableScrollPane1.getViewport().add(dataTable, null) ;

        // setup the pane
        JPanel p = new JPanel() ;

        p.setLayout(new BorderLayout()) ;
        p.add(tableScrollPane1, BorderLayout.CENTER) ;
        p.add(jStatusLabel, BorderLayout.SOUTH) ;
        p.add(toolbar, BorderLayout.NORTH) ;
        toolbar.add(dbNavToolbar, null) ;

        datasourceList.addItemListener(new MySchemaListener()) ;
        readDatasource() ;
        dbNavToolbar.add(datasourceList, -1) ;

        toolbar.add(jButtonAdvanceSQL, null) ;

        this.getContentPane().add(p, BorderLayout.CENTER) ;
        this.setSize(800, 600) ;
    }

    class MySchemaListener
        implements ItemListener
    {
        public void itemStateChanged(ItemEvent evt)
        {
            Object item = evt.getItem() ;

            if(evt.getStateChange() == ItemEvent.SELECTED)
            {
                // Item was just selected
                setSQL("SELECT * FROM " + item.toString() + ";") ;
                refresh() ;

            }
        }
    }

    /**
     * refresh
     */
    public void refresh()
    {
        DataSet ds = dataTable.getDataSet() ;
        if(ds != null && ds.isOpen())
        {
            ds.refresh() ;
        }
    }

    /**
     * setSQL
     *
     * @param newSQL String
     */
    public void setSQL(String newSQL)
    {
        if(newSQL == null)
        {
            query.setQuery(new QueryDescriptor(CacheDB, sql, null, true,
                Load.ALL)) ;
            dataTable.setDataSet(null) ;
        }
        else
        {
            try
            {
                query.closeStatement() ;
                if(dataTable.getDataSet() != null &&
                    dataTable.getDataSet().isOpen())
                {
                    dataTable.getDataSet().close() ;
                }
                query.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(
                    CacheDB, newSQL, null, true, Load.ALL)) ;
                //query.setQuery(new QueryDescriptor(CacheDB, newSQL, null, true,
                //    Load.ASYNCHRONOUS));

                //query.executeQuery();
                dataTable.setDataSet(query) ;
                sql = newSQL ;
            }
            catch(Exception ex)
            {
                ex.printStackTrace(System.err) ;
            }
        }
    }

    void readDatasource()
    {
        Vector v = pgJDBCUtils.getAllTable(CacheDB.getJdbcConnection()) ;
        GUIUtils.updateComboBox(datasourceList, v.toArray()) ;
    }

    void jButtonAdvancedQueryAction(ActionEvent e)
    {
        SQLDialog dlg = new SQLDialog(sql) ;
        GUIUtils.centerWithinScreen(dlg) ;
        dlg.setVisible(true) ;
        if(dlg.getSQL() != null)
        {
            sql = dlg.getSQL() ;
            //char ss[] = new char[s.length()];
            //s.getChars(0, s.length() - 1, ss, 0);
            //System.out.println(ss);
            System.out.println(sql) ;

            try
            {
                query.closeStatement() ;
                query.setQuery(new QueryDescriptor(CacheDB, sql, null, true,
                    Load.ALL)) ;
                //query.executeQuery();
                dataTable.setDataSet(query) ;
            }
            catch(Exception ex)
            {
                ex.printStackTrace(System.err) ;
            }
        }
    }

    // for test purpose
    public static void main(String[] args) throws HeadlessException
    {
        // the oracle example
        /*LocalCacheViewer dlg = new LocalCacheViewer( null , true , "test" ,
            "jdbc:oracle:thin:@boole.cs.iastate.edu:1521:indus" , "indus" ,
            "indus" , false , "oracle.jdbc.driver.OracleDriver" ) ;
         */

        // the postgresql example
        DBViewer dlg = new DBViewer(null, true, "test",
            "jdbc:postgresql://boole.cs.iastate.edu/indus",
            "indus",
            "indus", false, "org.postgresql.Driver") ;

        //Display the window.
        //dlg.pack();
        dlg.setVisible(true) ;
    }

}
