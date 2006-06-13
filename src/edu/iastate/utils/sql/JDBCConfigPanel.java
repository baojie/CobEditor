package edu.iastate.utils.sql;

import java.util.HashMap;
import java.util.Map;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.iastate.utils.gui.LabelledItemPanel;

/**
 * A JDBC configuration panel
 *
 * @author Jie Bao
 * @since 2005-04-20
 */
public class JDBCConfigPanel extends JPanel
{
    public JDBCConfigPanel()
    {
        try
        {
            //jbInit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public JTextField dbName = new JTextField(),
        dbMachineURL = new JTextField(),
        dbJdbcDriver = new JTextField(),
        dbUserID = new JTextField();
    public JPasswordField dbUserPwd = new JPasswordField();
    protected BorderLayout borderLayout1 = new BorderLayout();
    protected JButton btnTest = new JButton("Test Connection");

    protected String[] items =
        {
        "ORACLE", "MYSQL", "POSTGRE"};
    protected JComboBox dbType = new JComboBox(items);

    protected JPanel paneButton = new JPanel();
    protected LabelledItemPanel myContentPane = new LabelledItemPanel();

    protected boolean useName = false;

    // 2005-03-27
    class DbTypeListListener
        implements ItemListener
    {
        Map dbType2URL = new HashMap();
        Map dbType2Driver = new HashMap();

        DbTypeListListener()
        {
            dbType2URL.put("ORACLE", "jdbc:oracle:thin:@host:1521:YOUR_DB_NAME");
            dbType2Driver.put("ORACLE", "oracle.jdbc.driver.OracleDriver");
            dbType2URL.put("POSTGRE",
                           "jdbc:postgresql://host:5432/YOUR_DB_NAME");
            dbType2Driver.put("POSTGRE", "org.postgresql.Driver");
            dbType2URL.put("MYSQL", "jdbc:mysql://host:3306/YOUR_DB_NAME");
            dbType2Driver.put("MYSQL", "org.gjt.mm.mysql.Driver");
        }

        // This method is called only if a new item has been selected.
        public void itemStateChanged(ItemEvent evt)
        {
            // Get the affected item
            JComboBox cb = (JComboBox) evt.getSource();
            Object item = evt.getItem();

            if (cb == dbType && evt.getStateChange() == ItemEvent.SELECTED)
            {
                String dbTypeStr = (String) dbType.getSelectedItem();

                if (dbTypeStr == null)
                {
                    return;
                }

                if (dbMachineURL.getText() == null ||
                    dbMachineURL.getText().trim().length() == 0 ||
                    dbMachineURL.getText().endsWith("YOUR_DB_NAME"))
                {
                    dbMachineURL.setText( (String) dbType2URL.get(dbTypeStr));
                }
                dbJdbcDriver.setText( (String) dbType2Driver.get(dbTypeStr));
            }
        }
    }

    /**
     * Test if the given data source is connectable
     * @param evt ActionEvent
     * @since 2005-03-27
     *        2005-08-19: check driver existence
     */
    public void onTest(ActionEvent evt)
    {
        // if driver available?
        String driver = dbJdbcDriver.getText();
        String info;
        try
        {
            Class.forName(driver);
        }
        catch (ClassNotFoundException ex)
        {
            info = "JDBC driver " + driver + " is not found, connection failed";
            JOptionPane.showMessageDialog(this, info);
            return;
        }

        String good = "Given data source is connectable";
        String bad = "Given data source is NOT connectable";
        info = connectable() ? good : bad;
        JOptionPane.showMessageDialog(this, info);
    }

    public LocalDBConnection getConnectionSetting()
    {
        LocalDBConnection ds = new LocalDBConnection();

        ds.setUrl(dbMachineURL.getText());
        ds.setUser(dbUserID.getText());
        ds.setPassword(new String(dbUserPwd.getPassword()));
        ds.setDriver(dbJdbcDriver.getText());

        return ds;
    }

    public boolean connectable()
    {
        LocalDBConnection ds = getConnectionSetting();
        boolean c = ds.connect();
        ds.disconnect();
        return c;
    }

    protected void jbInit() throws Exception
    {
        this.setLayout(borderLayout1);

        myContentPane.setBorder(BorderFactory.createEtchedBorder());
        if (useName)
        {
            myContentPane.addItem("Database Name", dbName);
        }

        myContentPane.addItem("Database Type", dbType);
        dbType.addItemListener(new DbTypeListListener());

        myContentPane.addItem("DB Machine URL", dbMachineURL);
        myContentPane.addItem("JDBC Driver", dbJdbcDriver);
        myContentPane.addItem("User ID", dbUserID);
        myContentPane.addItem("Password", dbUserPwd);
        myContentPane.setBorder(BorderFactory.createEtchedBorder());

        this.add(myContentPane, java.awt.BorderLayout.CENTER);
        this.add(paneButton, java.awt.BorderLayout.SOUTH);
        paneButton.add(btnTest);

    }
}
