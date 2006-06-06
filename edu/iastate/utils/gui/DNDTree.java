package edu.iastate.utils.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/** Creates a JTree with Drag and Drop facilities.
 *
 * Create and use an object of DNDTree instead of a JTree to include Drag and
 * Drop features for your tree.
 *
 * @version 1.01 03/01/2001  modified 2005-04-18
 * @author Prathap G, Jie Bao
 */
public class DNDTree
    extends JTree implements DropTargetListener, DragSourceListener,
    DragGestureListener
{
    /** The Drop position. */
    private DropTarget dropTarget = null;
    /** The Drag node.*/
    private DragSource dragSource = null;
    /** The dragged node.*/
    private DefaultMutableTreeNode selnode = null;
    /** The droppped node.*/
    private DefaultMutableTreeNode dropnode = null;
    /** The TreeModel for the tree.*/
    private DefaultTreeModel treemodel = null;

    DragDropListener dragDropListener; // 2005-04-19

    public void addDrageDropListener(DragDropListener dragDropListener)
    {
        this.dragDropListener = dragDropListener;
    }

    public boolean enableDragDrop = false;

    /**
     * @author Jie Bao
     * @since 2004-04-18
     * @param node TreeNode
     */
    public DNDTree(TreeNode node)
    {
        super(node);
        treemodel = (DefaultTreeModel)this.getModel();
        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this,
            DnDConstants.ACTION_MOVE, this);
    }

    public DNDTree()
    {
        super();
        treemodel = (DefaultTreeModel)this.getModel();
        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this,
            DnDConstants.ACTION_MOVE, this);
    }

    /** Returns a new instance of the DNDTree for the specified TreeModel.*/
    public DNDTree(TreeModel model)
    {
        super(model);
        treemodel = (DefaultTreeModel) model;
        dropTarget = new DropTarget(this, this);
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this,
            DnDConstants.ACTION_MOVE, this);
    }

    /** Internally implemented, Do not override!*/
    public void dragEnter(DropTargetDragEvent event)
    {
        event.acceptDrag(DnDConstants.ACTION_MOVE);
    }

    /** Internally implemented, Do not override!*/
    public void dragExit(DropTargetEvent event)
    {
    }

    /** Internally implemented, Do not override!*/
    public void dragOver(DropTargetDragEvent event)
    {
        if (!this.enableDragDrop)
        {
            return;
        }
        // Jie Bao 2005-04-18 select the drag-over node
        Point droppoint = event.getLocation();
        TreePath droppath = getClosestPathForLocation(droppoint.x,
            droppoint.y);
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) droppath.
            getLastPathComponent();
        this.setSelectionPath(this.path(n));
    }

    /** Internally implemented, Do not override!*/
    public void drop(DropTargetDropEvent event)
    {
        if (!this.enableDragDrop)
        {
            return;
        }
        try
        {
            Transferable transferable = event.getTransferable();

            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                event.acceptDrop(DnDConstants.ACTION_MOVE);
                String s = (String) transferable.getTransferData(DataFlavor.
                    stringFlavor);
                Object occur = event.getSource();
                Point droppoint = event.getLocation();
                TreePath droppath = getClosestPathForLocation(droppoint.x,
                    droppoint.y);
                dropnode = (DefaultMutableTreeNode) droppath.
                    getLastPathComponent();
                event.getDropTargetContext().dropComplete(true);
            }
            else
            {
                event.rejectDrop();
            }
        }
        catch (IOException exception)
        {
            event.rejectDrop();
        }
        catch (UnsupportedFlavorException ufException)
        {
            event.rejectDrop();
        }
    }

    /** Internally implemented, Do not override!*/
    public void dropActionChanged(DropTargetDragEvent event)
    {
    }

    /** Internally implemented, Do not override!*/
    public void dragGestureRecognized(DragGestureEvent event)
    {
        if (!this.enableDragDrop)
        {
            return;
        }

        selnode = null;
        dropnode = null;
        Object selected = getSelectionPath();
        if (selected != null)
        {
            TreePath treepath = (TreePath) selected;
            selnode = (DefaultMutableTreeNode) treepath.getLastPathComponent();

            if (dragDropListener != null)
            {
                if (!dragDropListener.canDrag(selnode))
                {
                    return;
                }
            }

            StringSelection text = new StringSelection(selected.toString());
            dragSource.startDrag(event, DragSource.DefaultMoveDrop, text, this);
        }
    }

    // 2005-04-18
    private TreePath path(TreeNode node)
    {
        List list = new ArrayList();

        // Add all nodes to list
        while (node != null)
        {
            list.add(node);
            node = node.getParent();
        }
        Collections.reverse(list);

        // Convert array of nodes to TreePath
        TreePath path = new TreePath(list.toArray());
        return path;
    }

    /** Internally implemented, Do not override!.
     * throws IllegalArgumentException.
     */
    public void dragDropEnd(DragSourceDropEvent event)
    {
        if (!this.enableDragDrop)
        {
            return;
        }
        if (event.getDropSuccess())
        {
            try
            {
                if (dropnode.equals(selnode) ||
                    selnode.isNodeDescendant(dropnode))
                {
                    System.out.println("drag>=drop");
                    //throw new IllegalArgumentException( "the source is the >= as the destination");
                }
                else
                {
                    //treemodel.removeNodeFromParent(selnode);
                    //this.treemodel.insertNodeInto(selnode, dropnode, 0);
                    if (dragDropListener != null)
                    {
                        dragDropListener.onDrop(selnode, dropnode);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(this, "Node " + selnode +
                            " is droped on " + dropnode);
                    }

                    //dropnode.add(selnode);
                    //this.expandPath(path(dropnode));
                    //this.setSelectionPath(path(dropnode));

                }
            }
            catch (IllegalArgumentException iae)
            {
                throw new IllegalArgumentException(iae.toString());
            }
            this.repaint();
        }
    }

    /** Internally implemented, Do not override!*/
    public void dragEnter(DragSourceDragEvent event)
    {
    }

    /** Internally implemented, Do not override!*/
    public void dragExit(DragSourceEvent event)
    {
    }

    /** Internally implemented, Do not override!*/
    public void dragOver(DragSourceDragEvent event)
    {
    }

    /** Internally implemented, Do not override!*/
    public void dropActionChanged(DragSourceDragEvent event)
    {
    }

    public void setDragDropListener(DragDropListener dragDropListener)
    {
        this.dragDropListener = dragDropListener;
    }

    public void setEnableDragDrop(boolean enableDragDrop)
    {
        this.enableDragDrop = enableDragDrop;
    }
}
