package edu.iastate.ato.gui.details ;

import java.sql.Connection ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.Vector ;

import java.awt.BorderLayout ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JScrollPane ;
import javax.swing.tree.TreeNode ;
import javax.swing.tree.TreePath ;

import edu.iastate.anthill.indus.tree.TypedNode ;
import edu.iastate.anthill.indus.tree.TypedTree ;
import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.TypedTreePanel ;
import edu.iastate.ato.po.OntologySchema ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.ATOTreeRender ;
import edu.iastate.ato.tree.DBTermCloneNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.MetaTreeNode ;
import edu.iastate.ato.tree.Term2Tree ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07-28</p>
 */
public class NavigationPanel extends TypedTreePanel
{
    Connection db ;
    boolean topdown ;

    public NavigationPanel(Connection db, boolean topdown)
    {
        this.db = db ;
        this.topdown = topdown ;
        jbInit() ;
    }

    public void jbInit()
    {
        ATOTreeRender treeRender = new ATOTreeRender() ;
        treeOntology.setTop(new TypedNode("")) ;
        treeOntology.addMouseListener(new TreeClickListener(treeOntology,
            topdown)) ;
        treeOntology.setCellRenderer(treeRender) ;

        JScrollPane jScrollPane2 = new JScrollPane() ;
        jScrollPane2.getViewport().add(treeOntology) ;
        setLayout(new BorderLayout()) ;
        add(jScrollPane2, BorderLayout.CENTER) ;
    }

    public void onExpand()
    {
    }

    Map<String, Term2Tree> relation2loader = new HashMap<String, Term2Tree>() ;

    void update(ATOTreeNode selectedNode1)
    {
        if(selectedNode1 == null)
        {
            // if null, close
            treeOntology.setTop(new TypedNode("")) ;
        }
        else if(selectedNode1 instanceof DbTermNode)
        { // term node
            DbTermNode thisTerm = (DbTermNode)selectedNode1 ;
            if(thisTerm.getOid() == null)
            {
                return ; // a unsaved new term, no information in the database
            }

            System.out.println("topdown: " + topdown) ;
            relation2loader.clear() ;

            TypedNode root = new TypedNode( thisTerm.getLocalName() + " ",
                ATOTreeNode.ROOT, (String)thisTerm.getComment()) ;
            treeOntology.setTop(root) ;

            Vector<TreeNode> nodeToExpand = new Vector<TreeNode>() ;

            // get all available relations
            Vector<String> v = OntologySchema.getPartialOrders(db) ;
            for(String relation : v)
            {
                System.out.println("build the " + relation + " tree") ;

                Term2Tree treeLoader = new Term2Tree(db, relation, topdown) ;
                treeLoader.showPackageInformation = true ;
                relation2loader.put(relation, treeLoader) ;

                TypedNode node = treeLoader.makeDagFromTerm
                    (thisTerm.getOid(), 1, null) ;
                MetaTreeNode meta = new MetaTreeNode(relation, null) ;
                root.add(meta) ;

                // move all node's children to "meta"
                while(node.getChildCount() > 0)
                {
                    TypedNode nn = (TypedNode)node.getChildAt(0) ;
                    meta.add(nn) ;
                }
                if(meta.getChildCount() > 0)
                {
                    nodeToExpand.add(meta.getChildAt(0)) ;
                }
                //treeOntology.getModel().reload(meta);
            }
            treeOntology.repaint() ;
            for(TreeNode nn : nodeToExpand)
            {
                treeOntology.expandNode(nn) ;
            }
            //treeOntology.getModel().reload();
            //treeOntology.expandNode(meta.getChildAt(0));
        }
    }

    public void onClose()
    {
    }

    class TreeClickListener extends MouseAdapter
    {
        TypedTree tree ;
        boolean topdown ;

        public TreeClickListener(TypedTree tree, boolean topdown)
        {
            this.tree = tree ;
            this.topdown = topdown ;
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
            if(!(selectedNode instanceof DbTermNode))
            {
                return ;
            }

            // find which relation it is
            String relation = null ;
            TypedNode p = (TypedNode)selectedNode.getParent() ;
            while(p != null)
            {
                if(p instanceof MetaTreeNode)
                { // the meta node saves the relation type
                    relation = p.getLocalName() ; // e.g. is_a
                    break ;
                }
                p = (TypedNode)p.getParent() ;
            }

            if(e.getClickCount() == 1)
            {
                if(selectedNode instanceof DBTermCloneNode)
                {
                    // jump to the source node
                    // cloned term node, jump to the source node
                    TypedNode source = ((DBTermCloneNode)selectedNode).
                        sourceNode ;
                    tree.expandNode(source) ;
                    tree.setSelectionPath(tree.getPath(source)) ;

                    return ;
                }
                // no child, expand one level
                if(selectedNode.getChildCount() == 0)
                {
                    Term2Tree tt = relation2loader.get(relation) ;
                    tt.buildDAG(1, (DbTermNode)selectedNode, null) ;
                }
            }
            else if(e.getClickCount() == 2)
            {
                // jump to package view tree
                if(selectedNode instanceof DbTermNode)
                {
                    MOEditor.theInstance.getPackageTree().AddTerm
                        (((DbTermNode)selectedNode).getOid()) ;
                }
            }
        }
    }
}
