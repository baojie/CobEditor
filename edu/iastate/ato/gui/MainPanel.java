package edu.iastate.ato.gui ;

import java.sql.Connection ;
import java.util.Vector ;

import java.awt.BorderLayout ;
import javax.swing.JPanel ;
import javax.swing.JTabbedPane ;

import edu.iastate.ato.gui.packageview.PackageView ;
import edu.iastate.ato.gui.relationview.RelationView ;
import edu.iastate.ato.po.OntologySchema ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07-18</p>
 */
public class MainPanel extends JPanel
{
    JTabbedPane tabbedPane = new JTabbedPane() ;
    public PackageView packageView ;
    public Vector<RelationView> relationViews = new Vector<RelationView>() ;

    Connection db ;

    // 2005-08-19
    public void close()
    {
        // clear up all panes
        if(packageView != null)
        {
            packageView.onClose() ;
        }
        for(RelationView v : relationViews)
        {
            v.onClose() ;
        }

        tabbedPane.removeAll() ;
        this.db = null ;
    }

    public void rebuild(Connection db)
    {
        close() ;
        this.db = db ;

        packageView = new PackageView(db) ;
        tabbedPane.add(packageView, "Package View") ;

        this.add(tabbedPane, java.awt.BorderLayout.CENTER) ;

        // build partial order views
        // read all possible partial orders
        Vector<String> po = OntologySchema.getPartialOrders(db) ;
        for(String po_type : po)
        {
            RelationView rv = new RelationView(db, po_type) ;
            relationViews.add(rv) ;
            tabbedPane.add(rv, po_type + " View") ;
        }
    }

    public MainPanel()
    {
        setLayout(new BorderLayout()) ;
    }

    // 2005-07-27 Jie Bao
    public TypedTreePanel getSelectedPanel()
    {
        TypedTreePanel p = (TypedTreePanel)tabbedPane.getSelectedComponent() ;
        return p ;
    }
}
