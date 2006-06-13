package edu.iastate.ato.po.naming ;

import java.sql.Connection ;
import java.text.DecimalFormat ;
import java.text.NumberFormat ;

import edu.iastate.ato.po.OntologyEdit ;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-17</p>
 */
public class AutomaticNamingPolicy extends NamingPolicy
{
    // Pattern of the new term;
    String namingPattern = "0000000" ;
    // see example at http://javaalmanac.com/egs/java.text/FormatNum.html
    NumberFormat formatter = new DecimalFormat(namingPattern) ;

    Connection db ;

    public AutomaticNamingPolicy(Connection db)
    {
        enableRename = false ;
        uniqueRequired = true ;
        this.db = db ;
    }

    public String makeNameWhenSaving(String term_id)
    {
        String auto_id = OntologyEdit.applyID(db, term_id) ;
        int id = Integer.parseInt(auto_id) ;
        String s = formatter.format(id) ; // 0001235
        return s ;
    }

    public String getExplanation()
    {
        String str = "* Policy name: " + policyName ;
        str += "\n* Rename enabled: " + enableRename ;
        str += "\n* Unique name required: " + uniqueRequired ;
        str += "\n* New created term will have a temporary name" ;
        str += "\n* Term name is assigned when term is saved" ;
        return str ;
    }

    protected static String policyName = "Automatic Naming Policy" ;
    public String getPolicyName()
    {
        return policyName ;
    }

    public String makeNameWhenCreating(String baseName)
    {
        return makeTimeStampName() ;
    }

    public boolean isNameValid(String name)
    {
        return true ;
    }

}
