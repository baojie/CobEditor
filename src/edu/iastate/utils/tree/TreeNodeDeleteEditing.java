package edu.iastate.utils.tree;

import edu.iastate.utils.undo.EditingAction;

/**
 * @since 2005-04-20
 */
public class TreeNodeDeleteEditing
    extends EditingAction
{
    TypedNode parent;
    TypedTree tree;
    public TreeNodeDeleteEditing(TypedTree tree, TypedNode location,
                                 TypedNode parent)
    {
        this.tree = tree;
        this.location = location;
        this.parent = parent;
        this.summary = "Delete node '" + location + "' from '" + parent+"'";
    }

    public void undo()
    {
        // insert location to parent
        if ( ( (TypedNode) location).getParent() != null)
        {
            tree.getModel().removeNodeFromParent( (TypedNode) location);
        }
        tree.getModel().insertNodeInto( (TypedNode) location, parent,
                                       parent.getChildCount());
        tree.expandNode( (TypedNode) location);
    }

    public void redo()
    {
        // remove location from parent
        if (parent.isNodeChild( (TypedNode) location))
        {
            tree.getModel().removeNodeFromParent( (TypedNode) location);
        }
    }
}
