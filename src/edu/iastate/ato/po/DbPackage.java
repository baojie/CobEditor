package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.sql.ResultSet ;
import java.sql.SQLException ;
import java.sql.Statement ;

import edu.iastate.ato.shared.AtoConstent ;

import edu.iastate.utils.sql.JDBCUtils ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-06-07</p>
 */
public class DbPackage extends Package implements AtoConstent
{
    public String oid, pid, comment, author, modified ;

    public DbPackage(String oid, String pid, String comment, String author,
        String modified)
    {
        this.oid = oid ;
        this.pid = pid ;
        this.comment = comment ;
        this.author = author ;
        this.modified = modified ;
    }

    public static DbPackage read(Connection db, String oid)
    {
        // e.g. SELECT * FROM package WHERE oid = '00000'
        String sql = "SELECT * FROM package WHERE oid = '" + oid + "'" ;
        try
        {
            System.out.println(sql) ;

            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            if(resultSet.next())
            {
                String pid = resultSet.getString("pid") ;
                String comment = resultSet.getString("comment") ;
                String author = resultSet.getString("author") ;
                String modified = resultSet.getString("modified") ;
                return new DbPackage(oid, pid, comment, author, modified) ;
            }
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
        }
        return null ;
    }

    public boolean write(Connection db, String user, boolean keepModified)
    {
        //String oid, pid, comment, author, modified;
        // if oid is null, it is a unsaved package, insert it
        if(oid == null)
        {
            /* e.g
                 INSERT INTO package (pid, comment, author, modified)
                 VALUES ('001', 'something', 'baojie', '1961-06-16');
             */
            this.author = user ;
            this.modified = OntologyEdit.getTime() ;
            String sql = "INSERT INTO package (pid, comment, author, modified)" +
                "VALUES (" + JDBCUtils.toDBString(pid) + ", " +
                JDBCUtils.toDBString(comment) + ", " +
                JDBCUtils.toDBString(author) + ", " +
                JDBCUtils.toDBString(modified) + ")" ;
            JDBCUtils.updateDatabase(db, sql) ;
            // get the oid for this package
            this.oid = OntologyQuerier.getPackageOid(db, this.pid) ;
            return(oid != null) ;

        }
        // if ois is not null, update it
        else
        {
            /* e.g. UPDATE package SET pid = '001', comment = 'something',
                        author = 'baojie', modified = '1961-06-16'
                    WHERE oid = '001_oid';
             */
            this.author = user ;
            if(!keepModified)
            {
                this.modified = OntologyEdit.getTime() ;
            }
            String sql = "UPDATE package SET pid = " + JDBCUtils.toDBString(pid) +
                ", comment = " + JDBCUtils.toDBString(comment) + ", author = " +
                JDBCUtils.toDBString(author) + ", modified = " +
                JDBCUtils.toDBString(modified) + " WHERE oid = " + oid ;
            return JDBCUtils.updateDatabase(db, sql) ;
        }
    }
}
