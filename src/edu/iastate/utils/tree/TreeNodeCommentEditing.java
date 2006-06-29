package edu.iastate.utils.tree;

import edu.iastate.utils.undo.EditingAction;

/**
 * @since 2005-04-20
 */
public class TreeNodeCommentEditing
    extends EditingAction
{
    Object oldValue, newValue;
    public TreeNodeCommentEditing(TypedNode location, Object oldValue,
                                  Object newValue)
    {
        this.location = location;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.summary = "Change node '" + location + "'s comment '" +
            oldValue + "' -> '" + newValue + "'";
    }

    public void undo()
    {
        ( (TypedNode) location).setComment(oldValue);
    }

    public void redo()
    {
        ( (TypedNode) location).setComment(newValue);
    }
    }
