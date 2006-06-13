package edu.iastate.utils.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.iastate.utils.io.FileUtils;

/**
 * Example:
 *
 *   Logging logger = new Logging(m_rootPath + "\\log\\", "DSServer");
 *   String logInfo =
 *      "\n==============\n" +
        new Date().toString() + " " +
        "DataSourceEditorServer started at port " +
         port + "\n";
         logger.saveLogbyDate(logInfo);
 * @author Jie Bao
 * @since 1.0 2004-10-11
 */

public class Logging
{
    String basisPath;
    String nameStem;
    public Logging(String basisPath, String nameStem)
    {
        this.basisPath = basisPath;
        this.nameStem = nameStem;
    }
    /**
     * Save log with time stamp
     * @param info String
     * @author Jie Bao
     * @since 2005-03-09
     */
    public void saveLogbyDateWithTime(String info)
    {
        String s = new SimpleDateFormat("HH:mm:ss").format(new Date());
        saveLogbyDate(s + " : " + info);
    }

    public void saveLogbyDate(String info)
    {
        try
        {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String s = formatter.format(date);
            FileUtils.appendFile(basisPath + nameStem + "-" + s + ".txt", info);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * write logging information
     *
     * @param obj Object - could be 'this'
     * @param logFile String
     * @param information String
     * @author Jie Bao
     * @version 2004-07-11
     */
    public static void log(Object obj, String logFile, String information)
    {
        try
        {
            String location = "";
            if (obj != null)
            {
                location = obj.getClass().getName() + ": ";
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(
                logFile, true));
            out.write(new Date() + " from " +
                      location + information + "\n");
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

//    static String defaultLogFile = "/wikiont.debug.txt";
}
