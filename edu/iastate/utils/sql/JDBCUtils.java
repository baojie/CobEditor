package edu.iastate.utils.sql ;

import java.lang.reflect.Field ;
import java.sql.Connection ;
import java.sql.DatabaseMetaData ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;
import java.sql.ResultSetMetaData ;
import java.sql.SQLException ;
import java.sql.Statement ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.Map ;
import java.util.Vector ;

import edu.iastate.utils.Utility ;

/**
 * A set of JDBC utility operations
 *
 * @author Jie Bao
 * @since 1.0 2005-02-22
 */
public class JDBCUtils
{
    /**
     *
     * @param db Connection
     * @param tableName String
     * @param field_value Map
     * @return boolean
     */
    public static boolean insertDatabase(Connection db, String tableName,
        Map field_value)
    {
        String header = "INSERT INTO " + tableName + " (" ;
        String field = "", value = ") VALUES (" ;

        Iterator it = field_value.keySet().iterator() ;
        while(it.hasNext())
        {
            // Get key
            String key = (String)it.next() ;
            String val = (String)field_value.get(key) ;
            if(val == null)
            {
                System.err.println(key + " has null value!") ;
            }

            field += key + "," ;
            value += toDBString(val) + "," ;
        }
        //System.out.println(value);
        String sql = header + field.substring(0, field.length() - 1) +
            value.substring(0, value.length() - 1) + ") " ; // filter out the last ","
        return updateDatabase(db, sql) ;
    }

    public static boolean isTableExist(Connection db, String tableName,
        boolean caseSensitive)
    {

        try
        {
            Vector<String> allTable = getAllTable(db) ;

            if(caseSensitive)
            {
                return allTable.contains(tableName) ;
            }
            else
            {
                for(String str : allTable)
                {
                    if(str.equalsIgnoreCase(tableName))
                    {
                        return true ;
                    }
                }
                return false ;
            }
        }
        catch(Exception ex)
        {
            return false ;
        }

        /*           // Gets the database metadata
                   DatabaseMetaData dbmd = db.getMetaData();

                   ResultSet resultSet = dbmd.getTables(null, null,
                                                        tableName,
                                                        new String[]
                                                        {"TABLE"});

                   return resultSet.next();
         */

    }

    /**
     *
     * @param rs ResultSet
     * @param index int
     * @return String
     * @since 2005-03-06     */
    public static String getColumnNoNull(ResultSet rs, int index)
    {
        try
        {
            //the column value; if the value is SQL NULL, the value returned is null
            String col = rs.getString(index) ;
            if(col == null)
            {
                col = "" ;
            }
            return col.trim() ;
        }
        catch(SQLException ex)
        {
            return "" ;
        }
    }

    /**
     *
     * @param rs ResultSet
     * @param field String
     * @return String
     *
     * @since 2005-03-06
     */
    public static String getColumnNoNull(ResultSet rs, String field)
    {
        try
        {
            //the column value; if the value is SQL NULL, the value returned is null
            String col = rs.getString(field) ;
            if(col == null)
            {
                col = "" ;
            }
            return col.trim() ;
        }
        catch(SQLException ex)
        {
            return "" ;
        }
    }

    /**
     * Listing All Table Names in a Database
     * @param db Connection
     * @return Vector
     * @author Jie Bao
     * @since 2005-03-06
     */
    public static Vector getAllTable(Connection db)
    {
        try
        {
            // Gets the database metadata
            DatabaseMetaData dbmd = db.getMetaData() ;

            // Specify the type of object; in this case we want tables
            String[] types =
                {
                "TABLE"} ;
            ResultSet resultSet = dbmd.getTables(null, null, "%", types) ;

            // Get the table names
            Vector vec = new Vector() ;
            while(resultSet.next())
            {
                // Get the table name
                String tableName = resultSet.getString(3) ;
                vec.add(tableName) ;

                // Get the table's catalog and schema names (if any)
                //String tableCatalog = resultSet.getString(1);
                //String tableSchema = resultSet.getString(2);
            }
            return vec ;
        }
        catch(SQLException e)
        {
        }
        return null ;

    }

