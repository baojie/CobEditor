package edu.iastate.ato.gui.packageview ;

import java.sql.Connection ;
import java.util.Enumeration ;
import java.util.HashSet;
import java.util.Vector;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.po.Package ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.DBTermCloneNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode ;
import edu.iastate.utils.tree.TypedNode;
import edu.iastate.utils.tree.TypedTree;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class PackageTree extends TypedTree
{
    Connection db ;
    boolean modified = false ;

    public PackageTree(Connection db)
    {
        this.db = db ;
        
    }
    
    
    
    /**
     * AddTerm
     *
     * @param string String
     */
    public void AddTerm(String oid)
    {
        // find the home package oid
        String pkg_oid = OntologyQuerier.getHomePackage(db, oid) ;
        if(pkg_oid == null)
        {
            pkg_oid = OntologyQuerier.getPackageOid(db, Package.GlobalPkg) ;
        }

        // find the homepage node
        PackageNode home = null ;
        Vector<PackageNode> allPkg = this.getAllPackage() ;
        for(PackageNode pkg : allPkg)
        {
            if(pkg.getOid().equals(pkg_oid))
            {
                home = pkg ;
                break ;
            }
        }
        if(home == null)
        {
            return ;
        }

        // if the homepage is expanded, find the node
        // if not expaneded, do nothing
        if(home.expanded)
        {
            // find by oid in the package
            Vector<DbTermNode> terms = this.getTermsInPackage(home) ;
            for(DbTermNode t : terms)
            {
                if(t.getOid().equals(oid))
                {
                    expandNode(t) ;
                    setSelectionPath(getPath(t)) ;
                    break ;
                }
            }
        }
    }

    /**
     * Find a child with given value, of a given node
     * @param node ATOTreeNode
     * @param value String
     * @return ATOTreeNode
     */
    ATOTreeNode findChildByValue(ATOTreeNode node, String value)
    {
        Enumeration e = node.children() ;
        while(e.hasMoreElements())
        {
            ATOTreeNode n = (ATOTreeNode)e.nextElement() ;
            if(n.getLocalName().equals(value))
            {
                // find it.
                // if it is a cloned node, return the source
                if(n instanceof DBTermCloneNode)
                {
                    return((DBTermCloneNode)n).sourceNode ;
                }
                else
                {
                    return n ;
                }
            }
        }
        return null ;
    }
    
    

   

    /**
     * Cancel editing for a set of packages
     * @param packages Vector - set of package oid
     * @since 2005-07-25
     */
    public void cancelEditing(Vector<String> package_oid)
    {
        // search packages
        Vector<PackageNode> e = this.getAllPackage() ;
        for(PackageNode n : e)
        {
            if(package_oid.contains(n.getOid()) || n.getOid() == null)
            {
                n.setReadOnly(true) ;
                this.getModel().reload(n) ;
                MOEditor.theInstance.paneDetails.switchPropertyEditor(n) ;
            }
        }
    }

    public PackageNode moveBranch(DbTermNode selected, ATOTreeNode dropTarget)
    {
        PackageNode newPackage = null ;
        if(dropTarget instanceof PackageNode)
        {
            newPackage = (PackageNode)dropTarget ;
        }
        else if(dropTarget instanceof DbTermNode)
        {
            newPackage = ((DbTermNode)dropTarget).getHomePackageNode() ;
        }
        newPackage.status = PackageNode.MODIFIED;

        Enumeration<DbTermNode> en = selected.breadthFirstEnumeration() ;

        while(en.hasMoreElements())
        {
            DbTermNode term = en.nextElement() ;
            term.status = ATOTreeNode.MODIFIED ;
            term.setHomePackageNode(newPackage) ;
        }

        // keep a clone at the original place. (for saving purpose)
        TypedNode oldParent = (TypedNode)selected.getParent() ;
        //selected.getHomePackageNode().status = PackageNode.MODIFIED;
        
        // move
        this.moveNode(selected, dropTarget);
        
        //dropTarget.add(selected) ;
        selected.status = ATOTreeNode.MODIFIED ;

        if(oldParent instanceof DbTermNode)
        {
            DBTermCloneNode clone = addClone(selected, (DbTermNode)oldParent) ;
            clone.status = ATOTreeNode.DELETED_UPEDGE ;
        }

        getModel().reload(selected);
        getModel().reload(dropTarget);

        return newPackage ;
    }

    public void movePackage(PackageNode selected, PackageNode dropTarget)
    {
        getModel().removeNodeFromParent(selected) ;
        getModel().insertNodeInto(selected, dropTarget, 0) ;
        selected.status = ATOTreeNode.MODIFIED ;
        getModel().reload(selected) ;
    }

    public void moveTerm(DbTermNode selected, DbTermNode dropTarget)
    {
        // keep a clone at the original place. (for saving purpose)
        TypedNode oldParent = (TypedNode)selected.getParent() ;

        getModel().removeNodeFromParent(selected) ;
        getModel().insertNodeInto(selected, dropTarget, 0) ;
        selected.status = ATOTreeNode.MODIFIED ;

        if(oldParent instanceof DbTermNode)
        {
            DBTermCloneNode clone = addClone(selected, (DbTermNode)oldParent) ;
            clone.status = ATOTreeNode.DELETED_UPEDGE ;
        }

        getModel().reload(selected) ;
        getModel().reload(oldParent) ;
    }

    public DBTermCloneNode addClone(DbTermNode selected, DbTermNode dropTarget)
    {
        DBTermCloneNode newNode = new DBTermCloneNode(selected) ;
        newNode.showPackageInformation = true ;
        newNode.setHomePackageNode(dropTarget.getHomePackageNode()) ;
        
        getModel().insertNodeInto(newNode, dropTarget, 0);
        newNode.status = ATOTreeNode.MODIFIED ;
        getModel().reload(newNode) ;
        return newNode ;
    }

    public Vector<PackageNode> getAllPackage()
    {
        Vector<PackageNode> allnode = new Vector<PackageNode>() ;
        allnode.add((PackageNode)this.getTop()) ;
        getPackages((PackageNode)this.getTop(), allnode) ;
        return allnode ;
    }

    private void getPackages(PackageNode node, Vector<PackageNode> allnode)
    {
        for(int i = 0 ; i < node.getChildCount() ; i++)
        {
            if(node.getChildAt(i) instanceof PackageNode)
            {
                allnode.add((PackageNode)node.getChildAt(i)) ;
                getPackages((PackageNode)node.getChildAt(i), allnode) ;
            }
        }
    }

    public static Vector<DbTermNode> getTermsInPackage(PackageNode pkg)
    {
        Vector<DbTermNode> allnode = new Vector<DbTermNode>() ;
        for(int i = 0 ; i < pkg.getChildCount() ; i++)
        {
            if(pkg.getChildAt(i) instanceof DbTermNode)
            {
                allnode.add((DbTermNode)pkg.getChildAt(i)) ;
                getTerms((DbTermNode)pkg.getChildAt(i), allnode) ;
            }
        }
        return allnode ;
    }

    private static void getTerms(DbTermNode node, Vector<DbTermNode> allnode)
    {
        for(int i = 0 ; i < node.getChildCount() ; i++)
        {
            if(node.getChildAt(i) instanceof DbTermNode 
            	/*&& !(node.getChildAt(i) instanceof DBTermCloneNode)*/)
            {
                allnode.add((DbTermNode)node.getChildAt(i)) ;
                getTerms((DbTermNode)node.getChildAt(i), allnode) ;
            }
        }
    }

    /**
     * mergeTerm
     *
     * @param selected DbTermNode
     * @param dropTarget DbTermNode
     * @author Jie Bao
     * @since 2005-08-13
     */
    public void mergeTerm(DbTermNode selected, DbTermNode dropTarget)
    {
        selected.status = ATOTreeNode.MERGED ;
        try
        {
            selected.setMergedWith(dropTarget) ;
            this.getModel().reload(selected) ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
        }
    }

    // 2005-08-31
    public void save()
    {
        if(modified)
        {
            PackageView2Db tree2db = new PackageView2Db(db, this) ;
            tree2db.saveTree() ;
            this.modified = false ;
        }
    }
    
    public void savePackage(PackageNode pkg){
    	if(pkg.status == PackageNode.MODIFIED)
        {
            PackageView2Db tree2db = new PackageView2Db(db, this) ;
            tree2db.savePackage(pkg) ;
            pkg.status = PackageNode.UNMODIFIED;
        }    	
    }
}
