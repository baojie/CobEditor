package edu.iastate.utils.undo;

import java.util.Stack;

/**
 * @author Jie Bao
 * @since 2005-04-20
 */
public class UndoRedoStack
{

    Stack<EditingAction> undoStack = new Stack<EditingAction> ();
    Stack<EditingAction> redoStack = new Stack<EditingAction> ();

    int limit = 1000;

    public String toString()
    {
        String str = "Undo Stack: " + undoStack + "\n" +
            "Redo Stack: " + redoStack;
        return str;
    }

    // 2005-04-22
    public int getUndoSteps()
    {
        return undoStack.size();
    }

    // 2005-04-22
    public int getRedoSteps()
    {
        return redoStack.size();
    }

    // 2005-04-22
    public String topUndo()
    {
        if (undoStack.empty())
        {
            return null;
        }
        EditingAction action = undoStack.peek();
        return action == null ? null : action.summary;
    }

    // 2005-04-22
    public String topRedo()
    {
        if (redoStack.empty())
        {
            return null;
        }
        EditingAction action = redoStack.peek();
        return action == null ? null : action.summary;
    }

    public void addAction(EditingAction action)
    {
        if (undoStack.size() >= limit)
        {
            undoStack.removeElementAt(0);
        }

        undoStack.add(action);
        redoStack.clear();
        //System.out.println(this);
    }

    public EditingAction undo()
    {
        EditingAction action = undoStack.pop();
        redoStack.push(action);
        action.undo();
        return action;
    }

    public EditingAction redo()
    {
        EditingAction action = redoStack.pop();
        undoStack.push(action);
        action.redo();
        return action;
    }

    public boolean canUndo()
    {
        return!undoStack.empty();
    }

    public boolean canRedo()
    {
        return!redoStack.empty();
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    public int getLimit()
    {
        return limit;
    }

    /**
     * clear
     * @since 2005-04-24
     */
    public void clear()
    {
        this.undoStack.clear();
        this.redoStack.clear();
    }
}
