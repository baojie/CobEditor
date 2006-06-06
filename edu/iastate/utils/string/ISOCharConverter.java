/*   ********************************************************************   **
 **   Copyright notice                                                       **
 **                                                                          **
 **   (c) 2003 RSSOwl Development Team					                              **
 **   http://rssowl.sourceforge.net/   			                                **
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

package edu.iastate.utils.string;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.io.UnsupportedEncodingException;

/**
 * Class provides methods to decode ISO encoded
 * characters.
 * */
public class ISOCharConverter
{
    static final String HEX_DIGITS = "0123456789ABCDEF";

    /**
     * Decode ISO encoded character
     * @param isoBytes Bytes representing the char
     * @return String representing the encoded char
     */
    public static String decodeChar(byte[] isoBytes)
    {

        /** Return the Entity value if decode fails */
        String decodedChar = String.valueOf("&" + (int) isoBytes[0] + ";");

        /** Only decode if Charset ISO-8859-1 is available */
        if (Charset.isSupported("ISO-8859-1"))
        {
            Charset isoCharset = Charset.forName("ISO-8859-1");
            CharsetDecoder decoder = isoCharset.newDecoder();

            ByteBuffer isoBytesRef = ByteBuffer.wrap(isoBytes);
            CharBuffer decodedCharBuf = null;
            try
            {
                decodedCharBuf = decoder.decode(isoBytesRef);
            }
            catch (CharacterCodingException e)
            {
                //RSSOwlGUI.logger.log("decodeChar()", e);
            }
            return String.valueOf(decodedCharBuf);
        }
        return decodedChar;
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
                        decodeBytes[decodedByteCount++] = (byte) ( (HEX_DIGITS.
                            indexOf(bytes[++count]) << 4) +
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

}
