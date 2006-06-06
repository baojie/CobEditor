package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.sql.DatabaseMetaData ;
import java.sql.PreparedStatement ;
import java.sql.SQLException ;
import java.util.Vector ;

import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.ato.gui.OntologyServerInfo ;
import edu.iastate.ato.po.naming.BasicNamingPolicy ;
import edu.iastate.ato.shared.AtoConstent ;

import edu.iastate.utils.Debug ;
import edu.iastate.utils.sql.JDBCUtils ;
import edu.iastate.utils.sql.LocalDBConnection ;

/**
 * Create an ontology server
 *
 * SQL statements are PostgreSQL extensions.
 *
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-20</p>
 */
public class OntologyServerBuilder
{
    // 2005-08-20
    // make sure connection the database with superuser(can create user)
    static public String createUser(Connection db, String user, String pass)
    {
        // CREATE USER user WITH PASSWORD 'pass';
        String sql = "CREATE USER " + user + " WITH PASSWORD '" + pass + "' CREATEDB CREATEUSER" ;
        return JDBCUtils.updateDatabaseM(db, sql) ;
    }

    //2005-08-20
    static public String deleteUser(Connection db, String user)
    {
        // DROP USER user
        String sql = "DROP USER " + user ;
        return JDBCUtils.updateDatabaseM(db, sql) ;
    }

    //2005-08-20
    static public String createDatabase(Connection db, String dbName,
        String owner)
    {
        //CREATE DATABASE sales OWNER salesapp
        String sql = "CREATE DATABASE " + dbName + " OWNER " + owner ;
        return JDBCUtils.updateDatabaseM(db, sql) ;
    }

    static public Connection createDatabase(Connection db, String dbName,
        String owner, String password) throws
        Exception
    {

        //CREATE DATABASE sales OWNER salesapp
        String sql = "CREATE DATABASE " + dbName + " OWNER " + owner ;

        boolean suc = true ;
        try
        {
            System.out.println("     " + sql) ;
            PreparedStatement updatest = db.prepareStatement(sql) ;
            updatest.executeUpdate() ;
            updatest.close() ;
        }
        catch(SQLException ex)
        {
            suc = false ;
            throw ex ;
        }

        if(suc)
        {
            LocalDBConnection ds = new LocalDBConnection() ;

            DatabaseMetaData dmt = db.getMetaData() ;
            ds.setDriver(AtoConstent.JDBC_DRIVER) ;

            String url = dmt.getURL() ;
            //jdbc:postgresql://boole.cs.iastate.edu/ato/
            //jdbc:postgresql://boole.cs.iastate.edu/ato
            //jdbc:postgresql://boole.cs.iastate.edu/
            // ....://.../ is the leading string
            int pos = url.indexOf("://") ;
            pos = url.indexOf("/", pos + 3) ;
            String host = url.substring(0, pos) ;
            ds.setUrl(host + "/" + dbName) ;
            ds.setUser(owner) ;
            ds.setPassword(password) ;
            System.out.println(ds) ;
            ds.connect() ;
            return ds.db ;
        }
        return null ;
    }

    /**
     * Clear all contents in the database and re-initialize it
     * @param db Connection
     * @param deleteAdmin boolean - if delete admin accounts
     * @return String
     * @since 2005-08-21
     */
    static public String clearDatabase(Connection db, boolean deleteAdmin)
    {
        // delete
        String sql = "DELETE FROM auto_id; " +
            "DELETE FROM details; " +
            "DELETE FROM package; " +
            "DELETE FROM pkg_relation; " +
            "DELETE FROM online; " +
            "DELETE FROM privilege; " +
            "DELETE FROM relation; " +
            "DELETE FROM schema; " +
            "DELETE FROM term; " +
            "DELETE FROM users " +
            (deleteAdmin ? ";" : " WHERE role <> '" + User.ADMIN + "';") +
            "DELETE FROM editing; " ;
        JDBCUtils.updateDatabaseM(db, sql) ;
        // re-initialize
        return initData(db) ;
    }

    //2005-08-20
    static public String deleteDatabase(Connection db, String dbName)
    {
        // DROP DATABASE dbName
        String sql = "DROP DATABASE " + dbName ;
        String message = JDBCUtils.updateDatabaseM(db, sql) ;
        return message ;
    }

