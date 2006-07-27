package edu.iastate.ato.tree ;

import java.sql.Connection ;
import java.util.HashSet ;
import java.util.Set ;
import java.util.Vector ;

import javax.swing.Icon ;
import javax.swing.JOptionPane ;
import javax.swing.JProgressBar ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.packageview.PackageTree ;
import edu.iastate.ato.po.DbPackage ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.shared.IconLib ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.lang.StopWatch ;
import edu.iastate.utils.tree.TypedNode;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class PackageNode extends ATOTreeNode
{
    private DbPackage thisPackage;
    public boolean expanded = false;
    public MetaTreeNode obsoleteNodes ;
    public boolean editing = false;
    public boolean wasEdited = false;

    public void markDeleted()
    {
        status = ATOTreeNode.DELETED_NODE ;
        Vector<DbTermNode> terms = PackageTree.getTermsInPackage(this) ;
        // mark all its terms as deleted
        for(DbTermNode term : terms)
        {
            term.status = ATOTreeNode.DELETED_NODE ;
        }

        //move all its subpackages to its super package
        /*PackageNode superPkg = (PackageNode)this.getParent() ;
        for(int i = 0 ; i < this.getChildCount() ; i++)
        {
            TypedNode n = (TypedNode)this.getChildAt(i) ;
            if(n instanceof PackageNode)
            {
                superPkg.add(n) ;
                ((PackageNode)n).status = PackageNode.MODIFIED ;
            }
        }*/
    }

    // 2005-08-10
    public boolean hasSubPackage()
    {
        int n = this.getChildCount() ;
        for(int i = 0 ; i < n ; i++)
        {
            ATOTreeNode child = (ATOTreeNode)getChildAt(i) ;
            if(child instanceof PackageNode)
            {
                return true ;
            }
        }
        return false ;
    }

    // delete terms inside the package while keep sub packages
    public void clearPackage()
    {
        int n = this.getChildCount() ;
        Set<ATOTreeNode> toRemove = new HashSet<ATOTreeNode>() ;
        for(int i = 0 ; i < n ; i++)
        {
            ATOTreeNode child = (ATOTreeNode)getChildAt(i) ;
            if(!(child instanceof PackageNode))
            {
                toRemove.add(child) ;
            }
        }
        for(ATOTreeNode child : toRemove)
        {
            child.removeFromParent() ;
        }
        expanded = false ;
    }

    int max = 200 ;

    
    public void expand(Connection db, JProgressBar progress){
    	expand(db, progress, true);
    }
    
    public void expand(Connection db, JProgressBar progress, boolean reload)
    {
        if(this.status == ATOTreeNode.DELETED_NODE)
        {
            Debug.trace("Cannot expand a delete package") ;
            return ;
        }
        updateTermNumber(db) ;

        //{{ 2005-08-22
        int cutoff = -1 ;
        if(termNumber > max)
        {
            int answer = JOptionPane.showConfirmDialog(null,
                "Package have too many terms (>" + max +
                "), will you only expand the top level terms ? \n" +
                "(You can further expand term with '+' sign with double click)") ;
            if(answer == JOptionPane.YES_OPTION)
            {
                cutoff = 0 ;
            }
            else if(answer == JOptionPane.CANCEL_OPTION)
            {
                return ;
            }
        }
        //}}
        //String packageName = packageNode.getLocalName();
        if( reload == true ){
        	clearPackage();
        }
        
        updateTermNumber(db) ;

        treeLoader = new Term2Tree(db, getViewMode(), true) ;
        treeLoader.setProgress(progress) ;

        if( reload == true ){
	        StopWatch w = new StopWatch() ;
	        w.start() ;
	        	treeLoader.makeDagFromRootsQuick(this, cutoff, true, this) ;
	        
	        //treeLoader.makeDagFromRoots(this, cutoff, true, this) ;
	        w.stop();
	        System.out.println(w.print());
        }

        // make the Obsolete meta node

        if( reload == true ){
	        int n = OntologyQuerier.getObsoleteTermCount(db, this) ;
	        obsoleteNodes = new MetaTreeNode("Obsolete(" + n + ")", null) ;
	        this.add(obsoleteNodes) ;
        }
        // load all obsoleteNodes
        //treeLoader.addObsoleteTerms(obsoleteNodes, this) ;
        expanded = true ;
    }

    public Term2Tree treeLoader ;

    public String getOid()
    {
        return thisPackage.oid ;
    }

    int termNumber = -1 ;

    // 2005-08-17
    public void updateTermNumber(Connection db)
    {
        termNumber = OntologyQuerier.getTermCount(db, getOid()) ;
    }

    public PackageNode(DbPackage thisPackage)
    {
        super(thisPackage.pid, thisPackage.comment, PACKAGE) ;
        this.thisPackage = thisPackage ;
    }

    public String getViewMode()
    {
        return viewMode ;
    }

    public DbPackage getThisPackage()
    {
        return thisPackage ;
    }

    public void setViewMode(String viewMode)
    {
        this.viewMode = viewMode ;
    }

    private String viewMode = null ; // isa, partof...

    public String toString()
    {
        String str = super.toString() ;
        if(termNumber >= 0)
        {
            str += "(" + termNumber + ")" ;
        }
        if(viewMode != null)
        {
            str += " [" + viewMode + " View]" ;
        }
        return str ;
    }

    // change pid
    public void setUserObject(Object newObj)
    {
        super.setUserObject(newObj) ;
        thisPackage.pid = (String)newObj ;
    }

    // change comment
    public void setComment(Object c)
    {
        super.setComment(c) ;
        thisPackage.comment = (String)c ;
    }

    public int compareTo(Object o)
    {
        return 0 ;
    }

    public void save(Connection db)
    {
        thisPackage.write(db, MOEditor.theInstance.user.name, false) ;
    }

    public String getLocalName()
    {
        return getUserObject().toString() ;
    }

    public String status2string()
    {
        if(getOid() == null)
        {
            return "New Created and not Saved" ;
        }
        else
        {
            return super.status2string() ;
        }
    }

    // 2005-08-28
    public Icon getIcon()
    {
        if(expanded)
        {
            return IconLib.iconPackageOpen ;
        }
        else
        {
            return IconLib.iconPackage ;
        }
    }
}
