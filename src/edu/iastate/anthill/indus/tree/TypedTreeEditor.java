package edu.iastate.anthill.indus.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;

import edu.iastate.utils.undo.BulkEditingAction;
import edu.iastate.utils.undo.EditingAction;
import edu.iastate.utils.undo.UndoRedoStack;

public abstract class TypedTreeEditor
    extends TreePopupMenuListener
{
    public TypedTreeEditor()
    {
        super();
    }

    /**
     * update the tree
     *
     * @param top MyNode
     */
    protected void updateTree()
    {
        tree.repaint();
    }

    abstract protected void changed(TypedNode theNode); // 2005-04-17

    /**
     * @author Jie Bao
     * @since 1.0 2005-03-31
     */
    protected class DefaultDeleteAllChildrenAction
        implements ActionListener
    {
        TypedNode theNode;

        public DefaultDeleteAllChildrenAction(TypedNode theNode)
        {
            this.theNode = theNode;
        }

        public void actionPerformed(ActionEvent e)
        {
            deleteAllChildren(theNode);
        }
    }

    protected void deleteAllChildren(TypedNode theNode)
    {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        int child = theNode.getChildCount();
        BulkEditingAction bulk = new BulkEditingAction(theNode);
        bulk.summary = "Delete all children of '" + theNode + "'";
        for (int i = 0; i < child; i++)
        {
            TypedNode kid = (TypedNode) theNode.getChildAt(0);
            model.removeNodeFromParent(kid);
            TreeNodeDeleteEditing action = new TreeNodeDeleteEditing(
                tree, kid, theNode);
            bulk.addAction(action);
        }
        history.addAction(bulk);
        changed(theNode);
    }

    /**
     * @author Jie Bao
     * @since 1.0 2005-03-31
     */
    protected class DefaultDeleteButKeepChildrenAction
        implements ActionListener
    {
        TypedNode theNode;

        public DefaultDeleteButKeepChildrenAction(TypedNode theNode)
        {
            this.theNode = theNode;
        }

        public void actionPerformed(ActionEvent e)
        {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            TypedNode parent = (TypedNode) theNode.getParent();

            int child = theNode.getChildCount();

            BulkEditingAction bulk = new BulkEditingAction(parent);
            bulk.summary = "Delete '" + theNode + "' and keep its children";
            for (int i = 0; i < child; i++)
            {
                TypedNode kid = (TypedNode) theNode.getChildAt(0);
                //model.removeNodeFromParent(kid);
                model.insertNodeInto(kid, parent, parent.getChildCount());
                TreeNodeMoveEditing action = new TreeNodeMoveEditing(
                    tree, kid, theNode, parent);
                bulk.addAction(action);
            }

            model.removeNodeFromParent(theNode);

            TreeNodeDeleteEditing action = new TreeNodeDeleteEditing(tree,
                theNode, parent);
            bulk.addAction(action);

            history.addAction(bulk);
            changed(theNode);
            changed(parent);

        }
    }

    protected class DefaultEditCommentsAction
        implements ActionListener
    {
        TypedNode theNode;

        public DefaultEditCommentsAction(TypedNode theNode)
        {
            this.theNode = theNode;
        }

        public void actionPerformed(ActionEvent e)
        {
            String oldComments = (String) theNode.getComment();
            String newComments = JOptionPane.showInputDialog(
                "Give comment for the node", oldComments);
            if (newComments == null)
            {
                return;
            }

            theNode.setComment(newComments);
            changed(theNode);
            TreeNodeCommentEditing action = new TreeNodeCommentEditing(
                theNode, oldComments, newComments);
            history.addAction(action);
        }
    }

    /**
     * @author Jie Bao
     * @since 1.0 2004-10-08
     */
    protected class DefaultDeleteAction
        implements ActionListener
    {
        TypedNode theNode;

        public DefaultDeleteAction(TypedNode theNode)
        {
            this.theNode = theNode;
        }

        public void actionPerformed(ActionEvent e)
        {
            delete(theNode);
        }

        /**
         * DefaultDeleteAction
         */
        public DefaultDeleteAction()
        {
        }

    }

    protected void delete(TypedNode theNode)
    {
        TypedNode parent = (TypedNode) theNode.getParent();
        if (parent != null)
        {
            tree.getModel().removeNodeFromParent(theNode);
            changed(theNode);
            changed(parent);
            TreeNodeDeleteEditing action = new TreeNodeDeleteEditing(
                tree, theNode, parent);
            history.addAction(action);

        }
    }

    protected abstract class DefaultInsertParentAction
        implements ActionListener
    {
        public TypedNode theNode;

        public DefaultInsertParentAction(TypedNode theNode)
        {
            this.theNode = theNode;
        }

        public void actionPerformed(ActionEvent e)
        {
            TypedNode parent = (TypedNode) theNode.getParent();
            if (parent != null)
            {

                TypedNode newNode = getNewNode();

                if (newNode == null)
                {
                    return;
                }

                DefaultTreeModel model = tree.getModel();
                model.insertNodeInto(newNode, parent, parent.getChildCount());

                BulkEditingAction bulk = new BulkEditingAction(theNode);
                bulk.summary = "Insert parent '" + newNode + "' on '" + theNode +
                    "'";

                EditingAction action = new TreeNodeInsertEditing(
                    tree, newNode, parent);
                bulk.addAction(action);

                model.removeNodeFromParent(theNode);
                action = new TreeNodeDeleteEditing(tree, theNode, parent);
                bulk.addAction(action);

                model.insertNodeInto(theNode, newNode, 0);
                action = new TreeNodeInsertEditing(tree, theNode, newNode);
                bulk.addAction(action);

                history.addAction(bulk);

                changed(theNode);
            }
        }

        protected abstract TypedNode getNewNode();

    }

    protected abstract class DefaultRenameAction
        implements ActionListener
    {
        public TypedNode theNode;

        public DefaultRenameAction(TypedNode theNode)
        {
            this.theNode = theNode;
        }

        public void actionPerformed(ActionEvent e)
        {
            Object oldName = theNode.getUserObject();
            Object newName = getNewUserObject();

            if (newName != null)
            {
                theNode.setUserObject(newName);
                changed(theNode);
                TreeNodeRenameEditing action = new TreeNodeRenameEditing(
                    theNode, oldName, newName);
                history.addAction(action);
            }
        }

        protected abstract Object getNewUserObject();
    }



    // 2005-04-20
    public class DefaultUndoAction
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            undo();
        }
    }

    public void undo()
    {
        if (history.canUndo())
        {
            EditingAction action = history.undo();
            changed( (TypedNode) action.location);
        }
    }

    // 2005-04-20
    public class DefaultRedoAction
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            redo();
        }
    }

    public void redo()
    {
        if (history.canRedo())
        {
            EditingAction action = history.redo();
            changed( (TypedNode) action.location);
        }
    }

    protected abstract class DeafultCreateSubValueAction
        implements ActionListener
    {
        public TypedNode parent;

        public DeafultCreateSubValueAction(TypedNode parent)
        {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent e)
        {
            TypedNode newNode = getNewNode();
            if (newNode != null)
            {
                tree.getModel().insertNodeInto(newNode,
                                               parent, parent.getChildCount());
                tree.expandPath(newNode);
                tree.setSelectionPath(tree.getPath(newNode));
                changed(newNode);

                EditingAction action = new TreeNodeInsertEditing(
                    tree, newNode, parent);
                history.addAction(action);
            }
        }

        protected abstract TypedNode getNewNode();
    }

    // 2005-04-20
    public UndoRedoStack history = new UndoRedoStack();

    public void addHistory(EditingAction action)
    {
        history.addAction(action);
    }
}
