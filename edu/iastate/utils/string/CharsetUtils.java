package edu.iastate.utils.string;

import java.io.UnsupportedEncodingException;
import javax.mail.internet.MimeUtility;

import sun.misc.BASE64Decoder;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class CharsetUtils
{
    private static String toChinese(String input)
    {
        String from = "";
        try
        {
            if (input != null)
            {
                from = input.toString();
            }
            if (from.startsWith("=?GB") || from.startsWith("=?gb"))
            {
                from = MimeUtility.decodeText(from);
            }
            else
            {
                from = toGBK(from);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
//        from = StringUtil.replaceStr( from , " < " , " < " ) ;
        /* replaceStrÎª×Ö·û´®Ìæ»»º¯Êý */
//        from = StringUtil.replaceStr( from , ">" , ">" ) ;
        return from;
    }

    public static String toGBK(String strvalue)
    {
        try
        {
            if (strvalue == null)
            {
                return null;
            }
            else
            {
                strvalue = new String(strvalue.getBytes("ISO-8859-1"),
                                      "GBK");
                return strvalue;
            }
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String getFromBASE64(String s)
    {
        if (s == null)
        {
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String ISO88591toChinese(String in)
    {
        String s = null;
        byte temp[];
        if (in == null)
        {
            System.out.println("Warn:Chinese null founded!");
            return new String("");
        }
        try
        {
            temp = in.getBytes("ISO-8859-1");
            s = new String(temp);
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println
                (e.toString());
        }
        return s;
    }

    public static String parseChinese(String in)
    {
        return ISO88591toChinese(in);
    }

}
