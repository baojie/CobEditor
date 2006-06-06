/*   ********************************************************************   **
 **   Copyright notice                                                       **
 **                                                                          **
 **   (c) 2003 RSSOwl Development Team					    **
 **   http://rssowl.sourceforge.net/   			                    **
 **                                                                          **
 **   All rights reserved                                                    **
 **                                                                          **
 **   This script is part of the RSSOwl project. The RSSOwl						      **
 **   project is free software; you can redistribute it and/or modify        **
 **   it under the terms of the GNU General Public License as published by   **
 **   the Free Software Foundation; either version 2 of the License, or      **
 **   (at your option) any later version.                                    **
 **                                                                          **
 **   The GNU General Public License can be found at                         **
 **   http://www.gnu.org/copyleft/gpl.html.                                  **
 **   A copy is found in the textfile GPL.txt and important notices to the   **
 **   license from the team is found in the textfile LICENSE.txt distributed **
 **   in these package.                                                      **
 **                                                                          **
 **   This copyright notice MUST APPEAR in all copies of the file!           **
 **   ********************************************************************   */

package edu.iastate.utils ;

import java.text.SimpleDateFormat ;
import java.util.Calendar ;
import java.util.Date ;
import java.util.Locale ;
import java.util.TimeZone ;

/**
 * Utility class providing convenience methods to (XML) parsing
 * mechanisms.
 *
 * @author Niko Schmuck (niko@nava.de)
 * @version 0.63b
 */
public class ParseDate
{

    // Mon, 07 Oct 2002 03:16:15 GMT
    private static SimpleDateFormat dfA = new SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH) ;

    // 2002-09-19T02:51:16+0200
    private static SimpleDateFormat dfB = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ssZ") ;

    // 2002-09-19T02:51:16
    private static SimpleDateFormat dfC = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss") ;

    // 2002-09-19
    private static SimpleDateFormat dfD = new SimpleDateFormat("yyyy-MM-dd") ;

    /**
     * Tries different date formats to parse against the given string
     * representation to retrieve a valid Date object.
     * @param strdate Date as String
     * @return Date
     */
    public static Date getDate(String strdate)
    {
        Date result = null ;
        try
        {
            result = dfA.parse(strdate) ;
        }
        catch(java.text.ParseException eA)
        {
            try
            {
                result = dfB.parse(strdate) ;
            }
            catch(java.text.ParseException eB)
            {
                try
                {
                    result = dfC.parse(strdate) ;
                    // try to retrieve the timezone anyway
                    result = extractTimeZone(strdate, result) ;
                }
                catch(java.text.ParseException eC)
                {
                    try
                    {
                        result = dfD.parse(strdate) ;
                    }
                    catch(java.text.ParseException eD)
                    {
                    }
                }
            }
        }
        return result ;
    }

    /**
     * Extract the timezone from the Date
     * @param strdate
     * @param thedate
     * @return Date
     */
    private static Date extractTimeZone(String strdate, Date thedate)
    {
        // try to extract -06:00
        String tzSign = strdate.substring(strdate.length() - 6,
            strdate.length() - 5) ;
        String tzHour = strdate.substring(strdate.length() - 5,
            strdate.length() - 3) ;
        String tzMin = strdate.substring(strdate.length() - 2) ;
        if(tzSign.equals("-") || tzSign.equals("+"))
        {
            int h = Integer.parseInt(tzHour) ;
            int m = Integer.parseInt(tzMin) ;

            // NOTE: this is really plus, since perspective is from GMT
            if(tzSign.equals("+"))
            {
                h = -1 * h ;
                m = -1 * m ;
            }
            Calendar cal = Calendar.getInstance() ;
            cal.setTime(thedate) ;
            cal.add(Calendar.HOUR_OF_DAY, h) ;
            cal.add(Calendar.MINUTE, m) ;
            // calculate according the used timezone
            cal.add(Calendar.MILLISECOND,
                localTimeDiff(cal.getTimeZone(), thedate)) ;
            thedate = cal.getTime() ;
        }
        return thedate ;
    }

    /**
     * Calculate the local timezone difference
     * @param tz
     * @param date
     * @return int Difference
     */
    private static int localTimeDiff(TimeZone tz, Date date)
    {
        if(tz.inDaylightTime(date))
        {
            int dstSavings = 0 ;
            if(tz.useDaylightTime())
            {
                dstSavings = 3600000 ; // shortcut, JDK 1.4 allows cleaner impl
            }
            return tz.getRawOffset() + dstSavings ;
        }
        return tz.getRawOffset() ;
    }

    /**
     * Format the date
     * @param aDate
     * @return Formatted Date
     */
    public static String formatDate(Date aDate, Locale locale)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", locale) ;
        return dateFormat.format(aDate) ;
    }
    /**
     * Format date as all numbers e.g. 2006-02-27
     * @author Jie Bao
     * @since 2006-02-27
     * @param aDate String
     * @return String
     */
    public static String formatDate(String aDate)
    {
        // e.g. March 3,2006   3 March 2006   March 3rd 2006
        String s[] = aDate.toLowerCase().split("\\W+") ;
        if(s.length != 3)
        {
            return aDate ;
        }
        String month = s[0] ;
        String day = s[1] ;
        String year = s[2] ;

        if(Character.isDigit(month.charAt(0)))
        {
            month = s[1] ;
            day = s[0] ;
        }
        day = day.replaceAll("[st|nd|rd|th]","");

        String m[] =
            {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep",
            "oct", "nov", "dec"} ;
        for(int i = 0 ; i < 12 ; i++)
        {
            if(month.startsWith(m[i]))
            {
                month = (i + 1) + "" ;
                break ;
            }
        }
        if(month.length() == 1)
        {
            month = "0" + month ;
        }
        if(day.length() == 1)
        {
            day = "0" + day ;
        }

        // return 2006-03-03
        return year + "-" + month + "-" + day ;
    }
}
