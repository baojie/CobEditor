package edu.iastate.ato.gui.relationview ;

import java.sql.Connection ;

import java.awt.BorderLayout ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JScrollPane ;
import javax.swing.tree.TreePath ;

import edu.iastate.anthill.indus.tree.TypedNode ;
import edu.iastate.anthill.indus.tree.TypedTree ;
import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.TypedTreePanel ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.ATOTreeRender ;
import edu.iastate.ato.tree.DBTermCloneNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.MetaTreeNode ;
import edu.iastate.ato.tree.Term2Tree ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07-18</p>
 */
public class RelationView extends TypedTreePanel
{
    String relation ;
    Connection db ;
    MetaTreeNode isolated, obsolete ;

    public RelationView(Connection db, String relation)
    {
        try
        {
            this.db = db ;
            this.relation = relation ;
            jbInit() ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    private void jbInit() throws Exception
    {
        this.setLayout(borderLayout1) ;
        this.add(jScrollPane1, java.awt.BorderLayout.CENTER) ;
        jScrollPane1.getViewport().add(treeOntology) ;

        TypedNode root = new TypedNode("Ontology", ATOTreeNode.ROOT,
            relation + " hierarchy of the ontology") ;

        // make the meta node for isolated nodes
        isolated = new MetaTreeNode("Isolated Terms",
            "Terms have no parents or children is this relation") ;
        root.add(isolated) ;

        // make the meta node for obsolete nodes
        int n = OntologyQuerier.getObsoleteTermCount(db, null) ;
        obsolete = new MetaTreeNode("Obsolete(" + n + ")", null) ;
        root.add(obsolete) ;

        treeOntology.setTop(root) ;
        ATOTreeRender treeRender = new ATOTreeRender() ;
        treeOntology.setCellRenderer(treeRender) ;
        treeOntology.addMouseListener(new TreeClickListener(treeOntology)) ;

        treeLoader = new Term2Tree(db, relation, true) ;
        treeLoader.showPackageInformation = false ;
    }

    void makeTree()
    {
        Term2Tree tt = new Term2Tree(db, relation, true) ;
        tt.makeDagFromRoots(treeOntology.getTop(), 1, false, null) ;
    }

    JScrollPane jScrollPane1 = new JScrollPane() ;
    BorderLayout borderLayout1 = new BorderLayout() ;

    Term2Tree treeLoader ;

    /**
     * Expand one level on the tree
     *
     * @since 2005-07-27
     */
    public void onExpand()
    {
        // get the selected node
        TypedNode selectedNode = (TypedNode)treeOntology.getSelectedNode() ;

        // if the node is root , get all root node
        // if it is a term node , expand one level
        expand(selectedNode) ;
    }

    public void expand(TypedNode selectedNode)
    {
        if(selectedNode == null)
        {
            return ;
        }
        else if(selectedNode instanceof DbTermNode)
        {
            // if it is a cloned node, jump to the source
            if(selectedNode instanceof DBTermCloneNode)
            {
                // jump to the source node
                // cloned term node, jump to the source node
                TypedNode source = ((DBTermCloneNode)selectedNode).
                    sourceNode ;
                treeOntology.expandNode(source) ;
                treeOntology.setSelectionPath(treeOntology.getPath(source)) ;

                return ;
            }
            // normal term node, expand one level
            else if(selectedNode.getChildCount() == 0)
            {
                treeLoader.buildDAG(1, (DbTermNode)selectedNode, null) ;
            }
        }
        else if(selectedNode.getType() == ATOTreeNode.ROOT)
        {
            // if not yet loaded
            if(selectedNode.getChildCount() == 1)
            {
                // load roots
                treeLoader.makeDagFromRoots(selectedNode, 0, false, null) ;
            }
        }
        else if(selectedNode == this.isolated)
        {
            // load isolated nodes
            selectedNode.removeAllChildren() ;
            treeLoader.addIsolatedTerms(selectedNode) ;
        }
        else if(selectedNode == this.obsolete)
        {
            // load isolated nodes
            selectedNode.removeAllChildren() ;
            // load all obsoleteNodes
            treeLoader.addObsoleteTerms(obsolete, null) ;
        }
        treeOntology.getModel().reload(selectedNode) ;
    }

    public void onClose()
    {
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

            tree.setSelectionPath(selPath) ;
            final TypedNode selectedNode = (TypedNode)
                selPath.getLastPathComponent() ;

            if(e.getClickCount() == 1 && selectedNode instanceof DbTermNode)
            {
                MOEditor.theInstance.paneDetails.update((DbTermNode)
                    selectedNode) ;
            }
            // expand
            if(e.getClickCount() == 2)
            {
                expand(selectedNode) ;
            }
        }
    }

}
