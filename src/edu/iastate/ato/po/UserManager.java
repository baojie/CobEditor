package edu.iastate.ato.po ;

import java.sql.Connection ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.Vector ;

import edu.iastate.utils.sql.JDBCUtils ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-07-20 </p>
 *
 CREATE TABLE users
 (
   id varchar(255) NOT NULL,
   role varchar(32),
   name varchar(255),
   institution varchar(255),
   email varchar(32),
   pass varchar(32),
   create_date varchar(32),
   CONSTRAINT user_pkey PRIMARY KEY (id)
 )

 CREATE TABLE privilege
 (
  package_oid varchar(32),
  user_id varchar(255),
  rights varchar(8)
 )

 CREATE TABLE online
 (
  user_id varchar(256) NOT NULL,
  host varchar(32) NOT NULL,
  port varchar(8) NOT NULL,
  login_time varchar(32) NOT NULL
 )
 WITHOUT OIDS;


 */
public class UserManager
{
    static public boolean rightUser(Connection db, String user, String pass)
    {
        String sql = "SELECT pass FROM users WHERE id = '" + user + "'" ;
        String pass_db = JDBCUtils.getFirstValue(db, sql) ;
        if(pass == null)
        {
            return(pass_db == null) ;
        }
        else
        {
            return pass.equals(pass_db) ;
        }
    }

    static public String getEditor(Connection db, String pkg_oid)
    {
        String sql = "SELECT usr FROM editing WHERE package = '" + pkg_oid +
            "'" ;
        String editor = JDBCUtils.getFirstValue(db, sql) ;
        return editor ;
    }

    static public boolean beginEditing(Connection db, String pkg_oid,
        String user)
    {

        Map<String, String> fields = new HashMap() ;
        fields.put("package", pkg_oid) ;
        fields.put("usr", user) ;

        return JDBCUtils.insertOrUpdateDatabase(db, "editing", fields,
            "package") ;
    }

