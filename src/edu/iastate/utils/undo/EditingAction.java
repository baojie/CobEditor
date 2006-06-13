package edu.iastate.utils.undo;

/**
 * @author Jie Bao
 * @since 2005-04-20
 */
public abstract class EditingAction
{
    public String summary = "";
    public Object location = null;

    public String toString()
    {
        return summary;
    }

    abstract public void undo();
    abstract  public void redo();
}
