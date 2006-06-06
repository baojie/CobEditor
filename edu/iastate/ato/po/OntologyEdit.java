package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.text.SimpleDateFormat ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.Vector ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.shared.AtoConstent ;

import edu.iastate.utils.sql.JDBCUtils ;
import edu.iastate.utils.sql.LocalDBConnection ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-06-07</p>
 */
public class OntologyEdit
    implements AtoConstent
{
    public static void main(String[] args)
    {
        LocalDBConnection ds =
            MOEditor.getConnection(OntologyServerInfo.getAtoOntology()) ;
        ds.connect() ;

        OntologyEdit dmo = new OntologyEdit() ;
        //dmo.addPackage("p1");
        //dmo.addPackage("p2");
        //dmo.removePackage("p1");
        //dmo.addPackageRelation("p1", "import", "p2");
        //dmo.addPackageRelation("p2", "in", "p3");
        //dmo.removePackageRelation("p1", "import", "p2");
        //dmo.getAllPackages();
        //System.out.println(dmo.getTopLevelPackage());
    }

    public static boolean addTermProperty(Connection db, String term_oid,
        String property, String value, String user)
    {
        if(property == null || value == null)
        {
            return false ;
        }
        /* e.g.
         UPDATE details SET value = 'inch', author = 'user',  modified = '...'
              WHERE  term = '000' AND attribute = 'ScaleUnit' or

              INSERT INTO details ( term, attribute, value, author, modified)
              VALUES ('000', 'ScaleUnit' , 'inch', 'user', '....');
         */
        Map field_value = new HashMap<String, String>() ;
        field_value.put("term", term_oid) ;
        field_value.put("attribute", property) ;
        field_value.put("value", value) ;
        field_value.put("author", user) ;
        field_value.put("modified", getTime()) ;

        Vector pks = new Vector<String>() ;
        pks.add("term") ;
        pks.add("attribute") ;

        return JDBCUtils.insertOrUpdateDatabase(db, "details", field_value, pks) ;
    }

    final public static SimpleDateFormat dateFormat = new
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss z") ;

    public static String getTime()
    {
        return dateFormat.format(new Date()) ;
    }

    /**
     * Add a term
     * @param db Connection
     * @param id String
     * @param comment String
     * @param pkg String
     * @param slm String
     * @param author String
     * @return String - the oid, null if failed
     * @since 2005-07-24
     */
    public static String addTerm(Connection db, String id, String comment,
        String pkg, String slm, String is_obsolete, String author)
    {
        return addTerm(db, id, comment, pkg, slm, is_obsolete, author, getTime()) ;
    }

    public static String addTerm(Connection db, String id, String comment,
        String pkg, String slm, String is_obsolete, String author,
        String modified)
    {
        Map field_value = new HashMap() ;
        field_value.put("id", id) ;
        field_value.put("name", comment) ;
        field_value.put("slm", slm) ;
        field_value.put("is_obsolete", is_obsolete) ;
        field_value.put("package", pkg) ;
        field_value.put("author", author) ;
        field_value.put("modified", modified) ;
        JDBCUtils.insertOrUpdateDatabase(db, "term", field_value, "id") ;
        return OntologyQuerier.getTermOid(db, id) ;
    }

    /**
     * Add/update package to the database
     * @param db Connection
     * @param pid String
     * @param comment String
     * @param author String
     * @return boolean - package oid, null if failed
     * @since 2005-07-23
     */
    public static String addPackage(Connection db, String pid, String comment,
        String author)
    {
        return addPackage(db, pid, comment, author, getTime()) ;
    }

    /**
     * Import a package
     * @param db Connection
     * @param pid String
     * @param comment String
     * @param author String
     * @param modified String
     * @return String
     * @since 2005-08-24
     */
    public static String addPackage(Connection db, String pid, String comment,
        String author, String modified)
    {
        Map field_value = new HashMap() ;
        field_value.put("pid", pid) ;
        field_value.put("comment", comment) ;
        field_value.put("author", author) ;
        field_value.put("modified", modified) ;
        JDBCUtils.insertOrUpdateDatabase(db, "package", field_value, "pid") ;
        return OntologyQuerier.getPackageOid(db, pid) ;
    }

    /**
     * Add package relations
     * @param db Connection
     * @param p1 String
     * @param r String
     * @param p2 String
     * @param author String
     * @return boolean
     * @author Jie Bao
     * @since 2005-07-23
     */
    public static boolean addPackageRelation(Connection db, String p1_oid,
        String r, String p2_oid,
        String author)
    {
        Map m = new HashMap() ;
        m.put("p1", p1_oid) ;
        m.put("relation", r) ;
        m.put("p2", p2_oid) ;
        m.put("author", author) ;
        m.put("modified", getTime()) ;
        return JDBCUtils.insertOrDoNothing(db, pkgRelationTable, m,
            new String[]
            {"p1", "relation", "p2"}) ;
    }

    /**
     * Add a term relation
     * @param db Connection
     * @param id String
     * @param relation String
     * @param pid String
     * @param author String
     * @return boolean
     * @since 2005-07-24
     */
    public static boolean addTermRelation(Connection db, String id,
        String relation, String pid,
        String author)
    {
        Map m = new HashMap() ;
        m.put("id", id) ;
        m.put("relation", relation) ;
        m.put("pid", pid) ;
        m.put("author", author) ;
        m.put("modified", getTime()) ;

        // insert a new term relation; if that relation exists, do nothing
        return JDBCUtils.insertOrDoNothing(db, termRelationTable, m,
            new String[]
            {"id", "relation", "pid"}) ;
    }

    /**
     * Add package nest_in relation
     * @param db Connection
     * @param p1_oid String
     * @param p2_oid String
     * @param author String
     * @return boolean
     */
    public static boolean addPackageNesting(Connection db, String p1_oid,
        String p2_oid,
        String author)
    {
        Map m = new HashMap() ;
        m.put("p1", p1_oid) ;
        m.put("relation", NESTED_IN) ;
        m.put("p2", p2_oid) ;
        m.put("author", author) ;
        m.put("modified", getTime()) ;

        // insert the new nested_in relation, or update an existing one
        return JDBCUtils.insertOrUpdateDatabase(db, pkgRelationTable, m,
            new String[]
            {"p1", "relation"}) ;
    }

    /**
     * Remove a package relation from the ontology
     * @param db Connection
     * @param p1 String
     * @param r String
     * @param p2 String
     */
    public static void deletePackageRelation(Connection db, String p1_oid,
        String r, String p2_oid)
    {
        String sql = "DELETE FROM pkg_relation WHERE p1 = '" + p1_oid +
            "' AND p2 = '" + p2_oid + "' AND relation = '" + r + "';" ;
        JDBCUtils.updateDatabase(db, sql) ;
    }

    /**
     * Delete a package . the package must be empty !
     *
     * @param db Connection
     * @param pkg_oid String
     * @param deleteTerms boolean
     * @return boolean - if the package is deleted
     */
    public static boolean deletePackage(Connection db, String pkg_oid,
        String user)
    {
        // if the package is not empty, just do obsoleting
        if(OntologyQuerier.getTermCount(db, pkg_oid) > 0)
        {
            obsoletePackage(db, pkg_oid, user) ;
            return false ;
        }

        // remove the package
        String sql = "DELETE FROM " + packageTable + " WHERE oid = '"
            + pkg_oid + "';" ;
        JDBCUtils.updateDatabase(db, sql) ;

        // remove the package relations
        sql = "DELETE FROM " + pkgRelationTable + " WHERE p1 = '"
            + pkg_oid + "';" ;
        JDBCUtils.updateDatabase(db, sql) ;

        return true ;
    }

    // 2005-08-17
    public static boolean destroyTerm(Connection db, String term_oid)
    {
        // remove the term
        String sql = "DELETE FROM term WHERE oid = '" + term_oid + "';" ;
        // remove the term relations, if any
        sql += "DELETE FROM relation WHERE id = '" + term_oid + "' OR pid = '" +
            term_oid + "'" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    public static void obsoleteTerm(Connection db, String term_oid, String user)
    {
        // mark the term as obsolete
        // UPDATE term SET is_obsolete = 'true', author = user, modified = '...'
        // WHERE oid = '50010'
        String sql =
            "UPDATE term SET is_obsolete = 'true', author = '" + user +
            "', modified = '" + getTime() + "' " +
            "WHERE oid = '" + term_oid + "';" ;
        // remove the term relations
        sql += "DELETE FROM relation WHERE id = '" + term_oid + "' OR pid = '" +
            term_oid + "';" ;
        // remove term details
        sql += "DELETE FROM details WHERE term ='" + term_oid + "';" ;

        JDBCUtils.updateDatabase(db, sql) ;
    }

    /**
     * Obsolete all term in the package
     * @param db Connection
     * @param package_oid String
     * @since 2005-08-23
     */
    public static void obsoletePackage(Connection db, String package_oid,
        String user)
    {
        // UPDATE term SET is_obsolete = 'true', author = user, modified = '...'
        // WHERE is_obsolete <> 'true' AND package = 'package_oid');
        // DELETE FROM relation WHERE id IN
        //     (SELECT id FROM term WHERE package = 'package_oid');
        // DELETE FROM relation WHERE pid IN
        //     (SELECT id FROM term WHERE package = 'package_oid');
        // DELETE FROM details WHERE term IN
        //     (SELECT id FROM term WHERE package = 'package_oid');
        String localTerms = "(SELECT id FROM term WHERE package = '" +
            package_oid + "')" ;
        String sql = "UPDATE term SET is_obsolete = 'true', author = '" +
            user + "', modified = '" + getTime() + "' " +
            "WHERE is_obsolete <> 'true' AND package = '" + package_oid +
            "';\n" ;
        // remove the term relations
        sql += "DELETE FROM relation WHERE id IN " + localTerms + ";\n" ;
        sql += "DELETE FROM relation WHERE pid IN " + localTerms + ";\n" ;
        // remove term details
        sql += "DELETE FROM details WHERE term IN " + localTerms + ";" ;
        String msg = JDBCUtils.updateDatabaseM(db, sql) ;
        System.out.println(msg) ;
    }

    public static boolean deleteTermRelation(Connection db,
        String id, String parent,
        String relation)
    {
        // e.g. DELETE FROM relation WHERE id = '001' AND pid = '001'
        //              AND relation = 'is_a';
        String sql = "DELETE FROM relation WHERE id = '" + id +
            "' AND pid = '" + parent + "'" +
            " AND relation = '" + relation + "'" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    // move term form pkg1 to pkg2
    public static boolean moveTerm(Connection db, String term_oid, String pkg1,
        String pkg2)
    {
        // e.g. UPDATE term SET package = 'p2' WHERE oid = '000' AND
        //          package = 'p1'
        String sql = "UPDATE " + termTable +
            " SET package = '" + pkg2 + "' WHERE oid = '" + term_oid +
            "' AND package = '" + pkg1 + "'" + ";" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    // move a vector of terms from pkg1 to pkg2
    public static boolean moveTerm(Connection db, Vector<String> oids,
        String pkg1, String pkg2)
    {
        // e.g. UPDATE term SET package = 'p2' WHERE package = 'p1' AND
        //            id IN ('000','001')
        String sql = "UPDATE term SET package = '" + pkg2 +
            "' WHERE package = '" + pkg1 + "' AND id IN (" ;
        String ID = "" ;
        for(String term : oids)
        {
            ID += "'" + term + "'," ;
        }
        ID = ID.substring(0, ID.length() - 1) ; // remove the last ','

        sql = sql + ID + ")" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    /**
     * Merge the first term(killed) with the second(leave)
     * @param db Connection
     * @param oid_killed String
     * @param oid_leave String
     * @since 2005-08-14
     * @author Jie Bao
     */
    public static void mergeTerm(Connection db, String oid_killed,
        String oid_leave, String user)
    {
        // change the relation table

        // UPDATE relation SET id = oid_leave, author = 'user', modified = '2005-08-15' WHERE id = oid_killed
        String sql = " UPDATE relation SET id = '" + oid_leave +
            "' , author = '" +
            user + "', modified = '" + getTime() + "' WHERE id = '" +
            oid_killed + "'" ;
        JDBCUtils.updateDatabase(db, sql) ;
        // UPDATE relation SET pid = oid_leave, author = 'user', modified = '2005-08-15' WHERE pid = oid_killed
        sql = " UPDATE relation SET pid = '" + oid_leave + "' , author = '" +
            user + "', modified = '" + getTime() +
            "' WHERE pid = '" + oid_killed + "'" ;
        JDBCUtils.updateDatabase(db, sql) ;

        // remove "details" of the killed term
        // DELETE FROM details WHERE term =  oid_killed;
        sql = "DELETE FROM details WHERE term =  '" + oid_killed + "'" ;
        JDBCUtils.updateDatabase(db, sql) ;

        // remove the killed term
        // DELETE FROM term WHERE oid = oid_killed;
        sql = "DELETE FROM term WHERE oid = '" + oid_killed + "'" ;
        JDBCUtils.updateDatabase(db, sql) ;
    }

    /**
     * Apply an automatically assigned ID
     * @param db Connection
     * @param term_oid String
     * @return String
     */
    public static String applyID(Connection db, String term_oid)
    {
        // INSERT INTO auto_id (term_oid) VALUES ('term_oid')
        String sql = "INSERT INTO auto_id (term_oid) VALUES ('" + term_oid +
            "')" ;
        boolean suc = JDBCUtils.updateDatabase(db, sql) ;
        if(suc)
        {
            // SELECT id FROM auto_id WHERE term_oid = 'term_oid'
            sql = "SELECT id FROM auto_id WHERE term_oid = '" + term_oid + "'" ;
            return JDBCUtils.getFirstValue(db, sql) ;
        }
        return null ;
    }
}
