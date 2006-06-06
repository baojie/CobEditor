package edu.iastate.ato.agent ;

import java.text.SimpleDateFormat ;
import java.util.Date ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.event.ActionEvent ;
import java.awt.event.KeyEvent ;
import javax.swing.JButton ;
import javax.swing.JFrame ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;
import javax.swing.JTextField ;

import edu.iastate.utils.lang.MessageHandler ;
import edu.iastate.utils.lang.MessageMap ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-25</p>
 */
public class ChatPanel extends JPanel implements MessageHandler
{
    BorderLayout borderLayout1 = new BorderLayout() ;
    JPanel jPanel2 = new JPanel() ;
    BorderLayout borderLayout2 = new BorderLayout() ;
    JTextField textMsg = new JTextField() ;
    JButton btnSend = new JButton() ;
    JScrollPane jScrollPane1 = new JScrollPane() ;
    JTextArea chatHistory = new JTextArea() ;

    Buddy theBuddy ;

    public ChatPanel(Buddy b)
    {
        try
        {
            theBuddy = b ;
            jbInit() ;
        }
        catch(Exception exception)
        {
            exception.printStackTrace() ;
        }
    }

    JFrame frame ;

    public void showMe()
    {
        frame.setVisible(true) ;
    }

    private void jbInit() throws Exception
    {
        messageMap() ;

        this.setLayout(borderLayout1) ;
        jPanel2.setLayout(borderLayout2) ;
        textMsg.setText("") ;
        btnSend.setText("Send") ;
        btnSend.setMnemonic(KeyEvent.VK_ENTER) ;
        btnSend.setToolTipText("Alt+Enter") ;

        chatHistory.setBackground(Color.orange) ;
        chatHistory.setEditable(false) ;
        chatHistory.setLineWrap(true) ;
        this.add(jPanel2, java.awt.BorderLayout.SOUTH) ;
        jPanel2.add(btnSend, java.awt.BorderLayout.EAST) ;
        jPanel2.add(textMsg, java.awt.BorderLayout.CENTER) ;
        this.add(jScrollPane1, java.awt.BorderLayout.CENTER) ;
        jScrollPane1.getViewport().add(chatHistory) ;

        frame = new JFrame() ;
        frame.setSize(200, 400) ;
        frame.setContentPane(this) ;
        frame.setTitle("Chat with " + theBuddy.name) ;
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE) ;
    }

    final public static SimpleDateFormat dateFormat = new
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;

    public static String getTime()
    {
        return dateFormat.format(new Date()) ;
    }

    public void onSend(ActionEvent actionEvent)
    {
        final String msg = textMsg.getText() ;
        if(msg.length() == 0)
        {
            return ;
        }
        textMsg.setText("") ;

        new Thread()
        {
            public void run()
            {
                String res = MoAgent.sendMessage(msg, theBuddy) ;
                chatHistory.append("You said (" + getTime() + ")\n" + msg +
                    "[" + res + "]\n") ;
            } ;
        }.start() ;
    }

    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(this.btnSend, this, "onSend") ;
        }
        catch(Exception ex)
        {
        }
    }

    /**
     * append
     *
     * @param client_user String
     * @param content String
     */
    public void append(String client_user, String content)
    {
        chatHistory.append(client_user + " said (" + getTime() + ")\n" +
            content + "\n") ;
    }
}