    static public boolean cancelEditing(Connection db, String pkg_oid,
        String user)
    {
        //DELETE FROM editing WHERE usr = 'baojie' AND package = 'p1'
        String sql = "DELETE FROM editing WHERE usr = '" + user +
            "' AND package = '" + pkg_oid + "'" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    static public boolean cancelAllEditing(Connection db, String user)
    {
        //DELETE FROM editing WHERE usr = 'baojie'
        String sql = "DELETE FROM editing WHERE usr = '" + user + "'" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    // 2005-07-25 get the packages edited by the user
    // inOid - true: in oid, false : in package name(pid)
    static public Vector getEditingPackages(Connection db, String user,
        boolean inOid)
    {
        String sql ;
        if(inOid)
        {
            // SELECT package FROM editing WHERE usr = 'baojie'
            sql = "SELECT package FROM editing WHERE usr = '" + user + "'" ;
        }
        else
        {
            // SELECT pid FROM package, editing WHERE editing.usr = 'baojie' AND
            //        editing.package = package.oid
            sql = "SELECT pid FROM package, editing WHERE editing.usr = '"
                + user + "' AND editing.package = package.oid" ;
        }
        return JDBCUtils.getValues(db, sql) ;
    }

    /**
     * If the given user id exists on the ontology server
     * @param db Connection
     * @param user String
     * @return boolean
     *
     * @author Jie Bao
     * @since 2005-08-19
     */
    static public boolean ifUserExist(Connection db, String user)
    {
        // SELECT id FROM users WHERE id = 'user'
        String sql = "SELECT id FROM users WHERE id = '" + user + "'" ;
        String id = JDBCUtils.getFirstValue(db, sql) ;
        return(id != null) ;
    }

    /**
     * Apply a user name with given ontology connection
     * @param db Connection
     * @param user String
     * @param password String
     * @param email String
     * @param name String
     * @param institution String
     * @return boolean - if successful, return true, otherwise false
     *
     * @author Jie Bao
     * @since 2005-08-19
     */
    static public boolean applyForID(Connection db, String user,
        String password,
        String email, String name,
        String institution, String role)
    {
        //INSERT INTO users(id, name, institution, email, pass, create_date, role)
        //    VALUES('user','name','institution', 'email','password', '2005-08-13', 'role');
        String date = OntologyEdit.getTime() ;
        String sql =
            "INSERT INTO users(id, name, institution, email, pass, create_date, role) " +
            "VALUES('" + user + "','" + name + "','" + institution + "', '" +
            email + "','" + password + "','" + date + "' , '" + role + "')" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    /**
     * Query is a user has write privilege to a package
     *
     * @param db Connection
     * @param user_id String
     * @param pkg_oid String
     * @return boolean
     * @since 2005-08-20
     */
    static public boolean hasWritePrivilege(Connection db, String user_id,
        String pkg_oid)
    {
        // get role
        // SELECT role FROM users WHERE id = 'user_id'
        String sql = "SELECT role FROM users WHERE id = '" + user_id + "'" ;
        String role = JDBCUtils.getFirstValue(db, sql) ;

        if(User.GUEST.equals(role))
        {
            return false ;
        }
        else if(User.ADMIN.equals(role))
        {
            return true ;
        }
        else
        {
            // query the privilege table
            // SELECT packge_oid FROM privilege WHERE (rights = 'w' OR rights = 'rw') AND user_id = 'user_id'
            sql = "SELECT package_oid FROM privilege WHERE (rights = 'w' OR rights = 'rw') AND user_id = '" +
                user_id + "'" ;
            Vector allEditablePkg = JDBCUtils.getValues(db, sql) ;

            // if any super packages of pkg is editable by user_id return true
            Vector allSuperPkg =
                OntologyQuerier.getAllParentPackage(db, pkg_oid) ;
            allSuperPkg.add(pkg_oid) ;

            // if the intesection of them is not empty, return true;
            allSuperPkg.retainAll(allEditablePkg) ;
            return!allSuperPkg.isEmpty() ;
        }
    }

    // 2005-08-20
    static public boolean addPrivilege(Connection db, String user_id,
        String pkg_oid, String rights)
    {
        // INSERT INTO privilege (user_id, package_oid, rights)
        //     VALUES ('user_id', 'packge_oid', 'rights')
        String table = "privilege" ;
        Map pairs = new HashMap<String, String>() ;

        pairs.put("user_id", user_id) ;
        pairs.put("package_oid", pkg_oid) ;
        pairs.put("rights", rights) ;

        return JDBCUtils.insertOrUpdateDatabase(db, table, pairs, new String[]
            {"user_id", "package_oid"}) ;
    }

    static public boolean deletePrivilege(Connection db, String user_id,
        String pkg_oid, String rights)
    {
        String table = "privilege" ;
        Map pairs = new HashMap<String, String>() ;

        pairs.put("user_id", user_id) ;
        pairs.put("package_oid", pkg_oid) ;
        pairs.put("rights", rights) ;

        return JDBCUtils.delete(db, table, pairs) ;

    }

    // 2005-08-25
    static public boolean login(Connection db, String user_id, String host,
        String port)
    {
        // INSERT INTO online ( user_id, host, port, login_time )
        // VALUES ('baojie', '0.0.0.0', '1234', '2005-08-25 08:34:23 CST')
        String sql = "INSERT INTO online ( user_id, host, port, login_time) " +
            "VALUES ('" + user_id + "', '" + host + "', '" + port + "', '" +
            OntologyEdit.getTime() + "')" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    // 2005-08-25
    static public boolean logout(Connection db, String user_id, String host,
        String port)
    {
        // DELETE FROM online WEHRE user_id = 'baojie' AND host = '0.0.0,0'
        //        AND port = '1234';
        String sql = "DELETE FROM online WHERE user_id = '" + user_id +
            "' AND host = '" + host + "'  AND port = '" + port + "'" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    // 2005-08-25
    static public boolean clearLogin(Connection db, String user_id)
    {
        //DELETE FROM online WEHRE user_id = 'baojie'
        String sql = "DELETE FROM online WHERE user_id = '" + user_id + "'" ;
        return JDBCUtils.updateDatabase(db, sql) ;
    }

    // 2005-08-26
    static public OnlineInfo getOnlineInfo(Connection db, String user_id)
    {
        // SELECT host, port, login_time FROM online WHERE user_id = 'baojie'
        String sql = "SELECT host, port, login_time FROM online " +
            "WHERE user_id = '" + user_id + "'" ;
        Vector<String[]> v = JDBCUtils.getValues(db, sql, 3) ;
        if(v != null && v.size() > 0)
        {
            String s[] = v.elementAt(0) ;
            OnlineInfo info = new OnlineInfo() ;
            info.user = user_id ;
            info.host = s[0] ;
            info.port = s[1] ;
            info.login_time = s[2] ;
            return info ;
        }
        return null ;
    }

    // 2005-08-27
    static public Vector<String> getAllUsers(Connection db, String role)
    {
        // SELECT id FROM users [WHERE role = 'role'] ORDER BY id;
        String sql = "SELECT id FROM users " ;
        if(role != null)
        {
            sql += " WHERE role = '" + role + "'" ;
        }
        sql += " ORDER BY id" ;
        return JDBCUtils.getValues(db, sql) ;
    }
}