    /**
     * Delete all information in a table
     * @param db Connection
     * @param tableName String
     * @since 2005-02-24
     */
    public static boolean clearTable(Connection db, String tableName)
    {
        return updateDatabase(db, "DELETE FROM " + tableName) ;
    }

    public JDBCUtils()
    {
    }

    // insert, update, delete
    public static boolean updateDatabase(Connection db, String sql)
    {
        // add it into the database
        try
        {
            System.out.println("     " + sql) ;
            PreparedStatement updatest = db.prepareStatement(sql) ;
            updatest.executeUpdate() ;
            updatest.close() ;
            return true ;
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString()) ;
            ex.printStackTrace() ;
        }
        return false ;
    }

    /**
     * same as updateDatabase but return string message
     * @param db Connection
     * @param sql String  - OK if succeed, error message if failed
     * @throws Exception
     * @author Jie Bao
     * @since 2005-08-20
     */
    public static String updateDatabaseM(Connection db, String sql)
    {
        try
        {
            // add it into the database
            System.out.println("     " + sql) ;
            PreparedStatement updatest = db.prepareStatement(sql) ;
            updatest.executeUpdate() ;
            updatest.close() ;
        }
        catch(SQLException ex)
        {
            return ex.getMessage() ;
        }
        return OK ;
    }

    // 2005-08-20
    public static boolean isOK(String message)
    {
        if(message == null)
        {
            return false ;
        }
        else
        {
            return OK.equals(message) ;
        }
    }

    final public static String OK = "ok" ;

    /**
     * replace ( ) '
     * @param str String
     * @return String
     * @since 2005-02-24
     */
    public static String replaceDangerousChar(String str)
    {
        if(str == null)
        {
            return "" ;
        }

        String values = str ;
        //values = values.replaceAll("\\(", "[") ;
        //values = values.replaceAll("\\)", "]") ;
        //values = values.replaceAll("\\", "\\\\") ;
        values = values.replaceAll("'", "\\\\'") ;
        return values ;
    }

    public static String toDBString(String s)
    {
        if(s == null)
        {
            return "null" ;
        }
        else
        {
            return "'" + replaceDangerousChar(s) + "'" ;
        }
    }

    /**
     * Get value of the first column of the first result
     * @param db Connection
     * @param sql String
     * @return String - null if no value or exception
     *
     * @author Jie Bao
     * @since 2005-06-12
     */
    public static String getFirstValue(Connection db, String sql)
    {
        try
        {
            System.out.println(sql) ;

            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            if(resultSet.next())
            {
                String value = resultSet.getString(1) ;
                //System.out.println("    ==>" + value) ;
                return value ;
            }
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
        }
        return null ;
    }

    //2005-12-13
    // get multiple columns
    public static String[] getFirstValue(Connection db, String sql, int colNumber)
    {
        try
        {
            System.out.println(sql) ;

            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            if(resultSet.next())
            {
                String values[] = new String[colNumber];
                for(int i = 0 ; i < colNumber ; i++)
                {
                    values[i] = resultSet.getString(i + 1) ;
                }                //System.out.println("    ==>" + value) ;
                return values ;
            }
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
        }
        return null ;
    }

    /**
     * Query with multiple columns
     * @param db Connection
     * @param sql String
     * @param colNumber int
     * @return Vector
     * @author Jie Bao
     * @since 2005-08-22
     */
    public static Vector<String[]> getValues
        (Connection db, String sql, int colNumber)
    {
        try
        {
            System.out.println(sql) ;

            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            Vector v = new Vector() ;
            while(resultSet.next())
            {
                String values[] = new String[colNumber] ;
                for(int i = 0 ; i < colNumber ; i++)
                {
                    values[i] = resultSet.getString(i + 1) ;
                }
                v.add(values) ;
            }
            return v ;
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
            return null ;
        }
    }

    // get value of the first column in SELECT clause
    public static Vector getValues(Connection db, String sql)
    {
        try
        {
            System.out.println(sql) ;

            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            Vector v = new Vector() ;
            while(resultSet.next())
            {
                v.add(resultSet.getString(1)) ;
            }
            return v ;
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
            return null ;
        }
    }

    /**
     * Get count
     * @param db Connection
     * @param sql String  eg "SELECT COUNT(*) FROM my_table"
     * @return int -1 if error, 0 if not found
     * @author Jie Bao
     * @since 2005-02-22
     *        2005-08-30 : return 0 if no record found
     */
    public static int getCount(Connection db, String sql)
    {
        try
        {
            System.out.println(sql) ;
            // Select the number of rows in the table
            Statement stmt = db.createStatement() ;
            ResultSet resultSet = stmt.executeQuery(sql) ;

            // Get the number of rows from the result set
            if(resultSet.next())
            {
                int rowcount = resultSet.getInt(1) ;
                System.out.println("    ==>" + rowcount) ;
                return rowcount ;
            }
            else
            {
                return 0 ;
            }
        }
        catch(SQLException e)
        {
            System.err.println(sql) ;
            e.printStackTrace() ;
            return -1 ;
        }
    }

    /**
     * @param db Connection
     * @param tableName String
     * @param where String
     * @return int
     * @since 2005-02-22
     */
    public static int getCount(Connection db, String tableName, String where)
    {

        String sql = "SELECT COUNT(*) FROM " + tableName ;
        if(where != null && where.length() > 0)
        {
            sql = sql + " WHERE " + where + ";" ;
        }
        System.out.println(sql) ;
        return getCount(db, sql) ;
    }

    public static void printQeury(Connection db, String sql)
    {
        try
        {
            // Create a result set
            Statement stmt = db.createStatement() ;
            ResultSet rs = stmt.executeQuery(sql) ;

            // Get result set meta data
            ResultSetMetaData rsmd = rs.getMetaData() ;
            int numColumns = rsmd.getColumnCount() ;

            // Get the column names; column indices start from 1
            String tables = "\t", columns = "\t" ;
            for(int i = 1 ; i < numColumns + 1 ; i++)
            {
                String columnName = rsmd.getColumnName(i) ;
                columns += columnName + " |\t" ;

                // Get the name of the column's table name
                //String tableName = rsmd.getTableName(i);
                //tables += tableName + " |\t";
            }

            //System.out.println(tables);
            System.out.println(columns) ;

            // Fetch each row from the result set
            int count = 1 ;
            while(rs.next())
            {
                // Get the data from the row using the column index
                String row = (count++) + "\t" ;
                for(int i = 1 ; i < numColumns + 1 ; i++)
                {
                    String s = rs.getString(i).trim() ;
                    row += s + " |\t" ;
                }
                System.out.println(row) ;
            }

        }
        catch(SQLException e)
        {
            e.printStackTrace() ;
        }
    }

    /**
     * Insert a new record
     * @param tableName String
     * @param fields String[]
     * @param values String[]
     * @since 2005-02-20
     */
    public static boolean insertDatabase(Connection db, String tableName,
        String[] fields,
        String[] values)
    {
        String sql = "INSERT INTO " + tableName + " (" ;

        for(int i = 0 ; i < fields.length - 1 ; i++)
        {
            sql = sql + fields[i] + " , " ;
        }
        sql = sql + fields[fields.length - 1] + ") VALUES (" ;

        for(int j = 0 ; j < values.length - 1 ; j++)
        {
            sql = sql + toDBString(values[j]) + " , " ;
        }
        sql = sql + toDBString(values[values.length - 1]) + ") " ;

        return updateDatabase(db, sql) ;
    }

    /**
     * Update a record. Map contains the key-value pair
     * @param db Connection
     * @param tableName String
     * @param field_value Map
     */
    public static boolean updateDatabase(Connection db, String tableName,
        Map field_value, String where)
    {
        //eg UPDATE weather
        //   SET temp_lo = temp_lo+1, temp_hi = temp_lo+15, prcp = DEFAULT
        //   WHERE city = 'San Francisco' AND date = '2003-07-03';

        String sql = "UPDATE " + tableName + " SET " ;

        Iterator it = field_value.keySet().iterator() ;
        while(it.hasNext())
        {
            // Get key
            String key = (String)it.next() ;
            String val = (String)field_value.get(key) ;
            sql = sql + key + " = " + toDBString(val) + "," ;
        }
        sql = sql.substring(0, sql.length() - 1) ; // filter out the last ","
        if(where != null)
        {
            sql = sql + " WHERE " + where ;
        }
        return updateDatabase(db, sql) ;

    }

    public static boolean updateDatabase(Connection db, String tableName,
        String[] fields,
        String[] values, String where)
    {
        if(fields.length != values.length)
        {
            return false ;
        }

        //eg UPDATE weather
        //   SET temp_lo = temp_lo+1, temp_hi = temp_lo+15, prcp = DEFAULT
        //   WHERE city = 'San Francisco' AND date = '2003-07-03';

        String sql = "UPDATE " + tableName + " SET " ;

        for(int i = 0 ; i < fields.length - 1 ; i++)
        {
            sql = sql + fields[i] + " = " + toDBString(values[i]) + ", " ;
        }
        sql = sql + fields[fields.length - 1] + " = " +
            toDBString(values[fields.length - 1]) ;
        if(where != null)
        {
            sql = sql + " WHERE " + where ;
        }
        return updateDatabase(db, sql) ;
    }

    /**
     * Insert a record. If the record already exists in the database(duplicated
     *     PK), update the record
     * Note: Pk should be one of the key in the map(hashtable)
     * @param db Connection
     * @param tableName String
     * @param field_value Map
     * @param Pk String
     * @return boolean
     * @author Jie Bao
     * @since 2005-03-03
     */
    public static boolean insertOrUpdateDatabase(Connection db,
        String tableName,
        Map field_value, String Pk)
    {
        // query it
        String pkValue = (String)field_value.get(Pk) ;
        //Debug.trace(pkValue);

        if(pkValue != null)
        {
            String where = Pk + " = " + toDBString(pkValue) ;
            int count = getCount(db, tableName, where) ;
            //System.out.println(count);
            if(count == 0) // insert
            {
                return insertDatabase(db, tableName, field_value) ;
            }
            else if(count > 0) // exists, update
            {
                return updateDatabase(db, tableName, field_value, where) ;
            }
        }
        return false ;
    }

    /**
     * Insert a record. If the record already exists in the database(duplicated
     *     PK), update the record
     * Note: Pk should be one or more keys in the map(hashtable)
     *
     * @param db Connection
     * @param tableName String
     * @param field_value Map
     * @param Pks Vector - the set of primary keys
     * @return boolean
     * @author Jie Bao
     * @since 2005-04-22
     */
    public static boolean insertOrUpdateDatabase(Connection db,
        String tableName, Map field_value, Vector<String> Pks)
    {
        // query it
        if(Pks == null)
        {
            System.out.println("JDBCUtils:insertOrUpdateDatabase() " +
                "-  no primary key is provided") ;
            return false ;
        }

        String where = "" ; //= Pk + " = " + toDBString(pkValue);
        for(String pk : Pks)
        {
            String pkValue = (String)field_value.get(pk) ;
            if(pkValue == null)
            {
                System.out.println("JDBCUtils:insertOrUpdateDatabase() " +
                    "-  insert with null primary key") ;
                return false ;
            }
            where = where + pk + "=" + toDBString(pkValue) + " AND " ;
        }
        where = where.substring(0, where.length() - 5) ; // remove the last AND

        int count = getCount(db, tableName, where) ;
        //System.out.println(count);
        if(count <= 0) // insert
        {
            return insertDatabase(db, tableName, field_value) ;
        }
        else // exists, update
        {
            return updateDatabase(db, tableName, field_value, where) ;
        }
    }

    /**
     *
     * @param db Connection
     * @param tableName String
     * @param field_value Map
     * @return boolean
     * @since 2005-07-23
     */
    public static boolean delete(Connection db,
        String tableName, Map<String, String> field_value)
    {
        String header = "DELETE FROM " + tableName + " " ;

        String where = " WHERE " ; //= Pk + " = " + toDBString(pkValue);
        for(String pk : field_value.keySet())
        {
            String pkValue = (String)field_value.get(pk) ;
            if(pkValue == null)
            {
                System.out.println(
                    "JDBCUtils:delete() -  insert with null primary key") ;
                return false ;
            }
            where = where + pk + "=" + toDBString(pkValue) + " AND " ;
        }
        where = where.substring(0, where.length() - " AND ".length()) ; // remove the last AND
        String sql = header + where ;
        return updateDatabase(db, sql) ;

    }

    /**
     *
     * @param db Connection
     * @param tableName String
     * @param field_value Map
     * @param Pks Vector
     * @return boolean
     * @since 2005-07-23
     */
    public static boolean insertOrDoNothing(Connection db,
        String tableName,   Map field_value,   Vector<String> Pks)
    {
        // query it
        if(Pks == null)
        {
            System.err.println(
                "JDBCUtils:insertOrUpdateDatabase() -  no primary key is provided") ;
            return false ;
        }

        String where = "" ; //= Pk + " = " + toDBString(pkValue);
        for(String pk : Pks)
        {
            String pkValue = (String)field_value.get(pk) ;
            if(pkValue == null)
            {
                System.err.println(
                    "JDBCUtils:insertOrDoNothing() -  insert with null primary key") ;
                return false ;
            }
            where = where + pk + "=" + toDBString(pkValue) + " AND " ;
        }
        where = where.substring(0, where.length() - 5) ; // remove the last AND

        int count = getCount(db, tableName, where) ;
        //System.out.println(count);
        if(count <= 0) // insert
        {
            return insertDatabase(db, tableName, field_value) ;
        }
        else // exists, do nothing (do not modify)
        {
            return true ;
        }
    }

    public static boolean insertOrDoNothing(Connection db,
        String tableName,   Map field_value,  String[] pks)
    {
        return insertOrDoNothing(db, tableName, field_value,
            Utility.Array2Vector(pks)) ;
    }

    /**
     *
     * @param db Connection
     * @param tableName String
     * @param field_value Map
     * @param pks String[]
     * @return boolean
     *
     * @since 2005-07-23
     * @author Jie Bao
     */
    public static boolean insertOrUpdateDatabase(Connection db,
        String tableName,  Map field_value,  String[] pks)
    {
        return insertOrUpdateDatabase(db, tableName, field_value,
            Utility.Array2Vector(pks)) ;
    }

// This method returns the name of a JDBC type.
// Returns null if jdbcType is not recognized.
// @since 2005-03-27
    public static String getJdbcTypeName(int jdbcType)
    {
        // Use reflection to populate a map of int values to names
        if(jdbcTypeName == null)
        {
            jdbcTypeName = new HashMap() ;

            // Get all field in java.sql.Types
            Field[] fields = java.sql.Types.class.getFields() ;
            for(int i = 0 ; i < fields.length ; i++)
            {
                try
                {
                    // Get field name
                    String name = fields[i].getName() ;

                    // Get field value
                    Integer value = (Integer)fields[i].get(null) ;

                    // Add to map
                    jdbcTypeName.put(value, name) ;
                }
                catch(IllegalAccessException e)
                {
                }
            }
        }

        // Return the JDBC type name
        return(String)jdbcTypeName.get(new Integer(jdbcType)) ;
    }

    static Map jdbcTypeName ;

}
