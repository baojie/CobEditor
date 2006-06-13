package edu.iastate.utils.sql;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.iastate.utils.lang.MessageHandler;
import edu.iastate.utils.lang.MessageMap;
import edu.iastate.utils.string.ParserUtils;

/**
 * A simple SQL editor
 * @author Jie Bao
 * @since 1.0 2005-03-18
 */
public class SQLPanel
    extends JPanel implements MessageHandler
{
    BorderLayout borderLayout1 = new BorderLayout();
    public JButton jButtonCopy = new JButton("Copy");
    public JButton jButtonPaste = new JButton("Paste");

    public JPanel buttonPanel = new JPanel();
    JLabel jLabel1 = new JLabel();
    JScrollPane jScrollPane1 = new JScrollPane();
    public JTextArea sqlInput = new JTextArea();
    BorderLayout borderLayout2 = new BorderLayout();

    public SQLPanel()
    {
        try
        {
            jbInit();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        messageMap();

        jLabel1.setText(
            "Input SQL sentence here:(example SELECT * FROM mytable WHERE ID=1);");
        sqlInput.setBorder(BorderFactory.createEtchedBorder());
        this.setLayout(borderLayout2);

        buttonPanel.add(jButtonCopy, null);
        buttonPanel.add(jButtonPaste, null);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        jScrollPane1.getViewport().add(sqlInput, null);

        this.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        this.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        this.add(jLabel1, java.awt.BorderLayout.NORTH);
    }
    /**
     * @author Jie Bao
     * @since 2005-03-18
     */
    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(jButtonCopy, this, "onCopy");
            MessageMap.mapAction(jButtonPaste, this, "onPaste");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public void onCopy(ActionEvent e)
    {
        sqlInput.copy();
    }

    public void onPaste(ActionEvent e)
    {
        sqlInput.paste();
    }

    /**
     * Check the validity of the SQL sentence
     *
     * @param strSQL
     * @return
     * @author Jie Bao
     * @version 2003-11-11
     */
    public boolean verifySQL(String strSQL)
    {
        /* model
         SELECT XXX
         FROM YYY
         [ WHERE ZZZ ];
         */
        String pattern = ParserUtils.CASE_INSENSITIVE + // case insensitive
            ParserUtils.DOTALL + // CR and LF are ignored
            "SELECT" +
            ParserUtils.BLANKS +
            ParserUtils.ANY_WORD +
            ParserUtils.BLANKS +
            "FROM" +
            ParserUtils.BLANKS +
            ParserUtils.ANY_WORD
            //+ ";"
            ;
        return ParserUtils.isFound(pattern, strSQL);
    }

    public String getSqlInput()
    {
        return sqlInput.getText();
    }

    public void setSqlInput(String str)
    {
        sqlInput.setText(str);
    }

}
