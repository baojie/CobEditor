package edu.iastate.ato.gui.packageview ;

import java.sql.Connection ;
import java.util.Vector ;

import javax.swing.JFrame ;
import javax.swing.JScrollPane ;

import edu.iastate.anthill.indus.tree.TypedTree ;
import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.po.DbPackage ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.po.OntologySchema ;
import edu.iastate.ato.po.Package ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.sql.LocalDBConnection ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07</p>
 */
public class Package2Tree
{
    String defaultRelation ;
    protected Connection db ;

    public Package2Tree(Connection db)
    {
        this.db = db ;

        // find the default relation
        defaultRelation = "is_a" ;
        Vector<String> v = OntologySchema.getPartialOrders(db) ;
        if(v.size() > 1)
        {
            defaultRelation = v.elementAt(0) ;
        }

    }

    public String getRootOid()
    {
        return OntologyQuerier.getPackageOid(db, Package.GlobalPkg) ;
    }

    public PackageNode createNode(String oid)
    {
        DbPackage thePackage = DbPackage.read(db, oid) ;
        PackageNode n = new PackageNode(thePackage) ;
        n.updateTermNumber(db) ;
        n.setViewMode(defaultRelation) ;
        return n ;
    }

    /**
     *
     * @param cutoff int , < 0 if no cutoff, >=0 the cutoff depth
     * @param from DBTreeNode
     * @since 2005-03-11
     */
    protected void buildTree(TypedTree tree, int cutoff, PackageNode from)
    {
        // if it's a null node
        if(from == null)
        {
            return ;
        }
        if(cutoff != 0)
        {
            String ids = (String)from.getOid() ;
            Vector children = OntologyQuerier.getChildrenPackage(db, ids) ;

            System.out.println(ids + " -> " + children) ;
            for(int i = 0 ; i < children.size() ; i++)
            {
                String kid = (String)children.elementAt(i) ;
                PackageNode node = createNode(kid) ;
                from.add(node) ;

                buildTree(tree, cutoff - 1, node) ;
            }
        }
        return ;
    }

    public PackageTree getTree(String from_oid, int cutoff)
    {
        if(from_oid == null)
        {
            from_oid = this.getRootOid() ;
        }

        PackageTree tree = new PackageTree(db) ;
        PackageNode node = createNode(from_oid) ;

        buildTree(tree, cutoff, node) ;
        tree.setTop(node) ;
        return tree ;
    }
    
     /**
     * For test purpose
     * @param args String[]
     */
    public static void main(String[] args)
    {
        LocalDBConnection conn =
            MOEditor.getConnection(OntologyServerInfo.getAtoOntology()) ;
        if(conn.connect())
        {
            Package2Tree mm = new Package2Tree(conn.db) ;
            TypedTree t = mm.getTree(null, -1) ;
            conn.disconnect() ;

            // show it
            JFrame frame = new JFrame() ;
            frame.setSize(800, 600) ;
            JScrollPane scr = new JScrollPane(t) ;
            frame.getContentPane().add(scr) ;
            frame.setVisible(true) ;
            System.out.print(t) ;
        }
        else
        {
            Debug.trace("Cannot connect to database") ;
        }
    }

}
