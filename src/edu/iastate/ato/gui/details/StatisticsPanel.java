package edu.iastate.ato.gui.details ;

import java.sql.Connection ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.Vector ;

import java.awt.BorderLayout ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextField ;

import edu.iastate.ato.po.DbPackage ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.po.OntologySchema ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.gui.LabelledItemPanel ;

/**
 * <p>@author Jie Bao</p>
 *
 * <p>@since </p>
 */
public class StatisticsPanel extends JPanel
{
    BorderLayout borderLayout1 = new BorderLayout() ;
    Connection db ;

    LabelledItemPanel paneForPackageNode = new LabelledItemPanel(3) ;
    LabelledItemPanel paneForTermNode = new LabelledItemPanel(3) ;

    JScrollPane termModeView = new JScrollPane() ;
    JScrollPane packageModeView = new JScrollPane() ;

    // map from the property name to its JTextField
    Map<String, JTextField> attributeEditors = new HashMap() ;

    public StatisticsPanel(Connection db)
    {
        try
        {
            this.db = db ;
            jbInit() ;
        }
        catch(Exception exception)
        {
            exception.printStackTrace() ;
        }
    }

    String NAME = "Term" ;
    String PKG_NAME = "Package" ;
    String HOME = "Home Package" ;
    String STATUS = "Status" ;
    String PKG_STATUS = "Package Status" ;
    String SLM = "Visiblity" ;
    String SUBCLASS_NUM = "Direct Subclasses" ;
    String SUPERCLASS_NUM = "Direct Superclasses" ;
    String AUTHOR = "Author" ;
    String PKG_AUTHOR = "Package Author" ;
    String MODIFIED = "Modified" ;
    String PKG_MODIFIED = "PAckage Modified" ;

    private void jbInit() throws Exception
    {
        this.setLayout(borderLayout1) ;

        makeTermModeView() ;
        makePackageModeView() ;
    }

    private void makePackageModeView()
    {
        Vector<String> pkgStatistics = new Vector<String>() ;
        pkgStatistics.add(PKG_NAME) ;
        pkgStatistics.add(PKG_STATUS) ;
        pkgStatistics.add(PKG_AUTHOR) ;
        pkgStatistics.add(PKG_MODIFIED) ;

        for(String property : pkgStatistics)
        {
            JTextField tf = new JTextField() ;
            attributeEditors.put(property, tf) ;
            paneForPackageNode.addItem(property, tf) ;
            tf.setEditable(false) ;
        }
        packageModeView.getViewport().add(paneForPackageNode) ;
    }

    private void makeTermModeView()
    {
        Vector<String> termStatistics = new Vector<String>() ;
        termStatistics.add(NAME) ;
        termStatistics.add(HOME) ;
        termStatistics.add(STATUS) ;
        termStatistics.add(SLM) ;
        termStatistics.add(SUBCLASS_NUM) ;
        termStatistics.add(SUPERCLASS_NUM) ;
        termStatistics.add(AUTHOR) ;
        termStatistics.add(MODIFIED) ;

        for(String property : termStatistics)
        {
            JTextField tf = new JTextField() ;
            attributeEditors.put(property, tf) ;
            paneForTermNode.addItem(property, tf) ;
            tf.setEditable(false) ;
            //System.out.println(property);
        }
        termModeView.getViewport().add(paneForTermNode) ;
    }

    // 2005-08-20
    public void updatePanel(PackageNode selectedNode)
    {
        this.removeAll() ;
        this.add(packageModeView, BorderLayout.CENTER) ;

        String oid = selectedNode.getOid() ;

        // package name
        setProperty(PKG_NAME, selectedNode.getLocalName()) ;
        this.repaint() ;
        // node status
        String status_str = selectedNode.status2string() ;
        setProperty(PKG_STATUS, status_str) ;

        // get the author of the package
        String str = selectedNode.getThisPackage().author ;
        setProperty(PKG_AUTHOR, str) ;
        // get the modified time  of the package
        str = selectedNode.getThisPackage().modified ;
        setProperty(PKG_MODIFIED, str) ;

    }

    public void updatePanel(DbTermNode selectedNode)
    {
        this.removeAll() ;
        this.add(termModeView, BorderLayout.CENTER) ;

        String term_oid = selectedNode.getThisTerm().oid ;

        Vector<String> allRelations = OntologySchema.getPartialOrders(db) ;

        // term name
        setProperty(NAME, selectedNode.getThisTerm().id) ;

        // get the home package
        String pkg_oid = selectedNode.getThisTerm().package_oid ;
        DbPackage pkg = DbPackage.read(db, pkg_oid) ;
        setProperty(HOME, pkg.pid) ;

        // node status
        String status_str = selectedNode.status2string() ;
        setProperty(STATUS, status_str) ;

        // scope limitation
        String slm = ATOTreeNode.type2slm(selectedNode.getType()) ;
        setProperty(SLM, slm) ;

        String str = "" ;
        // get the number of subclasses of the node
        for(String relation : allRelations)
        {
            String count = OntologyQuerier.getDirectSubclassCount(db,
                term_oid, relation) ;
            str += count + "(" + relation + "), " ;
        }
        setProperty(SUBCLASS_NUM, str.substring(0, str.length() - 2)) ;
        // get the number of superclasses of the node
        str = "" ;
        for(String relation : allRelations)
        {
            String count = OntologyQuerier.getDirectSuperclassCount(db,
                term_oid,  relation) ;
            str += count + "(" + relation + "), " ;
        }
        setProperty(SUPERCLASS_NUM, str.substring(0, str.length() - 2)) ;
        // get the author of the term
        str = selectedNode.getThisTerm().author ;
        setProperty(AUTHOR, str) ;
        // get the modified time  of the term
        str = selectedNode.getThisTerm().modified ;
        setProperty(MODIFIED, str) ;

        this.repaint() ;
    }

    void setProperty(String attr, String value)
    {
        JTextField tf = attributeEditors.get(attr) ;
        tf.setText(value) ;
    }
}
