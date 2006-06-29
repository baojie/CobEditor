package edu.iastate.utils.tree;

import edu.iastate.utils.undo.EditingAction;

/**
 * @since 2005-04-20
 */
public class TreeNodeMoveEditing
    extends EditingAction
{
    TypedTree tree;

    TypedNode oldParent, newParent;

    public TreeNodeMoveEditing(TypedTree tree, TypedNode location,
                               TypedNode oldParent, TypedNode newParent)
    {
        this.tree = tree;
        this.location = location;
        this.oldParent = oldParent;
        this.newParent = newParent;
        this.summary = "Move node '" + location + "' from '" + oldParent
            + "' to '" + newParent + "'";
    }

    public void undo()
    {
        // remove location from newParent
        if (newParent.isNodeChild( (TypedNode) location))
        {
            tree.getModel().removeNodeFromParent( (TypedNode) location);
        }

        // and insert it to oldParent
        tree.getModel().insertNodeInto( (TypedNode) location, oldParent,
                                       oldParent.getChildCount());
        tree.expandNode( (TypedNode) location);
    }

    public void redo()
    {
        // remove location from oldParent
        if (oldParent.isNodeChild( (TypedNode) location))
        {
            tree.getModel().removeNodeFromParent( (TypedNode) location);
        }

        // insert location to newParent
        tree.getModel().insertNodeInto( (TypedNode) location, newParent,
                                       newParent.getChildCount());
        tree.expandNode( (TypedNode) location);
    }
}
