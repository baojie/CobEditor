package edu.iastate.utils ;

import java.text.NumberFormat ;
import java.util.Arrays ;
import java.util.StringTokenizer ;
import java.util.Vector ;

/**
 *
 * <p>Title: Utility</p>
 * <p>Description: General Utility tools</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Iowa State University</p>
 * @author Jie Bao
 * @version 1.0
 */
public class Utility
{
    /**
     * Private constructor to avoid accidental instantiation.
     */
    private Utility()
    {} ;

    /**
     *
     * @param longSize
     * @return
     * @author Jie Bao
     * @version 2003-11-01
     */
    public static String formatSize(long longSize)
    {
        return formatSize(longSize, -1) ;
    }

    /**
     *
     * @param longSize: a long number, eg. 242343543613
     * @param decimalPos: # of digits after the point(.) eg. 2
     * @return
     * @author Jie Bao
     * @version 2003-11-01
     */
    public static String formatSize(long longSize, int decimalPos)
    {
        NumberFormat fmt = NumberFormat.getNumberInstance() ;
        if(decimalPos >= 0)
        {
            fmt.setMaximumFractionDigits(decimalPos) ;
        }
        final double size = longSize ;
        double val = size / (1024 * 1024) ;
        if(val > 1)
        {
            return fmt.format(val).concat(" MB") ;
        }
        val = size / 1024 ;
        if(val > 1)
        {
            return fmt.format(val).concat(" KB") ;
        }
        return fmt.format(size).concat(" bytes") ;
    }

    /**
     * Creates a char array with the specified subcontents of given array.
     * @param buffer the buffer where to extract the information from.
     * @param start the starting position of the desired subcontents.
     * @param offset the subcontents length.
     * @return the sub-buffer, or null of any parameter is invalid.
     */
    public static char[] subBuffer(char[] buffer, int start, int offset)
    {
        char[] result = null ;

        if((buffer != null)
            && (start > -1)
            && (offset > 0))
        {
            int t_iEnd =
                (start + offset >= buffer.length)
                ? buffer.length
                : start + offset ;

            result = new char[t_iEnd - start] ;

            for(
                int t_iCharIndex = start ;
                t_iCharIndex < t_iEnd ;
                t_iCharIndex++)
            {
                result[t_iCharIndex - start] = buffer[t_iCharIndex] ;
            }
        }

        return result ;
    }

    /**
     * Creates a char array with the specified subcontents of given array.
     * @param buffer the buffer where to extract the information from.
     * @param start the starting position of the desired subcontents.
     * @return the sub-buffer.
     */
    public static char[] subBuffer(char[] buffer, int start)
    {
        char[] result = null ;

        if(buffer != null)
        {
            result = subBuffer(buffer, start, buffer.length) ;
        }

        return result ;
    }

    // fot test purpose
    public static void main(String[] args)
    {
        System.out.println("check begin") ;

        long i = 2633 ;
        Debug.trace(null, formatSize(i)) ;

        System.out.println("check finished") ;
    }

    /**
     * Checks if given object contains numeric information or not.
     * @param object the object to check if it represents a number.
     * @return true if such object actually is a number.
     */
    public static boolean isNumeric(Object object)
    {
        boolean result = false ;

        if(object != null)
        {
            String t_strValue = object.toString() ;

            StringTokenizer t_strtokFigures =
                new StringTokenizer(t_strValue, "0123456789.,", false) ;

            result = !t_strtokFigures.hasMoreTokens() ;
        }

        return result ;
    }

    /**
     * Retrieves the maximum between a list of integers.
     * @param numbers the collection of all numbers.
     * @result the maximum number,
     */
    public static int getMax(int[] numbers)
    {
        int result = 0 ;

        if(numbers != null)
        {
            for(int t_iIndex = 0 ; t_iIndex < numbers.length ; t_iIndex++)
            {
                result = Math.max(result, numbers[t_iIndex]) ;
            }
        }

        return result ;
    }

    /**
     * Retrieves the maximum between a list of doubles.
     * @param numbers the collection of all numbers.
     * @result the maximum number,
     */
    public static double getMax(double[] numbers)
    {
        double result = 0.0 ;

        if(numbers != null)
        {
            for(int t_iIndex = 0 ; t_iIndex < numbers.length ; t_iIndex++)
            {
                result = Math.max(result, numbers[t_iIndex]) ;
            }
        }

        return result ;
    }

    /**
     * return a vector for the array
     * @param obj Object[]
     * @return Vector
     */
    public static Vector Array2Vector(Object data[])
    {
        return new Vector(Arrays.asList(data)) ;
    }

}
