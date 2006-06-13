package edu.iastate.ato.agent ;

import java.util.Vector ;
import java.util.prefs.Preferences ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Frame ;
import java.awt.Point ;
import java.awt.Rectangle ;
import java.awt.Toolkit ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import java.awt.event.WindowAdapter ;
import java.awt.event.WindowEvent ;
import java.awt.event.WindowStateListener ;
import javax.swing.BorderFactory ;
import javax.swing.DefaultListModel ;
import javax.swing.ImageIcon ;
import javax.swing.JButton ;
import javax.swing.JDialog ;
import javax.swing.JFrame ;
import javax.swing.JLabel ;
import javax.swing.JList ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextField ;
import javax.swing.Timer ;

import edu.iastate.ato.gui.AboutBoxDialog;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.GUIUtils ;
import edu.iastate.utils.gui.LabelledItemPanel ;
import edu.iastate.utils.gui.StandardDialog ;
import edu.iastate.utils.lang.MessageHandler ;
import edu.iastate.utils.lang.MessageMap ;

import other.rath.tools.Win32Toolkit ;
import other.rath.tools.tray.AdvancedTrayIcon ;
import other.rath.tools.tray.NativeIcon ;
import other.rath.tools.tray.TrayEventAdapter ;
import other.rath.tools.tray.TrayIconManager ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-26</p>
 */
public class Messenger extends JFrame implements MessageHandler, ChatListener
{
    Preferences prefs = Preferences.userNodeForPackage(Messenger.class) ;

    BorderLayout borderLayout1 = new BorderLayout() ;
    JPanel jPanel1 = new JPanel() ;
    JScrollPane jScrollPane1 = new JScrollPane() ;

    JButton btnAdd = new JButton() ;
    JButton btnWho = new JButton() ;
    JButton btnAbout = new JButton() ;

    DefaultListModel lstModel = new DefaultListModel() ;
    JList buddyList = new JList(lstModel) ;

    MoAgent server = new MoAgent(prefs.get(NAME, null)) ;

    TrayIconManager tray ;
    AdvancedTrayIcon trayIcon ;

    public Messenger()
    {
        try
        {
            server.start() ;
            jbInit() ;
        }
        catch(Exception exception)
        {
            exception.printStackTrace() ;
        }
    }

    private void jbInit() throws Exception
    {
        messageMap() ;

        getContentPane().setLayout(borderLayout1) ;
        btnWho.setText("@") ;
        btnWho.setToolTipText("Who am I? Set your id and get your address") ;
        this.getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER) ;
        jScrollPane1.getViewport().add(buddyList) ;
        btnAdd.setText("+") ;
        btnAdd.setToolTipText("Add Buddy") ;

        btnAbout.setText("?") ;
        btnAbout.setToolTipText("About") ;

