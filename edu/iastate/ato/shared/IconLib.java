package edu.iastate.ato.shared ;

import javax.swing.Icon ;
import javax.swing.ImageIcon ;

import edu.iastate.utils.gui.GUIUtils ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-10</p>
 */
public class IconLib
{
    public static ImageIcon loadImageIcon(String name)
    {
        return(ImageIcon)GUIUtils.loadIcon("images/" + name) ;
    }

    public static Icon redoIcon = GUIUtils.loadIcon("images/redo.gif") ;
    public static Icon undoIcon = GUIUtils.loadIcon("images/undo.gif") ;
    public static Icon submitIcon = GUIUtils.loadIcon("images/save.gif") ;
    public static Icon configIcon = GUIUtils.loadIcon("images/config.gif") ;
    public static Icon aboutIcon = GUIUtils.loadIcon("images/about.gif") ;
    public static Icon expandIcon = GUIUtils.loadIcon("images/expand.gif") ;
    public static Icon expandAllIcon = GUIUtils.loadIcon("images/expandall.gif") ;
    public static Icon helpIcon = GUIUtils.loadIcon("images/help.gif") ;
    public static Icon reloadIcon = GUIUtils.loadIcon("images/reload.gif") ;
    public static Icon searchIcon = GUIUtils.loadIcon("images/search.gif") ;
    public static Icon findnextIcon = GUIUtils.loadIcon("images/findnext.gif") ;

    public static Icon iconChat = GUIUtils.loadIcon("images/msn.gif") ;

    // add Jie Bao , 2005-04-22 icon for avh editor
    public static ImageIcon iconRename = loadImageIcon("icon_rename.gif") ;
    public static ImageIcon iconComment = loadImageIcon("comment.gif") ;
    public static ImageIcon iconAddSup = loadImageIcon("addsup.gif") ;
    public static ImageIcon iconAddSub = loadImageIcon("addsub.gif") ;
    public static ImageIcon iconDelete = loadImageIcon("delete.gif") ;
    public static ImageIcon iconDeleteSub = loadImageIcon("deletesub.gif") ;
    public static ImageIcon iconDeleteSup = loadImageIcon("deletesup.gif") ;
    public static ImageIcon iconDbTree = loadImageIcon("dbtree.gif") ;
    public static ImageIcon iconDbSet = loadImageIcon("dbset.gif") ;

// add Jie Bao, 2005-04-22 icon for animal trait ontology render
    //public static ImageIcon iconAtoSpecies = loadImageIcon("species.gif") ;
    public static ImageIcon iconMeta = loadImageIcon("atoclass.gif") ;
    //public static ImageIcon iconAtoType = loadImageIcon("atotype.gif") ;
    //public static ImageIcon iconAtoTrait = loadImageIcon("atotrait.gif") ;

// add Jie Bao, 2005-04-22 for modular ontology
    public static ImageIcon iconPublic = loadImageIcon("i_public.gif") ;
    public static ImageIcon iconProtected = loadImageIcon("i_protected.gif") ;
    public static ImageIcon iconPrivate = loadImageIcon("i_private.gif") ;
    public static ImageIcon iconPublicPlus = loadImageIcon("i_public_plus.gif") ;
    public static ImageIcon iconProtectedPlus = loadImageIcon(
        "i_protected_plus.gif") ;
    public static ImageIcon iconPrivatePlus = loadImageIcon(
        "i_private_plus.gif") ;

// add Jie Bao 2005-04-23
    public static ImageIcon iconOK = loadImageIcon("ok.gif") ;
    public static ImageIcon iconCancel = loadImageIcon("cancel.gif") ;

    public static ImageIcon iconRoot = loadImageIcon("root.gif") ;
    public static ImageIcon iconPackage = loadImageIcon("package.gif") ;
    public static ImageIcon iconPackageOpen = loadImageIcon("item-open.gif") ;

    // add Jie Bao 2005-08-27
    public static ImageIcon iconBlank = loadImageIcon("blank.gif") ;

    public static ImageIcon iconLoad = loadImageIcon("load.gif") ;
    public static ImageIcon iconClose = loadImageIcon("close.gif") ;
    public static ImageIcon iconClear = loadImageIcon("clear.gif") ;
    public static ImageIcon iconOWL = loadImageIcon("owl-icon.gif") ;
    public static ImageIcon iconCreate = loadImageIcon("create.gif") ;
    public static ImageIcon iconSchema = loadImageIcon("ontschema.gif") ;
    public static ImageIcon iconUsers = loadImageIcon("users.gif") ;
    public static ImageIcon iconLogin = loadImageIcon("login.gif") ;
    public static ImageIcon iconLogout = loadImageIcon("logout.gif") ;
    public static ImageIcon iconEditor = loadImageIcon("editor.gif") ;
    public static ImageIcon iconAddme = loadImageIcon("add.gif") ;
    public static ImageIcon iconUndelete = loadImageIcon("undelete.gif") ;
    public static ImageIcon iconReloadPackage = loadImageIcon("icon-reload.gif") ;
    public static ImageIcon iconVisibility = loadImageIcon("visibility.gif") ;
    public static ImageIcon iconStartPackage = loadImageIcon("startedit.gif") ;
    public static ImageIcon iconCancalPackage = loadImageIcon("stopedit.gif") ;
    public static ImageIcon iconAddSubPackage = loadImageIcon(
        "addsubpackage.gif") ;
    public static ImageIcon iconAddSuperPackage = loadImageIcon(
        "addsuperpackage.gif") ;
    public static ImageIcon iconAddTerm = loadImageIcon("addterm.gif") ;
    public static ImageIcon iconMerge = loadImageIcon("merge_icon.gif") ;
    public static ImageIcon iconSplit = loadImageIcon("split.gif") ;
    public static ImageIcon iconRenameTerm = loadImageIcon("rename1.gif") ;
    public static ImageIcon iconDestroyTerm = loadImageIcon("destroy.gif") ;
    public static ImageIcon iconCloneTerm = loadImageIcon("clone.gif") ;
    public static ImageIcon iconLock = loadImageIcon("lock.gif") ;
    public static ImageIcon iconMergeTerm = loadImageIcon("mergeterm.gif") ;
    public static ImageIcon iconDeleteRelation = loadImageIcon(
        "deleterelation.gif") ;
    public static ImageIcon iconUndeleteRelation = loadImageIcon(
        "undeleterelation.gif") ;

}
