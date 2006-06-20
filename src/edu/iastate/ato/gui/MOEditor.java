package edu.iastate.ato.gui ;

import java.io.IOException ;
import java.util.Enumeration ;
import java.util.Vector ;

import java.awt.BorderLayout ;
import java.awt.event.ActionEvent ;
import java.awt.event.WindowAdapter ;
import java.awt.event.WindowEvent ;
import javax.swing.Icon ;
import javax.swing.JDialog ;
import javax.swing.JEditorPane ;
import javax.swing.JFrame ;
import javax.swing.JLabel ;
import javax.swing.JOptionPane ;
import javax.swing.JScrollPane ;

import edu.iastate.anthill.indus.tree.TypedTree ;
import edu.iastate.ato.gui.dialog.LoginPanel ;
import edu.iastate.ato.gui.dialog.OnlineBuddyPanel ;
import edu.iastate.ato.gui.dialog.SchemaPanel ;
import edu.iastate.ato.gui.dialog.SettingPanel ;
import edu.iastate.ato.gui.dialog.UserManagementCenter ;
import edu.iastate.ato.gui.wizard.SeverBuilder ;
import edu.iastate.ato.po.DB2OBO ;
import edu.iastate.ato.po.DB2OWL ;
import edu.iastate.ato.po.OBO2DB ;
import edu.iastate.ato.po.OntologyServerBuilder ;
import edu.iastate.ato.po.User ;
import edu.iastate.ato.po.UserManager ;
import edu.iastate.ato.shared.AtoConstent ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.GUIUtils ;
import edu.iastate.utils.io.FileUtils ;
import edu.iastate.utils.lang.*;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class MOEditor extends MOEditorGui implements MessageHandler
{
    public static MOEditor theInstance ;

    private boolean canClose = true ;

    public MOEditor()
    {
        try
        {
            theInstance = this ;
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public static void main(String[] args)
    {
        MOEditor atoeditor = new MOEditor() ;

        atoeditor.mainFrame.getContentPane().add(atoeditor) ;
        //atoeditor.mainFrame.setSize(1024, 768);
        GUIUtils.maximize(atoeditor.mainFrame) ;
        atoeditor.jSplitPane1.setDividerLocation(600) ;
        //atoeditor.mainFrame.setTitle("Animal Trait Ontology Editor");
        atoeditor.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
        atoeditor.mainFrame.setVisible(true) ;
    }

    public void updateTitle()
    {
        String str = AtoConstent.APP_NAME + ", Current Ontology: " ;

        if(selectedServer == null)
        {
            str += "<none>" ;
        }
        else
        {
            str += selectedServer.name ;
            if(selectedServer.loaded == false)
            {
                str += " (not loaded) " ;
            }

            str += ", User: " + user ;
        }

        mainFrame.setTitle(str) ;

    }

    private void jbInit() throws Exception
    {
        messageMap() ;

        // start a server (for Instant Message)
        messenger.start() ;

        // read registered ontology list, and load the selected
        config.load(this) ;
        if(selectedServer != null)
        {
            load(selectedServer) ;
        }

        // update GUI
        updateTitle() ;
    }

    // 2005-08-25
    public void updateUser(User newUser)
    {
        if(this.user == newUser)
        {
            return ;
        }
        // report the log off the current user
        if(this.user != null)
        {
            // check in all packages
            UserManager.cancelAllEditing(conn.db, user.name) ;
            UserManager.logout(conn.db, user.name, messenger.getIp(),
                "" + messenger.getPort()) ;
            messenger.myName = User.GUEST ;
        }

        //report the login  of the new user
        if(newUser != null)
        {
            // cancel all editing by this user [may cause by incorrect exiting of last run]
            UserManager.cancelAllEditing(conn.db, newUser.name) ;
            UserManager.login(conn.db, newUser.name, messenger.getIp(),
                "" + messenger.getPort()) ;
            messenger.myName = newUser.name ;
        }
        this.user = newUser ;
    }

    public User askForUserName()
    {
        LoginPanel p = new LoginPanel(conn.db, user) ;
        p.showDlg() ;

        if(p.ok)
        {
            // if is the same id
            if(user != null && p.getUser().equals(user.name))
            {
                return user ;
            }

            User newUser = new User(conn.db, p.getUser()) ;
            //System.out.println(newUser);
            return newUser ;
        }
        else
        {
            return null ;
        }
    }

    public void onCancalAllEditing(ActionEvent e)
    {
        Vector<String>
            package_oid = UserManager.getEditingPackages(conn.db, user.name, true) ;
        Vector<String>
            package_pid = UserManager.getEditingPackages(conn.db, user.name, false) ;
        if(package_oid.size() == 0)
        {
            Debug.trace("There is no package edited by " + user) ;
        }
        else
        {
            UserManager.cancelAllEditing(conn.db, user.name) ;
            // set those packages readonly

            this.getPackageTree().cancelEditing(package_oid) ;
            Debug.trace(
                "You cancel the editing privilege for following packages:\n "
                + package_pid) ;
        }
    }

    /**
     * @since 2005-04-22
     * @param e ActionEvent
     */
    public void onAbout(ActionEvent e)
    {

        String infoAbout = "<html>" +
            "<font color=\"#FF0099\"><b>" +
            AtoConstent.APP_NAME + "</b></font><br>Version " + 2.1 +
            "<br>" + "<br><b>Jie Bao, Peter Wong, LaRon Hughes </b><br>2005-2006<br>" +
            "Iowa State University<br><a href=\"http://sourceforge.net/projects/cob/\">" +
            "http://sourceforge.net/projects/cob/</a><br>" +  "<br>" +
            "</html>" ;

        AboutBoxDialog dlg = new AboutBoxDialog(infoAbout, "About ATO Editor") ;
        dlg.showAboutBox() ;
    }

    /**
     * @since 2005-04-24
     * @param e ActionEvent
     */
    public void onHelp(ActionEvent e)
    {
        Icon hogIcon = GUIUtils.loadIcon("images/hog.jpg") ;
        JLabel hog = new JLabel(hogIcon) ;
        JDialog dlg = new JDialog(this.mainFrame, true) ;
        dlg.getContentPane().setLayout(new BorderLayout()) ;
        dlg.getContentPane().add(hog, BorderLayout.NORTH) ;

        JEditorPane editorPane = new JEditorPane() ;
        editorPane.setEditable(false) ;
        editorPane.setContentType("text/html") ;

        String info = AtoConstent.APP_NAME + "<br>Jie Bao, Aug 2005" ;
        try
        {
            info = FileUtils.readFile("atohelp.html") ;
        }
        catch(IOException ex)
        {
        }

        editorPane.setText(info) ;
//        editorPane.scrollToReference("Animal");
        JScrollPane jsp = new JScrollPane(editorPane) ;
        dlg.getContentPane().add(jsp, BorderLayout.CENTER) ;
        dlg.setTitle("Help on ATO Editor") ;
        dlg.setSize(400, 400) ;
        GUIUtils.centerWithinScreen(dlg) ;
        dlg.setVisible(true) ;
    }

    /**
     * @since 2005-04-22
     */
    public void onExit()
    {

        // logout and get off line
        this.closeOntology(this.selectedServer, true) ;
        updateUser(null) ;

        if (conn!= null )
        {
        conn.disconnect() ;
        conn = null ;
        }
        //config.save(this) ;
        setVisible(false) ;
        mainFrame.dispose() ;
        System.exit(0) ;
    }

    // 2005-08-31
    public void saveConfig()
    {
        config.save(this) ;
    }

    // 2005-08-19
    public void onLoad(ActionEvent e)
    {
        if(selectedServer != null)
        {
            load(this.selectedServer) ;
        }
        else
        {
            Debug.trace("No ontology is connected") ;
        }
    }

    public void onConfig(ActionEvent e)
    {
        //System.out.println(this.selectedOnt);

        JDialog dlg = new JDialog(mainFrame, true) ;
        SettingPanel p = new SettingPanel(serverList, selectedServer,dlg) ;
        dlg.getContentPane().add(p) ;
        dlg.setTitle("Ontology Server Setting") ;
        dlg.setSize(500, 330) ;
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE) ;
        GUIUtils.centerWithinParent(dlg) ;

        dlg.setVisible(true) ;
        this.serverList = p.getOntologyConfigs() ;

        if(p.selectedOnt != this.selectedServer)
        {
            load(p.selectedOnt) ;
        }
        if (p.isOK)
        {
            saveConfig();
        }
    }

    // 2005-08-19
    public void onClose(ActionEvent e)
    {
        if(selectedServer != null)
        {
            closeOntology(this.selectedServer, false) ;
        }
        else
        {
            Debug.trace("No ontology is connected") ;
        }
    }

    public void closeOntology(OntologyServerInfo info, boolean keepConnection)
    {
        if(!canClose)
        {
            Debug.trace("Close is forbidden, long task is running") ;
            return ;
        }
        if(info != null && conn != null)
        {
            // close all panels
            this.paneMain.close() ;
            this.paneDetails.close() ;

            // change menu
            mainFrame.setJMenuBar(makeSmallMenuBar()) ;
            jToolBar1.setVisible(false) ;

            // close the database connection
            info.loaded = false ;
            if(!keepConnection)
            {
                conn.disconnect() ;
                conn = null ;
            }
            updateTitle() ;
        }
    }

    // 2005-08-19
    public boolean load(OntologyServerInfo info)
    {
        if(info.loaded == true)
        {
            Debug.trace("Ontology '" + info.name + "' is already loaded") ;
            return false ;
        }

        int answer = JOptionPane.showConfirmDialog(null,
            "Load ontology '" + info.name + "'?") ;
        if(answer != JOptionPane.YES_OPTION)
        {
            return false ;
        }
        // close current ontology
        closeOntology(this.selectedServer,false) ;
        this.selectedServer = null ;
        updateTitle() ;

        // load it
        // 1. create connection
        conn = getConnection(info) ;
        if(conn == null)
        {
            Debug.trace("Cannot connect to the server") ;
            //System.exit(0);
            return false ;
        }

        // 2. ask for user name
        User newUser = askForUserName() ;
        if(newUser == null)
        {
            return false ;
        }
        updateUser(newUser) ;
        //System.out.println(user) ;

        //3. read ontology schema
        prepareNamingPolicy(conn.db) ;

        // 4. build the interface
        paneMain.rebuild(conn.db) ;
        this.paneDetails.rebuild(conn.db) ;
        mainFrame.setJMenuBar(makeFullMenuBar()) ;
        jToolBar1.setVisible(true) ;

        // update the selected ontology information
        this.selectedServer = info ;
        selectedServer.loaded = true ;

        // update the frame title
        updateTitle() ;
        return true ;
    }

    public void onOntologySchema(ActionEvent e)
    {
        SchemaPanel p = new SchemaPanel(conn.db) ;
        JDialog dlg = new JDialog(GUIUtils.getRootFrame(this)) ;
        dlg.getContentPane().add(p) ;
        dlg.setTitle("Ontology Schema") ;
        dlg.setSize(480, 360) ;
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE) ;
        GUIUtils.centerWithinParent(dlg) ;

        dlg.setVisible(true) ;
    }

    String searchTerm = null ;
    Enumeration enumSearch = null ;

    public void onFindNext(ActionEvent ae)
    {
        if(searchTerm == null)
        {
            String newTerm = JOptionPane.showInputDialog(
                "Search node on the ontology", searchTerm) ;
            if(newTerm == null)
            {
                return ;
            }
            searchTerm = newTerm ;
        }
        find() ;
    }

    // 2005-04-24
    public void onFind(ActionEvent ae)
    {
        String newTerm = JOptionPane.showInputDialog(
            "Search node on the ontology", searchTerm) ;
        if(newTerm == null)
        {
            return ;
        }
        searchTerm = newTerm ;

        find() ;

    }

    private void find()
    {
        TypedTree tree = this.getPackageTree() ;
        if(enumSearch == null || !enumSearch.hasMoreElements())
        {
            ATOTreeNode root = (ATOTreeNode)tree.getModel().getRoot() ;
            enumSearch = root.preorderEnumeration() ;
        }
        while(enumSearch.hasMoreElements())
        {
            ATOTreeNode node = (ATOTreeNode)enumSearch.nextElement() ;
            if(node.getComment() != null)
            {
                if(node.getComment().toString().indexOf(searchTerm) >= 0)
                {
                    tree.expandNode(node) ;
                    tree.setSelectionPath(tree.getPath(node)) ;
                    return ;
                }
            }
            if(node.getUserObject() != null)
            {
                if(node.getUserObject().equals(searchTerm))
                {
                    tree.expandNode(node) ;
                    tree.setSelectionPath(tree.getPath(node)) ;
                    return ;
                }
            }
        }
        Debug.trace(searchTerm + " has no (more) occurrence") ;
    }

    /**
     * Submit changes to the database
     *
     * @since 2005-04-20
     * @param e ActionEvent
     */
    public void onSubmit(ActionEvent e)
    {
        this.paneMain.packageView.onSave() ;
    }

    // 2005-04-23
    public void onExpand(ActionEvent e)
    {
        this.paneMain.getSelectedPanel().onExpand() ;
    }
   
    

    // 2005-08-15
    public void onExportOWL(ActionEvent e)
    {
        final String title = "Export to OWL" ;
        final String extension = "owl" ;
        final String description = "OWL Documents" ;

        String fileName = getFileName(title, extension, description, true) ;
        // get the ontology from the database
        if(fileName != null)
        {
            DB2OWL.exportOWL(conn.db, fileName) ;
        }
    }

    // 2005-08-15
    public void onExportOBO(ActionEvent e)
    {
        final String title = "Export to OBO" ;
        final String extension = "obo" ;
        final String description = "OBO Documents" ;

        String fileName = getFileName(title, extension, description, true) ;
        // get the ontology from the database
        if(fileName != null)
        {
            DB2OBO exporter = new DB2OBO() ;
            exporter.exportOBO(conn.db, fileName, user.name, true) ;
        }
    }
    
    //LaRon 06/18/06
    public void onExportText(ActionEvent e)
    {
    	final String title = "Export to Text" ;
        final String extension = "text" ;
        final String description = "Text Documents" ;
    	String fileName = getFileName(title, extension, description, true) ;
        // get the ontology from the database
        if(fileName != null)
        {
        	this.paneMain.getSelectedPanel().DB2TEXT(fileName);
        }
    
    	this.paneMain.getSelectedPanel().DB2TEXT(fileName);
    }
    
    
    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(this.btnUndo, this, "onUndo") ;
            MessageMap.mapAction(this.menuUndo, this, "onUndo") ;
            MessageMap.mapAction(this.btnRedo, this, "onRedo") ;
            MessageMap.mapAction(this.menuRedo, this, "onRedo") ;
            MessageMap.mapAction(this.btnConfig, this, "onConfig") ;
            MessageMap.mapAction(this.btnSubmit, this, "onSubmit") ;
            MessageMap.mapAction(this.btnAbout, this, "onAbout") ;
            MessageMap.mapAction(this.menuAbout, this, "onAbout") ;
            MessageMap.mapAction(this.btnExpand, this, "onExpand") ;
            MessageMap.mapAction(this.menuExpand, this, "onExpand") ;
            MessageMap.mapAction(this.menuExpandAll, this, "onExpandAll") ;
            MessageMap.mapAction(this.btnHelp, this, "onHelp") ;
            MessageMap.mapAction(this.menuHelp, this, "onHelp") ;
            MessageMap.mapAction(this.btnReload, this, "onReload") ;
            MessageMap.mapAction(this.btnFind, this, "onFind") ;
            MessageMap.mapAction(this.menuFind, this, "onFind") ;
            MessageMap.mapAction(this.menuFindNext, this, "onFindNext") ;
            MessageMap.mapAction(this.menuOntologySchema, this,
                "onOntologySchema") ;
            MessageMap.mapAction(this.menuConfig, this, "onConfig") ;
            MessageMap.mapAction(this.menuCancalAllEditing, this,
                "onCancalAllEditing") ;
            MessageMap.mapAction(this.menuExportOWL, this, "onExportOWL") ;
            MessageMap.mapAction(this.menuExportOBO, this, "onExportOBO") ;
            //LaRon 06/18/06
            MessageMap.mapAction(this.menuExportTEXT, this, "onExportText");
            MessageMap.mapAction(this.menuLoad, this, "onLoad") ;
            MessageMap.mapAction(this.menuClose, this, "onClose") ;
            MessageMap.mapAction(this.menuLogin, this, "onLogin") ;
            MessageMap.mapAction(this.menuLogout, this, "onLogout") ;
            MessageMap.mapAction(this.menuSetServer, this, "onSetServer") ;
            MessageMap.mapAction(this.menuClear, this, "onClear") ;
            MessageMap.mapAction(this.menuImportOBO, this, "onImportOBO") ;
            MessageMap.mapAction(this.menuUserMgmt, this, "onUserMgmt") ;
            MessageMap.mapAction(this.btnBuddy, this, "onBuddy") ;         
            
            mainFrame.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent evt)
                {
                    onExit() ;
                }
            }) ;
        }
        catch(Exception ex)
        {
        }
    }	public void messageMap() {
		try {
			MessageMap.mapAction(this.btnUndo, this, "onUndo");
			MessageMap.mapAction(this.menuUndo, this, "onUndo");
			MessageMap.mapAction(this.btnRedo, this, "onRedo");
			MessageMap.mapAction(this.menuRedo, this, "onRedo");
			MessageMap.mapAction(this.btnConfig, this, "onConfig");
			MessageMap.mapAction(this.btnSubmit, this, "onSubmit");
			MessageMap.mapAction(this.btnAbout, this, "onAbout");
			MessageMap.mapAction(this.menuAbout, this, "onAbout");
			MessageMap.mapAction(this.btnExpand, this, "onExpand");
			MessageMap.mapAction(this.menuExpand, this, "onExpand");
			MessageMap.mapAction(this.menuExpandAll, this, "onExpandAll");
			MessageMap.mapAction(this.btnHelp, this, "onHelp");
			MessageMap.mapAction(this.menuHelp, this, "onHelp");
			MessageMap.mapAction(this.btnReload, this, "onReload");
			MessageMap.mapAction(this.btnFind, this, "onFind");
			MessageMap.mapAction(this.menuFind, this, "onFind");
			MessageMap.mapAction(this.menuFindNext, this, "onFindNext");
			MessageMap.mapAction(this.menuOntologySchema, this,
					"onOntologySchema");
			MessageMap.mapAction(this.menuConfig, this, "onConfig");
			MessageMap.mapAction(this.menuCancalAllEditing, this,
					"onCancalAllEditing");
			MessageMap.mapAction(this.menuExportOWL, this, "onExportOWL");
			MessageMap.mapAction(this.menuExportOBO, this, "onExportOBO");
			MessageMap.mapAction(this.menuLoad, this, "onLoad");
			MessageMap.mapAction(this.menuClose, this, "onClose");
			MessageMap.mapAction(this.menuLogin, this, "onLogin");
			MessageMap.mapAction(this.menuLogout, this, "onLogout");
			MessageMap.mapAction(this.menuSetServer, this, "onSetServer");
			MessageMap.mapAction(this.menuClear, this, "onClear");
			MessageMap.mapAction(this.menuImportOBO, this, "onImportOBO");
			MessageMap.mapAction(this.menuUserMgmt, this, "onUserMgmt");
			MessageMap.mapAction(this.btnBuddy, this, "onBuddy");

			mainFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					onExit();
				}
			});
		} catch (Exception ex) {
		}
	}

    // 2005-08-25
    public void onBuddy(ActionEvent e)
    {
        OnlineBuddyPanel buddyList = new OnlineBuddyPanel(mainFrame,
            selectedServer) ;
        GUIUtils.centerWithinParent(buddyList) ;
        buddyList.setVisible(true) ;
    }

    // 2005-08-23
    public void onUserMgmt(ActionEvent e)
    {
        if(user.isAdmin())
        {
            UserManagementCenter dlg = new UserManagementCenter
                (this.mainFrame, this.selectedServer) ;
            GUIUtils.centerWithinParent(dlg) ;
            dlg.setVisible(true) ;
        }
        else
        {
            Debug.trace("You should have admin privilege") ;
        }
    }

    // reload the ontology package tree
    // 2005-08-22
    public void onReload(ActionEvent e)
    {
        paneMain.rebuild(conn.db) ;
        this.paneDetails.rebuild(conn.db) ;
    }

    // 2005-08-21
    public void onImportOBO(ActionEvent e)
    {
        if(!user.isAdmin())
        {
            JOptionPane.showMessageDialog(this,
                "You must be admin to import an ontology") ;
            return ;
        }
        // open a file
        final String title = "Import OBO Ontology" ;
        final String extension = "obo" ;
        final String description = "OBO Documents" ;

        final String fileName = getFileName(title, extension, description, true) ;
        // get the ontology from the database
        if(fileName != null)
        {
            Debug.trace("Please don't close the ontology during importing") ;

            Thread t = new Thread()
            {
                public void run()
                {
                    int pb = statusBar.addProgressBar(true, 0, 0) ;
                    statusBar.updateProgressBar(pb, "Importing " + fileName) ;

                    try
                    {
                        // do something here
                        OBO2DB loader = new OBO2DB(conn.db, user.name) ;
                        loader.setProgress(statusBar.getProgressBar(pb)) ;

                        StopWatch w = new StopWatch();
                        w.start();
                        boolean suc = loader.importOBO(fileName, true) ;
                        w.stop();
                        System.out.println(w.print());

                        // reload the
                        Debug.trace(suc) ;
                        paneMain.rebuild(conn.db) ;
                        paneDetails.rebuild(conn.db) ;
                    }
                    catch(Exception ex)
                    {
                    }
                    statusBar.removeProgressBar(pb) ;

                }
            } ;
            t.start() ;
        }
    }

    // 2005-08-21
    public void onClear(ActionEvent e)
    {
        // to check if the user is admin
        if(!user.isAdmin())
        {
            JOptionPane.showMessageDialog(this,
                "You must be admin to clear the ontology") ;
            return ;
        }

        int answer = JOptionPane.showConfirmDialog(null,
            "Are you sure to delete all content in the database?\n" +
            "You will also delete all non-admin acounts\n" +
            "The deletion can NOT be undone.") ;
        if(answer == JOptionPane.YES_OPTION)
        {
            // @todo if anyone else is editing the ontology, close the editing?

            // do the deletion
            String msg = OntologyServerBuilder.clearDatabase(conn.db, false) ;
            System.out.println(msg) ;
            // clear the interface
            this.paneMain.rebuild(conn.db) ;
            this.paneDetails.rebuild(conn.db) ;
        }
    }

    // 2005-08-20
    public void onSetServer(ActionEvent e)
    {
        SeverBuilder builder = new SeverBuilder() ;
    }

    // 2005-08-19
    public void onLogin(ActionEvent e)
    {
        User newUser = askForUserName() ;
        if(newUser != null && newUser != this.user)
        {
            logout() ;
            updateUser(newUser) ;
            updateTitle() ;
        }
    }

    public void logout()
    {
        // cancel editing in database
        UserManager.cancelAllEditing(conn.db, user.name) ;
        // cancel editing on the GUI
        Vector<PackageNode> pkgs = this.getPackageTree().getAllPackage() ;
        for(PackageNode pkg : pkgs)
        {
            pkg.setReadOnly(true) ;
        }
    }

    // 2005-08-19
    public void onLogout(ActionEvent e)
    {
        if(user.isGuest())
        {
            return ;
        }

        logout() ;
        // login as guest
        updateUser(User.getGuest()) ;
        updateTitle() ;
    }
}
