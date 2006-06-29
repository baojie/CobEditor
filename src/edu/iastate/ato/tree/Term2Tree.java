package edu.iastate.ato.tree ;

import java.sql.Connection ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.Vector ;

import edu.iastate.ato.po.DbPackage ;
import edu.iastate.ato.po.DbTerm ;
import edu.iastate.ato.po.OntologyQuerier ;
import edu.iastate.ato.shared.LongTask ;
import edu.iastate.utils.tree.TypedNode;

/**
 * <p>@author Jie Bao</p>
 * <p>@since </p>
 */
public class Term2Tree extends LongTask
{
    protected Connection db ;
    String relation ;
    boolean topdown ; // true: build a from parent to children tree. false: the inverse

    public boolean showPackageInformation = false ;

    public Term2Tree(Connection db, String relation, boolean topdown)
    {
        this.db = db ;
        this.relation = relation ;
        this.topdown = topdown ;
    }

    public void addIsolatedTerms(TypedNode parent)
    {
        Vector<String> terms = OntologyQuerier.getIsolatedTerms(db, relation) ;
        int number = terms.size() ;
        for(int i = 0 ; i < number ; i++)
        {
            // create a node for the term
            String t = terms.elementAt(i) ;
            DbTermNode node = (DbTermNode)createNode(t, (PackageNode)null) ;
            parent.add(node) ;
            this.updateProgress("Read isolated node (" + i + " of " + number +
                ")"
                + node.getLocalName()) ;
        }
    }

    public void addObsoleteTerms(TypedNode parent, PackageNode hp)
    {
        try
        {
            String pkg_oid = null ;
            if(hp != null)
            {
                pkg_oid = hp.getOid() ;
            }
            Vector<String> obo = OntologyQuerier.getObsoleteTerm(db, pkg_oid) ;
            int number = obo.size() ;
            for(int i = 0 ; i < number ; i++)
            {
                String term_oid = obo.elementAt(i) ;

                DbTermNode newNode = this.createNode(term_oid, hp) ;
                newNode.showPackageInformation = false ;
                parent.add(newNode) ;
                this.updateProgress("Read obsolete node (" + i + " of " +
                    number + ")" + newNode.getLocalName()) ;
            }
        }
        catch(Exception ex)
        {
        }

    }

    /**
     * Read DAG from the database, with given cutoff; start from root nodes
     *
     * @param parent TypedNode
     * @param relation String
     * @param topdown boolean true : build a super 2 sub tree
     *                        false: build a sub to super node
     */
    public void makeDagFromRoots(TypedNode parent, int cutoff,
        boolean includeIsolatedTerm, PackageNode homePackage)
    {
        Vector<String> root_oids = OntologyQuerier.getRootTerms(db, relation,
            topdown, includeIsolatedTerm, homePackage) ;
        System.out.println("roots : " + root_oids) ;

        representatives.clear() ;

        // add dag for each top node
        int number = root_oids.size() ;
        for(int i = 0 ; i < number ; i++)
        {
            String oneRoot = root_oids.elementAt(i) ;
            TypedNode node = makeDagFromTerm(oneRoot, cutoff, homePackage) ;
            parent.add(node) ;
            System.out.println(node) ;
            this.updateProgress("Find top node (" + i + " of " + number + ")"
                + node.getLocalName()) ;
        }
    }

    // 2005-08-30
    public void makeDagFromRootsQuick(TypedNode parent, int cutoff,
        boolean includeIsolatedTerm, PackageNode homePackage)
    {

        String termConditionSQL = OntologyQuerier.getRootTermsSQL(db, relation,
            topdown, includeIsolatedTerm, homePackage) ;

        representatives.clear() ;

        // make the root nodes
        Vector<DbTermNode> roots = batchCreateNode(termConditionSQL,
            homePackage) ;

        // build tree for each of the root node
        int number = roots.size() ;
        for(int i = 0 ; i < number ; i++)
        {
            DbTermNode node = roots.elementAt(i);
            // the term may clready be in the tree
            DbTermNode oldNode = (DbTermNode)representatives.get(node.getOid()) ;
            // if yes, return a clone
            if(oldNode != null)
            {
                parent.add(new DBTermCloneNode(node)) ;
            }
            // if not build the branch
            else
            {
                representatives.put(node.getOid(), node) ;
                buildDAG(cutoff, node, homePackage) ;
                parent.add(node);
            }
            this.updateProgress("Find top node (" + i + " of " + number + ")"
                + node.getLocalName()) ;
        }
    }

    Map<String, TypedNode> representatives = new HashMap<String,
        TypedNode>() ;