        this.getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH) ;
        buddyList.setBackground(new Color(184, 199, 153)) ;
        jPanel1.add(btnWho) ;
        jPanel1.add(btnAdd) ;
        jPanel1.add(btnAbout) ;

        server.addMessageListener(this) ;

        buddyList.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent evt)
            {
                JList list = (JList)evt.getSource() ;
                if(evt.getClickCount() == 2) // Double-click
                {
                    // Get item index
                    int index = list.locationToIndex(evt.getPoint()) ;
                    Buddy buddy = (Buddy)list.getModel().getElementAt(index) ;
                    startTalk(buddy) ;
                }
            }
        }) ;

        int delay = 60 * 1000 ; //milliseconds
        ActionListener taskPerformer = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                onTimer(evt) ;
            }
        } ;
        new Timer(delay, taskPerformer).start() ;

        if(prefs.get(NAME, null) == null)
        {
            String inputValue = null ;
            // ask for nickname
            while(inputValue == null || inputValue.trim().length() == 0)
            {
                inputValue = JOptionPane.showInputDialog(
                    "Please choose your nickname") ;
            }
            server.myName = inputValue ;
            prefs.put(NAME, inputValue) ;
        }

        // hide the frame if iconized
        final JFrame thisFrame = this ;
        WindowStateListener listener = new WindowAdapter()
        {
            public void windowStateChanged(WindowEvent evt)
            {
                int oldState = evt.getOldState() ;
                int newState = evt.getNewState() ;

                if((oldState & Frame.ICONIFIED) == 0
                    && (newState & Frame.ICONIFIED) != 0)
                {
                    // Frame was iconized
                    thisFrame.setVisible(false) ;
                }
            }
        } ;
        // Register the listener with the frame
        addWindowStateListener(listener) ;

    }

    ImageIcon icon = (ImageIcon)GUIUtils.loadIcon("images/species.gif") ;

    public void registerTrayIcon()
    {
        try
        {
            tray = new TrayIconManager(Win32Toolkit.getInstance()) ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
        if(tray == null)
        {
            return ;
        }
        NativeIcon icon1 = new NativeIcon(icon.getImage()) ;

        trayIcon = new AdvancedTrayIcon(icon1,
            APP_NAME + "\nDouble click to show the tool") ;
        trayIcon.setBaloonTitle(APP_NAME + " is running") ;
        trayIcon.setBaloonText(APP_NAME + "\nDouble click to show the tool") ;
        trayIcon.setBaloonIcon(trayIcon.ICON_INFORMATION) ;

        final JFrame thisFrame = this ;

        tray.addTrayIcon(trayIcon, new TrayEventAdapter()
        {
            public void mouseDblClicked(Point p)
            {
                GUIUtils.deiconify(thisFrame) ;
                thisFrame.setVisible(true) ;
                thisFrame.validate() ;
            }
        }) ;
    }

    // clear the inactive buddy list
    public void onTimer(ActionEvent evt)
    {
        Thread thread = new Thread()
        {
            public void run()
            {
                Vector<Buddy> activeBuddy = new Vector() ;
                for(int i = 0 ; i < lstModel.getSize() ; i++)
                {
                    Buddy buddy = (Buddy)lstModel.getElementAt(i) ;
                    if(server.isServerOn(buddy.host, buddy.port))
                    {
                        activeBuddy.add(buddy) ;
                    }
                }
                lstModel.removeAllElements() ;
                for(Buddy b : activeBuddy)
                {
                    lstModel.addElement(b) ;
                }
            }
        } ;
        thread.start() ;
    }

    public void startTalk(final Buddy buddy)
    {
        new Thread()
        {
            public void run()
            {
                ChatPanel window = server.startChat(buddy.name, buddy.host
                    , buddy.port) ;

                if(window == null)
                {
                    Debug.trace("Connot connect to selected user") ;
                }
                else
                {
                    window.showMe() ;
                }
            }
        }.start() ;
    }

    public static void main(String[] args)
    {
        Messenger messenger = new Messenger() ;
        messenger.setSize(200, 400) ;
        messenger.setTitle("Bob Talk") ;
        messenger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
        GUIUtils.centerWithinScreen(messenger) ;
        messenger.setVisible(true) ;
        try
        {
            messenger.registerTrayIcon() ;
        }
        catch(Exception ex)
        {
        }
    }

// add a buddy
    public void onAdd(ActionEvent actionEvent)
    {
        String strName = prefs.get(NAME, null) ;
        if(strName == null || strName.trim().length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Please set your name first") ;
            onWho(actionEvent) ;
        }
        if(strName == null || strName.trim().length() == 0)
        {
            return ;
        }
        StandardDialog dlg = new StandardDialog() ;
        dlg.setTitle("Buddy information") ;

        JTextField ip = new JTextField(),
            port = new JTextField() ;

        LabelledItemPanel myContentPane = new LabelledItemPanel() ;
        myContentPane.setBorder(BorderFactory.createEtchedBorder()) ;
        myContentPane.addItem("", new JLabel("Please ask buddy his/her:")) ;
        myContentPane.addItem("IP", ip) ;
        myContentPane.addItem("port", port) ;

        dlg.setContentPane(myContentPane) ;
        dlg.setSize(300, 200) ;
        GUIUtils.centerWithinScreen(dlg) ;
        dlg.setVisible(true) ;

        if(!dlg.hasUserCancelled())
        {
            String buddyHost = ip.getText() ;
            String buddyPort = port.getText() ;

            if(buddyHost.equals(server.ip) && buddyPort.equals(server.port + ""))
            {
                JOptionPane.showMessageDialog(this,
                    "Well, why talk with yourself?") ;
                return ;
            }

            // ask buddy's name
            String buddyName = server.addBuddy(buddyHost, buddyPort) ;
            if(buddyName == null || MoAgent.FAIL.equals(buddyName))
            {
                JOptionPane.showMessageDialog(this, "Buddy is not answering!") ;
            }
            else
            {
                Buddy b = new Buddy() ;
                b.name = buddyName ;
                b.host = buddyHost ;
                b.port = buddyPort ;
                b.myID = server.myName ;
                lstModel.addElement(b) ;
            }
        }
    }

    public void onWho(ActionEvent actionEvent)
    {
        StandardDialog dlg = new StandardDialog() ;
        dlg.setTitle("Your name and address") ;

        String strName = prefs.get(NAME, server.ip + ":" + server.port) ;

        JTextField name = new JTextField(strName),
            ip = new JTextField(server.ip),
            port = new JTextField(server.port + "") ;
        ip.setEditable(false) ;
        port.setEditable(false) ;

        LabelledItemPanel myContentPane = new LabelledItemPanel() ;
        myContentPane.setBorder(BorderFactory.createEtchedBorder()) ;
        myContentPane.addItem("Your name", name) ;
        myContentPane.addItem("", new JLabel("Tell your buddy the follows:")) ;
        myContentPane.addItem("Your IP", ip) ;
        myContentPane.addItem("Your port", port) ;

        dlg.setContentPane(myContentPane) ;
        dlg.setSize(300, 300) ;
        GUIUtils.centerWithinScreen(dlg) ;
        dlg.setVisible(true) ;

        if(!dlg.hasUserCancelled())
        {
            prefs.put(NAME, name.getText()) ;
            server.myName = name.getText() ;
        }
    }

    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(this.btnAdd, this, "onAdd") ;
            MessageMap.mapAction(this.btnWho, this, "onWho") ;
            MessageMap.mapAction(this.btnAbout, this, "onAbout") ;
        }
        catch(Exception ex)
        {
        }
    }

    static String APP_NAME = "Bob Talk Instant Messenger" ;

    public void onAbout(ActionEvent e)
    {

        String infoAbout = "<html>" +
            "<font color=\"#FF0099\"><b>" +
            APP_NAME + "</b></font><br>Version " + 1.0 +
            " beta<br>" + "<br><b>Jie Bao</b><br>Aug 2005<br>" +
            "Iowa State University<br><a href=\"mailto:baojie@iastate.edu\">" +
            "baojie@iastate.edu</a><br><a href=\"http://www.cs.iastate.edu/~baojie\">" +
            "http://www.cs.iastate.edu/~baojie</a><br>" +
            "</html>" ;

        AboutBoxDialog dlg = new AboutBoxDialog(infoAbout, "About " + APP_NAME) ;
        dlg.showAboutBox() ;
    }

    static String NAME = "name" ;

    public void onBeAddedAsBuddy(final String buddyName, final String buddyHost,
        final String buddyPort)
    {
        new Thread()
        {
            public void run()
            {
                Buddy b = new Buddy() ;
                b.name = buddyName ;
                b.host = buddyHost ;
                b.port = buddyPort ;
                b.myID = server.myName ;
                lstModel.addElement(b) ;

                JLabel ll = new JLabel(b.name + " added you as buddy") ;
                ll.setBackground(Color.YELLOW) ;

                JDialog dlg = new JDialog() ;
                dlg.setTitle("You get a new buddy!") ;
                dlg.setModal(false) ;
                dlg.setLayout(new BorderLayout()) ;
                dlg.setUndecorated(true) ;

                dlg.add(new JLabel(icon), BorderLayout.NORTH) ;
                dlg.add(ll, BorderLayout.CENTER) ;
                //dlg.getContentPane().setBackground(Color.YELLOW) ;

                dlg.setSize(150, 50) ;

                final Toolkit toolKit = Toolkit.getDefaultToolkit() ;
                final Rectangle rcScreen = new Rectangle(toolKit.getScreenSize()) ;
                dlg.setLocation(rcScreen.width - 160, rcScreen.height - 150) ;

                dlg.setVisible(true) ;
                try
                {
                    this.sleep(5000) ;
                }
                catch(InterruptedException ex)
                {
                }
                dlg.setVisible(false) ;
                dlg.dispose() ;
                return ;
            }
        }.start() ;

    }
}
