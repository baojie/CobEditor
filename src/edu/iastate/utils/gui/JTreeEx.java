package edu.iastate.utils.gui ;

import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.Collections ;
import java.util.Enumeration ;
import java.util.HashSet ;
import java.util.List ;
import java.util.Set ;

import javax.swing.JTree ;
import javax.swing.tree.DefaultMutableTreeNode ;
import javax.swing.tree.DefaultTreeModel ;
import javax.swing.tree.MutableTreeNode ;
import javax.swing.tree.TreeModel ;
import javax.swing.tree.TreeNode ;
import javax.swing.tree.TreePath ;

/**
 * @author Jie Bao
 * @since 2005-04-07
 */
public class JTreeEx extends DNDTree
{

    /**
     * @param tree JTree
     * @param parent TreePath
     * @param nodes Object[]
     * @param depth int
     * @param mode int
     *    0 - by node
     *    1- by name
     *    2- by user object
     * @return TreePath
     */
    protected static TreePath find2(JTree tree, TreePath parent, Object[] nodes,
        int depth, int mode)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)parent.
            getLastPathComponent() ;
        Object onTree, toCompare ;
        onTree = node ;
        toCompare = nodes[depth] ;

        if(mode == 0) // by node
        {
            onTree = node ;
            toCompare = nodes[depth] ;
        }
        else if(mode == 1) // by name
        {
            onTree = node.toString() ;
            toCompare = nodes[depth].toString() ;
        }
        else if(mode == 2) // by user object
        {
            onTree = node.getUserObject() ;
            if(nodes[depth] instanceof DefaultMutableTreeNode)
            {
                toCompare = ((DefaultMutableTreeNode)nodes[depth]).
                    getUserObject() ;
            }
            else
            {
                toCompare = nodes[depth] ;
            }
        }

        // If equal, go down the branch
        //System.out.println("on tree: " + o);
        //System.out.println("to query: " + nodes[depth]);

        if(onTree.equals(toCompare))
        {
            // If at end, return match
            if(depth == nodes.length - 1)
            {
                return parent ;
            }

            // Traverse children
            if(node.getChildCount() >= 0)
            {
                for(Enumeration e = node.children() ; e.hasMoreElements() ; )
                {
                    TreeNode n = (TreeNode)e.nextElement() ;
                    TreePath path = parent.pathByAddingChild(n) ;
                    TreePath result = find2(tree, path, nodes, depth + 1,
                        mode) ;
                    // Found a match
                    if(result != null)
                    {
                        return result ;
                    }
                }
            }
        }

        // No match at this branch
        return null ;
    }

    // Finds the path in tree as specified by the array of names. The names array is a
    // sequence of names where names[0] is the root and names[i] is a child of names[i-1].
    // Comparison is done using String.equals(). Returns null if not found.
    public static TreePath findByName(JTree tree, String[] names)
    {
        TreeNode root = (TreeNode)tree.getModel().getRoot() ;
        return find2(tree, new TreePath(root), names, 0, 1) ;
    }

    /**
     * Get the path for a node
     * @param node TreeNode
     * @return TreePath
     * @author Jie Bao
     * @since 2004-10-08
     */
    static public TreePath getPath(TreeNode node)
    {
        List list = new ArrayList() ;

        // Add all nodes to list
        while(node != null)
        {
            list.add(node) ;
            node = node.getParent() ;
        }
        Collections.reverse(list) ;

        // Convert array of nodes to TreePath
        return new TreePath(list.toArray()) ;
    }

    public TreeNode getSelectedNode()
    {
        TreePath path = this.getSelectionPath() ;
        if(path != null)
        {
            TreeNode n = (TreeNode)path.getLastPathComponent() ;
            return n ;
        }
        else
        {
            return null ;
        }
    }

    /**
     * JTreeEx
     *
     * @param node TreeNode
     */
    public JTreeEx(TreeNode node)
    {
        super(node) ;
    }

    /**
     * JTreeEx
     */
    public JTreeEx()
    {
        super() ;
    }

    public JTreeEx(DefaultTreeModel model)
    {
        super(model) ;
    }

    /**
     * Find the first node given a value
     * @param tree JTree
     * @param value Object
     * @return DefaultMutableTreeNode
     * @since 2005-03-27
     * @author Jie Bao
     */
    public static DefaultMutableTreeNode findFirst(JTree tree, Object value)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)
            tree.getModel().getRoot() ;
        Enumeration e = root.breadthFirstEnumeration() ;
        while(e.hasMoreElements())
        {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement() ;
            //Debug.trace(null, n+"\n"+name);
            if(n.getUserObject().equals(value))
            {
                //Debug.trace(null, n);
                return n ;
            }
        }
        return null ;
    }

    // Finds the path in tree as specified by the node array. The node array is a sequence
    // of nodes where nodes[0] is the root and nodes[i] is a child of nodes[i-1].
    // Comparison is done using Object.equals(). Returns null if not found
    public static TreePath find(JTree tree, Object[] nodes)
    {
        TreeNode root = (TreeNode)tree.getModel().getRoot() ;
        return find2(tree, new TreePath(root), nodes, 0, 0) ;
    }

    public static TreePath findByUserObject(JTree tree, Object[] names)
    {
        TreeNode root = (TreeNode)tree.getModel().getRoot() ;
        return find2(tree, new TreePath(root), names, 0, 2) ;
    }

    /**
     * Return the set of all ancestor nodes of given node on the tree
     *   result doesn't include the given node itself
     * @param node TreeNode
     * @return Set
     * @author Jie Bao
     * @since 2005-03-19
     */
    public static Set findAncestor(TreeNode node)
    {
        Set ss = new HashSet() ;

        TreePath path = getPath(node) ;
        Object[] ancestor = path.getPath() ;
        ss.addAll(Arrays.asList(ancestor)) ;
        ss.remove(node) ;
        return ss ;
    }

    /**
     * Return the set of all offspring nodes of the given node on the tree
     *   result doesn't include the given node itself
     * @param node TreeNode
     * @return Set
     * @author Jie Bao
     * @since 2005-03-19
     */
    public static Set findAllOffspring(TreeNode node)
    {
        Set ss = new HashSet() ;
        // Traverse children
        if(node.getChildCount() > 0)
        {
            for(Enumeration e = node.children() ; e.hasMoreElements() ; )
            {
                TreeNode n = (TreeNode)e.nextElement() ;
                ss.add(n) ;
                ss.addAll(findAllOffspring(n)) ;
            }
            ss.remove(node) ;
        }
        return ss ;
    }

    /**
     * Select the first node with given value
     * @param value Object
     * @return DefaultMutableTreeNode - the selected node
     */
    public DefaultMutableTreeNode selectFirst(Object value)
    {
        if(value != null)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                findFirst(this, value) ;
            if(node != null)
            {
                TreePath path = getPath(node) ;
                setSelectionPath(path) ;
                return node ;
            }
        }
        return null ;
    }

    /**
     *
     * @param node TreeNode
     * @author Jie Bao
     * @since 2004-10-08
     */
    public void expandPath(TreeNode node)
    {
        TreePath path = getPath(node) ;
        expandPath(path) ;
        this.scrollPathToVisible(path) ; // add 2005-04-24
    }

    // @since 2004-10-13
    // make sure the node will be shown
    public void expandNode(TreeNode node)
    {
        if(node == null)
        {
            return ;
        }
        /*// if it's not leaf
           if (node.getChildCount() > 0)
           {
               expandPath(node);
           }
           else // leaf
           {
               // expand its parent, unless its the root itself
               if (node.getParent() != null)
               {
          expandPath(node.getParent());
               }
           }*/
       expandPath(node) ;
    }

    // @since 2004-10-13
    public void CollapsePath(TreeNode node)
    {
        collapsePath(getPath(node)) ;
    }

    public static DefaultMutableTreeNode findFirst(JTree tree, String name)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)
            tree.getModel().getRoot() ;
        Enumeration e = root.breadthFirstEnumeration() ;
        while(e.hasMoreElements())
        {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement() ;
            //Debug.trace(null, n+"\n"+name);
            if(n.getUserObject().toString().compareTo(name) == 0)
            {
                //Debug.trace(null, n);
                return n ;
            }
        }
        return null ;
    }

    // @since 2004-10-13
    public void expandNode(String node)
    {
        expandNode(findFirst(this, node)) ;
    }

    // @since 2004-10-13
    public void expandPath(String node)
    {
        expandPath(getPath(findFirst(this, node))) ;
    }

    /**
     * @since 2005-04-19
     * @return DefaultTreeModel
     */
    public DefaultTreeModel getModel()
    {
        return(DefaultTreeModel)super.getModel() ;
    }

    /**
     * Move n1 as child of n2
     * @param n1 MutableTreeNode
     * @param n2 MutableTreeNode
     * @since 2005-08-16
     * @author Jie Bao
     */
    public void moveNode(MutableTreeNode n1, MutableTreeNode n2)
    {
        if(n1.getParent() != null)
        {
            getModel().removeNodeFromParent(n1) ;
        }
        getModel().insertNodeInto(n1, n2, 0) ;
        System.out.println("move " + n1 + " ->" + n2) ;
    }

    // 2005-08-17
    public void delete(MutableTreeNode n)
    {
        getModel().removeNodeFromParent(n) ;
    }
}
