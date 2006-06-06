package edu.iastate.ato.gui ;

import java.awt.BorderLayout ;
import javax.swing.JButton ;
import javax.swing.JMenu ;
import javax.swing.JMenuBar ;
import javax.swing.JMenuItem ;
import javax.swing.JScrollPane ;
import javax.swing.JSplitPane ;
import javax.swing.JToolBar ;
import javax.swing.KeyStroke ;
import javax.swing.ToolTipManager ;

import edu.iastate.ato.gui.packageview.PackageTree ;
import edu.iastate.ato.gui.details.DetailsView ;
import edu.iastate.ato.shared.IconLib ;

import edu.iastate.utils.gui.JStatusBar ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07</p>
 */
public class MOEditorGui extends MOEditorBasis
{
    public MOEditorGui()
    {
        try
        {
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public PackageTree getPackageTree()
    {
        return(PackageTree)paneMain.packageView.getTreeOntology() ;
    }

    protected JToolBar jToolBar1 = new JToolBar() ;
    public JStatusBar statusBar = new JStatusBar(
        "Double click a node to expand, right click on node to see menu") ;

    public JButton btnUndo = new JButton() ;
    public JButton btnRedo = new JButton() ;
    protected JButton btnConfig = new JButton() ;
    protected JButton btnSubmit = new JButton() ;
    protected JButton btnReload = new JButton() ;
    protected JButton btnFind = new JButton() ;
    protected JButton btnAbout = new JButton() ;
    protected JButton btnExpand = new JButton() ;
    protected JButton btnHelp = new JButton() ;
    protected JButton btnBuddy = new JButton() ;

    protected JSplitPane jSplitPane1 = new JSplitPane() ;

    public DetailsView paneDetails = new DetailsView() ;

    protected JMenu jmenuEdit = new JMenu() ;
    protected JMenuItem menuExpand = new JMenuItem() ;
    protected JMenuItem menuExpandAll = new JMenuItem() ;
    protected JMenuItem menuFindNext = new JMenuItem() ;
    protected JMenuItem menuFind = new JMenuItem() ;
    protected JMenuItem menuUndo = new JMenuItem() ;
    protected JMenuItem menuRedo = new JMenuItem() ;
    protected JMenuItem menuLoad = new JMenuItem() ;
    protected JMenuItem menuClose = new JMenuItem() ;
    protected JMenuItem menuClear = new JMenuItem() ;
    protected JMenuItem menuExportOWL = new JMenuItem() ;
    protected JMenuItem menuExportOBO = new JMenuItem() ;
    protected JMenuItem menuImportOBO = new JMenuItem() ;

    protected JMenu jmenuHelp = new JMenu() ;
    protected JMenuItem menuHelp = new JMenuItem() ;
    protected JMenuItem menuAbout = new JMenuItem() ;

    protected JMenu jmenuSetting = new JMenu() ;
    public JMenuItem menuConfig = new JMenuItem() ;
    public JMenuItem menuOntologySchema = new JMenuItem() ;
    protected JMenuItem menuLogin = new JMenuItem() ;
    protected JMenuItem menuLogout = new JMenuItem() ;
    protected JMenuItem menuCancalAllEditing = new JMenuItem() ;
    protected JMenuItem menuSetServer = new JMenuItem() ;
    protected JMenuItem menuUserMgmt = new JMenuItem() ;

    private void jbInit() throws Exception
    {
        // Show tool tips immediately
        ToolTipManager.sharedInstance().setInitialDelay(0) ;

        this.setLayout(new BorderLayout()) ;

        jmenuEdit.setText("Edit") ;

        menuLoad.setText("Load Ontology") ;
        menuLoad.setIcon(IconLib.iconLoad) ;
        menuLoad.setAccelerator(KeyStroke.getKeyStroke("control L")) ;

        menuClose.setText("Close Ontology") ;
        menuClose.setIcon(IconLib.iconClose) ;
        menuClose.setAccelerator(KeyStroke.getKeyStroke("control F4")) ;

        menuClear.setText("Clear Ontology") ;
        menuClear.setIcon(IconLib.iconClear) ;

        menuFindNext.setIcon(IconLib.findnextIcon) ;
        menuFindNext.setText("Find Next") ;
        menuFindNext.setAccelerator(KeyStroke.getKeyStroke("F3")) ;

        menuFind.setText("Find") ;
        menuFind.setAccelerator(KeyStroke.getKeyStroke("control F3")) ;
        menuFind.setIcon(IconLib.searchIcon) ;

        menuUndo.setIcon(IconLib.undoIcon) ;
        menuUndo.setText("Undo") ;
        menuUndo.setAccelerator(KeyStroke.getKeyStroke("control Z")) ;

        menuRedo.setIcon(IconLib.redoIcon) ;
        menuRedo.setText("Redo") ;
        menuRedo.setAccelerator(KeyStroke.getKeyStroke("control R")) ;

        jmenuHelp.setText("Help") ;
        menuHelp.setIcon(IconLib.helpIcon) ;
        menuHelp.setText("Help...") ;
        menuHelp.setAccelerator(KeyStroke.getKeyStroke("F1")) ;

        menuAbout.setIcon(IconLib.aboutIcon) ;
        menuAbout.setText("About...") ;

        menuExpand.setIcon(IconLib.expandIcon) ;
        menuExpand.setText("Expand") ;
        menuExpand.setAccelerator(KeyStroke.getKeyStroke("control E")) ;

        menuExpandAll.setIcon(IconLib.expandAllIcon) ;
        menuExpandAll.setText("Expand All") ;
        menuExpandAll.setAccelerator(KeyStroke.getKeyStroke("control A")) ;

        jmenuSetting.setText("Setting") ;

        menuSetServer.setText("Create Ontology") ;
        menuSetServer.setIcon(IconLib.iconCreate) ;
        menuSetServer.setAccelerator(KeyStroke.getKeyStroke("control N")) ;

        menuConfig.setText("Choose Ontology") ;
        menuConfig.setIcon(IconLib.configIcon) ;

        menuOntologySchema.setText("Ontology Schema") ;
        menuOntologySchema.setIcon(IconLib.iconSchema) ;

        menuUserMgmt.setText("User Management") ;
        menuUserMgmt.setIcon(IconLib.iconUsers) ;

        menuLogin.setText("Log in as...") ;
        menuLogin.setIcon(IconLib.iconLogin) ;

        menuLogout.setText("Log out") ;
        menuLogout.setIcon(IconLib.iconLogout) ;

        menuCancalAllEditing.setText("Quit All Editing") ;
        menuCancalAllEditing.setIcon(IconLib.iconCancel) ;

        menuExportOWL.setText("Export to OWL Format") ;
        menuExportOWL.setIcon(IconLib.iconOWL) ;

        menuExportOBO.setText("Export to OBO Format") ;
        menuExportOBO.setIcon(IconLib.iconBlank) ;

        menuImportOBO.setText("Import from OBO Format") ;
        menuImportOBO.setIcon(IconLib.iconBlank) ;

        makeToolbar() ;

        this.add(statusBar, BorderLayout.SOUTH) ;

        this.add(jSplitPane1, BorderLayout.CENTER) ;
        jSplitPane1.setOrientation(JSplitPane.HORIZONTAL_SPLIT) ;
        jSplitPane1.add(paneMain, JSplitPane.LEFT) ;
        jSplitPane1.add(paneDetails, JSplitPane.RIGHT) ;

        mainFrame.setJMenuBar(makeSmallMenuBar()) ;
        jToolBar1.setVisible(false) ;
        this.add(jToolBar1, BorderLayout.WEST) ;
    }

    private void makeToolbar()
    {
        btnUndo.setToolTipText("Undo") ;
        btnUndo.setIcon(IconLib.undoIcon) ;
        btnRedo.setToolTipText("Redo") ;
        btnRedo.setIcon(IconLib.redoIcon) ;
        btnSubmit.setToolTipText("Submit Change") ;
        btnSubmit.setIcon(IconLib.submitIcon) ;
        btnConfig.setToolTipText("Choose Ontology") ;
        btnConfig.setIcon(IconLib.configIcon) ;
        btnAbout.setToolTipText("About") ;
        btnAbout.setIcon(IconLib.aboutIcon) ;
        btnExpand.setToolTipText("Expand") ;
        btnExpand.setIcon(IconLib.expandIcon) ;
        btnHelp.setToolTipText("Help") ;
        btnHelp.setIcon(IconLib.helpIcon) ;
        btnReload.setToolTipText("Reload Ontology") ;
        btnReload.setIcon(IconLib.reloadIcon) ;
        btnBuddy.setToolTipText("View online buddy") ;
        btnBuddy.setIcon(IconLib.iconChat) ;

        btnFind.setToolTipText("Find (F3 to repeat last search)") ;
        btnFind.setIcon(IconLib.searchIcon) ;

        jToolBar1.setOrientation(JToolBar.VERTICAL) ;
        jToolBar1.add(btnExpand) ;
        jToolBar1.add(btnFind) ;

        //jToolBar1.add(btnUndo) ;
        //jToolBar1.add(btnRedo) ;
        jToolBar1.add(btnReload) ;
        jToolBar1.add(btnSubmit) ;
        jToolBar1.add(btnConfig) ;
        jToolBar1.add(btnBuddy) ;
        jToolBar1.add(btnHelp) ;
        jToolBar1.add(btnAbout) ;
    }

    // 2005-08-19
    public JMenuBar makeFullMenuBar()
    {
        JMenuBar jMenuBar1 = new JMenuBar() ;

        jmenuEdit.removeAll() ;
        jmenuSetting.removeAll() ;
        jmenuHelp.removeAll() ;

        jMenuBar1.add(jmenuEdit) ;
        jMenuBar1.add(jmenuSetting) ;
        jMenuBar1.add(jmenuHelp) ;

        jmenuEdit.add(menuLoad) ;
        jmenuEdit.add(menuClose) ;
        jmenuEdit.add(menuClear) ;
        jmenuEdit.addSeparator() ;
        jmenuEdit.add(menuExpand) ;
        //jmenuEdit.add(menuExpandAll) ;
        jmenuEdit.addSeparator() ;
        jmenuEdit.add(menuFind) ;
        jmenuEdit.add(menuFindNext) ;
        jmenuEdit.addSeparator() ;
        //jmenuEdit.add(menuUndo) ;
        //jmenuEdit.add(menuRedo) ;
        //jmenuEdit.addSeparator() ;
        jmenuEdit.add(menuExportOWL) ;
        jmenuEdit.add(menuExportOBO) ;
        jmenuEdit.add(menuImportOBO) ;

        jmenuSetting.add(menuSetServer) ;
        jmenuSetting.add(menuConfig) ;
        jmenuSetting.addSeparator() ;
        jmenuSetting.add(menuOntologySchema) ;
        jmenuSetting.add(menuUserMgmt) ;
        jmenuSetting.addSeparator() ;
        jmenuSetting.add(menuLogin) ;
        jmenuSetting.add(menuLogout) ;
        jmenuSetting.add(menuCancalAllEditing) ;

        jmenuHelp.add(menuHelp) ;
        jmenuHelp.add(menuAbout) ;

        return jMenuBar1 ;
    }

    // 2005-08-19
    public JMenuBar makeSmallMenuBar()
    {
        JMenuBar jMenuBar1 = new JMenuBar() ;
        jmenuSetting.removeAll() ;
        jmenuHelp.removeAll() ;
        jMenuBar1.add(jmenuSetting) ;
        jMenuBar1.add(jmenuHelp) ;

        jmenuSetting.add(menuSetServer) ;
        jmenuSetting.add(menuConfig) ;
        jmenuSetting.add(menuLoad) ;

        jmenuHelp.add(menuHelp) ;
        jmenuHelp.add(menuAbout) ;

        return jMenuBar1 ;
    }

    protected MainPanel paneMain = new MainPanel() ;
    protected JScrollPane paneTree = new JScrollPane() ;
}
