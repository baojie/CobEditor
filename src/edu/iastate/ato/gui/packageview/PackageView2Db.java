package edu.iastate.ato.gui.packageview ;

import java.sql.Connection ;
import java.util.Vector ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.po.OntologyEdit ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.DBTermCloneNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode ;
import edu.iastate.utils.Debug;
import edu.iastate.utils.tree.TypedNode;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07</p>
 */
public class PackageView2Db
{
    Connection db ;
    PackageTree tree ;

    public PackageView2Db(Connection db, PackageTree tree)
    {
        this.db = db ;
        this.tree = tree ;
    }

    private void saveNode(TypedNode node)
    {
        ATOTreeNode atoNode = (ATOTreeNode)node ;

        // do not save a read only node / unmodified node
        if(atoNode.isReadOnly() || !atoNode.isChanged())
        {
            return ;
        }

        //System.out.println("Save node " + atoNode);

        // save a modified/ merged node
        if(atoNode.status == ATOTreeNode.MODIFIED ||
            atoNode.status == ATOTreeNode.MERGED)
        {
            // save a package
            if(atoNode instanceof PackageNode)
            {
                writePackage((PackageNode)node) ;
            }
            else if(node instanceof DbTermNode)
            { // save a term
                writeTerm((DbTermNode)node) ;
            }
        }
        //System.out.println("    node status: " + atoNode.status2string());
    }

    /**
     * Save edges in a package
     * @param node
     */
    private void saveEdge(TypedNode node)
    {
        ATOTreeNode atoNode = (ATOTreeNode)node ;

        //System.out.println("Save edge for " + atoNode);
        //System.out.println(" status  " + atoNode.status2string());

// do not save a read only node / unmodified node
        if( (atoNode.isReadOnly() && ! (node instanceof DBTermCloneNode))
        		|| !atoNode.isChanged())
        {
        	//System.out.println("ready only or not changes: " + atoNode);
            return ;
        }

        // save a modified node
        if(atoNode.status == ATOTreeNode.MODIFIED)
        {
            // save a package
            if(atoNode instanceof PackageNode)
            {
                writePackageEdge((PackageNode)node) ;
            }
            else if(node instanceof DbTermNode || node instanceof DBTermCloneNode)
            { // save a term
                writeTermEdge((DbTermNode)node) ;
            }
            //tree.getModel().reload(atoNode);
        }
        else if(atoNode.status == ATOTreeNode.DELETED_NODE)
        {
            deleteNode((ATOTreeNode)node) ;
            //deleteParent(id);
            //tree.getModel().reload(atoNode);
        }
        else if(atoNode.status == ATOTreeNode.DELETED_UPEDGE)
        {
            deleteUpEdge(atoNode) ;
            //tree.getModel().reload(atoNode);
        }

    }

    /**
     * deleteNode (the node must be leaf in this version)
     *
     * @param node TypedNode
     */
    private void deleteNode(ATOTreeNode node)
    {
        ATOTreeNode atoNode = (ATOTreeNode)node ;

        // do not save a read only node / unmodified node
        if(atoNode.isReadOnly() || !atoNode.isChanged())
        {
            return ;
        }

        // save a modified and non-readonly node
        if(atoNode.status == ATOTreeNode.DELETED_NODE)
        {
            // save a package
            if(atoNode instanceof PackageNode)
            {
                // delete package (don't delete the terms in it)
                // (the terms should have individual deleted marks)
                String pkg_oid = ((PackageNode)node).getOid() ;
                boolean deleted = OntologyEdit.deletePackage(db, pkg_oid,
                    MOEditor.user.name) ;
                if(deleted)
                {
                    tree.delete(node) ;
                }
            }
            else if(node instanceof DBTermCloneNode)
            {
                tree.delete(node) ;
            }
            else if(node instanceof DbTermNode)
            { // delete a term
                // delete term
                DbTermNode theTerm = (DbTermNode)node ;

                PackageNode hp = theTerm.getHomePackageNode() ;
                // delete it if the home package is not deleted
                // why? if the homep package is deleted, all terms in the package
                //    will be automatically obsoleted
                if(hp.status != ATOTreeNode.DELETED_NODE)
                {
                    String term_oid = theTerm.getOid() ;
                    OntologyEdit.obsoleteTerm(db, term_oid, MOEditor.user.name) ;
                }

                // update the GUI
                theTerm.thisTerm.is_obsolete = "true" ;
                // move it to the obsolete area
                tree.moveNode(node, hp.obsoleteNodes) ;
                tree.getModel().reload(hp.obsoleteNodes) ;
            }
        }
    }

