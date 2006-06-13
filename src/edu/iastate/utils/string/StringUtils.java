package edu.iastate.utils.string;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to peform common String manipulation algorithms.
 */
public class StringUtils
{

    // Constants used by escapeHTMLTags
    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();

    /**
     * Replaces all instances of oldString with newString in line.
     *
     * @param line the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     *
     * @return a String will all instances of oldString replaced by newString
     */
    public static final String replace(String line, String oldString,
                                       String newString)
    {
        if (line == null)
        {
            return null;
        }
        int i = 0;
        if ( (i = line.indexOf(oldString, i)) >= 0)
        {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ( (i = line.indexOf(oldString, i)) > 0)
            {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * Replaces all instances of oldString with newString in line with the
     * added feature that matches of newString in oldString ignore case.
     *
     * @param line the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     *
     * @return a String will all instances of oldString replaced by newString
     */
    public static final String replaceIgnoreCase(String line,
                                                 String oldString,
                                                 String newString)
    {
        if (line == null)
        {
            return null;
        }
        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;
        if ( (i = lcLine.indexOf(lcOldString, i)) >= 0)
        {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ( (i = lcLine.indexOf(lcOldString, i)) > 0)
            {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * Replaces all instances of oldString with newString in line with the
     * added feature that matches of newString in oldString ignore case.
     * The count paramater is set to the number of replaces performed.
     *
     * @param line the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     * @param count a value that will be updated with the number of replaces
     *      performed.
     *
     * @return a String will all instances of oldString replaced by newString
     */
    public static final String replaceIgnoreCase(String line,
                                                 String oldString,
                                                 String newString,
                                                 int[] count)
    {
        if (line == null)
        {
            return null;
        }
        String lcLine = line.toLowerCase();
        String lcOldString = oldString.toLowerCase();
        int i = 0;
        if ( (i = lcLine.indexOf(lcOldString, i)) >= 0)
        {
            int counter = 1;
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ( (i = lcLine.indexOf(lcOldString, i)) > 0)
            {
                counter++;
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            count[0] = counter;
            return buf.toString();
        }
        return line;
    }

    /**
     * Replaces all instances of oldString with newString in line.
     * The count Integer is updated with number of replaces.
     *
     * @param line the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     *
     * @return a String will all instances of oldString replaced by newString
     */
    public static final String replace(String line, String oldString,
                                       String newString, int[] count)
    {
        if (line == null)
        {
            return null;
        }
        int i = 0;
        if ( (i = line.indexOf(oldString, i)) >= 0)
        {
            int counter = 1;
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ( (i = line.indexOf(oldString, i)) > 0)
            {
                counter++;
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            count[0] = counter;
            return buf.toString();
        }
        return line;
    }

    /**
     * Substitutes most common HTML reserved characters with their escaped
     * version.<br/>
     * i.e. it takes all "<" and replaces them with "&amp;lt;".
     * @param htmlContent the html contents to escape.
     * @return the escaped version of such contents.
     */
    public static String escapeHTML(String htmlContents)
    {
        String result = htmlContents;

        result = replace(result, "<", "&lt;");
        result = replace(result, ">", "&gt;");
        result = replace(result, "\"", "&quot;");
        result = replace(result, "'", "&apos;");

        return result;
    }

    /**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
     * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
     * their HTML escape sequences.
     *
     * @param in the text to be converted.
     * @return the input string with the characters '&lt;' and '&gt;' replaced
     *  with their HTML escape sequences.
     */
    public static final String escapeHTMLTags(String in)
    {
        if (in == null)
        {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuffer out = new StringBuffer( (int) (len * 1.3));
        for (; i < len; i++)
        {
            ch = input[i];
            if (ch > '>')
            {
                continue;
            }
            else if (ch == '<')
            {
                if (i > last)
                {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            }
            else if (ch == '>')
            {
                if (i > last)
                {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(GT_ENCODE);
            }
        }
        if (last == 0)
        {
            return in;
        }
        if (i > last)
        {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * Used by the hash method.
     */
    private static MessageDigest digest = null;

    /**
     * Hashes a String using the Md5 algorithm and returns the result as a
     * String of hexadecimal numbers. This method is synchronized to avoid
     * excessive MessageDigest object creation. If calling this method becomes
     * a bottleneck in your code, you may wish to maintain a pool of
     * MessageDigest objects instead of using this method.
     * <p>
     * A hash is a one-way function -- that is, given an
     * input, an output is easily computed. However, given the output, the
     * input is almost impossible to compute. This is useful for passwords
     * since we can store the hash and a hacker will then have a very hard time
     * determining the original password.
     * <p>
     * In Jive, every time a user logs in, we simply
     * take their plain text password, compute the hash, and compare the
     * generated hash to the stored hash. Since it is almost impossible that
     * two passwords will generate the same hash, we know if the user gave us
     * the correct password or not. The only negative to this system is that
     * password recovery is basically impossible. Therefore, a reset password
     * method is used instead.
     *
     * @param data the String to compute the hash of.
     * @return a hashed version of the passed-in String
     */
    public synchronized static final String hash(String data)
    {
        if (digest == null)
        {
            try
            {
                digest = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException nsae)
            {
                System.err.println("Failed to load the MD5 MessageDigest. " +
                                   "Jive will be unable to function normally.");
                nsae.printStackTrace();
            }
        }
        // Now, compute hash.
        digest.update(data.getBytes());
        return encodeHex(digest.digest());
    }

    /**
     * Turns an array of bytes into a String representing each byte as an
     * unsigned hex number.
     * <p>
     * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
     * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
     * Distributed under LGPL.
     *
     * @param bytes an array of bytes to convert to a hex-string
     * @return generated hex string
     */
    public static final String encodeHex(byte[] bytes)
    {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        int i;

        for (i = 0; i < bytes.length; i++)
        {
            if ( ( (int) bytes[i] & 0xff) < 0x10)
            {
                buf.append("0");
            }
            buf.append(Long.toString( (int) bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * Turns a hex encoded string into a byte array. It is specifically meant
     * to "reverse" the toHex(byte[]) method.
     *
     * @param hex a hex encoded String to transform into a byte array.
     * @return a byte array representing the hex String[
     */
    public static final byte[] decodeHex(String hex)
    {
        char[] chars = hex.toCharArray();
        byte[] bytes = new byte[chars.length / 2];
        int byteCount = 0;
        for (int i = 0; i < chars.length; i += 2)
        {
            byte newByte = 0x00;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[i + 1]);
            bytes[byteCount] = newByte;
            byteCount++;
        }
        return bytes;
    }

    /**
     * Returns the the byte value of a hexadecmical char (0-f). It's assumed
     * that the hexidecimal chars are lower case as appropriate.
     *
     * @param ch a hexedicmal character (0-f)
     * @return the byte value of the character (0x00-0x0F)
     */
    private static final byte hexCharToByte(char ch)
    {
        switch (ch)
        {
            case '0':
                return 0x00;
            case '1':
                return 0x01;
            case '2':
                return 0x02;
            case '3':
                return 0x03;
            case '4':
                return 0x04;
            case '5':
                return 0x05;
            case '6':
                return 0x06;
            case '7':
                return 0x07;
            case '8':
                return 0x08;
            case '9':
                return 0x09;
            case 'a':
                return 0x0A;
            case 'b':
                return 0x0B;
            case 'c':
                return 0x0C;
            case 'd':
                return 0x0D;
            case 'e':
                return 0x0E;
            case 'f':
                return 0x0F;
        }
        return 0x00;
    }

    //*********************************************************************
     //* Base64 - a simple base64 encoder and decoder.
      //*
       //*     Copyright (c) 1999, Bob Withers - bwit@pobox.com
        //*
         //* This code may be freely used for any purpose, either personal
          //* or commercial, provided the authors copyright notice remains
           //* intact.
            //*********************************************************************

             /**
              * Encodes a String as a base64 String.
              *
              * @param data a String to encode.
              * @return a base64 encoded String.
              */
             public static String encodeBase64(String data)
             {
                 return encodeBase64(data.getBytes());
             }

    /**
     * Encodes a byte array into a base64 String.
     *
     * @param data a byte array to encode.
     * @return a base64 encode String.
     */
    public static String encodeBase64(byte[] data)
    {
        int c;
        int len = data.length;
        StringBuffer ret = new StringBuffer( ( (len / 3) + 1) * 4);
        for (int i = 0; i < len; ++i)
        {
            c = (data[i] >> 2) & 0x3f;
            ret.append(cvt.charAt(c));
            c = (data[i] << 4) & 0x3f;
            if (++i < len)
            {
                c |= (data[i] >> 4) & 0x0f;

            }
            ret.append(cvt.charAt(c));
            if (i < len)
            {
                c = (data[i] << 2) & 0x3f;
                if (++i < len)
                {
                    c |= (data[i] >> 6) & 0x03;

                }
                ret.append(cvt.charAt(c));
            }
            else
            {
                ++i;
                ret.append( (char) fillchar);
            }

            if (i < len)
            {
                c = data[i] & 0x3f;
                ret.append(cvt.charAt(c));
            }
            else
            {
                ret.append( (char) fillchar);
            }
        }
        return ret.toString();
    }

    /**
     * Decodes a base64 String.
     *
     * @param data a base64 encoded String to decode.
     * @return the decoded String.
     */
    public static String decodeBase64(String data)
    {
        return decodeBase64(data.getBytes());
    }

    /**
     * Decodes a base64 aray of bytes.
     *
     * @param data a base64 encode byte array to decode.
     * @return the decoded String.
     */
    public static String decodeBase64(byte[] data)
    {
        int c, c1;
        int len = data.length;
        StringBuffer ret = new StringBuffer( (len * 3) / 4);
        for (int i = 0; i < len; ++i)
        {
            c = cvt.indexOf(data[i]);
            ++i;
            c1 = cvt.indexOf(data[i]);
            c = ( (c << 2) | ( (c1 >> 4) & 0x3));
            ret.append( (char) c);
            if (++i < len)
            {
                c = data[i];
                if (fillchar == c)
                {
                    break;
                }

                c = cvt.indexOf( (char) c);
                c1 = ( (c1 << 4) & 0xf0) | ( (c >> 2) & 0xf);
                ret.append( (char) c1);
            }

            if (++i < len)
            {
                c1 = data[i];
                if (fillchar == c1)
                {
                    break;
                }

                c1 = cvt.indexOf( (char) c1);
                c = ( (c << 6) & 0xc0) | c1;
                ret.append( (char) c);
            }
        }
        return ret.toString();
    }

    private static final int fillchar = '=';
    private static final String cvt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        + "abcdefghijklmnopqrstuvwxyz"
        + "0123456789+/";

    /**
     * Converts a line of text into an array of lower case words using a
     * BreakIterator.wordInstance(). <p>
     *
     * This method is under the Jive Open Source Software License and was
     * written by Mark Imbriaco.
     *
     * @param text a String of text to convert into an array of words
     * @return text broken up into an array of words.
     */
    public static final String[] toLowerCaseWordArray(String text)
    {
        if (text == null || text.length() == 0)
        {
            return new String[0];
        }

        ArrayList wordList = new ArrayList();
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(text);
        int start = 0;

        for (int end = boundary.next(); end != BreakIterator.DONE;
             start = end, end = boundary.next())
        {
            String tmp = text.substring(start, end).trim();
            // Remove characters that are not needed.
            tmp = replace(tmp, "+", "");
            tmp = replace(tmp, "/", "");
            tmp = replace(tmp, "\\", "");
            tmp = replace(tmp, "#", "");
            tmp = replace(tmp, "*", "");
            tmp = replace(tmp, ")", "");
            tmp = replace(tmp, "(", "");
            tmp = replace(tmp, "&", "");
            if (tmp.length() > 0)
            {
                wordList.add(tmp);
            }
        }
        return (String[]) wordList.toArray(new String[wordList.size()]);
    }

    /**
     * Pseudo-random number generator object for use with randomString().
     * The Random class is not considered to be cryptographically secure, so
     * only use these random Strings for low to medium security applications.
     */
    private static Random randGen = new Random();

    /**
     * Array of numbers and letters of mixed case. Numbers appear in the list
     * twice so that there is a more equal chance that a number will be picked.
     * We can use the array to get a random number or letter by picking a random
     * array index.
     */
    private static char[] numbersAndLetters = (
        "0123456789abcdefghijklmnopqrstuvwxyz" +
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

    /**
     * Returns a random String of numbers and letters (lower and upper case)
     * of the specified length. The method uses the Random class that is
     * built-in to Java which is suitable for low to medium grade security uses.
     * This means that the output is only pseudo random, i.e., each number is
     * mathematically generated so is not truly random.<p>
     *
     * The specified length must be at least one. If not, the method will return
     * null.
     *
     * @param length the desired length of the random String to return.
     * @return a random String of numbers and letters of the specified length.
     */
    public static final String randomString(int length)
    {
        if (length < 1)
        {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++)
        {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    /**
     * Intelligently chops a String at a word boundary (whitespace) that occurs
     * at the specified index in the argument or before. However, if there is a
     * newline character before <code>length</code>, the String will be chopped
     * there. If no newline or whitespace is found in <code>string</code> up to
     * the index <code>length</code>, the String will chopped at <code>length</code>.
     * <p>
     * For example, chopAtWord("This is a nice String", 10) will return
     * "This is a" which is the first word boundary less than or equal to 10
     * characters into the original String.
     *
     * @param string the String to chop.
     * @param length the index in <code>string</code> to start looking for a
     *       whitespace boundary at.
     * @return a substring of <code>string</code> whose length is less than or
     *       equal to <code>length</code>, and that is chopped at whitespace.
     */
    public static final String chopAtWord(String string, int length)
    {
        if (string == null || string.length() == 0)
        {
            return string;
        }

        char[] charArray = string.toCharArray();
        int sLength = string.length();
        if (length < sLength)
        {
            sLength = length;
        }

        // First check if there is a newline character before length; if so,
        // chop word there.
        for (int i = 0; i < sLength - 1; i++)
        {
            // Windows
            if (charArray[i] == '\r' && charArray[i + 1] == '\n')
            {
                return string.substring(0, i + 1);
            }
            // Unix
            else if (charArray[i] == '\n')
            {
                return string.substring(0, i);
            }
        }
        // Also check boundary case of Unix newline
        if (charArray[sLength - 1] == '\n')
        {
            return string.substring(0, sLength - 1);
        }

        // Done checking for newline, now see if the total string is less than
        // the specified chop point.
        if (string.length() < length)
        {
            return string;
        }

        // No newline, so chop at the first whitespace.
        for (int i = length - 1; i > 0; i--)
        {
            if (charArray[i] == ' ')
            {
                return string.substring(0, i).trim();
            }
        }

        // Did not find word boundary so return original String chopped at
        // specified length.
        return string.substring(0, length);
    }

    /**
     * Reformats a string where lines that are longer than <tt>width</tt>
     * are split apart at the earliest wordbreak or at maxLength, whichever is
     * sooner. If the width specified is less than 5 or greater than the input
     * Strings length the string will be returned as is.
     * <p>
     * Please note that this method can be lossy - trailing spaces on wrapped
     * lines may be trimmed.
     *
     * @param input the String to reformat.
     * @param width the maximum length of any one line.
     * @return a new String with reformatted as needed.
     */
    public static String wordWrap(String input, int width, Locale locale)
    {
        // protect ourselves
        if (input == null)
        {
            return "";
        }
        else if (width < 5)
        {
            return input;
        }
        else if (width >= input.length())
        {
            return input;
        }

        // default locale
        if (locale == null)
        {
            locale = Locale.US;
        }

        StringBuffer buf = new StringBuffer(input);
        boolean endOfLine = false;
        int lineStart = 0;

        for (int i = 0; i < buf.length(); i++)
        {
            if (buf.charAt(i) == '\n')
            {
                lineStart = i + 1;
                endOfLine = true;
            }

            // handle splitting at width character
            if (i > lineStart + width - 1)
            {
                if (!endOfLine)
                {
                    int limit = i - lineStart - 1;
                    BreakIterator breaks = BreakIterator.getLineInstance(
                        locale);
                    breaks.setText(buf.substring(lineStart, i));
                    int end = breaks.last();

                    // if the last character in the search string isn't a space,
                    // we can't split on it (looks bad). Search for a previous
                    // break character
                    if (end == limit + 1)
                    {
                        if (!Character.isWhitespace(buf.charAt(lineStart +
                            end)))
                        {
                            end = breaks.preceding(end - 1);
                        }
                    }

                    // if the last character is a space, replace it with a \n
                    if (end != BreakIterator.DONE && end == limit + 1)
                    {
                        buf.replace(lineStart + end, lineStart + end + 1,
                                    "\n");
                        lineStart = lineStart + end;
                    }
                    // otherwise, just insert a \n
                    else if (end != BreakIterator.DONE && end != 0)
                    {
                        buf.insert(lineStart + end, '\n');
                        lineStart = lineStart + end + 1;
                    }
                    else
                    {
                        buf.insert(i, '\n');
                        lineStart = i + 1;
                    }
                }
                else
                {
                    buf.insert(i, '\n');
                    lineStart = i + 1;
                    endOfLine = false;
                }
            }
        }

        return buf.toString();
    }

    /**
     * Escapes all necessary characters in the String so that it can be used
     * in an XML doc.
     *
     * @param string the string to escape.
     * @return the string with appropriate characters escaped.
     */
    public static final String escapeForXML(String string)
    {
        if (string == null)
        {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = string.toCharArray();
        int len = input.length;
        StringBuffer out = new StringBuffer( (int) (len * 1.3));
        for (; i < len; i++)
        {
            ch = input[i];
            if (ch > '>')
            {
                continue;
            }
            else if (ch == '<')
            {
                if (i > last)
                {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(LT_ENCODE);
            }
            else if (ch == '&')
            {
                if (i > last)
                {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(AMP_ENCODE);
            }
            else if (ch == '"')
            {
                if (i > last)
                {
                    out.append(input, last, i - last);
                }
                last = i + 1;
                out.append(QUOTE_ENCODE);
            }
        }
        if (last == 0)
        {
            return string;
        }
        if (i > last)
        {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * Unescapes the String by converting XML escape sequences back into normal
     * characters.
     *
     * @param string the string to unescape.
     * @return the string with appropriate characters unescaped.
     */
    public static final String unescapeFromXML(String string)
    {
        string = replace(string, "&lt;", "<");
        string = replace(string, "&gt;", ">");
        string = replace(string, "&quot;", "\"");
        return replace(string, "&amp;", "&");
    }

    private static final char[] zeroArray = "0000000000000000".toCharArray();

    /**
     * Pads the supplied String with 0's to the specified length and returns
     * the result as a new String. For example, if the initial String is
     * "9999" and the desired length is 8, the result would be "00009999".
     * This type of padding is useful for creating numerical values that need
     * to be stored and sorted as character data. Note: the current
     * implementation of this method allows for a maximum <tt>length</tt> of
     * 16.
     *
     * @param string the original String to pad.
     * @param length the desired length of the new padded String.
     * @return a new String padded with the required number of 0's.
     */
    public static final String zeroPadString(String string, int length)
    {
        if (string == null || string.length() > length)
        {
            return string;
        }
        StringBuffer buf = new StringBuffer(length);
        buf.append(zeroArray, 0, length - string.length()).append(string);
        return buf.toString();
    }

    /**
     * Formats a Date as a fifteen character long String made up of the Date's
     * padded millisecond value.
     *
     * @return a Date encoded as a String.
     */
    public static final String dateToMillis(Date date)
    {
        return zeroPadString(Long.toString(date.getTime()), 15);
    }

    static final String HEX_DIGITS = "0123456789ABCDEF";

    /**
     *  java.net.URLEncoder.encode() method in JDK < 1.4 is buggy.  This duplicates
     *  its functionality.
     */
    protected static String urlEncode(byte[] rs)
    {
        StringBuffer result = new StringBuffer();

        // Does the URLEncoding.  We could use the java.net one, but
        // it does not eat byte[]s.

        for (int i = 0; i < rs.length; i++)
        {
            char c = (char) rs[i];

            switch (c)
            {
                case '_':
                case '.':
                case '*':
                case '-':
                    result.append(c);
                    break;

                case ' ':
                    result.append('+');
                    break;

                default:
                    if ( (c >= 'a' && c <= 'z') ||
                        (c >= 'A' && c <= 'Z') ||
                        (c >= '0' && c <= '9'))
                    {
                        result.append(c);
                    }
                    else
                    {
                        result.append('%');
                        result.append(HEX_DIGITS.charAt( (c & 0xF0) >> 4));
                        result.append(HEX_DIGITS.charAt(c & 0x0F));
                    }
            }

        } // for

        return result.toString();
    }

    /**
     *  URL encoder does not handle all characters correctly.
     *  See <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4257115.html">
     *  Bug parade, bug #4257115</A> for more information.
     *  <P>
     *  Thanks to CJB for this fix.
     */
    protected static String urlDecode(byte[] bytes) throws
        UnsupportedEncodingException,
        IllegalArgumentException
    {
        if (bytes == null)
        {
            return null;
        }

        byte[] decodeBytes = new byte[bytes.length];
        int decodedByteCount = 0;

        try
        {
            for (int count = 0; count < bytes.length; count++)
            {
                switch (bytes[count])
                {
                    case '+':
                        decodeBytes[decodedByteCount++] = (byte) ' ';
                        break;

                    case '%':
                        decodeBytes[decodedByteCount++] = (byte) ( (
                            HEX_DIGITS.indexOf(bytes[++count]) << 4) +
                            (HEX_DIGITS.indexOf(bytes[++count])));

                        break;

                    default:
                        decodeBytes[decodedByteCount++] = bytes[count];
                }
            }

        }
        catch (IndexOutOfBoundsException ae)
        {
            throw new IllegalArgumentException("Malformed UTF-8 string?");
        }

        String processedPageName = null;

        try
        {
            processedPageName = new String(decodeBytes, 0, decodedByteCount,
                                           "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new UnsupportedEncodingException(
                "UTF-8 encoding not supported on this platform");
        }

        return (processedPageName.toString());
    }

    /**
     *  As java.net.URLEncoder class, but this does it in UTF8 character set.
     */
    public static String urlEncodeUTF8(String text)
    {
        byte[] rs =
            {};

        try
        {
            rs = text.getBytes("UTF-8");
            return urlEncode(rs);
        }
        catch (UnsupportedEncodingException e)
        {
            return java.net.URLEncoder.encode(text);
        }

    }

    /**
     *  As java.net.URLDecoder class, but for UTF-8 strings.
     */
    public static String urlDecodeUTF8(String utf8)
    {
        String rs = null;

        try
        {
            rs = urlDecode(utf8.getBytes("ISO-8859-1"));
        }
        catch (UnsupportedEncodingException e)
        {
            rs = java.net.URLDecoder.decode(utf8);
        }

        return rs;
    }

    /**
     * Provides encoded version of string depending on encoding.
     * Encoding may be UTF-8 or ISO-8859-1 (default).
     *
     * <p>This implementation is the same as in
     * FileSystemProvider.mangleName().
     */
    public static String urlEncode(String data, String encoding)
    {
        // Presumably, the same caveats apply as in FileSystemProvider.
        // Don't see why it would be horribly kludgy, though.
        if ("UTF-8".equals(encoding))
        {
            return (urlEncodeUTF8(data));
        }
        else
        {
            return (urlEncode(data.getBytes()));
        }
    }

    /**
     * Provides decoded version of string depending on encoding.
     * Encoding may be UTF-8 or ISO-8859-1 (default).
     *
     * <p>This implementation is the same as in
     * FileSystemProvider.unmangleName().
     */
    public static String urlDecode(String data, String encoding) throws
        UnsupportedEncodingException,
        IllegalArgumentException
    {
        // Presumably, the same caveats apply as in FileSystemProvider.
        // Don't see why it would be horribly kludgy, though.
        if ("UTF-8".equals(encoding))
        {
            return (urlDecodeUTF8(data));
        }
        else
        {
            return (urlDecode(data.getBytes()));
        }
    }

    /**
     *  Replaces the relevant entities inside the String.
     *  All &gt;, &lt; and &quot; are replaced by their
     *  respective names.
     *
     *  @since 1.6.1
     */
    public static String replaceEntities(String src)
    {
        src = replaceString(src, "<", "&lt;");
        src = replaceString(src, ">", "&gt;");
        src = replaceString(src, "\"", "&quot;");

        return src;
    }

    /**
     *  Replaces a string with an other string.
     *
     *  @param orig Original string.  Null is safe.
     *  @param src  The string to find.
     *  @param dest The string to replace <I>src</I> with.
     */

    public static String replaceString(String orig, String src, String dest)
    {
        if (orig == null)
        {
            return null;
        }

        StringBuffer res = new StringBuffer();
        int start, end = 0, last = 0;

        while ( (start = orig.indexOf(src, end)) != -1)
        {
            res.append(orig.substring(last, start));
            res.append(dest);
            end = start + src.length();
            last = start + src.length();
        }

        res.append(orig.substring(end));

        return res.toString();
    }

    /**
     *  Replaces a part of a string with a new String.
     *
     *  @param start Where in the original string the replacing should start.
     *  @param end Where the replacing should end.
     *  @param orig Original string.  Null is safe.
     *  @param text The new text to insert into the string.
     */
    public static String replaceString(String orig, int start, int end,
                                       String text)
    {
        if (orig == null)
        {
            return null;
        }

        StringBuffer buf = new StringBuffer(orig);

        buf.replace(start, end, text);

        return buf.toString();
    }

    /**
     *  Parses an integer parameter, returning a default value
     *  if the value is null or a non-number.
     */

    public static int parseIntParameter(String value, int defvalue)
    {
        int val = defvalue;

        try
        {
            val = Integer.parseInt(value);
        }
        catch (Exception e)
        {}

        return val;
    }

    /**
     *  Gets a boolean property from a standard Properties list.
     *  Returns the default value, in case the key has not been set.
     *  <P>
     *  The possible values for the property are "true"/"false", "yes"/"no", or
     *  "on"/"off".  Any value not recognized is always defined as "false".
     *
     *  @param props   A list of properties to search.
     *  @param key     The property key.
     *  @param defval  The default value to return.
     *
     *  @return True, if the property "key" was set to "true", "on", or "yes".
     *
     *  @since 2.0.11
     */
    public static boolean getBooleanProperty(Properties props,
                                             String key,
                                             boolean defval)
    {
        String val = props.getProperty(key);

        if (val == null)
        {
            return defval;
        }

        return isPositive(val);
    }

    /**
     *  Returns true, if the string "val" denotes a positive string.  Allowed
     *  values are "yes", "on", and "true".  Comparison is case-insignificant.
     *  Null values are safe.
     *
     *  @param val Value to check.
     *  @return True, if val is "true", "on", or "yes"; otherwise false.
     *
     *  @since 2.0.26
     */
    public static boolean isPositive(String val)
    {
        if (val == null)
        {
            return false;
        }

        return (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("on") ||
                val.equalsIgnoreCase("yes"));
    }

    /**
     *  Makes sure that the POSTed data is conforms to certain rules.  These
     *  rules are:
     *  <UL>
     *  <LI>The data always ends with a newline (some browsers, such
     *      as NS4.x series, does not send a newline at the end, which makes
     *      the diffs a bit strange sometimes.
     *  <LI>The CR/LF/CRLF mess is normalized to plain CRLF.
     *  </UL>
     *
     *  The reason why we're using CRLF is that most browser already
     *  return CRLF since that is the closest thing to a HTTP standard.
     */
    public static String normalizePostData(String postData)
    {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < postData.length(); i++)
        {
            switch (postData.charAt(i))
            {
                case 0x0a: // LF, UNIX
                    sb.append("\r\n");
                    break;

                case 0x0d: // CR, either Mac or MSDOS
                    sb.append("\r\n");

                    // If it's MSDOS, skip the LF so that we don't add it again.
                    if (i < postData.length() - 1 &&
                        postData.charAt(i + 1) == 0x0a)
                    {
                        i++;
                    }
                    break;

                default:
                    sb.append(postData.charAt(i));
                    break;
            }
        }

        if (sb.length() < 2 || !sb.substring(sb.length() - 2).equals("\r\n"))
        {
            sb.append("\r\n");
        }

        return sb.toString();
    }

    private static final int EOI = 0;
    private static final int LOWER = 1;
    private static final int UPPER = 2;
    private static final int DIGIT = 3;
    private static final int OTHER = 4;

    private static int getCharKind(int c)
    {
        if (c == -1)
        {
            return EOI;
        }

        char ch = (char) c;

        if (Character.isLowerCase(ch))
        {
            return LOWER;
        }
        else if (Character.isUpperCase(ch))
        {
            return UPPER;
        }
        else if (Character.isDigit(ch))
        {
            return DIGIT;
        }
        else
        {
            return OTHER;
        }
    }

    /**
     *  Adds spaces in suitable locations of the input string.  This is
     *  used to transform a WikiName into a more readable format.
     *
     *  @param s String to be beautified.
     *  @return A beautified string.
     */
    public static String beautifyString(String s)
    {
        StringBuffer result = new StringBuffer();

        if (s == null || s.length() == 0)
        {
            return "";
        }

        int cur = s.charAt(0);
        int curKind = getCharKind(cur);

        int prevKind = LOWER;
        int nextKind = -1;

        int next = -1;
        int nextPos = 1;

        while (curKind != EOI)
        {
            next = (nextPos < s.length()) ? s.charAt(nextPos++) : -1;
            nextKind = getCharKind(next);

            if ( (prevKind == UPPER) && (curKind == UPPER) &&
                (nextKind == LOWER))
            {
                result.append(' ');
                result.append( (char) cur);
            }
            else
            {
                result.append( (char) cur);
                if ( ( (curKind == UPPER) && (nextKind == DIGIT))
                    ||
                    ( (curKind == LOWER) &&
                     ( (nextKind == DIGIT) || (nextKind == UPPER)))
                    ||
                    ( (curKind == DIGIT) &&
                     ( (nextKind == UPPER) || (nextKind == LOWER))))
                {
                    result.append(' ');
                }
            }
            prevKind = curKind;
            cur = next;
            curKind = nextKind;
        }

        return result.toString();
    }

    /**
     *
     * @param str
     * @return
     * @author Jie Bao
     * @version 2003-11-01
     */
    public static boolean isStringEmpty(String str)
    {
        return str == null || str.length() == 0;
    }

    /**
     * Removes all duplicates of specified char in given text.
     * @param text the text to parse.
     * @param lookfor the char to remove duplicates.
     * @return the updated text.
     */
    public static String removeDuplicate(
        String original,
        char lookfor)
    {
        String result = original;

        if (!isStringEmpty(original))
        {
            String t_strLookFor = new String(new char[]
                                             {lookfor});

            StringTokenizer t_strTokenizer =
                new StringTokenizer(original, t_strLookFor, false);

            if (t_strTokenizer.hasMoreTokens())
            {
                result = "";
            }

            while (t_strTokenizer.hasMoreTokens())
            {
                result += t_strTokenizer.nextToken();

                if (t_strTokenizer.hasMoreTokens())
                {
                    result += t_strLookFor;
                }
            }
        }

        return result;
    }

    /**
     * Removes all quotes at the beginning and ending of given string.
     * @param text the content to unquote.
     * @return the unquoted version of such content.
     */
    public static String unquote(String literal)
    {
        String result = "";

        if ( (literal != null)
            && (literal.length() > 0))
        {
            if (literal.charAt(0) != '\"')
            {
                if (literal.charAt(0) != '\'')
                {
                    result = literal;
                }
                else
                {
                    if (literal.trim().length() > 0)
                    {
                        result =
                            literal.substring(1, literal.length() - 1);
                    }
                }
            }
            else
            {
                result =
                    literal.substring(1, literal.length() - 1);
            }
        }

        return result;
    }

    /**
     * Substitutes most common HTML escape sequences with their unescaped
     * version.<br/>
     * i.e. it takes all "&amp;lt;" and replaces them with "<".
     * @param htmlContent the escaped html contents.
     * @return the unescaped version of such contents.
     */
    public static String unescapeHTML(String htmlContents)
    {
        String result = htmlContents;

        result = replace(result, "&lt;", "<");
        result = replace(result, "&gt;", ">");
        result = replace(result, "&quot;", "\"");
        result = replace(result, "&apos;", "'");

        return result;
    }

    /**
     * Puts a quote before and after given string.
     * @param text the content to quote.
     * @return the quoted version of such content.
     */
    public static String quote(String literal)
    {
        String result = "";

        boolean t_bProcessed = true;

        if ( (literal != null)
            && (literal.length() > 0))
        {
            if (literal.charAt(0) != '\"')
            {
                if (literal.charAt(0) != '\'')
                {
                    result = "\"" + literal + "\"";
                }
                else
                {
                    if (literal.trim().length() > 0)
                    {
                        result =
                            "\""
                            + literal.substring(1, literal.length() - 1)
                            + "\"";
                    }
                    else
                    {
                        t_bProcessed = false;
                    }
                }
            }
            else
            {
                result = literal;
            }
        }

        if (!t_bProcessed)
        {
            result = "\"\"";
        }

        return result;
    }

    /**
     * Replaces a sequence with another inside a text content.
     * @param htmlContent the text content to update.
     * @param tagToBeReplaced the undesired occurrence.
     * @param replacingTag the desired one.
     * @return the updated version of the original text.
     */
    public static String replace(
        CharSequence htmlContent,
        String tagToBeReplaced,
        String replacingTag)
    {
        Pattern t_Pattern = Pattern.compile("(.*?)" + tagToBeReplaced + "(.*)");
        Matcher t_Matcher = t_Pattern.matcher(htmlContent);
        String t_sbResult = t_Matcher.replaceAll(replacingTag);
        return t_sbResult;
    }

}