    static public String createTables(Connection db, String user)
    {
        String sql =
            // table auto_id
            "CREATE TABLE auto_id\n" +
            "(\n" +
            "  id serial NOT NULL,\n" +
            "  term_oid varchar(255),\n" +
            "  CONSTRAINT auto_id_pkey PRIMARY KEY (id)\n" +
            ") \n" +
            "WITHOUT OIDS;\n" +
            //"ALTER TABLE auto_id OWNER TO $user$;\n" +
            "\n" +
            // table details
            "CREATE TABLE details\n" +
            "(\n" +
            "  term varchar(255),\n" +
            "  attribute varchar(255),\n" +
            "  value text,\n" +
            "  author varchar(255),\n" +
            "  modified varchar(32)\n" +
            ") \n" +
            "WITHOUT OIDS;\n" +
            //"ALTER TABLE details OWNER TO $user$;\n" +
            "COMMENT ON COLUMN details.term IS 'term oid';\n" +
            "\n" +
            // table editing
            "CREATE TABLE editing\n" +
            "(\n" +
            "  package varchar(256),\n" +
            "  usr varchar(256)\n" +
            ") \n" +
            "WITHOUT OIDS;\n" +
            //"ALTER TABLE editing OWNER TO $user$;\n" +
            //"GRANT ALL ON TABLE editing TO $user$;\n" +
            "COMMENT ON COLUMN editing.package IS 'package oid';\n" +
            "\n" +
            // table online, 2005-08-25
            "CREATE TABLE online\n" +
            "(\n" +
            "user_id varchar(256) NOT NULL,\n" +
            "host varchar(32) NOT NULL,\n" +
            "port varchar(8) NOT NULL,\n" +
            "login_time varchar(32) NOT NULL\n" +
            ") \n" +
            "WITHOUT OIDS;\n" +
            //"ALTER TABLE online OWNER TO $user$;\n" +
            "COMMENT ON TABLE online IS 'Who it online';\n" +
            // table package
            "CREATE TABLE package\n" +
            "(\n" +
            "  pid varchar(255) NOT NULL,\n" +
            "  \"comment\" varchar(256),\n" +
            "  author varchar(255),\n" +
            "  modified varchar(32),\n" +
            "  CONSTRAINT package_pkey PRIMARY KEY (pid)\n" +
            ") \n" +
            "WITH OIDS;\n" +
            //"ALTER TABLE package OWNER TO $user$;\n" +
            "\n" +
            // table pkg_relation
            "CREATE TABLE pkg_relation\n" +
            "(\n" +
            "  p1 varchar(255) NOT NULL,\n" +
            "  relation varchar(255) NOT NULL,\n" +
            "  p2 varchar(255) NOT NULL,\n" +
            "  author varchar(256),\n" +
            "  modified varchar(32),\n" +
            "  CONSTRAINT pkg_relation_pkey PRIMARY KEY (p1, relation, p2)\n" +
            ") \n" +
            "WITHOUT OIDS;\n" +
            //"ALTER TABLE pkg_relation OWNER TO $user$;\n" +
            "\n" +
            // table privilege
            "CREATE TABLE privilege\n" +
            "(\n" +
            "  package_oid varchar(32),\n" +
            "  user_id varchar(255),\n" +
            "  rights varchar(8)\n" +
            ") \n" +
            "WITH OIDS;\n" +
            //"ALTER TABLE privilege OWNER TO $user$;\n" +
            "\n" +
            // table relation
            "CREATE TABLE relation\n" +
            "(\n" +
            "  id varchar(255) NOT NULL,\n" +
            "  pid varchar(255) NOT NULL,\n" +
            "  relation varchar(255) NOT NULL,\n" +
            "  author varchar(256),\n" +
            "  modified varchar(32)\n" +
            ") \n" +
            "WITH OIDS;\n" +
            //"ALTER TABLE relation OWNER TO $user$;\n" +
            "COMMENT ON COLUMN relation.id IS 'the child term oid\n" +
            "';\n" +
            "COMMENT ON COLUMN relation.pid IS 'the parent term oid';\n" +
            "\n" +
            // table schema
            "CREATE TABLE \"schema\"\n" +
            "(\n" +
            "  \"type\" varchar(32) NOT NULL,\n" +
            "  name varchar(256) NOT NULL,\n" +
            "  CONSTRAINT schema_pkey PRIMARY KEY (\"type\", name)\n" +
            ") \n" +
            "WITHOUT OIDS;\n" +
            //"ALTER TABLE \"schema\" OWNER TO $user$;\n" +
            "\n" +
            "\n" +
            // table term
            "CREATE TABLE term\n" +
            "(\n" +
            "  id varchar(255) NOT NULL,\n" +
            "  name varchar(255),\n" +
            "  package varchar(255),\n" +
            "  slm varchar(10),\n" +
            "  author varchar(256),\n" +
            "  modified varchar,\n" +
            "  is_obsolete varchar(5) DEFAULT 'false'::character varying,\n" +
            "  CONSTRAINT term_pkey PRIMARY KEY (id)\n" +
            ") \n" +
            "WITH OIDS;\n" +
            //"ALTER TABLE term OWNER TO $user$;\n" +
            "\n" +
            // table users
            "CREATE TABLE users\n" +
            "(\n" +
            "  id varchar(255) NOT NULL,\n" +
            "  role varchar(32) NOT NULL,\n" +
            "  name varchar(255),\n" +
            "  institution varchar(255),\n" +
            "  email varchar(32) NOT NULL,\n" +
            "  pass varchar(32) NOT NULL,\n" +
            "  create_date varchar(32),\n" +
            "  CONSTRAINT user_pkey PRIMARY KEY (id)\n" +
            ") \n" +
            "WITHOUT OIDS;\n"
            //+"ALTER TABLE users OWNER TO $user$;\n"
            //+"GRANT ALL ON TABLE users TO $user$;"
            ;
        sql = sql.replaceAll("\\$user\\$", user) ;
        return JDBCUtils.updateDatabaseM(db, sql) ;
    }

