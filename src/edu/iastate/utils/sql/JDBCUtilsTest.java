package edu.iastate.utils.sql;

import java.sql.Connection;

/**
 * <p>@author Jie Bao , baojie@cs.iastate.edu</p>
 * <p>@since 2005-03-25</p>
 */
public class JDBCUtilsTest
{
    // 2003-03-25
    public static void testIsTableExist(Connection db)
    {
        //IndusBasis basis = new IndusBasis();
        //Connection db = basis.indusDataSourceDB.db;

        String sql = "DROP TABLE " + "NOTHING3252346562";
        //System.out.println(pgJDBCUtils.updateDatabase(db, sql));
        System.out.println(JDBCUtils.isTableExist(db, "NOTHING3252346562",true));
        System.out.println(JDBCUtils.isTableExist(db, "w1",true));
    }

    public static void testReplaceDatabaseDangerousCharacters()
    {
        String str = "haha \n\n i'm \' ( this is good)";
        System.out.println(str);
        str = JDBCUtils.replaceDangerousChar(str);
        System.out.println(str);
    }

    public static void main(String[] args)
    {
        testReplaceDatabaseDangerousCharacters();
    }
}
