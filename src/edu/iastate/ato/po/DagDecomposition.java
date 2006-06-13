package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.SQLException ;
import java.util.HashMap ;
import java.util.HashSet ;
import java.util.Map ;
import java.util.Set ;
import java.util.Vector ;

import edu.iastate.utils.lang.StopWatch ;
import edu.iastate.utils.sql.JDBCUtils ;

/**
 * Decompose a DAG with multiple relations(eg. both isa and part-of) into
 *    modules. The ontology is stored in two tables:
 *    term (oid, id ,....) // oid is the automatically assign number for a term
 *    relation (id, relation, pid,...) // id and pid are foreign key to term.oid
 *
 * <p>@author Jie Bao</p>
 * <p>@since 2005-09-02</p>
 */
public class DagDecomposition
{
    Connection db ;
    public DagDecomposition(Connection db)
    {
        this.db = db ;
    }

    public void decompose()
    {
        init() ;

        // make all isolated terms in one package
        makeIsolatedPackage() ;

        // group top nodes
        String sql = "UPDATE isa_term SET note = 'top' WHERE id IN " +
            "(SELECT distinct pid FROM is_a EXCEPT SELECT distinct id FROM is_a); " ;
        JDBCUtils.updateDatabase(db, sql) ;
        groupTop("top") ;

    }

    /**
     * groupTop
     */
    private void groupTop(String topLabel)
    {
        String sql = "DELETE FROM membership; " ;
        sql += "INSERT INTO membership (SELECT id, pid FROM is_a_tc WHERE pid" +
            " IN (SELECT id FROM isa_term WHERE note = '" + topLabel + "'));" ;
        JDBCUtils.updateDatabase(db, sql) ;

        sql = "SELECT term, branch FROM membership WHERE term IN( " +
            "SELECT term FROM membership GROUP BY term HAVING count(branch) >1) ORDER BY term" ;
        Vector<String[]> v = JDBCUtils.getValues(db, sql, 2) ;

        Map<String, Set> term2branch = new HashMap() ;

        for(String[] r : v)
        {
            String term = r[0] ; //term
            String branch = r[1] ; //branch
            Set<String> branches = term2branch.get(term) ;
            if(branches != null) // if the term is known
            {
                branches.add(branch) ;
            }
            else // if the term is unkown before
            {
                branches = new HashSet() ;
                branches.add(branch) ;
                term2branch.put(term, branches) ;
            }
        }

        Map<String, Set> branch2group = new HashMap() ;

        for(String term : term2branch.keySet())
        {
            Set<String> branches = term2branch.get(term) ;
            // merge them
            int i = 0 ;
            Set masterSet = null ;
            for(String b : branches)
            {
                if(i == 0) // the first one
                {
                    masterSet = getBranchGroup(b, branch2group) ;
                }
                else
                {
                    Set<String> ToKill = getBranchGroup(b, branch2group) ;
                    // merge ToKill to masterSet
                    if(ToKill != masterSet)
                    {
                        masterSet.addAll(ToKill) ;
                        // redirect members of TOKill to masterSet
                        for(String bb : ToKill)
                        {
                            branch2group.put(bb, masterSet) ;
                        }
                    }
                }
                i++ ;
            }
        }

        // wrote the isa_term table
        sql = "" ;
        Set<Set> processed = new HashSet() ;
        for(String branch : branch2group.keySet())
        {
            Set<String> group = branch2group.get(branch) ;
            if(!processed.contains(group))
            {
                int i = 0 ;
                String first = null ;
                for(String tt : group)
                {
                    if(i == 0)
                    {
                        first = tt ;
                    }

                    sql += "UPDATE isa_term SET package = '" + first +
                        "' WHERE id = '" + tt + "';\n" ;
                    i++ ;
                }

                processed.add(group) ;
            }
        }
        sql += "UPDATE isa_term SET package = id WHERE note = '" + topLabel +
            "' AND package IS NULL" ;
        JDBCUtils.updateDatabase(db, sql) ;

        // further decompose large groups
        // number of each group
        sql =
            "SELECT package, count(DISTINCT r.id) FROM isa_term t, is_a_tc r " +
            "WHERE r.pid = t.id AND note = '" + topLabel + "' " +
            "GROUP BY package ORDER BY count(DISTINCT r.id)" ;
        Vector<String[]> size = JDBCUtils.getValues(db, sql, 2) ;
        for(String item[] : size)
        {
            String pkg = item[0] ;
            int count = Integer.parseInt(item[1]) ;
            if(count > packageSizeLimit)
            {
                // decompose it
                // get top level nodes inside the package
                sql = "UPDATE isa_term SET note = '" + pkg + "' WHERE id IN" +
                    "(SELECT id FROM is_a WHERE pid IN " +
                    "(SELECT id FROM isa_term WHERE package = '" + pkg + "'))" ;
                JDBCUtils.updateDatabase(db, sql) ;
                groupTop(pkg);
            }
        }

    }

    Set getBranchGroup(String branch, Map<String, Set> branch2group)
    {
        Set s = branch2group.get(branch) ;
        if(s == null)
        {
            s = new HashSet() ;
            s.add(branch) ;
            branch2group.put(branch, s) ;
        }
        return s ;
    }

    /**
     * init
     */
    private void init()
    {
        String sql = "UPDATE isa_term SET note = null, package = null" ;
        JDBCUtils.updateDatabase(db, sql) ;
    }

    /**
     * Terms have no child or parent in any relation
     * makeIsolatedPackage
     */
    private void makeIsolatedPackage()
    {
        String sql =
            "UPDATE isa_term SET note = 'isolated' WHERE id NOT IN " +
            "(SELECT distinct id FROM is_a UNION " +
            " SELECT distinct pid FROM is_a)" ;
        JDBCUtils.updateDatabase(db, sql) ;
    }

    int packageSizeLimit = 1000 ;

    // test
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
            DagDecomposition dagdecomposition = new DagDecomposition(db) ;

            StopWatch w = new StopWatch() ;
            w.start() ;
            dagdecomposition.decompose() ;
            w.stop() ;
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
