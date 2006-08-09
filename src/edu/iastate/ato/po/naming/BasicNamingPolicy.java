package edu.iastate.ato.po.naming ;

import java.sql.Connection;

import edu.iastate.ato.po.OntologyEdit;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-17</p>
 */
public class BasicNamingPolicy extends NamingPolicy
{
    public static String policyName = "Basic Naming Policy" ;

    Connection db ;

    
    public BasicNamingPolicy(Connection db)
    {
    	this.db = db;
        enableRename = true ;
        uniqueRequired = true ;
    }

    /**
     * makeNameWhenCreating
     *
     * @param baseName String
     * @return String
     * @todo Implement this edu.iastate.anthill.cob.ui.naming.NamingPolicy method
     */
    public String makeNameWhenCreating(String baseName)
    {
        return makeTimeStampName() ;
    }

    /**
     * makeNameWhenSaving
     *
     * @param baseName String
     * @return String
     * @todo Implement this edu.iastate.ato.ui.naming.NamingPolicy method
     */
    public String makeNameWhenSaving(String baseName)
    {
    	OntologyEdit.applyID(db, baseName);
        return baseName ;
    }

    public String getExplanation()
    {
        String str = "* Policy name: " + policyName ;
        str += "\n* Rename enabled: " + enableRename ;
        str += "\n* Unique name required: " + uniqueRequired ;
        str += "\n* Name of new created term will be asked from the user" ;
        return str ;
    }

    public String getPolicyName()
    {
        return policyName ;
    }

    public boolean isNameValid(String name)
    {
        if(name == null)
        {
            return false ;
        }
        else if(name.length() == 0)
        {
            return false ;
        }
        else if(!name.matches("[a-zA-Z0-9][\\s\\w\\-._\\(\\)]*"))
        {
            return false ;
        }
        return true ;
    }
}
