package edu.iastate.utils.net ;

import edu.iastate.utils.string.* ;
import java.io.IOException ;
import edu.iastate.ato.gui.MOEditor ;
import edu.iastate.utils.Debug ;
import edu.iastate.ato.gui.dialog.*;

/**
 * <p>@author Jie Bao</p>
 * <p>@since 2005-08-27</p>
 */
public class EmailTools
{
    public static boolean isEmailAddress(String ea)
    {
        return Validator.isEmailAddress(ea) ;
    }

    public static boolean sendWindowsEmail(String email, String subject,
        String body)
    {
        if(isEmailAddress(email))
        {
            String url = "mailto:" + email + "?subject=" + subject +
                "&body=" + body ;
            //System.out.println(url) ;
            try
            {
                Runtime.getRuntime().exec("cmd.exe  /c  start " + url) ;
                return true;
            }
            catch(IOException ex)
            {
            }
        }
        return false ;
    }
}
