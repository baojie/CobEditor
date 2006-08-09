package edu.iastate.ato.po.naming ;

import java.sql.Connection ;
import java.util.Vector ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-18</p>
 */
public class NamingPolicyFactory
{
    // 2005-08-24
    public static Vector<NamingPolicy> getAllNamingPolicy(Connection db)
    {
        Vector<NamingPolicy> allNamingPolicy = new Vector() ;
        allNamingPolicy.add(new BasicNamingPolicy(db)) ;
        allNamingPolicy.add(new AutomaticNamingPolicy(db)) ;

        return allNamingPolicy ;
    }

    // 2005-08-18
    public static NamingPolicy buildPolicyFromName(String name, Connection db)
    {
        if(name.equals(BasicNamingPolicy.policyName))
        {
            return new BasicNamingPolicy(db) ;
        }
        else if(name.equals(AutomaticNamingPolicy.policyName))
        {
            return new AutomaticNamingPolicy(db) ;
        }
        else
        {
            return null ;
        }
    }
}
