package edu.iastate.ato.gui.packageview ;

import java.sql.Connection ;

import java.awt.BorderLayout ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JOptionPane ;
import javax.swing.JScrollPane ;
import javax.swing.tree.TreePath ;

import edu.iastate.anthill.indus.tree.TypedNode ;
import edu.iastate.anthill.indus.tree.TypedTree ;
import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.TypedTreePanel ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.ATOTreeRender ;
import edu.iastate.ato.tree.DBTermCloneNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.MetaTreeNode ;
import edu.iastate.ato.tree.PackageNode ;
import edu.iastate.ato.tree.Term2Tree ;
import edu.iastate.utils.lang.*;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07-18</p>
 */
public class PackageView extends TypedTreePanel
{
    Connection db ;

    public PackageView(Connection db)
    {
        try
        {
            this.db = db ;

            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    public ATOTreeRender treeRender ;
    public PackageTreeEditor treeEditor ;

    private void jbInit() throws Exception
    {
        this.setLayout(borderLayout1) ;
        this.add(jScrollPane1, BorderLayout.CENTER) ;

        Package2Tree mm = new Package2Tree(db) ;
        treeOntology = (PackageTree)mm.getTree(null, -1) ;

        treeRender = new ATOTreeRender() ;
        treeOntology.setCellRenderer(treeRender) ;

        treeEditor = new PackageTreeEditor((PackageTree)treeOntology, db) ;
        treeOntology.addMouseListener(treeEditor) ;
        treeOntology.addMouseListener(new TreeClickListener(treeOntology)) ;

        // enable drag & drop
        treeOntology.enableDragDrop = true ;
        PackageTreeDargDropListener l = new PackageTreeDargDropListener(
            db, (PackageTree)treeOntology) ;
        treeOntology.addDrageDropListener(l) ;

        jScrollPane1.getViewport().add(treeOntology) ;
    }

    JScrollPane jScrollPane1 = new JScrollPane() ;
    BorderLayout borderLayout1 = new BorderLayout() ;

    public void onExpand()
    {
        TypedNode selected = (TypedNode)treeOntology.getSelectedNode() ;

        if(selected instanceof PackageNode)
        {
            expandPackage((PackageNode)selected) ;
            treeOntology.getModel().reload(selected) ;
            
            // 2006-06-12 generate a text version of the DAG
            System.out.println(treeOntology.toString());
        }
        else if(selected instanceof MetaTreeNode) // obsolete node, 2005-08-30
        {
            PackageNode pkg = (PackageNode)selected.getParent() ;
            pkg.treeLoader.addObsoleteTerms(selected, pkg) ;
        }
    }

    // 2005-08-23
    public void expandPackage(PackageNode thePackage)
    {
        treeEditor.expandPackage(thePackage) ;
    }

    // 2005-08-31
    public void onClose()
    {
        // prompt to save
        PackageTree tree = (PackageTree)treeOntology ;
        if(tree.modified)
        {
            //ask to save
            int answer = JOptionPane.showConfirmDialog(this, "Save Changes? ") ;
            if(answer == JOptionPane.YES_OPTION)
            {
                onSave() ;
            }
        }
    }

    public void onSave()
    {
        PackageTree tree = (PackageTree)treeOntology ;
        tree.save() ;
    }

    class TreeClickListener extends MouseAdapter
    {
        TypedTree tree ;
        public TreeClickListener(TypedTree tree)
        {
            this.tree = tree ;
        }

        public void mouseClicked(MouseEvent e)
        {
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY()) ;
            if(selPath == null)
            {
                return ;
            }
            final ATOTreeNode selectedNode = (ATOTreeNode)selPath.
                getLastPathComponent() ;
            tree.setSelectionPath(selPath) ;

            if(e.getClickCount() == 1)
            {
                StopWatch w = new StopWatch();
                w.start();
                MOEditor.theInstance.paneDetails.update(selectedNode) ;
                w.stop();
                System.out.println(w.print());
            }
            else if(e.getClickCount() == 2)
            {
                onDoubleClick(selectedNode) ;
            }
        }

        private void onDoubleClick(ATOTreeNode selectedNode)
        {
            //Debug.trace("double clicked on " + selectedNode);
            //addOneLevel(selectedNode);

            // package node, load/reload it
            if(selectedNode instanceof PackageNode)
            {
                PackageNode thePackage = (PackageNode)selectedNode ;

                if(!thePackage.expanded)
                {
                    // if loaded, ask for reload
                    int answer = JOptionPane.showConfirmDialog(null,
                        "Reload from database? ") ;
                    if(answer == JOptionPane.YES_OPTION)
                    {
                        expandPackage(thePackage) ;
                    }
                }
                else
                { // not loaded yet, load it
                    expandPackage(thePackage) ;
                }
            }
            else if(selectedNode instanceof DBTermCloneNode)
            {
                // cloned term node, jump to the source node
                TypedNode source = ((DBTermCloneNode)selectedNode).
                    sourceNode ;
                tree.expandNode(source) ;
                tree.setSelectionPath(tree.getPath(source)) ;
            }
            else if(selectedNode instanceof DbTermNode)
            { // 2005-08-23
                // if term not yet expand, expand it
                DbTermNode theTerm = (DbTermNode)selectedNode ;
                if(theTerm.hasMore)
                {
                    PackageNode hp = theTerm.getHomePackageNode() ;
                    Term2Tree treeLoader = hp.treeLoader ;
                    treeLoader.buildDAG(1, theTerm, hp) ;
                }
            }
        }
    }

}