    public static String createFunctions(Connection db, String user)
    {
        String sql =
            // function bj_sub_pkg
            "CREATE OR REPLACE FUNCTION bj_sub_pkg(\"varchar\", \"varchar\")\n" +
            "  RETURNS SETOF pkg_relation AS\n" +
            "$BODY$\n" +
            "\n" +
            "DECLARE \n" +
            "   _row      pkg_relation%ROWTYPE;\n" +
            "   _id       ALIAS FOR $1;\n" +
            "   _relation ALIAS FOR $2;\n" +
            "\n" +
            "BEGIN\n" +
            "\n" +
            "    SELECT INTO _row * FROM pkg_relation WHERE p2 = _id;\n" +
            "\n" +
            "    WHILE FOUND LOOP\n" +
            "        RETURN NEXT _row;\n" +
            "        SELECT INTO _row * FROM pkg_relation WHERE p2 = _row.p1 and relation = _relation;\n" +
            "    END LOOP;\n" +
            "    RETURN;\n" +
            "END\n" +
            "\n" +
            "$BODY$\n" +
            "  LANGUAGE 'plpgsql' VOLATILE;\n" +
            "ALTER FUNCTION bj_sub_pkg(\"varchar\", \"varchar\") OWNER TO $user$;\n" +
            "\n" +
            // function bj_sub_term
            "CREATE OR REPLACE FUNCTION bj_sub_term(\"varchar\", \"varchar\")\n" +
            "  RETURNS SETOF relation AS\n" +
            "$BODY$\n" +
            "\n" +
            "DECLARE \n" +
            "   _row      relation%ROWTYPE;\n" +
            "   _id       ALIAS FOR $1;\n" +
            "   _relation ALIAS FOR $2;\n" +
            "\n" +
            "BEGIN\n" +
            "\n" +
            "    SELECT INTO _row * FROM relation WHERE pid = _id;\n" +
            "\n" +
            "    WHILE FOUND LOOP\n" +
            "        RETURN NEXT _row;\n" +
            "        SELECT INTO _row * FROM relation \n" +
            "        WHERE pid = _row.id and relation = _relation;\n" +
            "    END LOOP;\n" +
            "    RETURN;\n" +
            "END\n" +
            "\n" +
            "$BODY$\n" +
            "  LANGUAGE 'plpgsql' VOLATILE;\n" +
            "ALTER FUNCTION bj_sub_term(\"varchar\", \"varchar\") OWNER TO $user$;\n" +
            "\n" +
            "\n" +
            // function bj_super_pkg
            "CREATE OR REPLACE FUNCTION bj_super_pkg(\"varchar\", \"varchar\")\n" +
            "  RETURNS SETOF pkg_relation AS\n" +
            "$BODY$\n" +
            "\n" +
            "DECLARE \n" +
            "   _row      pkg_relation%ROWTYPE;\n" +
            "   _id       ALIAS FOR $1;\n" +
            "   _relation ALIAS FOR $2;\n" +
            "\n" +
            "BEGIN\n" +
            "\n" +
            "    SELECT INTO _row * FROM pkg_relation WHERE p1 = _id;\n" +
            "\n" +
            "    WHILE  FOUND LOOP\n" +
            "        RETURN NEXT _row;\n" +
            "        SELECT INTO _row * FROM pkg_relation WHERE p1 = _row.p2 and relation = _relation;\n" +
            "    END LOOP;\n" +
            "    RETURN;\n" +
            "END\n" +
            "\n" +
            "$BODY$\n" +
            "  LANGUAGE 'plpgsql' VOLATILE;\n" +
            "ALTER FUNCTION bj_super_pkg(\"varchar\", \"varchar\") OWNER TO $user$;\n" +
            "\n" +
            // function bj_super_term
            "CREATE OR REPLACE FUNCTION bj_super_term(\"varchar\", \"varchar\")\n" +
            "  RETURNS SETOF relation AS\n" +
            "$BODY$\n" +
            "\n" +
            "DECLARE \n" +
            "   _row      relation%ROWTYPE;\n" +
            "   _id       ALIAS FOR $1;\n" +
            "   _relation ALIAS FOR $2;\n" +
            "\n" +
            "BEGIN\n" +
            "\n" +
            "    SELECT INTO _row * FROM relation WHERE id = _id;\n" +
            "\n" +
            "    WHILE FOUND LOOP\n" +
            "        RETURN NEXT _row;\n" +
            "        SELECT INTO _row * FROM relation \n" +
            "        WHERE id = _row.pid and relation = _relation;\n" +
            "    END LOOP;\n" +
            "    RETURN;\n" +
            "END\n" +
            "\n" +
            "$BODY$\n" +
            "  LANGUAGE 'plpgsql' VOLATILE;\n" +
            "ALTER FUNCTION bj_super_term(\"varchar\", \"varchar\") OWNER TO $user$;" ;
        sql = sql.replaceAll("\\$user\\$", user) ;
        return JDBCUtils.updateDatabaseM(db, sql) ;
    }

