package edu.iastate.utils.tree;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import edu.iastate.utils.gui.JTreeEx;

/**
 * Generic tree data structure with typed node
 *
 * @author Jie Bao
 * @since 2004-05-01
 */

public class TypedTree
    extends JTreeEx
{
    public TypedTree(DefaultTreeModel model)
    {
        super(model);
    }

    public TypedTree()
    {
        super();
    }

    public TypedTree(TypedNode top)
    {
        super(top);
        this.setTop(top);
    }

    public String toString()
    {
        return toString(false);
    }

    public String toString(boolean toHTML)
    {
        //String blank = toHTML ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "    ";
        String endline = "|---";
        String line = toHTML ? "|&nbsp;&nbsp;&nbsp;" : "|   ";
        String crlf = toHTML ? "<BR></BR>" : "\n";

        if (getTop() == null)
        {
            return "";
        }
        String toPrint = "";

        Enumeration e = getTop().preorderEnumeration();
        while (e.hasMoreElements())
        {
            DefaultMutableTreeNode nn = (DefaultMutableTreeNode) e.nextElement();
            String leading = line;

            if (nn.getLevel() == 0)
            {
                leading = "";
            }
            else if (nn.getLevel() == 1)
            {
                leading = endline;
            }
            else
            {
                leading = endline;
                for (int i = 0; i < nn.getLevel() - 1; i++)
                {
                    leading = line + leading;
                }
            }
            toPrint += leading + nn + crlf;
        }

        if (toHTML)
        {
            return toPrint;
        }
        else
        {
            return toPrint;
        }
    }

    public void setTop(TypedNode top)
    {
        getModel().setRoot(top);
    }

    public TypedNode getTop()
    {
        return (TypedNode) getModel().getRoot();
    }

    public void buildSampleTree()
    {
        TypedNode top = new TypedNode("http://semanticWWW.com/indus.owl#USA");

        TypedNode iowa = new TypedNode(
            "http://semanticWWW.com/indus.owl#Iowa");
        top.add(iowa);
        iowa.add(new TypedNode(
            "http://semanticWWW.com/indus.owl#Ames"));
        iowa.add(new TypedNode(
            "http://semanticWWW.com/indus.owl#DesMoines"));

        TypedNode va = new TypedNode(
            "http://semanticWWW.com/indus.owl#Virginia");
        top.add(va);
        va.add(new TypedNode(
            "http://semanticWWW.com/indus.owl#Richmond"));
        va.add(new TypedNode(
            "http://semanticWWW.com/indus.owl#Petersberg"));

        setTop(top);

    }

    /**
     * replace the value of old node to that of the new node. if new node has
     *   children, they will be children of the old node.
     * @param tree TypedTree
     * @param node TypedNode
     * @param newNode TypedNode
     * @return TypedNode - the old node
     * @since 2005-03-31
     * @author Jie Bao
     */
    public static TypedNode amendNode(TypedTree tree, TypedNode node,
                                      TypedNode newNode)
    {
        try
        {
            DefaultTreeModel model = tree.getModel();

            // get the existing children
            Vector oldSons = new Vector();
            int oldSonCount = node.getChildCount();
            //System.out.println("node has children " + oldSonCount);

            for (int j = 0; j < oldSonCount; j++)
            {
                TypedNode son = (TypedNode) node.getChildAt(j);
                oldSons.add(son.getUserObject());
            }
            //System.out.println("oldSons" + oldSons);

            // add all children of new node to oldNode if it's new
            Vector newSons = new Vector();
            for (Enumeration e = newNode.children(); e.hasMoreElements(); )
            {
                newSons.add(e.nextElement());
            }
            for (Enumeration e = newSons.elements(); e.hasMoreElements(); )
            {
                TypedNode kid = (TypedNode) e.nextElement();
                System.out.println(kid);
                if (!oldSons.contains(kid.getUserObject()))
                {
                    model.insertNodeInto(kid, node, node.getChildCount());
                }
                //node.add(kid);
            }
            //Debug.trace(child + " children moved");


            // copy value of new node to old node
            //if (node.getParent() != null)
            //{
            //TypedNode parent = (TypedNode) node.getParent();
            //int index = parent.getIndex(node);
            //model.removeNodeFromParent(node);
            //Debug.trace("remove");
            //model.insertNodeInto(newNode, parent, index);
            //Debug.trace("insert");

            node.setUserObject(newNode.getUserObject());
            node.setComment(newNode.getComment());
            //}
            return node;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

// for test purpose
    public static void main(String[] args)
    {
        TypedTree t = new TypedTree();
        t.buildSampleTree();

        JFrame frame = new JFrame();
        frame.setSize(800, 600);

        JEditorPane pane = new JEditorPane();
        frame.getContentPane().add(new JScrollPane(pane));

        pane.setContentType("text/html");
        pane.setText(t.toString(true));

        frame.setVisible(true);

        // System.out.print(t);
    }
}
