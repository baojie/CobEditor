package edu.iastate.utils.sql;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import edu.iastate.utils.gui.GUIUtils;
import edu.iastate.utils.lang.MessageHandler;
import edu.iastate.utils.lang.MessageMap;

/**
 * <p>Title: PDBViewer</p>
 * <p>Description: A viewer for RDB database</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 1.0
 */

public class SQLDialog
    extends JDialog implements MessageHandler
{
    JButton jButtonOK = new JButton("OK");
    JButton jButtonCancel = new JButton("Cancel");

    private String SQL = "";

    public String getSQL()
    {
        return SQL;
    }

    public void setSQL(String SQL)
    {
        this.SQL = SQL;
        panel.setSqlInput(SQL);
    }

    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(jButtonOK, this, "onOK");
            MessageMap.mapAction(jButtonCancel, this, "onCancel");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public SQLPanel panel = new SQLPanel();

    public SQLDialog(String sql) throws HeadlessException
    {
        super();
        this.setModal(true);
        panel.setSqlInput(sql);
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        messageMap();

        panel.buttonPanel.add(jButtonOK, null);
        panel.buttonPanel.add(jButtonCancel, null);

        GUIUtils.setJButtonSizesTheSame(new JButton[]
                                        {jButtonOK, jButtonCancel,
                                        panel.jButtonCopy, panel.jButtonPaste});

        this.setContentPane(panel);
        this.setSize(500, 300);
    }

    public void onOK(ActionEvent e)
    {
        String strSQL = panel.getSqlInput();
        if (panel.verifySQL(strSQL))
        {
            this.SQL = strSQL;
            this.dispose();
        }
        else
        {
            JOptionPane.showMessageDialog(this, "SQL syntax is not correct.");
        }
    }

    public void onCancel(ActionEvent e)
    {
        SQL = null;
        this.dispose();
    }

    public static void main(String[] args) throws HeadlessException
    {
        SQLDialog dlg = new SQLDialog("");
        dlg.show();
    }

}