    public TypedNode makeDagFromTerm(String from_oid, int cutoff,
        PackageNode homePackage)
    {
        // String thePackage = homePackage.getLocalName();

        if(from_oid == null)
        {
            return null ;
        }

        DbTermNode node = (DbTermNode)representatives.get(from_oid) ;

        if(node == null)
        { // if the node is not found refore
            node = (DbTermNode)createNode(from_oid, homePackage) ;
            representatives.put(from_oid, node) ;

            buildDAG(cutoff, node, homePackage) ;
            return node ;
        }
        else
        { // if been found, make a clone
            return new DBTermCloneNode(node) ;
        }
    }

    public DbTermNode expandOneLevelQuick(String from_oid)
    {
        return null;
    }

    public void buildDAG(int cutoff, DbTermNode from, PackageNode homePackage)
    {
        from.getThisTerm().print() ;

        // if it's a null node
        if(from == null)
        {
            return ;
        }
        if(cutoff != 0)
        {
            boolean inHomePackageOnly = (homePackage != null) ;
            String oid = (String)from.getOid() ;
            String package_oid = inHomePackageOnly ? homePackage.getOid() : null ;
            Vector newNodes = topdown ? OntologyQuerier.getChildrenTerm(
                db, oid, relation, inHomePackageOnly, package_oid) :
                OntologyQuerier.getParentTerm(
                db, oid, relation, inHomePackageOnly, package_oid) ;
            System.out.println(oid + " -> " + newNodes) ;
            for(int i = 0 ; i < newNodes.size() ; i++)
            {
                String oneNode = (String)newNodes.elementAt(i) ;

                DbTermNode node = (DbTermNode)representatives.get(oneNode) ;

                if(node == null)
                { // if the node is not found refore

                    node = (DbTermNode)createNode(oneNode, homePackage) ;
                    representatives.put(oneNode, node) ;

                    from.add(node) ;
                    buildDAG(cutoff - 1, node, homePackage) ;
                }
                else
                { // if been found, make a clone
                    node = new DBTermCloneNode(node) ;
                    from.add(node) ;
                }

            }
        }
        return ;
    }

    /**
     * Create a set of terms in single sql query. Those term should be from the
     *    same package
     * @param sqlCondition String - must in the form that
     *     SELECT oid FROM  term WHERE....
     * @param homePackage PackageNode
     * @return Vector<DbTermNode>
     *
     * @author Jie Bao
     * @since 2005-08-30
     */
    public Vector<DbTermNode> batchCreateNode(String sqlCondition,
        PackageNode homePackage)
    {
        if(homePackage == null)
        {
            return null ;
        }
        Vector<DbTermNode> nodes = new Vector<DbTermNode>() ;
        Vector<DbTerm> terms = DbTerm.batchRead(db, sqlCondition) ;
        Map<String, Integer> counts = OntologyQuerier.getBatchNeighborCount
            (db, topdown, sqlCondition, relation, homePackage.getOid()) ;
        for(DbTerm thisTerm : terms)
        {
            DbTermNode newNode = new DbTermNode(thisTerm, homePackage) ;
            newNode.showPackageInformation = this.showPackageInformation ;
            Integer childCount = counts.get(thisTerm.oid) ;
            if(childCount == null)
            {
                newNode.hasMore = false ;
            }
            else
            {
                newNode.hasMore = (childCount > 0) ;
            }
            nodes.add(newNode);
        }

        return nodes ;
    }

    public DbTermNode createNode(String oid, PackageNode homePackage)
    {
        if(homePackage != null)
        {
            DbTermNode node = (DbTermNode)createNode(oid, homePackage.getOid()) ;
            node.setHomePackageNode(homePackage) ;
            return node ;
        }
        else
        { // make one [it's used to show the node in term@pkg form]
            DbTermNode node = (DbTermNode)createNode(oid, (String)null) ;
            // find the package
            String pkg_oid = OntologyQuerier.getHomePackage(db, oid) ;
            PackageNode hp = new PackageNode(DbPackage.read(db, pkg_oid)) ;
            hp.setReadOnly(false) ;
            node.setHomePackageNode(hp) ;
            return node ;
        }
        //System.out.println("Created a new node: "+  node.getThisTerm());
    }

    private DBTreeNode createNode(String oid, String package_oid)
    {
        try
        {
            DbTerm thisTerm = DbTerm.read(db, oid) ;
            DbTermNode newNode = new DbTermNode(thisTerm, null /*homePackage*/) ;
            newNode.showPackageInformation = this.showPackageInformation ;
            int childCount = OntologyQuerier.getNeighborTermCount
                (db, topdown, oid, relation, package_oid) ;
            newNode.hasMore = (childCount > 0) ;
            //System.out.println(newNode);

            return newNode ;
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
            return null ;
        }
    }
}
