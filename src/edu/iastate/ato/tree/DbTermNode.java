package edu.iastate.ato.tree ;

import java.sql.Connection ;
import java.util.HashSet ;

import javax.swing.Icon ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.po.DbTerm ;
import edu.iastate.ato.po.OntologyEdit ;
import edu.iastate.ato.shared.IconLib ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07</p>
 */
public class DbTermNode extends ATOTreeNode
{
    public DbTerm thisTerm ;

    private PackageNode homePackageNode ;
    public DbTermNode mergedWith ;

    public boolean showPackageInformation = false ;
    public boolean hasMore = false ;

    public HashSet<DBTermCloneNode> cloned = new HashSet<DBTermCloneNode>() ;

    public DbTermNode(DbTerm thisTerm, PackageNode homePackageNode)
    {
        super(thisTerm.id, thisTerm.name, slm2type(thisTerm.slm)) ;
        this.thisTerm = thisTerm ;
        this.homePackageNode = homePackageNode ;
        if(homePackageNode != null && homePackageNode.status == DELETED_NODE)
        {
            this.status = DELETED_NODE ;
        }
    }

    public String toString()
    {
        String str = "" ;
        if(showPackageInformation)
        {
            String more = (comment == null) ? "" : (String)comment ;
            if(homePackageNode != null)
            {
                more = "@" + homePackageNode.getLocalName() + " : " + more ;
            }
            else
            {
                more = " : " + more ;
            }
            str = getUserObject() + more ;
        }
        else
        {
            str = super.toString() ;
        }

        if(mergedWith != null)
        {
            String m = "[-> " + mergedWith.getUserObject() + "]" ;
            str = m + str ;
        }

        return str ;

    }

    public void addCloned(DBTermCloneNode n)
    {
        cloned.add(n) ;
    }

    public void removeCloned(DBTermCloneNode n)
    {
        cloned.remove(n) ;
    }

    public void setType(short t)
    {
        super.setType(t) ;
        if(cloned != null)
        {
            for(DBTermCloneNode c : cloned)
            {
                c.setType(t) ;
            }
        }
        if(thisTerm != null)
        {
            thisTerm.slm = type2slm(t) ;
        }
    }

    // 2005-08-23
    public Icon getIcon()
    {
        if(hasMore)
        {
            if(getType() == PUBLIC_TERM)
            {
                return IconLib.iconPublicPlus ;
            }
            else if(getType() == this.PRIVATE_TERM)
            {
                return IconLib.iconProtectedPlus ;
            }
            else if(getType() == this.PRIVATE_TERM)
            {
                return IconLib.iconPrivatePlus ;
            }
        }
        return null ; // else use default
    }

    public void setUserObject(Object obj)
    {
        super.setUserObject(obj) ;
        for(DBTermCloneNode c : cloned)
        {
            c.setUserObject(obj) ;
        }
        thisTerm.id = (String)obj ;
    }

    public void setComment(Object obj)
    {
        super.setComment(obj) ;
        for(DBTermCloneNode c : cloned)
        {
            c.setComment(obj) ;
        }
        thisTerm.name = (String)obj ;
    }

    public boolean isObsolete()
    {
        return thisTerm.is_obsolete.equalsIgnoreCase("true") ;
    }

    public boolean isReadOnly()
    {
        return homePackageNode.isReadOnly() || isObsolete() ;
    }

    public String getViewMode()
    {
        return homePackageNode.getViewMode() ;
    }

    public void save(Connection db)
    {
        // the home package may haven't been saved...
        // make sure the homepackage node is saved!
        if(thisTerm.package_oid == null || thisTerm.package_oid.length() == 0)
        {
            // try to read from the package node
            // make sure always save package node first!
            thisTerm.package_oid = homePackageNode.getOid() ;
        }
        thisTerm.write(db) ;

        // termporary ID may be changed according to the naming policy
        // 2005-08-18
        setUserObject(thisTerm.id) ;
    }

    // 2005-08-15
    public void markDeleted()
    {
        this.status = ATOTreeNode.DELETED_NODE ;
        //this.thisTerm.is_obsolete = "true";
        for(DBTermCloneNode c : cloned)
        {
            c.status = ATOTreeNode.DELETED_NODE ;
        }
    }

    // 2005-08-14
    public void merge(Connection db)
    {
        if(mergedWith != null)
        {
            // change the database
            String oid_killed = thisTerm.oid ;
            String oid_leave = mergedWith.getOid() ;
            String user = MOEditor.user.name ;
            OntologyEdit.mergeTerm(db, oid_killed, oid_leave, user) ;
            /* change the interface
             * move children to the target node
             * change all cloned node to the clone of target node
             * delete the node
             */
            for(int i = 0 ; i < this.getChildCount() ; i++)
            {
                mergedWith.add((DbTermNode)getChildAt(i)) ;
            }

            for(DBTermCloneNode c : cloned)
            {
                c.updateSourceNode(mergedWith) ;
            }
            this.removeFromParent() ; // delete the node from the tree
        }
    }

    public String getOid()
    {
        return thisTerm.oid ;
    }

    public PackageNode getHomePackageNode()
    {
        return homePackageNode ;
    }

    public DbTerm getThisTerm()
    {
        return thisTerm ;
    }

    public void setHomePackageNode(PackageNode homePackageNode)
    {
        this.homePackageNode = homePackageNode ;
        this.thisTerm.package_oid = homePackageNode.getOid() ;
    }

    public void setMergedWith(DbTermNode mergedWith) throws Exception
    {
        if(mergedWith != null)
        {
            if(mergedWith.getHomePackageNode() != this.homePackageNode)
            {
                throw new Exception("Term can only be merged inside a package!") ;
            }
            status = MERGED ;
            this.mergedWith = mergedWith ;
        }
    }

    public String getLocalName()
    {
        return getUserObject().toString() ;
    }

    public String status2string()
    {
        if(isObsolete())
        {
            return "Obsolete" ;
        }
        if(mergedWith != null)
        {
            return "Merged with " + mergedWith.getUserObject() + "" ;
        }

        if(getOid() == null)
        {
            return "New Created and not Saved" ;
        }
        else
        {
            return super.status2string() ;
        }
    }
}