    private void deleteUpEdge(ATOTreeNode atoNode)
    {
        // get parent (must be another term)
        ATOTreeNode p = (ATOTreeNode)atoNode.getParent() ;
        if(p != null && p instanceof DbTermNode)
        {
            DbTermNode thisTerm = (DbTermNode)atoNode ;
            String oid = thisTerm.getOid() ;
            String parent_oid = ((DbTermNode)p).getOid() ;
            String relation = thisTerm.getViewMode() ;
            OntologyEdit.deleteTermRelation(db, oid, parent_oid, relation) ;
            // delete it on the interface
            tree.getModel().removeNodeFromParent(atoNode) ;
            // if it has no other clone, remove to the root of package
            if(thisTerm.cloned.size() == 0 &&
                !(thisTerm instanceof DBTermCloneNode))
            {
                tree.moveNode(thisTerm, thisTerm.getHomePackageNode()) ;
            }
            // if it has a clone, replace the first clone with the node it self
            else
            {
                for(DBTermCloneNode c : thisTerm.cloned)
                {
                    if(!c.isDeleted())
                    {
                        TypedNode ppp = (TypedNode)c.getParent() ;
                        tree.getModel().removeNodeFromParent(c) ;
                        tree.moveNode(thisTerm, ppp) ;
                        tree.getModel().reload(c.getParent()) ;
                        break ;
                    }
                }
            }
        }
    }

    private void writeTerm(DbTermNode node)
    {
        // save node information only if it is not a clone
        if(!(node instanceof DBTermCloneNode))
        {
            // save the term itself
            node.getThisTerm().print() ;
            node.save(db) ;

            // how about if the homepage is not saved when the term node created?
            //
        }
    }

    private void writeTermEdge(DbTermNode node)
    {
        // save the tree edge
        TypedNode parent = (TypedNode)node.getParent() ;
        if(parent != null && parent instanceof DbTermNode)
        {
            String parent_oid = ((DbTermNode)parent).getOid() ;
            OntologyEdit.addTermRelation(db, node.getOid(),
                node.getViewMode(),
                parent_oid, MOEditor.user.name) ;

            //Debug.trace("save edge : " + parent +"->" + node);
        }
    }

    private void writePackage(PackageNode node)
    {
        // save the package information
        node.save(db) ;
    }

    private void writePackageEdge(PackageNode node)
    {
        // save the tree edge, delete the old edge
        PackageNode parent = (PackageNode)node.getParent() ;
        if(parent != null)
        {
            String parent_oid = parent.getOid() ;
            OntologyEdit.addPackageNesting(db, node.getOid(), parent_oid,
                MOEditor.user.name) ;
        }
    }

     
    private void savePackageNode(PackageNode pkg)
    {
    	saveNode(pkg) ;
        Vector<DbTermNode> terms = tree.getTermsInPackage(pkg) ;
        for(DbTermNode term : terms)
        {
            saveNode(term) ;
        }
    }

    private void savePackageEdge(PackageNode pkg){
    	saveEdge(pkg) ;
        Vector<DbTermNode> terms = PackageTree.getTermsInPackage(pkg) ;
        for(DbTermNode term : terms)
        {
            saveEdge(term) ;
            if(!term.isMerged() && term.isChanged())
            {
                term.status = ATOTreeNode.UNMODIFIED ;
                tree.getModel().reload(term) ;
            }
        }
        pkg.status = ATOTreeNode.UNMODIFIED ;
    }
    
    /**
     * Saves an individual package
     * @author Peter Wong
     * @param pkg
     */
    public void savePackage(PackageNode pkg){
    	savePackageNode(pkg);
    	savePackageEdge(pkg);

        // save edge change
        Vector<DbTermNode> allMerged = new Vector<DbTermNode>() ;
    	Vector<DbTermNode> terms = PackageTree.getTermsInPackage(pkg) ;
        for(DbTermNode term : terms)
        {
            if(term.isMerged())
            {
                allMerged.add(term) ;
            }
        }
        
        for(int i=0; i<pkg.getChildCount();++i){
        	if(pkg.getChildAt(i) instanceof PackageNode){
        		PackageNode child = (PackageNode) pkg.getChildAt(i);
        		if(child.status == PackageNode.DELETED_NODE){
        			savePackage(child);
        		}
        	}
        }
        
        // do merging
        for(DbTermNode mergedNode : allMerged)
        {
            mergedNode.merge(db) ;
            mergedNode.status = ATOTreeNode.UNMODIFIED ;
            tree.getModel().reload(mergedNode.mergedWith) ;
        }
    }
    
    public void saveTree()
    {
        Vector<PackageNode> allPackages = tree.getAllPackage() ;
        //System.out.println(allPackages);

        // save node change
        for(PackageNode pkg : allPackages)
        {
        	savePackageNode(pkg);
        }
        // save edge change
        Vector<DbTermNode> allMerged = new Vector<DbTermNode>() ;
        for(PackageNode pkg : allPackages)
        {
        	savePackageEdge(pkg);
            
            Vector<DbTermNode> terms = PackageTree.getTermsInPackage(pkg) ;
            for(DbTermNode term : terms)
            {
                if(term.isMerged())
                {
                    allMerged.add(term) ;
                }
            }
            pkg.status = ATOTreeNode.UNMODIFIED ;
        }

        // do merging
        for(DbTermNode mergedNode : allMerged)
        {
            mergedNode.merge(db) ;
            mergedNode.status = ATOTreeNode.UNMODIFIED ;
            tree.getModel().reload(mergedNode.mergedWith) ;
        }
    }
}
