package edu.iastate.ato.po.naming ;

import java.text.SimpleDateFormat ;
import java.util.Date ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-17</p>
 */
abstract public class NamingPolicy
{
    abstract public String getPolicyName() ;

    public boolean enableRename ;
    public boolean uniqueRequired ;

    abstract public String makeNameWhenCreating(String baseName) ;

    abstract public String makeNameWhenSaving(String baseName) ;

    abstract public String getExplanation() ;

    abstract public boolean isNameValid(String name) ;

    public String toString()
    {
        return getPolicyName() ;
    }

    final public static SimpleDateFormat dateFormat = new
        SimpleDateFormat("yyyyMMddHHmmssS") ;

    public String makeTimeStampName()
    {
        return dateFormat.format(new Date()) ;
    }

}
