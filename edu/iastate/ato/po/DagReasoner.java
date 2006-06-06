package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.SQLException ;
import java.util.* ;
import edu.iastate.utils.sql.JDBCUtils ;
import edu.iastate.utils.lang.* ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-09-04</p>
 */
public class DagReasoner
{
    Connection db ;
    public DagReasoner(Connection db)
    {
        this.db = db ;
    }

    public Set<String> getSub(String term_oid)
    {
        Set<String> offsprings = new TreeSet() ;

        // SELECT id FROM relation WHERE pid = 'term_oid'
        String sql = "SELECT id FROM relation WHERE pid = '" + term_oid + "'" ;

        Set<String> newFoundChildren = new TreeSet() ;
        newFoundChildren.addAll(JDBCUtils.getValues(db, sql)) ;

        while(newFoundChildren.size() > 0)
        {
            offsprings.addAll(newFoundChildren) ;

            String ss = newFoundChildren.toString() ; //[1, 2, 3]
            ss = ss.replaceAll(", ", "','") ;
            ss = ss.replaceAll("\\[", "('") ;
            ss = ss.replaceAll("\\]", "')") ;
            sql = "SELECT id FROM relation WHERE pid IN " + ss ;
            newFoundChildren = new TreeSet<String>() ;
            newFoundChildren.addAll(JDBCUtils.getValues(db, sql)) ;
            newFoundChildren.removeAll(offsprings) ; // remove all checked terms
        }
        return offsprings ;
    }

    public Vector<String> getSub2(String term_oid, String relation_table)
    {
        String sql = "DELETE FROM tempterm; " +
            "INSERT INTO tempterm (SELECT id FROM " + relation_table +
            " WHERE pid = '" + term_oid + "' )" ;
        JDBCUtils.updateDatabase(db, sql) ;

        int depth = 12 ;
        sql = "" ;

        for(int i = 0 ; i < depth ; i++)
        {
            sql += "INSERT INTO tempterm (SELECT id FROM " + relation_table +
                " WHERE pid IN (SELECT id FROM  tempterm)  EXCEPT SELECT id FROM tempterm);" ;
        }
        JDBCUtils.updateDatabase(db, sql) ;

        sql = "SELECT id FROM tempterm" ;
        return JDBCUtils.getValues(db, sql) ;
    }

    public Vector<String> getTop(String relation_table)
    {
        String sql = "SELECT DISTINCT pid FROM "+relation_table+
            " except SELECT distinct id FROM "+relation_table+"";
        return JDBCUtils.getValues(db, sql) ;
    }

    public static void main(String[] args)
    {
        String url = "jdbc:postgresql://boole.cs.iastate.edu/uuu" ;
        String user = "uuu" ;
        String password = "uuu" ;
        String driver = "org.postgresql.Driver" ;

        try
        {
            Class.forName(driver) ;
            Connection db = DriverManager.getConnection(url, user, password) ;
            DagReasoner dagreasoner = new DagReasoner(db) ;
            System.out.println("OK") ;

            StopWatch w = new StopWatch() ;
            w.start() ;
            Vector<String> offsprings = dagreasoner.getSub2("80641","is_a") ;
            w.stop() ;
            System.out.println(offsprings.size()) ;
            System.out.println(w.print()) ;

        }
        catch(SQLException ex)
        {
        }
        catch(ClassNotFoundException ex)
        {
        }

    }
}
