package edu.iastate.ato.gui.packageview ;

import java.sql.Connection ;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector ;

import java.awt.HeadlessException ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import javax.swing.JOptionPane ;
import javax.swing.JSeparator ;

import edu.iastate.ato.agent.ChatPanel ;
import edu.iastate.ato.agent.MoAgent ;
import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.dialog.PrivilegeEditor ;
import edu.iastate.ato.po.DbTerm ;
import edu.iastate.ato.po.OnlineInfo ;
import edu.iastate.ato.po.OntologyEdit ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.po.OntologySchema ;
import edu.iastate.ato.po.Package ;
import edu.iastate.ato.po.UserManager ;
import edu.iastate.ato.shared.AtoConstent ;
import edu.iastate.ato.shared.IconLib ;
import edu.iastate.ato.tree.ATOTreeNode ;
import edu.iastate.ato.tree.DBTermCloneNode ;
import edu.iastate.ato.tree.DbTermNode ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.gui.GUIUtils ;
import edu.iastate.utils.net.EmailTools ;
import edu.iastate.utils.tree.TypedNode;
import edu.iastate.utils.tree.TypedTreeEditor;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class PackageTreeEditor extends PackageTreeEditorBasis
{
    PackageTree thisTree ;
    HashSet<String> editingPkg_OIDs = new HashSet<String>();
    HashSet<PackageNode> editingPkgs = new HashSet<PackageNode>();
    boolean isQuitEditingAll = false;
    boolean isBeginEditing =true;
    
    public PackageTreeEditor(PackageTree tree, Connection db)
    {
        this.tree = tree ;
        this.thisTree = tree ;
        this.db = db ;
    }

    public HashSet<String> getEditingPackageOIDs(){
    	HashSet<String> rtnVal = new HashSet<String>();
    	for(String s:editingPkg_OIDs){
    		rtnVal.add(s);
    	}
    	return rtnVal;
    }
    
    protected void buildContextMenu(TypedNode selectedNode)
    {
        ATOTreeNode theNode = (ATOTreeNode)selectedNode ;
        boolean isRoot = (theNode.getParent() == null) ;

        boolean isTerm = theNode instanceof DbTermNode ;
        boolean isPackage = (theNode instanceof PackageNode) ;
        boolean isCloned = (theNode instanceof DBTermCloneNode) ;

        boolean isDeleted = theNode.isDeleted() ;

        boolean isLeaf = (theNode.getChildCount() == 0) ;
        // if it is a term with unexpanded children, not a leaf
        if(theNode instanceof DbTermNode)
        {
            isLeaf = isLeaf && !((DbTermNode)theNode).hasMore ;
        }

        boolean isTopLevel = (theNode.getParent() instanceof PackageNode) ;
        boolean isMerged = (theNode.status == ATOTreeNode.MERGED) ;

        boolean readOnly = theNode.isReadOnly() ;
        boolean isGuest = MOEditor.theInstance.user.isGuest() ;
        boolean isAdmin = MOEditor.theInstance.user.isAdmin() ;

        // rename if enabled
        //System.out.println(AtoEditor2.selectedNamingPolicy);
        boolean enableRename = MOEditor.theInstance.selectedNamingPolicy.
            enableRename ;

        // for package nodes
        if(isPackage)
        {
            PackageNode thePackage = (PackageNode)theNode ;

            boolean hasWriteRights = hasPrivilege(MOEditor.user.name,
                thePackage) ;
            //System.out.println(privilegeCache);

            // section 1: check in/out, users
            if(readOnly && hasWriteRights)
            {
                // to check out the package
                addMenuItem("Edit the package", IconLib.iconStartPackage,
                    new EditPackageAction(thePackage)) ;
            }
            if(!readOnly)
            {
                addMenuItem("Quit editing", IconLib.iconCancalPackage,
                    new QuitEditingAction(thePackage)) ;
            }

            if(hasWriteRights)
            {
                addMenuItem("Package Editors...", IconLib.iconEditor,
                    new AddEditorAction(thePackage)) ;
            }

            if(!hasWriteRights && !isGuest && !isAdmin)
            {
                // to apply for editor of package
                addMenuItem("Apply for Editor", IconLib.iconAddme,
                    new ApplyEditorAction(thePackage)) ;
            }

            popup.add(new JSeparator()) ;

            // 2. read
            if(!isDeleted)
            {
                addMenuItem("Reload Package", IconLib.iconReloadPackage,
                    new ReloadAction(thePackage)) ;
                addMenuItem("Change Package View Mode", IconLib.iconVisibility,
                    new ChangeViewModeAction(thePackage)) ;
                popup.add(new JSeparator()) ;
            }

            // 3. edut package
            if(!isRoot && !readOnly && !isDeleted && hasWriteRights)
            { //selectedNode.getParent() != null)
                addMenuItem("Change Package ID", IconLib.iconRename,
                    new RenameAction(theNode)) ;
                addMenuItem("Comments", IconLib.iconComment,
                    new DefaultEditCommentsAction(theNode)) ;
                popup.add(new JSeparator()) ;
            }

            if(!isDeleted && !isGuest)
            {
                addMenuItem("Add Sub Package", IconLib.iconAddSubPackage,
                    new CreateSubPackageAction(thePackage)) ;
                // insert parent if not root
                if(!readOnly && hasWriteRights && !isRoot && enableInsertParent)
                {
                    addMenuItem("Add Super Package",
                        IconLib.iconAddSuperPackage,
                        new CreateSuperPackageAction(thePackage)) ;
                }
            }
            if(!readOnly && !isDeleted && hasWriteRights &&
                !isRoot && thePackage.expanded)
            {
                addMenuItem("Delete Package", IconLib.iconDelete,
                    new DeletePackageAction(thePackage)) ;

                String info =
                    "Drag the package and drop it on the package to be merged" ;
                addMenuItem("Merge Into...", IconLib.iconMerge,
                    new DoNothingAction(thePackage, info)) ;
            }
            if(isDeleted && hasWriteRights)
            {
                addMenuItem("Undelete Package", IconLib.iconUndelete,
                    new UndeletePackageAction(thePackage)) ;
            }

            // 4 term
            if(!readOnly && !isDeleted && hasWriteRights)
            { // editing is enabled
                popup.add(new JSeparator()) ;
                addMenuItem("Add Term", IconLib.iconAddTerm,
                    new CreateSubTermAction(thePackage)) ;

            }
        }
        else if(isTerm)
        { // for term node
            DbTermNode theTerm = (DbTermNode)theNode ;
            PackageNode hp = theTerm.getHomePackageNode() ;
            boolean isObsolete = theTerm.isObsolete() ;

            if(!isRoot && !isCloned && !readOnly && !isMerged && !isDeleted)
            {
                if(enableRename)
                {
                    addMenuItem("Change Primary ID", IconLib.iconRename,
                        new RenameAction(theNode)) ;
                }
                addMenuItem("Change Term Secondary Name",
                    IconLib.iconRenameTerm,
                    new DefaultEditCommentsAction(theNode)) ;
                addMenuItem("Change Visiblity", IconLib.iconLock,
                    new ChangeSLMAction(theTerm)) ;
                popup.add(new JSeparator()) ;
            }

            if(isObsolete)
            {
                addMenuItem("Destroy Term", IconLib.iconDestroyTerm,
                    new DestroyTermAction(theTerm)) ;
            }
            else
            {
                // deleted but not because th home package is deleted
                if(isDeleted && !hp.isDeleted())
                {
                    if(theTerm.status == ATOTreeNode.DELETED_UPEDGE)
                    {
                        addMenuItem("Undelete Relation",
                            IconLib.iconUndeleteRelation,
                            new UndeleteTermRelationAction(theTerm)) ;
                    }
                    else if(theTerm.status == ATOTreeNode.DELETED_NODE)
                    {
                        addMenuItem("Undelete Term", IconLib.iconUndelete,
                            new UndeleteTermAction(theTerm)) ;
                    }

                }
                else if(!readOnly && !isDeleted && !isMerged)
                {
                    if(!isCloned)
                    {
                        addMenuItem("Copy Term", IconLib.iconCloneTerm,
                            new CloneTermAction(theTerm)) ;
                        addMenuItem("Insert Sub Term", IconLib.iconAddSub,
                            new CreateSubTermAction(theTerm)) ;
                        addMenuItem("Insert Super Term", IconLib.iconAddSup,
                            new CreateSuperTermAction(theTerm)) ;
                        addMenuItem("Split Branch as Package",
                            IconLib.iconSplit,
                            new SplitPackageAction(theTerm)) ;
                        popup.add(new JSeparator()) ;
                    }

                    if(!isTopLevel && !isMerged)
                    {
                        addMenuItem("Delete This Relation",
                            IconLib.iconDeleteRelation,
                            new DeleteTermRelationAction(theTerm)) ;
                    }
                    if( !isMerged && !isCloned){
	                    if(isLeaf)
	                    {
	                            addMenuItem("Delete Term", IconLib.iconDelete,
	                                new DeleteTermAction(theTerm)) ;
	
	                            String info =
	                                "Drag the term and drop it on the term to be merged" ;
	                            addMenuItem("Merge Term with...",
	                                IconLib.iconMergeTerm,
	                                new DoNothingAction(theTerm, info)) ;
	                            
	                    // Add Branch Relation Delete 
	                    }else{
                            addMenuItem("Delete Branch Relations", IconLib.iconDelete,
                                new DeleteRelationBranchAction(theTerm)) ;
	                    }
                    }
                    
                }
            } // end of if (!isObsolete)
        }
    }

    //2005-08-27
    class AddEditorAction
        implements ActionListener
    {
        PackageNode theNode ;

        public AddEditorAction(PackageNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            // view the privilege of this package
            PrivilegeEditor dlg = new PrivilegeEditor
                (MOEditor.theInstance.mainFrame,
                MOEditor.theInstance.selectedServer,
                theNode.getOid(), theNode.getLocalName()) ;
            GUIUtils.centerWithinParent(dlg) ;
            dlg.setVisible(true) ;
        }
    }

    // 2005-08-26
    class ApplyEditorAction
        implements ActionListener
    {
        PackageNode theNode ;

        public ApplyEditorAction(PackageNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            String package_oid = theNode.getOid() ;
            String author = theNode.getThisPackage().author ;
            String email = OntologyQuerier.getAuthorEmail(db, package_oid) ;

            // if auhtor is online send instant message
            OnlineInfo info = UserManager.getOnlineInfo(db, author) ;
            if(info != null)
            {
                MoAgent myServer = MOEditor.theInstance.messenger ;
                ChatPanel window = myServer.startChat(author, info.host,
                    info.port) ;

                if(window == null)
                {
                    Debug.trace("Connot connect to selected user") ;
                }
                else
                {
                    window.showMe() ;
                    return ;
                }

            }

            // or send email
            if(email != null)
            {
                String ontologyName = MOEditor.theInstance.selectedServer.name ;
                String userName = MOEditor.theInstance.user.name ;
                String packageName = theNode.getLocalName() ;
                String subject = "\"Apply for editor of ontology '" +
                    ontologyName + "', package '" + packageName +
                    "' from user '" + userName + "'\"" ;
                String body = "From " + AtoConstent.APP_NAME ;
                boolean suc = EmailTools.sendWindowsEmail(email, subject, body) ;
                if(!suc)
                {
                    Debug.trace("Failed to send email to " + author + "<" +
                        email + ">") ;
                }
            }

        }
    }

    // 2005-08-17
    // we can only destroy obsolete terms
    class DestroyTermAction
        implements ActionListener
    {
        DbTermNode theNode ;

        public DestroyTermAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            int answer = JOptionPane.showConfirmDialog(null,
                "Are you sure to delete the term permanently ? ") ;
            if(answer == JOptionPane.YES_OPTION)
            {
                // delete it from database
                boolean suc = OntologyEdit.destroyTerm(db, theNode.getOid()) ;

                // delete it from the interface
                if(suc)
                {
                    tree.delete(theNode) ;
                }
                theNode.getHomePackageNode().status = PackageNode.MODIFIED;
                ((PackageTree)tree).modified = true;
            }
        }
    }

    // 2005-08-17
    class UndeletePackageAction
        implements ActionListener
    {
        PackageNode theNode ;

        public UndeletePackageAction(PackageNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            theNode.status = ATOTreeNode.MODIFIED ;
            Vector<DbTermNode> terms = thisTree.getTermsInPackage(theNode) ;
            for(DbTermNode term : terms)
            {
                term.status = ATOTreeNode.MODIFIED ;
            }
            ((PackageTree)tree).modified = true;
        }
    }

    // 2005-08-16
    class UndeleteTermAction
        implements ActionListener
    {
        DbTermNode theNode ;

        public UndeleteTermAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            theNode.status = ATOTreeNode.MODIFIED ;
            ((PackageTree)tree).modified = true;
        }
    }

    // 2005-08-16
    class UndeleteTermRelationAction
        implements ActionListener
    {
        DbTermNode theNode ;

        public UndeleteTermRelationAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            theNode.status = ATOTreeNode.MODIFIED ;
            ((PackageTree)tree).modified = true;
        }
    }

    class CloneTermAction
        implements ActionListener
    {
        DbTermNode theNode ;

        public CloneTermAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            // copy the term as a new term
            // create a new term
            DbTerm oldTerm = theNode.getThisTerm() ;
            DbTerm newTerm = new DbTerm(null, "copy of " + oldTerm.id,
                oldTerm.name, oldTerm.package_oid,
                oldTerm.slm,
                MOEditor.user.name, null, "false") ;
            DbTermNode newNode = new DbTermNode(newTerm,
                theNode.getHomePackageNode()) ;

            TypedNode n = (TypedNode)theNode.getParent() ;
            n.add(newNode) ;
            tree.getModel().reload(n) ;

            newNode.status = ATOTreeNode.MODIFIED ;
            newNode.getHomePackageNode().status = PackageNode.MODIFIED;
            ((PackageTree)tree).modified = true;
        }
    }

    class DoNothingAction
        implements ActionListener
    {
        TypedNode theNode ;
        String info ;

        public DoNothingAction(TypedNode theNode, String info)
        {
            this.theNode = theNode ;
            this.info = info ;
        }

        public void actionPerformed(ActionEvent e)
        {
            // NOTE: the two packages should have the same viewmode
            // move all nodes to the new package
            Debug.trace(info) ;
        }
    }

    // 2005-07-29
    class SplitPackageAction extends DeafultCreateSubValueAction
    {
        DbTermNode theNode ;

        public SplitPackageAction(DbTermNode theNode)
        {
            super(theNode.getHomePackageNode()) ; // get home package
            this.theNode = theNode ;
        }

        protected TypedNode getNewNode()
        {
            // make the new branch as a new package
            // confirm
            int answer = JOptionPane.showConfirmDialog(null,
                "Do you want to make the branch as a new package? ") ;
            if(answer == JOptionPane.YES_OPTION)
            {
                // ask for the new package
                PackageNode newPackage = makeNewPackageNode((PackageNode)
                    parent, theNode.getLocalName()+"Terms") ;

                // move the node to the new package
                if(newPackage != null)
                {
                    try
                    {
                        // enable editing the new package
                        editPackage(newPackage) ;

                        // move the branch
                        ((PackageTree)tree).moveBranch(theNode, newPackage) ;
                        tree.getModel().reload(newPackage);
                        ((PackageTree)tree).modified = true;
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                return newPackage ;
            }
            return null ;
        }
    }

    class QuitEditingAction
        implements ActionListener
    {
        PackageNode thePackageNode ;

        public QuitEditingAction(PackageNode theNode)
        {
            this.thePackageNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            quitEditing(thePackageNode) ;
        }
    }

    public void quitEditingAll(){
    	Iterator<PackageNode> it = this.editingPkgs.iterator();
    	isQuitEditingAll = true;
    	while(it.hasNext()){
    		quitEditing(it.next());
    	}
    	editingPkgs.clear();
    	isQuitEditingAll = false;
    }
    
    public void quitEditing(PackageNode thePackageNode)
    {
    	int answer = -1;
    	if(thePackageNode.status == PackageNode.MODIFIED){
    		//ask to save
            answer = JOptionPane.showConfirmDialog(this.fatherPanel, "Save Changes to "+thePackageNode.getLocalName()+"? ");
            if(answer == JOptionPane.YES_OPTION)
            {
            	this.thisTree.savePackage(thePackageNode);
            }
    	}
    	if(answer != JOptionPane.CANCEL_OPTION || answer == -1){
        	thePackageNode.editing = false;
    		thePackageNode.expanded = false; 
    		
    		editingPkg_OIDs.remove(thePackageNode.getOid());
    		if(!isQuitEditingAll){
    			editingPkgs.remove(thePackageNode);
    		}
    		
        	UserManager.cancelEditing(db, thePackageNode.getOid(),
                    MOEditor.user.name) ;
                thePackageNode.setReadOnly(true) ;

                // diseable the property editor if the node is under editing
                MOEditor.theInstance.paneDetails.switchPropertyEditor(
                    thePackageNode) ;
        }
    }

    class EditPackageAction
        implements ActionListener
    {
        PackageNode theNode ;

        public EditPackageAction(PackageNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            editPackage(theNode) ;
        }
    }

    /**
     * Begin editing for a set of packages
     * @param packages Vector - set of package oid
     * @since 2005-07-25
     */
    public void beginEditing(HashSet<String> pkg_oids)
    {
        // search packages
        Vector<PackageNode> e =  thisTree.getAllPackage();
        isBeginEditing = true;
        for(PackageNode n : e)
        {
            if(pkg_oids.contains(n.getOid())){
            	editPackage(n);
            }
        }
        isBeginEditing = false;
    }
    
    
    public void editPackage(PackageNode node)
    {
        // check if the package is under editing
        String editor = UserManager.getEditor(db, node.getOid()) ;
        if(editor != null)
        {
            Debug.trace("Requested package '" + node.getLocalName() +
                "' is edited by user '" + editor + "'") ;
            return ;
        }

        // if not, check it out
        boolean suc = UserManager.beginEditing(db, node.getOid(),
            MOEditor.user.name) ;

        // make all term child editable
        if(suc)
        {
            node.editing = true;
            editingPkg_OIDs.add(node.getOid());
    		editingPkgs.add(node);
            
        	if( !isBeginEditing )
        		Debug.trace("You can edit terms in package '" +
                node.getLocalName() + "'") ;
            //System.out.println("EditPackageAction: " + suc);
            node.setReadOnly(false) ;
            // if the package not yet expanded, expand it
            if(node.expanded == false)
            {
            	if( node.wasEdited == true){
            		expandPackage(node, false) ;
            	}else{
            		expandPackage(node, true) ;
            	}
            	node.wasEdited = true;
            }
        }
        else
        {
            Debug.trace(
                "Editing request failed - Unable access the database") ;
        }
    }

    // we only delete leaf so far
    class DeleteTermAction extends DefaultDeleteAction
    {
        DbTermNode theNode ;

        public DeleteTermAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            theNode.markDeleted() ;
            theNode.getHomePackageNode().status = PackageNode.MODIFIED;
            tree.getModel().reload(theNode) ;
            ((PackageTree)tree).modified = true;
        }

    }

    class DeleteTermRelationAction extends DefaultDeleteAction
    {
        DbTermNode theNode ;

        public DeleteTermRelationAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            theNode.status = ATOTreeNode.DELETED_UPEDGE ;
            theNode.getHomePackageNode().status = PackageNode.MODIFIED;
            tree.getModel().reload(theNode) ;
            ((PackageTree)tree).modified = true;
        }
    }
    
    class DeleteRelationBranchAction extends DefaultDeleteAction
    {
        DbTermNode theNode ;

        public DeleteRelationBranchAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        private void deleteRelationRec(DbTermNode tn){
        	tn.status = ATOTreeNode.DELETED_UPEDGE ;
        	for(int i=0; i<tn.getChildCount(); ++i){
        		if( tn.getChildAt(i) instanceof DbTermNode){
        			deleteRelationRec((DbTermNode)tn.getChildAt(i));
        		}
        	}
        }
        
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            theNode.status = ATOTreeNode.DELETED_UPEDGE ;
            theNode.getHomePackageNode().status = PackageNode.MODIFIED;
            
            deleteRelationRec(theNode);
            
            tree.getModel().reload(theNode) ;
            ((PackageTree)tree).modified = true;
        }
    }

    class DeletePackageAction extends DefaultDeleteAction
    {
        PackageNode theNode ;

        public DeletePackageAction(PackageNode theNode)
        {
            this.theNode = theNode ;
        }
        
        
        private void deletePackageRec(PackageNode tn){
        	tn.markDeleted();
        	for(int i=0; i<tn.getChildCount(); ++i){
        		if( tn.getChildAt(i) instanceof PackageNode){
        			deletePackageRec((PackageNode)tn.getChildAt(i));
        		}
        	}
        }
        
        public void actionPerformed(ActionEvent e)
        {

            if(theNode.hasSubPackage())
            {
            	/*Debug.trace("Cannot delete a package with sub packages. \n" +
                    "Please delete all sub packages and try again") ;
            	*/
            	
            	int answer = JOptionPane.showConfirmDialog(null,
    	                "Are you sure to delete (obsolete) all terms(inclued unexpanded) in the package and subpackages? \n" +
    	                "The package and subpackges themselves will be deleted only if it has no term (including obsoleted terms)") ;
	            if(answer == JOptionPane.YES_OPTION)
	            {
	                theNode.markDeleted() ;
	                ((PackageNode)theNode.getParent()).status = PackageNode.MODIFIED;
	                deletePackageRec(theNode);
	                tree.getModel().reload(theNode.getParent()) ;
	                ((PackageTree)tree).modified = true;
	            }
                return ;
                
            }else{
	            int answer = JOptionPane.showConfirmDialog(null,
	                "Are you sure to delete (obsolete) all terms(inclued unexpanded) in the package? \n" +
	                "The package itself will be deleted only if it has no term (including obsoleted terms)") ;
	            if(answer == JOptionPane.YES_OPTION)
	            {
	                theNode.markDeleted() ;
	                ((PackageNode)theNode.getParent()).status = PackageNode.MODIFIED;
	                tree.getModel().reload(theNode.getParent()) ;
	                ((PackageTree)tree).modified = true;
	            }
            }
        }
    }

    class CreateSuperPackageAction extends DefaultInsertParentAction
    {
        public PackageNode newPackageNode = null;
        
    	public CreateSuperPackageAction(PackageNode theNode)
        {
            super(theNode) ;
        }
        

        public void actionPerformed(ActionEvent e){
        	super.actionPerformed(e);
        	editPackage(newPackageNode);
        }
        
        protected TypedNode getNewNode()
        {
            return (newPackageNode = makeNewPackageNode((PackageNode)theNode, null) );
        }
    }

    class CreateSubPackageAction extends DeafultCreateSubValueAction
    {
    	public PackageNode newPackageNode = null;
    	
        public CreateSubPackageAction(PackageNode parent)
        {
            super(parent) ;
        }

        public void actionPerformed(ActionEvent e){
        	super.actionPerformed(e); 
        	editPackage(newPackageNode);        	
        }

        protected TypedNode getNewNode()
        {
            return (newPackageNode = makeNewPackageNode((PackageNode)parent, null) );
        }
    }

    class ChangeViewModeAction
        implements ActionListener
    {
        public PackageNode theNode ;

        public ChangeViewModeAction(PackageNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            Vector po = OntologySchema.getPartialOrders(db) ; ;
            Object data[] = po.toArray() ;
            String newView = (String)JOptionPane.showInputDialog(null,
                "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null,
                data, theNode.getViewMode()) ;
            if(newView == null || theNode.getViewMode().equals(newView))
            {
                return ;
            }

            // delete old nodes (ask for save if modified)
            theNode.setViewMode(newView) ;

            // add new nodes
            expandPackage(theNode) ;
        }
    }

    class ChangeSLMAction
        implements ActionListener
    {
        public DbTermNode theNode ;

        public ChangeSLMAction(DbTermNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            // get current type
            short type = theNode.getType() ;

            String data[] =
                {
                "public", "protected", "private"} ;
            String slm = (String)JOptionPane.showInputDialog(null,
                "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null,
                data, ATOTreeNode.type2slm(type)) ;

            if(slm == null)
            {
                return ; // user cancelled
            }

            // select new type
            if(ATOTreeNode.slm2type(slm) == type)
            {
                return ; // no change
            }

            theNode.setType(ATOTreeNode.slm2type(slm)) ;
            changed(theNode) ;

        }
    }

    class CreateSuperTermAction extends DefaultInsertParentAction
    {
        public CreateSuperTermAction(TypedNode theNode)
        {
            super(theNode) ;
        }

        protected TypedNode getNewNode()
        {
            return makeNewTermNode(theNode) ;
        }
    }

    class CreateSubTermAction extends DeafultCreateSubValueAction
    {
        public CreateSubTermAction(TypedNode parent)
        {
            super(parent) ;
        }

        protected TypedNode getNewNode()
        {
            return makeNewTermNode(parent) ;
        }
    }

    class RenameAction
        implements ActionListener
    {

        public ATOTreeNode theNode ;

        public RenameAction(ATOTreeNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            Object newName = getNewUserObject() ;

            if(newName != null)
            {
                theNode.rename(newName.toString()) ;
                if( theNode instanceof PackageNode ){
                	
                }else if(theNode instanceof PackageNode ){
                	
                }
                tree.getModel().reload(theNode) ;
                //TreeNodeRenameEditing action = new TreeNodeRenameEditing(
                //    theNode, oldName, newName);
                //history.addAction(action);
                ((PackageTree)tree).modified = true;
            }
        }

        protected String getNewUserObject() throws HeadlessException
        {
            String oldName = (String)theNode.getUserObject() ;
            String newName = JOptionPane.showInputDialog(
                "Give the new name of the object", oldName) ;
            if(newName == null || oldName.equals(newName))
            {
                return null ;
            }

            // validate the name
            if(!isLegalName(newName) ||
                Package.GlobalPkg.equals(newName))
            {
                JOptionPane.showMessageDialog(null, "Name is not legal!") ;
                return null ;
            }

            // make sure no duplicated names
            if(!checkDuplicateName(newName))
            {
                return null ;
            }

            return newName ;
        }
    }

    class ReloadAction
        implements ActionListener
    {
        PackageNode theNode ;

        public ReloadAction(PackageNode theNode)
        {
            this.theNode = theNode ;
        }

        public void actionPerformed(ActionEvent e)
        {
            // same as expand            
            expandPackage(theNode) ;
        }
    }
}
