package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.sql.ResultSet ;
import java.sql.SQLException ;
import java.sql.Statement ;

import edu.iastate.ato.gui.MOEditor ;

import edu.iastate.utils.sql.JDBCUtils ;
import java.util.* ;

/**
 * @author Jie Bao
 * @since2005-07-31
 *
 CREATE TABLE term
 (
   id varchar(255) NOT NULL,
   name varchar(255),
   "domain" varchar(255),
   package varchar(255),
   slm varchar(10),
   author varchar(256),
   modified varchar,
   is_obsolete varchar(5) DEFAULT 'false'::character varying,
   CONSTRAINT term_pkey PRIMARY KEY (id)
 )
 WITH OIDS;
 */
public class DbTerm
{
    public String oid, id, name, package_oid, slm, author, modified ;
    public String is_obsolete = "false" ;
    public DbTerm(String oid, String id, String name, String package_oid,
        String slm, String author, String modified,
        String is_obsolete)
    {
        this.oid = oid ;
        this.id = id ;
        this.name = name ;
        this.package_oid = package_oid ;
        this.slm = slm ;
        this.is_obsolete = is_obsolete ;
        this.author = author ;
        this.modified = modified ;
    }

    public String toString()
    {
        String str = "oid = " + oid + ", id = " + id + ", name = " + name +
            ", package oid = " + package_oid + ", slm = " + slm +
            ", author = " + author + ", modified = " + modified +
            ", is_obsolete = " + is_obsolete ;
        return str ;
    }

    public void print()
    {
        System.out.println(toString()) ;
    }

    /**
     * read a set of terms
     * @param db Connection
     * @param sqlSelection String - must in the form that
     *     SELECT oid FROM  term WHERE....
     * @return Vector
     * @since 2005-08-30
     */
    public static Vector<DbTerm> batchRead(Connection db, String sqlSelection)
    {
        // e.g. SELECT * FROM term WHERE oid IN (SELECT oid FROM  term WHERE....)
        String sql =
            "SELECT oid, id, name, package, slm, author, modified, is_obsolete " +
            "FROM term WHERE oid IN (" + sqlSelection + ")" ;
        try
        {
            System.out.println(sql) ;

            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            Vector<DbTerm> allTerms = new Vector<DbTerm>() ;

            while(resultSet.next())
            {
                String oid = resultSet.getString("oid") ;
                String id = resultSet.getString("id") ;
                String name = resultSet.getString("name") ;
                String package_oid = resultSet.getString("package") ;
                String slm = resultSet.getString("slm") ;
                String author = resultSet.getString("author") ;
                String modified = resultSet.getString("modified") ;
                String obsolete = resultSet.getString("is_obsolete") ;
                DbTerm t = new DbTerm(oid, id, name, package_oid, slm, author,
                    modified, obsolete) ;
                allTerms.add(t) ;
            }
            return allTerms;
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
        }
        return null ;
    }

    public static DbTerm read(Connection db, String oid) throws Exception
    {
        // e.g. SELECT * FROM term WHERE oid = '00000'
        String sql = "SELECT * FROM term WHERE oid = '" + oid + "'" ;
        try
        {
            System.out.println(sql) ;

            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            if(resultSet.next())
            {
                String id = resultSet.getString("id") ;
                String name = resultSet.getString("name") ;
                String package_oid = resultSet.getString("package") ;
                String slm = resultSet.getString("slm") ;
                String author = resultSet.getString("author") ;
                String modified = resultSet.getString("modified") ;
                String obsolete = resultSet.getString("is_obsolete") ;
                return new DbTerm(oid, id, name, package_oid, slm, author,
                    modified, obsolete) ;
            }
            else
            {
                throw new Exception("No such term with oid " + oid) ;
            }
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
        }
        return null ;
    }

    public boolean write(Connection db)
    {
        //String oid, id, name, package_oid, slm, author, modified;
        // if oid is null, it is a unsaved term, insert it
        if(oid == null)
        {
            /* e.g
             INSERT INTO term (id, name, package, slm, author, modified, is_obsolete)
             VALUES ('001', 'something', 'ato', 'public','baojie', '1961-06-16', false);
             */
            this.author = MOEditor.user.name ;
            this.modified = OntologyEdit.getTime() ;
            String sql =
                "INSERT INTO term (id, name, package, slm, author, modified, is_obsolete)" +
                "VALUES (" + JDBCUtils.toDBString(id) + ", " +
                JDBCUtils.toDBString(name) + ", " +
                JDBCUtils.toDBString(package_oid) + ", " +
                JDBCUtils.toDBString(slm) + "," +
                JDBCUtils.toDBString(author) + ", " +
                JDBCUtils.toDBString(modified) + ", " +
                JDBCUtils.toDBString(is_obsolete) +
                ")" ;
            JDBCUtils.updateDatabase(db, sql) ;
            // get the oid for this package
            this.oid = OntologyQuerier.getTermOid(db, this.id) ;

            // make a new name according to naming policy
            String newID = MOEditor.theInstance.
                selectedNamingPolicy.makeNameWhenSaving(id) ;
            if(newID != null && !newID.equals(id))
            {
                id = newID ;
                // UPDATE term SET id = 'newID' WHERE oid = 'oid'
                sql = "UPDATE term SET id = '" + newID + "' WHERE oid = '" +
                    oid + "'" ;
                JDBCUtils.updateDatabase(db, sql) ;
            }

            return(oid != null) ;

        }
        // if oid is not null, update it
        else
        {
            /* e.g. UPDATE term SET id = '001', name = 'something',
                        package = 'ato', slm = 'public',
                        author = 'baojie', modified = '1961-06-16',
                        is_obsolete = 'false'
                    WHERE oid = '001_oid';
             */
            this.author = MOEditor.user.name ;
            this.modified = OntologyEdit.getTime() ;
            String sql = "UPDATE term SET id = " + JDBCUtils.toDBString(id) +
                ", name = " + JDBCUtils.toDBString(name) +
                ", package = " + JDBCUtils.toDBString(package_oid) +
                ", slm = " + JDBCUtils.toDBString(slm) +
                ", author = " + JDBCUtils.toDBString(author) +
                ", modified = " + JDBCUtils.toDBString(modified) +
                ", is_obsolete = " + JDBCUtils.toDBString(is_obsolete) +
                " WHERE oid = " + JDBCUtils.toDBString(oid) ;
            return JDBCUtils.updateDatabase(db, sql) ;
        }
    }
}
