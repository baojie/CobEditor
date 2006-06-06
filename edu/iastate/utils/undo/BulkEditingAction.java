package edu.iastate.utils.undo;

import java.util.Vector;

/**
 * @since 2005-04-20
 */
public class BulkEditingAction
    extends EditingAction
{
    Vector<EditingAction> actions = new Vector<EditingAction> ();

    public BulkEditingAction(Object location)
    {
        this.location = location;
    }

    public void addAction(EditingAction ea)
    {
        actions.add(ea);
    }

    public void undo()
    {
        for (int i = actions.size() - 1; i >= 0; i--)
        {
            EditingAction ea = actions.elementAt(i);
            ea.undo();
        }
    }

    public void redo()
    {
        for (int i = 0; i < actions.size(); i++)
        {
            EditingAction ea = actions.elementAt(i);
            ea.redo();
        }
    }
}
