package edu.iastate.utils.gui;

import javax.swing.tree.TreeNode;

/**
 * @since 2005-04-19
 */
public abstract class DragDropListener
{
    abstract public void onDrop(TreeNode selected, TreeNode dropTarget);
    abstract public boolean canDrag(TreeNode selected);

}
