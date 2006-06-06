package edu.iastate.ato.gui ;

import javax.swing.JPanel ;

import edu.iastate.anthill.indus.tree.TypedTree ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public abstract class TypedTreePanel extends JPanel
{
    protected TypedTree treeOntology = new TypedTree() ;
    abstract public void onExpand() ;
    abstract public void onClose();

    public TypedTree getTreeOntology()
    {
        return treeOntology ;
    }
}
