package edu.iastate.ato.gui.packageview ;

import java.sql.Connection ;

import javax.swing.JOptionPane ;
import javax.swing.tree.TreeNode ;

import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.DBTermCloneNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.DragDropListener ;

// 2005-07-19
public class PackageTreeDargDropListener extends DragDropListener
{
    PackageTree tree ;
    Connection db ;

    public PackageTreeDargDropListener(Connection db, PackageTree tree)
    {
        this.db = db ;
        this.tree = tree ;
    }

    public boolean canDrop(TreeNode selected, TreeNode dropTarget)
    {
        // if target is the parent of selected, , or target is a cloned node, do nothing
        if(dropTarget instanceof DBTermCloneNode)
        {
            return false ;
        }
        // if the source / target is  read only, do nothing
        ATOTreeNode source = (ATOTreeNode)selected ;
        ATOTreeNode target = (ATOTreeNode)dropTarget ;

        if(source.isReadOnly() || target.isReadOnly())
        {
            return false ;
        }

        // if target is merged or deleted, do nothing
        if(target.isDeleted() || target.isMerged())
        {
            return false ;
        }

        // if a clone of selected is the child of dropTarget, alert
        // 2005-08-14
        if(source instanceof DbTermNode)
        {
            DbTermNode theTerm = (DbTermNode)source ;
            for(DBTermCloneNode c : theTerm.cloned)
            {
                if(target.isNodeChild(c))
                {
                    Debug.trace(
                        "A clone of the selected node is already the child of the target.\n" +
                        "You can't drop the selected node here.") ;
                    return false ;
                }
            }
        }

        return true ;
    }

    public void onDrop(TreeNode selected, TreeNode dropTarget)
    {
        if(!canDrop(selected, dropTarget))
        {
            return ;
        }

        // if the source is term, target is another term in the same package,
        // create a clone, or move
        if(selected instanceof DbTermNode)
        {
            if(selected.getParent() == dropTarget)
            {
                return ;
            }
            if(dropTarget instanceof DbTermNode)
            {
                moveTermToTerm((DbTermNode)selected, (DbTermNode)dropTarget) ;
                tree.modified = true;
            }
            // if the source is term, traget is a package, move term to new package
            // not implemented so far.
            else if(dropTarget instanceof PackageNode)
            {
                //Debug.trace("Move to another package is not allowed - yet");
                moveBranch((DbTermNode)selected, (PackageNode)dropTarget) ;
                tree.modified = true;
            }
        }
        else if(selected instanceof PackageNode)
        {
            if(dropTarget instanceof PackageNode)
            {
                PackageNode sourcePkg = (PackageNode)selected ;
                PackageNode targetPkg = (PackageNode)dropTarget ;

                movePackageToPackage(sourcePkg, targetPkg) ;
                tree.modified = true;
            }
        }
    }

    private void movePackageToPackage(PackageNode sourcePkg,
        PackageNode targetPkg)
    {
        if(sourcePkg.getViewMode().equals(targetPkg.getViewMode()))
        {
            // ask if to move or to merge
            int answer = JOptionPane.showConfirmDialog(null,
                "Move here (NO to merge)? ") ;
            if(answer == JOptionPane.YES_OPTION)
            {
                tree.movePackage(sourcePkg, targetPkg) ;
            }
            else
            {
                // merge two packages
                for(int i = 0 ; i < sourcePkg.getChildCount() ; i++)
                {
                    ATOTreeNode n = (ATOTreeNode)sourcePkg.getChildAt(i) ;
                    if(n instanceof PackageNode)
                    {
                        tree.getModel().removeNodeFromParent(n) ;
                        tree.getModel().insertNodeInto(n, targetPkg, 0) ;
                        n.status = ATOTreeNode.MODIFIED ;
                    }
                    else if(n instanceof DbTermNode)
                    {
                        tree.moveBranch((DbTermNode)n, targetPkg) ;
                    }
                }
                // mark the old package as deleted
                sourcePkg.status = ATOTreeNode.DELETED_NODE ;
            }
        }
    }

    private void moveTermToTerm(DbTermNode selected, DbTermNode dropTarget)
    {
        // if the two are in the same package
        if(selected.getHomePackageNode() == dropTarget.getHomePackageNode())
        {
            String MOVE = "Move as sub term" ;
            String CLONE = "Add a relation to this term" ;
            String MERGE = "Merge with this term" ;

            // ask if move or clone or merge
            Object[] possibleValues =
                {
                MOVE, CLONE, MERGE} ;
            Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]) ;
            if(MOVE.equals(selectedValue))
            {
                //TreeNode oldParent = ( (TypedNode) selected).getParent();
                tree.moveTerm(selected, dropTarget) ;
            }
            else if(CLONE.equals(selectedValue))
            {
                DBTermCloneNode newNode = tree.addClone(selected, dropTarget) ;
                newNode.showPackageInformation = false ;
            }
            else if(MERGE.equals(selectedValue))
            {
                // merge the selected node with drop target
                // what to be done for a merging operation
                // on the GUI
                // 1. Mark the term as merged
                tree.mergeTerm(selected, dropTarget) ;

                // on saving (DB)
                // 1. term table
                // 2. relation table
                // 3. details table

            }
        }
        else
        { // in different package;
            int answer = JOptionPane.showConfirmDialog(null,
                "Move the branch to new package? (NO to clone the selected node only)? ") ;
            if(answer == JOptionPane.YES_OPTION)
            {
                moveBranch(selected, dropTarget) ;
            }
            else
            {
                // create an new edge to cloned node
                tree.addClone(selected, dropTarget) ;
            }
        }
    }

    // 2005-07-29
    void moveBranch(DbTermNode selected, ATOTreeNode target)
    {
        // branch move is possible only when the two packages have the same viewmode
        String oldPackage = selected.getHomePackageNode().getLocalName() ;
        boolean same = selected.getHomePackageNode().getViewMode().equals(
            target.getViewMode()) ;
        if(!same)
        {
            Debug.trace("The two package have different hierarchy view mode, cannot move the selected branch") ;
            return ;
        }

        // change all nodes in this branch to the new package
        PackageNode newPackage = tree.moveBranch((DbTermNode)selected, target) ;

        Debug.trace("You move the branch under " + selected.getLocalName() +
            " from package " + oldPackage + " to package " +
            newPackage.getLocalName()) ;
    }

    // don't drag: delete, readonly, merged
    public boolean canDrag(TreeNode selected)
    {
        ATOTreeNode source = (ATOTreeNode)selected ;
        boolean readOnly = source.isReadOnly() ;
        boolean isDeleted = source.isDeleted() ;
        boolean isMerged = source.isMerged() ;

        return!readOnly && !isDeleted && !isMerged ;
    }

}
