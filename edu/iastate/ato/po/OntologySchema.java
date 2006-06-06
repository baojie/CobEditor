package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.Vector ;

import edu.iastate.ato.po.naming.NamingPolicy ;

import edu.iastate.utils.Utility ;
import edu.iastate.utils.sql.JDBCUtils ;

/**
 * To query and update ontology schema
 *
 CREATE TABLE "schema"
 (
  "type" varchar(32) NOT NULL,
  name varchar(256) NOT NULL,
  CONSTRAINT schema_pkey PRIMARY KEY ("type", name)
 )
 WITHOUT OIDS;

 * @author Jie Bao
 * @since 2005-08-18
 */
public class OntologySchema
{
    public static String PARTIAL_ORDER = "partial-order" ;
    public static String TERM_PROPERTY = "term-property" ;
    public static String NAMING_POLICY = "naming-policy" ;
    public static String PREFIX = "prefix" ;

    Vector<String> allPartialOrders = new Vector() ;
    Vector<String> allTermProperties = new Vector<String>() ;
    NamingPolicy namingPolicy ;
    String prefix ;

    // 2005-08-24
    public static String clear(Connection db, String type)
    {
        String sql = "DELETE FROM schema " ;
        if(type != null)
        {
            sql += "WHERE type = '" + type + "'" ;
        }
        return JDBCUtils.updateDatabaseM(db, sql) ;
    }

    public static Vector getPredicate(Connection db, String name)
    {
        String sql = "SELECT name FROM schema WHERE type = '" + name +
            "' ORDER BY name" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    public static Vector getPartialOrders(Connection db)
    {
        return getPredicate(db, PARTIAL_ORDER) ;
    }

    /**
     * Add partial order
     *
     * @param db Connection
     * @param relation String
     * @return boolean
     * @author Jie Bao
     * @since 2005-08-22
     */
    public static boolean addPartialOrders(Connection db, String relation)
    {
        Map field_value = new HashMap<String, String>() ;
        field_value.put("type", OntologySchema.PARTIAL_ORDER) ;
        field_value.put("name", relation) ;

        return JDBCUtils.insertOrDoNothing(db, "schema", field_value,
            new String[]
            {"type", "name"}) ;

    }

    // 2005-08-24
    public static boolean addPredicate(Connection db, String type, String name)
    {
        Map field_value = new HashMap<String, String>() ;
        field_value.put("type", type) ;
        field_value.put("name", name) ;

        return JDBCUtils.insertOrDoNothing(db, "schema", field_value,
            new String[]
            {"type", "name"}) ;
    }

    /**
     * Add many schema predicates in one SQL
     * @param db Connection
     * @param pairs Vector
     * @return boolean
     * @since 2005-08-31
     */
    public static boolean addPredicateBacth(Connection db,
        Vector<TagValuePair> pairs)
    {
        StringBuffer buf = new StringBuffer() ;
        for(TagValuePair p : pairs)
        {
            // ensure there is unique pair
            buf.append("DELETE FROM schema WHERE type = '" + p.tag +
                "' AND name = '" + p.value + "';\n") ;
            buf.append("INSERT INTO schema (type, name) VALUES ('" + p.tag +
                "','" + p.value + "');\n") ;
        }
        String sql = buf.toString() ;
        return JDBCUtils.updateDatabase(db,sql);
    }

    public static Vector<String> getTermProperties(Connection db)
    {
        return getPredicate(db, TERM_PROPERTY) ;
    }

    // 2005-08-18
    public static String getNamingPolicy(Connection db)
    {
        String sql = "SELECT name FROM schema WHERE type = '"
            + NAMING_POLICY + "'" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    // 2005-08-24
    public static String getPrefix(Connection db)
    {
        String sql = "SELECT name FROM schema WHERE type = '" + PREFIX + "'" ;
        return JDBCUtils.getFirstValue(db, sql) ;
    }

    // 2005-08-24
    public static void setPrefix(Connection db, String prefix)
    {
        Map field_value = new HashMap<String, String>() ;
        field_value.put("type", PREFIX) ;
        field_value.put("name", prefix) ;

        JDBCUtils.insertOrUpdateDatabase(db, "schema", field_value,
            new String[]
            {"type"}) ;
    }

    public static String obo_properties[] = new String[]
        {"alt_id", "namespace", "def",
        "def_xref", "comment", "subset",
        "synonym", "related_synonym", "exact_synonym", "broad_synonym",
        "narrow_synonym", "xref_analog", "xref_unknown", "use_term"} ;
    /**
     * Add OBO ontology schema into database
     * @since 2005-08-22
     */
    public static void initOBOSchema(Connection db)
    {
        String sql = "" ;
        for(int i = 0 ; i < obo_properties.length ; i++)
        {
            // PRIMARY KEY ("type", name) - avoid duplicate
            sql += "DELETE FROM schema WHERE type = '" + TERM_PROPERTY +
                "' AND name = '" + obo_properties[i] + "';\n" ;
            sql += "INSERT INTO schema (type, name) VALUES ( '" +
                TERM_PROPERTY + "', '" + obo_properties[i] + "');\n" ;
        }
        JDBCUtils.updateDatabase(db, sql) ;
    }

    static boolean isOBOProperty(String prop)
    {
        String[] sys =
            {"alt_id", "namespace", "def", "def_xref", "comment", "subset",
            "synonym", "related_synonym", "exact_synonym", "broad_synonym",
            "narrow_synonym", "xref_analog", "xref_unknown", "relationship",
            "is_obsolete", "use_term", "is_a"} ;
        return Utility.Array2Vector(sys).contains(prop) ;
    }

}
