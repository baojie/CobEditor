package edu.iastate.ato.po ;

import java.sql.Connection ;

import edu.iastate.utils.sql.JDBCUtils ;

/**
 *
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-20</p>
 */
public class User
{
    final public static String GUEST = "guest" ;
    final public static String ADMIN = "admin" ;
    final public static String PACKAGE_ADMIN = "pkgadmin" ;

    final public static String READ_WRITE = "rw" ;

    public String name ;
    public String role ;
    public String pass ;

    public String toString()
    {
        return name ;
    }

    public boolean isAdmin()
    {
        return ADMIN.equals(role) ;
    }

    public boolean isGuest()
    {
        return GUEST.equals(role) ;
    }

    public boolean isNormalRole()
    {
        return(!GUEST.equals(role) && !ADMIN.equals(role)) ;
    }

    public static User getGuest()
    {
        User u = new User() ;
        u.name = GUEST ;
        u.role = GUEST ;
        return u ;
    }

    public User()
    {}

    public User(Connection db, String user_id)
    {
        this.name = user_id ;
        String sql = "SELECT role FROM users WHERE id = '" + user_id + "'" ;
        this.role = JDBCUtils.getFirstValue(db, sql) ;
        sql = "SELECT pass FROM users WHERE id = '" + user_id + "'" ;
        this.pass = JDBCUtils.getFirstValue(db, sql) ;
    }
}
