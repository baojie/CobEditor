package edu.iastate.anthill.indus.tree;

import edu.iastate.utils.undo.EditingAction;

/**
 * @since 2005-04-20
 */
public class TreeNodeRenameEditing
    extends EditingAction
{
    Object oldValue, newValue;
    public TreeNodeRenameEditing(TypedNode location, Object oldValue,
                                 Object newValue)
    {
        this.location = location;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.summary = "Change node '" + oldValue + "' -> '" + newValue +
            "'";
    }

    public void undo()
    {
        ( (TypedNode) location).setUserObject(oldValue);
    }

    public void redo()
    {
        ( (TypedNode) location).setUserObject(newValue);
    }
}
