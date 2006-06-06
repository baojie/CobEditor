package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.sql.ResultSet ;
import java.sql.SQLException ;
import java.sql.Statement ;
import java.util.Vector ;

import edu.iastate.ato.shared.AtoConstent ;
import edu.iastate.ato.tree.PackageNode ;

import edu.iastate.utils.lang.SortedVector ;
import edu.iastate.utils.sql.JDBCUtils ;
import java.util.* ;

/**
 * To query ontology
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-17</p>
 */
public class OntologyQuerier
    implements AtoConstent
{
    // 2005-06-13 - the vector of oid
    public static Vector getAllPackages(Connection db)
    {
        String sql = "SELECT oid FROM " + packageTable ;
        Vector v = new Vector<String>() ;

        try
        {
            Statement stmt = db.createStatement() ;
            ResultSet rs = stmt.executeQuery(sql) ;
            while(rs.next())
            {
                v.add(rs.getString(1)) ;
            }
        }
        catch(SQLException ex)
        {
            return null ;
        }
        System.out.println(v) ;

        return v ;
    }

    /**
     * Get all terms in a package
     * @param pkg String
     * @return Vector
     */
    public static Vector<String> getAllTerms(Connection db,
        String package_oid)
    {
        String sql = "SELECT oid FROM " + termTable ;

        if(package_oid != null)
        {
            sql += " WHERE package = '" + package_oid + "' " ;
        }
        System.out.println(sql) ;
        Vector v = new Vector<String>() ;

        try
        {
            Statement stmt = db.createStatement() ;
            ResultSet rs = stmt.executeQuery(sql) ;
            while(rs.next())
            {
                v.add(rs.getString(1)) ;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace() ;
            return null ;
        }
        //System.out.println(v);

        return v ;
    }

    /**
     * Get child package
     * @param from_id String
     * @return Vector - of oids
     */
    public static Vector getChildrenPackage(Connection db, String from_id)
    {
        // e.g. SELECT p1 FROM pkg_relation WHERE p2 = '002' AND relation = 'nested_in'
        String sql = "SELECT p1 FROM pkg_relation WHERE p2 = '" + from_id +
            "' AND relation = '" + NESTED_IN + "' " ;
        return JDBCUtils.getValues(db, sql) ;
    }

    public static String getDirectParentPackage(Connection db, String from_oid)
    {
        // e.g. SELECT p2 FROM pkg_relation WHERE p1= '001' AND relation = 'nested_in'
        String sql = "SELECT p2 FROM pkg_relation WHERE p1 = '" + from_oid +
            "' AND relation = '" + NESTED_IN + "' " ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    // 2005-08-20
    public static Vector getAllParentPackage(Connection db, String from_oid)
    {
        // SELECT p2 FROM bj_super_pkg('from_oid', 'nest_in')
        String sql = "SELECT p2 FROM bj_super_pkg('" + from_oid +
            "', '" + AtoConstent.NESTED_IN + "')" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    // 2005-08-20
    public static Vector getAllChildrenPackage(Connection db, String from_oid)
    {
        // SELECT p1 FROM bj_sub_pkg('from_oid', 'nest_in')
        String sql = "SELECT p1 FROM bj_sub_pkg('" + from_oid +
            "', '" + AtoConstent.NESTED_IN + "')" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    // 2005-08-20
    public static Vector getAllParentTerm(Connection db, String from_oid,
        String relation)
    {
        // SELECT pid FROM bj_super_term('from_oid', 'relation')
        String sql = "SELECT pid FROM bj_super_term('" + from_oid +
            "', '" + relation + "')" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    // 2005-08-20
    public static Vector getAllChildTerm(Connection db, String from_oid,
        String relation)
    {
        // SELECT id FROM bj_sub_term('from_oid', 'relation')
        String sql = "SELECT id FROM bj_sub_term('" + from_oid +
            "', '" + relation + "')" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    public static Vector getParentTerm(Connection db, String from_id,
        String relation,
        boolean inHomePackageOnly,
        String hp_oid)
    {
        String relationTable = "relation" ;
        String childCol = "id" ;
        String parentCol = "pid" ;
        String relationCol = "relation" ;
        String relationType = relation ;
        try
        {
            String sql = "SELECT DISTINCT " + parentCol + " FROM " +
                relationTable +
                " WHERE " + childCol + "='" + from_id + "'" ;
            if(relationCol != null && relationType != null)
            {
                sql += " AND " + relationCol + " ='" + relationType + "'" ;
            }
            if(inHomePackageOnly)
            {
                sql += " AND pid IN (SELECT oid FROM term WHERE package = '" +
                    hp_oid + "')" ;
            }

            /*
                         SELECT DISTINCT pid FROM relation
                         WHERE id = '000'
                         AND relation = 'isa'
                         AND pid IN (SELECT oid FROM term WHERE package = 'p1')
             */

            System.out.println(sql) ;
            Statement stmt = db.createStatement() ;
            ResultSet rs = stmt.executeQuery(sql) ;
            SortedVector vec = new SortedVector() ;
            while(rs.next())
            {
                String str = rs.getString(parentCol) ;
                vec.add(str) ;
            }
            vec.sort() ;

            return vec ;
        }
        catch(SQLException ex)
        {
            ex.printStackTrace() ;
        }
        return null ;
    }

    /**
     * Count number of direct parents or children
     * @param db Connection
     * @param topdown boolean - true: count children, false, count parents
     * @param from_id String
     * @param relation String
     * @param homePackage_oid String
     * @return int
     *
     * @since 2005-08-23
     * @author Jie Bao
     */
    public static int getNeighborTermCount(Connection db, boolean topdown,
        String from_id, String relation, String homePackage_oid)
    {
        /*  SELECT DISTINCT count(id) FROM relation
            WHERE pid = '23331'
            AND relation = 'is_a'
            AND id IN (SELECT oid FROM term WHERE package = '50839')
         */
        String neighbor = topdown ? "id" : "pid" ;
        String me = topdown ? "pid" : "id" ;

        String sql = "SELECT DISTINCT count(" + neighbor + ") FROM relation " +
            " WHERE " + me + " = '" + from_id + "'" +
            " AND relation = '" + relation + "'" ;
        if(homePackage_oid != null)
        {
            sql += " AND " + neighbor + " IN (SELECT oid FROM term " +
                " WHERE package = '" + homePackage_oid + "')" ;
        }
        return JDBCUtils.getCount(db, sql) ;
    }

    // 2005-08-30
    // return: mapping term_oid -> neighbor count
    public static Map<String, Integer> getBatchNeighborCount(Connection db,
        boolean topdown, String sqlConditions, String relation,
        String homePackage_oid)
    {
        /*SELECT DISTINCT count(id) AS count, pid AS oid
         FROM relation
         WHERE relation = 'is_a' AND oid IN (....)
         [AND id IN (SELECT oid FROM term WHERE package = '50839')]
         GROUP BY pid
         */
        String neighbor = topdown ? "id" : "pid" ;
        String me = topdown ? "pid" : "id" ;

        String sql = "SELECT DISTINCT count(" + neighbor + ") AS count, " + me +
            " AS oid FROM relation " +
            " WHERE " + me + " IN (" + sqlConditions + ")" +
            " AND relation = '" + relation + "'" ;
        if(homePackage_oid != null)
        {
            sql += " AND " + neighbor + " IN (SELECT oid FROM term " +
                " WHERE package = '" + homePackage_oid + "')" ;
        }
        sql += " GROUP BY " + me ;

        try
        {
            System.out.println(sql) ;
            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            Map<String, Integer> m = new HashMap() ;

            // Get the number of rows from the result set
            while(resultSet.next())
            {
                int count = resultSet.getInt(1) ;
                String oid = resultSet.getString(2) ;
                m.put(oid, count) ;
            }
            return m ;

        }
        catch(SQLException e)
        {
            e.printStackTrace() ;
        }
        return null ;
    }

    public static Vector getChildrenTerm(Connection db, String from_id,
        String relation,
        boolean inHomePackageOnly,
        String homePackage_oid)
    {
        String relationTable = "relation" ;
        String childCol = "id" ;
        String parentCol = "pid" ;
        String relationCol = "relation" ;
        String relationType = relation ;

        try
        {
            String sql = "SELECT DISTINCT " + childCol + " FROM " +
                relationTable +
                " WHERE " + parentCol + "='" + from_id + "'" ;
            if(relationCol != null && relationType != null)
            {
                sql += " AND " + relationCol + " ='" + relationType + "'" ;
            }
            if(inHomePackageOnly)
            {
                sql += " AND id IN (SELECT oid FROM term WHERE package = '" +
                    homePackage_oid + "')" ;
            }

            /*  SELECT DISTINCT id FROM relation
                WHERE pid = '23331'
                AND relation = 'is_a'
                AND id IN (SELECT oid FROM term WHERE package = '50839')
             */

            System.out.println(sql) ;
            Statement stmt = db.createStatement() ;
            ResultSet rs = stmt.executeQuery(sql) ;
            SortedVector vec = new SortedVector() ;
            while(rs.next())
            {
                String str = rs.getString(childCol).trim() ;
                vec.add(str) ;
            }
            vec.sort() ;
            return vec ;
        }
        catch(SQLException ex)
        {
            ex.printStackTrace() ;
        }
        return null ;
    }

    public static String getDirectSubclassCount(Connection db,
        String term_oid,
        String relation)
    {
        //  *** wrong  e.g SELECT count(*) FROM relation WHERE pid = '000' AND relation = 'is_a' -
        //String sql = "SELECT count(*) FROM relation WHERE pid = '" +  term_oid + "' AND relation = '" + relation + "'";

        // their may be duplicated triples
        // SELECT count(*) FROM (SELECT DISTINCT id FROM relation WHERE pid = '59044' AND relation = 'is_a') AS foo
        String sql =
            "SELECT count(*) FROM (SELECT DISTINCT id FROM relation WHERE pid = '" +
            term_oid + "' AND relation = '" + relation + "') AS foo" ;

        return JDBCUtils.getFirstValue(db, sql) ;
    }

    public static String getDirectSuperclassCount(Connection db,
        String term_oid,
        String relation)
    {
        // e.g SELECT count(*) FROM relation WHERE id = '001' AND relation = 'is_a'
        //String sql = "SELECT count(*) FROM relation WHERE id = '" + term_oid + "' AND relation = '" + relation + "'";

        // their may be duplicated triples
        // SELECT count(*) FROM (SELECT DISTINCT pid FROM relation WHERE id = '59044' AND relation = 'is_a') AS foo
        String sql =
            "SELECT count(*) FROM (SELECT DISTINCT pid FROM relation WHERE id = '" +
            term_oid + "' AND relation = '" + relation + "') AS foo" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    // Jie Bao 2005-07-25
    public static String getTermAuthor(Connection db, String term_oid)
    {
        // e.g. SELECT author FROM term WHERE oid = '000'
        String sql = "SELECT author FROM term WHERE oid = '" + term_oid +
            "'" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    // Jie Bao 2005-07-25
    public static String getTermModifiedTime(Connection db, String term_oid)
    {
        // e.g. SELECT modified FROM term WHERE oid = '000'
        String sql = "SELECT modified FROM term WHERE oid = '" + term_oid +
            "'" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    public static String getHomePackage(Connection db, String term_oid)
    {
        // e.g.SELECT package FROM term WHERE oid = '000'
        String sql = "SELECT package FROM term WHERE oid = '" + term_oid +
            "'" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    // fina a father for the given term
    public static String getOneFather(Connection db, String term_oid,
        String relation, boolean inHomePackage)
    {

        String sql = "SELECT r.pid FROM relation r " +
            " WHERE r.id = '" + term_oid + "' " +
            " AND r.relation = '" + relation + "' " ;
        if(inHomePackage)
        {
            sql += " AND r.pid IN" +
                "(SELECT term.oid FROM term WHERE package IN " +
                "(SELECT package FROM term WHERE oid = '" + term_oid +
                "') )" ;

        }
        /* e.g.
         SELECT r.pid FROM relation r
         WHERE r.id = '001'
         AND r.relation = 'is_a'
         [AND pid IN
            (SELECT term.oid FROM term WHERE package IN
             (SELECT package FROM term WHERE oid = '001') )]

         */
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    /**
     * Get terms that have no parents or children with given relation
     * @param db Connection
     * @param relation String
     * @return Vector
     */
    public static Vector getIsolatedTerms(Connection db, String relation)
    {
        /* eg. SELECT oid FROM term WHERE is_obsolete = 'false' AND oid NOT IN
                    SELECT id FROM relation WHERE relation = 'is_a' AND
                      oid NOT IN
                    SELECT pid FROM relation WHERE relation = 'is_a'
         */
        String sql =
            "SELECT oid FROM term WHERE is_obsolete = 'false' AND oid NOT IN " +
            "  (SELECT id FROM relation WHERE relation = '" + relation +
            "') AND " + "  oid NOT IN " +
            " (SELECT pid FROM relation WHERE relation = '" + relation +
            "')" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    /**
     * Get all properties of a given term
     * @param db Connection
     * @param term_oid String
     * @return Vector
     * @since 2005-08-22
     */
    public static Vector<TagValuePair> getTermAllProperty(Connection db,
        String term_oid)
    {
        // eg. SELECT attribute, value FROM details WHERE term = '000'
        String sql = "SELECT attribute, value FROM details WHERE term = '" +
            term_oid + "'" ;
        Vector<String[]> v = JDBCUtils.getValues(db, sql, 2) ;
        Vector<TagValuePair> result = new Vector<TagValuePair>() ;
        for(String[] pair : v)
        {
            TagValuePair p = new TagValuePair(pair[0], pair[1]) ;
            result.add(p) ;
        }
        return result ;
    }

    /**
     * return the first value of a given property, given term
     * @param db Connection
     * @param term_oid String
     * @param property String
     * @return String
     */
    public static String getTermProperty(Connection db, String term_oid,
        String property)
    {
        String sql = "SELECT value FROM details " +
            "WHERE term = '" + term_oid + "' AND attribute = '" + property +
            "'" ;
        /* eg.
            SELECT value FROM details
            WHERE term = '000' AND attribute = 'ScaleUnit'
         */
        String value = JDBCUtils.getFirstValue(db, sql) ;
        return value ;
    }

    // 2005-07-31
    public static String getPackageOid(Connection db, String pid)
    {
        // e.g. SELECT oid FROM package WHERE pid = 'pid'
        String sql = "SELECT oid FROM package WHERE pid = '" + pid + "'" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

// 2005-07-31
    public static String getTermOid(Connection db, String id)
    {
        // e.g. SELECT oid FROM term WHERE id = 'id'
        String sql = "SELECT oid FROM term WHERE id = '" + id + "'" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    // 2005-08-15
    public static Vector getObsoleteTerm(Connection db, String pkg_oid)
    {
        // SELECT oid, id FROM term WHERE is_obsolete = 'true' [AND package = '50840']
        // ORDER BY id
        String sql = "SELECT oid, id FROM term WHERE is_obsolete = 'true' " ;
        if(pkg_oid != null)
        {
            sql += " AND package = '" + pkg_oid + "' " ;
        }
        sql += "ORDER BY id" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    // 2005-08-16
    public static int getObsoleteTermCount(Connection db,
        PackageNode homePackage)
    {
        // SELECT count(oid) FROM term WHERE is_obsolete = 'true' [AND package = '50840']
        String sql =
            "SELECT count(oid) FROM term WHERE is_obsolete = 'true' " ;
        if(homePackage != null)
        {
            String pkgOid = homePackage.getOid() ;
            sql += " AND package = '" + pkgOid + "' " ;
        }
        return JDBCUtils.getCount(db, sql) ;

    }

    public static Vector<String> getRootTerms(Connection db, String relation,
        boolean topdown, boolean includeIsolatedTerm,
        PackageNode homePackage)
    {
        String sql = getRootTermsSQL(db, relation, topdown, includeIsolatedTerm,
            homePackage) ;
        return JDBCUtils.getValues(db, sql) ;
    }

    // 2005-08-30
    public static String getRootTermsSQL(Connection db, String relation,
        boolean topdown, boolean includeIsolatedTerm, PackageNode homePackage)
    {
        String sql = "" ;
        if(topdown)
        {
            // find all top nodes (nodes that have no parent in this package)
            sql =
                "SELECT oid  FROM term WHERE is_obsolete = 'false' AND " ;
            if(homePackage != null)
            {
                String thePackage = homePackage.getOid() ;
                sql += "package = '" + thePackage + "' AND " ;
            }

            // those nodes that have no parent
            sql += "oid NOT IN (SELECT id FROM relation WHERE" +
                " relation.relation = '" + relation + "'" ;
            if(homePackage != null)
            {
                String thePackage = homePackage.getOid() ;
                sql +=
                    " AND pid IN (SELECT oid FROM term WHERE package = '" +
                    thePackage + "') " ;
            }
            sql += ") " ;

            if(!includeIsolatedTerm)
            {
                // those nodes that have no parent but have children
                sql += "AND oid IN (SELECT pid FROM relation" +
                    "  WHERE relation.relation = '" + relation + "')" ;
            }

            sql += " ORDER BY id" ;

            /* e.g.
                SELECT oid FROM term
                WHERE  is_obsolete = 'false' AND
                       [ package = '50840' AND ] oid NOT IN
                       (SELECT id FROM relation
                        WHERE relation.relation = 'is_a' [AND pid IN (SELECT oid FROM term WHERE package = 'p10')])
                       [ AND oid IN (SELECT pid FROM relation
                         WHERE relation.relation = 'is_a')]
                ORDER BY id
             */
        }
        else
        {
            // find all leaf nodes
            sql =
                "SELECT oid FROM term WHERE is_obsolete = 'false' AND " ;
            if(homePackage != null)
            {
                String thePackage = homePackage.getOid() ;
                sql += "package = '" + thePackage + "' AND  " ;
            }

            // those nodes that have no children
            sql += "oid NOT IN (SELECT pid FROM relation WHERE" +
                " relation.relation = '" + relation + "'" ;
            if(homePackage != null)
            {
                String thePackage = homePackage.getOid() ;
                sql += " AND id IN (SELECT id FROM term WHERE package = '" +
                    thePackage + "') " ;
            }
            sql += ") " ;

            if(!includeIsolatedTerm)
            {
                // those nodes that have no children but have parent
                sql += "AND oid IN (SELECT id FROM relation" +
                    "  WHERE relation.relation = '" + relation + "')" ;
            }
            sql += " ORDER BY id " ;
            /* e.g.
                SELECT oid FROM term
                WHERE is_obsolete = 'false' AND
                      package = 'p1' AND
                      oid NOT IN
                           (SELECT pid FROM relation
                            WHERE relation.relation = 'is_a')
                      [ AND oid IN (SELECT id FROM relation
                                     WHERE relation.relation = 'is_a')]
                ORDER BY id
             */
        }
        return sql ;
    }

    public static boolean isVisible(Connection db, String term_oid,
        String pkg_oid)
    {
        String sql = "SELECT slm FROM " + termTable + " WHERE oid = '" +
            term_oid + "'" ;
        String slm = JDBCUtils.getFirstValue(db, sql) ;
        sql = "SELECT package FROM " + termTable + " WHERE oid = '" +
            term_oid + "'" ;
        String hp_oid = JDBCUtils.getFirstValue(db, sql) ;

        // public term: visible from anywhere
        if(Package.PUBLIC.equals(slm))
        {
            return true ;
        }
        // private term: visible from home package
        else if(Package.PRIVATE.equals(slm))
        {
            return pkg_oid.equals(hp_oid) ;
        }
        // protected term: visible from nested-in packages
        else if(Package.PROTECTED.equals(slm))
        {
            return isNestedIn(db, hp_oid, pkg_oid) || pkg_oid.equals(hp_oid) ;
        }
        return false ;
    }

    public static boolean isNestedIn(Connection db, String pkg1_oid,
        String pkg2_oid)
    {
        String father = getDirectParentPackage(db, pkg1_oid) ;
        while(father != null)
        {
            if(father.equals(pkg2_oid))
            {
                return true ;
            }
            father = getDirectParentPackage(db, father) ;
        }
        return false ;
    }

// return a vector of package oid
    public static Vector getTopLevelPackage(Connection db)
    {
        String sql = "SELECT oid FROM package WHERE oid NOT IN ( " +
            " SELECT p1 FROM pkg_relation WHERE relation = '" +
            NESTED_IN + "' AND p2 IS NOT NULL);" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    /**
     *
     * @param db Connection
     * @param pkg_oid String
     * @since 2005-08-17
     */
    public static int getTermCount(Connection db, String pkg_oid)
    {
        // SELECT count(*) FROM term [WHERE package = pkg_oid]
        String sql = "SELECT count(*) FROM term" ;
        if(pkg_oid != null)
        {
            sql += " WHERE package = '" + pkg_oid + "'" ;
        }
        return JDBCUtils.getCount(db, sql) ;
    }

    public static boolean isPackageExist(Connection db, String oid)
    {
        return JDBCUtils.getCount(db, "package", "oid='" + oid + "'") > 0 ;
    }

    public static Vector getVisibleTerms(Connection db, String package_oid)
    {
        String sql = "SELECT oid FROM " + termTable + " WHERE slm = '" +
            Package.PUBLIC + "'" ;
        if(package_oid != null)
        {
            sql += " AND package = '" + package_oid + "' " ;
        }
        Vector v = new Vector<String>() ;

        try
        {
            Statement stmt = db.createStatement() ;
            ResultSet rs = stmt.executeQuery(sql) ;
            while(rs.next())
            {
                v.add(rs.getString(1)) ;
            }
        }
        catch(SQLException ex)
        {
            return null ;
        }
        System.out.println(v) ;

        return v ;
    }

    // 2005-08-26
    // get the email of the author of a given package
    public static String getAuthorEmail(Connection db, String package_oid)
    {
        // SELECT users.email FROM package, users WHERE package.author = users.id AND oid = '116419'
        String sql = "SELECT users.email FROM package, users " +
            "WHERE package.author = users.id AND oid = '" + package_oid +
            "' " ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }
}
