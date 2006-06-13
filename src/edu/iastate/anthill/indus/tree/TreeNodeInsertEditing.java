package edu.iastate.anthill.indus.tree;

import edu.iastate.utils.undo.EditingAction;

/**
 * @since 2005-04-20
 */
public class TreeNodeInsertEditing
    extends EditingAction
{
    TypedNode parent;
    TypedTree tree;
    public TreeNodeInsertEditing(TypedTree tree, TypedNode location,
                                 TypedNode parent)
    {
        this.tree = tree;
        this.location = location;
        this.parent = parent;
        this.summary = "Insert node '" + location + "' under '" + parent+"'";
    }

    public void redo()
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

    public void undo()
    {
        // remove location from parent
        if (parent.isNodeChild( (TypedNode) location))
        {
            tree.getModel().removeNodeFromParent( (TypedNode) location);

        }
    }
}
