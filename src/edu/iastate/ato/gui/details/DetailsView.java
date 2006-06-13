package edu.iastate.ato.gui.details ;

import java.sql.Connection ;

import java.awt.BorderLayout ;
import java.awt.event.ActionEvent ;
import java.awt.event.ComponentAdapter ;
import java.awt.event.ComponentEvent ;
import javax.swing.JButton ;
import javax.swing.JLabel ;
import javax.swing.JPanel ;
import javax.swing.SwingConstants ;

import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.gui.HolderPanel ;
import edu.iastate.utils.lang.MessageHandler ;
import edu.iastate.utils.lang.MessageMap ;

/**
 * <p>@author Jie Bao</p>
 *
 * <p>@since </p>
 */
public class DetailsView extends HolderPanel implements MessageHandler
{
    Connection db ;

    JLabel labelNodeName = new JLabel() ;

    JPanel buttonPane = new JPanel() ;
    JButton btnLine = new JButton("Line Panels") ;
    JButton btnTile = new JButton("Tile Panels") ;
    JButton btnShowAll = new JButton("Show All") ;

    HolderPanel allDetails = new HolderPanel() ;
    NavigationPanel paneSub, paneSuper ;
    TermPropertyPanel paneProperty ;
    StatisticsPanel paneStat ;

    JButton lastCaller = btnTile ;

    public DetailsView()
    {
        messageMap() ;
        this.setLayout(new BorderLayout()) ;
        this.addComponentListener(new ComponentAdapter()
        {
            public void componentResized(ComponentEvent e)
            {
                onResize() ;
            }
        }) ;

        buttonPane.add(this.btnLine) ;
        buttonPane.add(this.btnTile) ;
        buttonPane.add(this.btnShowAll) ;

    }

    // 2005-08-19
    public void close()
    {
        // prompt to save
        allDetails.removeAll() ;
        this.removeAll() ;
        this.db = null ;
    }

    public void rebuild(Connection db)
    {
        try
        {
            close() ;
            this.db = db ;
            jbInit() ;
            onTile(null) ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public String getString(Object obj)
    {
        return(obj == null) ? null : obj.toString() ;
    }

    private void jbInit() throws Exception
    {
        try
        {
            // 1. The name of the selected node
            labelNodeName.setHorizontalAlignment(SwingConstants.CENTER) ;
            labelNodeName.setText(" ") ;
            this.add(labelNodeName, java.awt.BorderLayout.NORTH) ;

            // 2. all the details panes
            this.add(allDetails, java.awt.BorderLayout.CENTER) ;

            // 2.1 Statistics pane
            paneStat = new StatisticsPanel(db) ;
            allDetails.addPane(paneStat, "Node Statistics") ;

            // 2.2 sub class pane
            paneSub = new NavigationPanel(db, true) ;
            allDetails.addPane(paneSub, "Sub Classes") ;

            // 2.3 super class pane
            paneSuper = new NavigationPanel(db, false) ;
            allDetails.addPane(paneSuper, "Super Classes") ;

            // 2.4 properties pane
            paneProperty = new TermPropertyPanel(db) ;
            allDetails.addPane(paneProperty, "Properties") ;

            // 3. button
            this.add(this.buttonPane, java.awt.BorderLayout.SOUTH) ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public void onResize()
    {
        if(lastCaller == this.btnLine)
        {
            this.onLine(null) ;
        }
        else if(lastCaller == this.btnTile)
        {
            this.onTile(null) ;
        }
    }

    public void messageMap()
    {
        try
        {
            MessageMap.mapAction(this.btnLine, this, "onLine") ;
            MessageMap.mapAction(this.btnTile, this, "onTile") ;
            MessageMap.mapAction(this.btnShowAll, this, "onShowAll") ;
        }
        catch(Exception ex)
        {
        }
    }

    public void onShowAll(ActionEvent evt)
    {
        allDetails.showAll() ;
        onResize() ;
    }

    public void onLine(ActionEvent evt)
    {
        if(allDetails != null)
        {
            lastCaller = this.btnLine ;
            allDetails.line() ;
        }
    }

    public void onTile(ActionEvent evt)
    {
        if(allDetails != null)
        {
            lastCaller = this.btnTile ;
            allDetails.tile() ;
        }
    }

    public void update(ATOTreeNode selectedNode)
    {
        labelNodeName.setText(selectedNode.getLocalName()) ;

        if(selectedNode instanceof DbTermNode)
        { // term node
            ((DbTermNode)selectedNode).getThisTerm().print() ;
            paneSub.update(selectedNode) ;
            paneSuper.update(selectedNode) ;
            makePropertyPane((DbTermNode)selectedNode) ;
            makeStatisticsPane((DbTermNode)selectedNode) ;
        }
        else if(selectedNode instanceof PackageNode)
        {
            paneSub.update((ATOTreeNode)null) ;
            paneSuper.update((ATOTreeNode)null) ;
            paneStat.updatePanel((PackageNode)selectedNode) ;
            makePropertyPane((DbTermNode)null) ;
        }
    }

    /**
     * update the statistic panel
     * @param selectedNode DbTermNode
     * @since 2005-07-23
     */
    public void makeStatisticsPane(DbTermNode selectedNode)
    {
        this.paneStat.updatePanel(selectedNode) ;
    }

    /**
     * make a property panel for a node
     * @param selectedNode ATOTreeNode
     * @since 2005-07-22
     */
    public void makePropertyPane(DbTermNode selectedNode)
    {
        paneProperty.updatePanel(selectedNode) ;
    }

    // if the
    public boolean switchPropertyEditor(ATOTreeNode thePackageNode)
    {
        // if the
        if(paneProperty.selectedNode != null)
        {
            boolean enabled = (paneProperty.selectedNode.getHomePackageNode() !=
                thePackageNode) ;
            paneProperty.enableEditing(enabled) ;
            return enabled ;
        }
        return false ;
    }
}
