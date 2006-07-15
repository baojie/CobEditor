package edu.iastate.ato.gui.packageview ;

import java.sql.Connection ;
import java.util.Enumeration ;
import java.util.HashMap ;
import java.util.Map ;

import java.awt.HeadlessException ;
import javax.swing.JOptionPane ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.po.DbPackage ;
import edu.iastate.ato.po.DbTerm ;
import edu.iastate.ato.po.OntologyEdit ;
import edu.iastate.ato.po.Package ;
import edu.iastate.ato.po.User ;
import edu.iastate.ato.po.UserManager ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.gui.JStatusBar ;
import java.util.* ;
import edu.iastate.utils.lang.StopWatch ;
import edu.iastate.utils.tree.TypedNode;
import edu.iastate.utils.tree.TypedTreeEditor;

public abstract class PackageTreeEditorBasis extends TypedTreeEditor
{
    public PackageTreeEditorBasis()
    {
        super() ;
    }

    protected String makeDefaultName(TypedNode selected)
    {
        return MOEditor.theInstance.selectedNamingPolicy.
            makeNameWhenCreating(selected.getLocalName()) ;
    }

    public static boolean isLegalName(String name)
    {
        if (name == null)
        {
            return false;
        }
        else if (name.length() == 0)
        {
            return false;
        }
        else if (!name.matches("[a-zA-Z][\\s\\w\\-._]*"))
        {
            return false;
        }
        return true;
    }

    protected Connection db ;
    protected Map<String, Map> privilegeCache = new HashMap() ;
    public boolean enableInsertParent = true ;
    protected PackageNode makeNewPackageNode(PackageNode selected,
        String defaultName)
    {
        if(defaultName == null)
        {
            defaultName = makeDefaultName(selected) ;
        }
        String newName = JOptionPane.showInputDialog(
            "Give the name for new node", defaultName) ;
        if(newName == null)
        {
            return null ;
        }

        if(!isLegalName(newName) ||
            Package.GlobalPkg.equals(newName))
        {
            JOptionPane.showMessageDialog(null, "Name is not legal!") ;
            return null ;
        }

        // make sure no duplicated names
        if(checkDuplicateName(newName))
        {
            DbPackage newPkg = new DbPackage(null, newName, null,
                MOEditor.user.name, null) ;
            PackageNode newNode = new PackageNode(newPkg) ;
            newNode.status = ATOTreeNode.MODIFIED ;

            // Peter: Makes sure the parent package asks whether to save or not, when editing is done.
           // ((PackageNode)selected.getParent()).status = PackageNode.MODIFIED;
            
            newNode.setViewMode(selected.getViewMode()) ;
            newNode.status = PackageNode.MODIFIED;

            // submit the new package to the database (because we want the oid)
            newNode.save(db) ;
            OntologyEdit.addPackageNesting(db, newNode.getOid(),
                selected.getOid(), MOEditor.user.name) ;
            // add access privilege
            if(MOEditor.user.isNormalRole())
            {
                UserManager.addPrivilege(db, MOEditor.user.name,
                    newNode.getOid(), User.READ_WRITE) ;
            }
            ((PackageTree)tree).modified = true;
            selected.status = PackageNode.MODIFIED;
            return newNode ;
        }

        return null ;
    }

    protected boolean checkDuplicateName(String newName)
    {
        TypedNode n = (TypedNode)tree.getModel().getRoot() ;
        Enumeration ee = n.depthFirstEnumeration() ;
        while(ee.hasMoreElements())
        {
            TypedNode node = (TypedNode)ee.nextElement() ;
            String name = node.toString() ;
            if(name.equals(newName))
            {
                JOptionPane.showMessageDialog(null, "'" + newName +
                    "' is used, please try again") ;
                return false ;
            }
        }
        return true ;
    }

    public void expandPackage(final PackageNode thePackage){
    	expandPackage(thePackage, true);    	
    }
    
    // 2005-08-22 load with progress bar
    public void expandPackage(final PackageNode thePackage, final boolean reload)
    {
        final JStatusBar statusBar = MOEditor.theInstance.statusBar ;
        Thread t = new Thread()
        {
            public void run()
            {

                int pb = statusBar.addProgressBar(true, 0, 0) ;
                statusBar.updateProgressBar(pb,
                    "Load Package " + thePackage.getLocalName()) ;

                try
                {
                    // do something here
                    thePackage.expand(db, statusBar.getProgressBar(pb), reload) ;
                    tree.getModel().reload(thePackage) ;
                }
                catch(Exception ex)
                {
                }
                statusBar.removeProgressBar(pb) ;
            }
        } ;
        t.start() ;
    }

    // user -> the table of editablity

    // 2005-08-20
    protected boolean hasPrivilege(String user, PackageNode pkg)
    {
        Object editable = privilegeCache.get(user) ;
        if(editable == null)
        {
            privilegeCache.put(user, new HashMap<PackageNode, Boolean>()) ;
        }
        Map editableTable = privilegeCache.get(user) ;

        if(pkg.getOid() == null)
        { // not saved, editable
            editableTable.put(pkg, true) ;
            return true ;
        }
        else
        { // query the cache
            if(editableTable.get(pkg) != null)
            {
                return(Boolean)editableTable.get(pkg) ;
            }
            else
            {// not found, query the database
                boolean mayI = UserManager.hasWritePrivilege(db, user,
                    pkg.getOid()) ;
                editableTable.put(pkg, mayI) ;
                return mayI ;
            }
        }
    }

    protected void changed(TypedNode theNode)
    {
        tree.getModel().reload(theNode) ;
        tree.expandNode(theNode) ;
        ((ATOTreeNode)theNode).status = ATOTreeNode.MODIFIED ;

        ((PackageTree)tree).modified = true;
        //tree.repaint();
    }

    protected TypedNode makeNewTermNode(TypedNode selected) throws
        HeadlessException
    {
        String defaultName = makeDefaultName(selected) ;
        String newName = JOptionPane.showInputDialog(
            "Give the name for new node", defaultName) ;
        if(newName == null)
        {
            return null ;
        }

        if(!MOEditor.theInstance.selectedNamingPolicy.isNameValid(newName))
        {
            JOptionPane.showMessageDialog(null, "Name is not legal!") ;
            return null ;
        }

        // make sure no duplicated names
        if(!checkDuplicateName(newName))
        {
            return null ;
        }

        short type = ATOTreeNode.PUBLIC_TERM ;

        PackageNode home = null ;

        if(selected instanceof PackageNode)
        {
            home = (PackageNode)selected ;
        }
        else if(selected instanceof DbTermNode)
        {
            home = ((DbTermNode)selected).getHomePackageNode() ;
        }

        DbTerm t = new DbTerm(null, newName, newName, home.getOid(),
            ATOTreeNode.type2slm(type), MOEditor.user.name, null,
            "false") ;
        DbTermNode newNode = new DbTermNode(t, home) ;
        
        newNode.status = ATOTreeNode.MODIFIED ;
        home.status = PackageNode.MODIFIED;
        
        ((PackageTree)tree).modified = true;

        return newNode ;
    }
}