    public static String initData(Connection db)
    {
        String sql = "SELECT count(id) FROM users WHERE id = 'admin'" ;
        int adminCount = JDBCUtils.getCount(db, sql) ;

        sql = "INSERT INTO users (id, role, name, email, pass) " +
            "VALUES ('guest','guest','guest','null', 'guest'); \n" +
            "INSERT INTO package (pid) VALUES ('" + Package.GlobalPkg + "');\n" +
            "INSERT INTO schema (type, name) VALUES ('" +
            OntologySchema.NAMING_POLICY + "','" +
            BasicNamingPolicy.policyName + "');" +
            "INSERT INTO schema (type, name) VALUES ('" +
            OntologySchema.PARTIAL_ORDER + "','is_a');" ;
        if(adminCount == 0)
        {
            sql += "INSERT INTO users (id, role, name, email, pass) " +
                "VALUES ('admin','admin','admin','null', 'admin'); \n" ;
        }
        return JDBCUtils.updateDatabaseM(db, sql) ;
    }

    public static boolean checkIntegrity(Connection db)
    {
        Vector<String> allTables = JDBCUtils.getAllTable(db) ;
        return allTables.contains("auto_id") &&
            allTables.contains("details") &&
            allTables.contains("editing") &&
            allTables.contains("package") &&
            allTables.contains("pkg_relation") &&
            allTables.contains("privilege") &&
            allTables.contains("relation") &&
            allTables.contains("schema") &&
            allTables.contains("term") &&
            allTables.contains("users") ;
    }

    // for test
    public static void main(String[] args)
    {
        LocalDBConnection ds =
            MOEditor.getConnection(OntologyServerInfo.getAtoOntology()) ;
        ds.connect() ;

        try
        {
            //OntologyServerBuilder.createUser(ds.db, "testuser", "test");
            //OntologyServerBuilder.deleteUser(ds.db, "testuser");
            OntologyServerBuilder.deleteDatabase(ds.db, "test2") ;
            Connection db = OntologyServerBuilder.createDatabase
                (ds.db, "test2", "ato", "ato") ;
            if(db != null)
            {
                OntologyServerBuilder.createTables(db, "testuser") ;
                OntologyServerBuilder.createFunctions(db, "testuser") ;
                OntologyServerBuilder.initData(db) ;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace() ;
            Debug.trace(ex.getMessage()) ;
        }

        ds.disconnect() ;
    }
}
