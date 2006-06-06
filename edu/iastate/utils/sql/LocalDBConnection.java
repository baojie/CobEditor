package edu.iastate.utils.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LocalDBConnection
{

    protected String url;
    protected String user;
    protected String password;
    protected String driver;

    public LocalDBConnection()
    {
    }

    protected void finalize()
    {
       // disconnect();
    }

    /**
     * @since 2005-03-25
     */
    public boolean connect()
    {
        try
        {
            //System.out.println(driver);
            Class.forName(driver);

            if (db == null || db.isClosed())
            {
                db = DriverManager.getConnection(url, user, password);
            }

            return true;
        }
        catch (SQLException ex)
        {
            System.out.println("Cannot connect to the data source");
            //ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            System.out.println("'" + driver + "' not found!");
            //ex.printStackTrace();
        }
        return false;
    }

    public void connect(String driver, String dburl, String user, String passwd)
    {
        this.url = dburl;
        this.driver = driver;

        this.user = user;
        this.password = passwd;

        this.connect();
    }

    public Connection db;

    /**
     * 2005-02-17
     */
    public void disconnect()
    {
        if (db != null)
        {
            try
            {
                if (!db.isClosed())
                {
                    db.close();
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public String getDriver()
    {
        return driver;
    }

    public String getPassword()
    {
        return password;
    }

    public String getUrl()
    {
        return url;
    }

    public String getUser()
    {
        return user;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    // 2005-08-20
    public String toString()
    {
        String str = "Url : "+ this.url +
            ", User:  " +this.user +
            ", Password: " + this.password +
            ", Driver: " + this.driver;
        return str;
    }

}
