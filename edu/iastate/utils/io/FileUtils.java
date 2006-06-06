/*
                        Utility classes.

 ******************************************************************************
 *
 * Filename: $RCSfile: FileUtils.java,v $
 *
 * Author: Jose San Leandro Armendáriz
 *
 * Description: Provides some useful methods when working with files.
 *
 * File version: $Revision: 1.1 $
 *
 * Project version: $Name:  $
 *                  ("Name" means no concrete version has been checked out)
 *
 * $Id: FileUtils.java,v 1.1 2006/06/06 18:57:38 baojie Exp $
 *
 */
package edu.iastate.utils.io;

/*
 * Importing some JDK1.3 classes.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Provides some useful methods when working with files.
 * @author <a href="mailto:jsanleandro@yahoo.es"
           >Jose San Leandro Armendáriz</a>
 * @version $Revision: 1.1 $
 * @stereotype Utils
 */
public abstract class FileUtils
//    implements	Utils
{
    /**
     * Private constructor to avoid accidental instantiation.
     */
    private FileUtils()
    {};

    /**
     * Reads a file pointed by given path, and returns its contents.
     * @param filePath the path to the file to be read.
     * @return the contents of the file.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the operation is forbidden because of
               security manager settings.
     * @throws IOException if some I/O exception occurs.
     */
    public static String readFile(String filePath) throws FileNotFoundException,
        SecurityException,
        IOException
    {
        return readFile(new File(filePath));
    }

    /**
     * Reads a file and returns its contents.
     * @param file the file to be read.
     * @return the contents of the file.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the operation is forbidden because of
               security manager settings.
     * @throws IOException if some I/O exception occurs.
     */
    public static String readFile(File file) throws FileNotFoundException,
        SecurityException,
        IOException
    {
        String result = null;

        /*
         * Instantiate a FileReader object to read file's contents.
         */
        FileReader t_frPageReader = new FileReader(file);

        /*
         * To read file's contents it's better to use BufferedReader class.
         */
        BufferedReader t_frPageBufferedReader =
            new BufferedReader(t_frPageReader);

        /*
         * Next, I find out the necessary size of the array where file's
         * contents will be copied into.
         */
        char[] t_acFileContents = new char[ (int) file.length()];

        /*
         * Now I actually read the file, and fill the array.
         */
        t_frPageBufferedReader.read(
            t_acFileContents,
            0,
            t_acFileContents.length);

        t_frPageBufferedReader.close();
        t_frPageReader.close();

        /*
         * Finally, we are lucky and can use a String constructor that exactly
         * fits our needs.
         */
        result = new String(t_acFileContents);

        /*
         * End of the method.
         */
        return result;
    }

    /**
     * Reads a file by its path and returns its contents, if possible. If some
     * exception occurs, it's ignored, and returns an empty String. This method
     * is used to avoid declaring try/catch blocks in client code.
     * @param filePath the path to the file to be read.
     * @return the contents of the file, or empty if reading cannot be
               accomplished.
     */
    public static String readFileIfPossible(String filePath)
    {
        String result = new String();

        try
        {
            result = readFile(filePath);
        }
        catch (FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }

        return result;
    }

    /**
     * Reads a file and returns its contents, if possible. If some exception
     * occurs, it's ignored, and returns an empty String. This method is used
     * to avoid declaring try/catch blocks in client code.
     * @param file the file to be read.
     * @return the contents of the file, or empty if reading cannot be
               accomplished.
     */
    public static String readFileIfPossible(File file)
    {
        String result = new String();

        try
        {
            result = readFile(file);
        }
        catch (FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }

        return result;
    }

    /**
     * Saves the contents to a file.
     * @param filePath the path of the file.
     * @param contents the text to save.
     * @return true if the process has been successfully accomplished.
     */
    public static boolean writeFileIfPossible(String filePath,
                                              String contents)
    {
        return writeFileIfPossible(new File(filePath), contents);
    }

    /**
     * Saves the contents to a file.
     * @param file the file to be overwritten.
     * @param contents the text to save.
     * @return true if the process has been successfully accomplished.
     */
    public static boolean writeFileIfPossible(File file, String contents)
    {
        boolean result = false;

        try
        {
            writeFile(file, contents);
        }
        catch (FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }

        return result;
    }

    /**
     * Writes a file referred by given path, with given contents.
     * @param filePath the path of the file.
     * @param contents the text to write.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
               operation.
     * @throws IOException if any other I/O error occurs.
     */
    public static void writeFile(String filePath, String contents) throws
        FileNotFoundException,
        SecurityException,
        IOException
    {
        writeFile(new File(filePath), contents);
    }

    /**
     * Writes a file with given contents.
     * @param file the file to write.
     * @param contents the text to write.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
               operation.
     * @throws IOException if any other I/O error occurs.
     */
    public static void writeFile(File file, String contents) throws
        FileNotFoundException,
        SecurityException,
        IOException
    {
        if ( (file != null)
            && (contents != null))
        {
            FileWriter t_fwWriter = new FileWriter(file);
            PrintWriter t_pwWriter = new PrintWriter(t_fwWriter);
            t_pwWriter.println(contents);

            t_pwWriter.close();
            t_fwWriter.close();
        }
    }

    /**
     * Copies one file from its current path to another.
     * @param filePath file's path.
     * @param destinationPath the new path of the file.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
               operation.
     * @throws IOException if any other I/O error occurs.
     */
    public static void copy(String filePath, String destinationPath) throws
        FileNotFoundException,
        SecurityException,
        IOException
    {
        copy(new File(filePath), new File(destinationPath));
    }

    /**
     * Copies the contents of a file to another.
     * @param original the content to copy.
     * @param destination the file to be overwritten.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
               operation.
     * @throws IOException if any other I/O error occurs.
     */
    public static void copy(File original, File destination) throws
        FileNotFoundException,
        SecurityException,
        IOException
    {
        FileWriter t_FileWriter = new FileWriter(destination);

        t_FileWriter.write(readFile(original));

        t_FileWriter.close();
    }

    /**
     * Copies the contents of a file (referred by given path) to another.
     * @param originalPath the path of the file to copy.
     * @param destinationPath the path of the file to be overwritten.
     * @return true if the operation ends up successfully.
     */
    public static boolean copyIfPossible(
        String originalPath, String destinationPath)
    {
        return
            copyIfPossible(new File(originalPath),
                           new File(destinationPath));
    }

    /**
     * Copies the contents of a file to another.
     * @param original the content to copy.
     * @param destination the file to be overwritten.
     * @return true if the operation ends up successfully.
     */
    public static boolean copyIfPossible(File original, File destination)
    {
        boolean result = false;

        try
        {
            copy(original, destination);

            result = true;
        }
        catch (FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }

        return result;
    }

    /**
     * Moves a file.
     * @param originalFile the file to move.
     * @param destinationFile the new file.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
               operation.
     * @throws IOException if any other I/O error occurs.
     */
    public static void move(File originalFile, File destinationFile) throws
        FileNotFoundException,
        SecurityException,
        IOException
    {
        copy(originalFile, destinationFile);

        originalFile.delete();
    }

    /**
     * Moves a file from one path to another, if possible.
     * @param filePath the path of the file to move.
     * @param destinationPath the new file's path.
     * @throws FileNotFoundException if the file is not found.
     * @throws SecurityException if the security manager forbids this
               operation.
     * @throws IOException if any other I/O error occurs.
     */
    public static void move(String filePath, String destinationPath) throws
        FileNotFoundException,
        SecurityException,
        IOException
    {
        move(new File(filePath), new File(destinationPath));
    }

    /**
     * Moves a file, if possible.
     * @param originalFile the file to move.
     * @param destinationFile the new file.
     * @return true if the operation ends up successfully.
     */
    public static boolean moveIfPossible(
        File originalFile, File destinationFile)
    {
        boolean result = false;

        try
        {
            move(originalFile, destinationFile);
            result = true;
        }
        catch (FileNotFoundException fileNotFoundException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (SecurityException securityException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }
        catch (IOException ioException)
        {
            /*
             * We have chosen not to notify of exceptions, so this
             * block of code is only descriptive.
             */
        }

        return result;
    }

    /**
     * Moves a file from one path to another, if possible.
     * @param filePath the path of the file to move.
     * @param destinationPath the new file's path.
     * @return true if the operation ends up successfully.
     */
    public static boolean moveIfPossible(
        String filePath,
        String destinationPath)
    {
        return
            moveIfPossible(
                new File(filePath),
                new File(destinationPath));
    }

    /**
     * @param f File
     * @return String
     * @author Jie Bao
     * @version 2004-07-12
     */
    public static String findExtension(String fullname)
    {
        String extensionName = "";
        // search starts from the last "/" or "\"
        int jj = fullname.lastIndexOf("/");
        if (jj == -1)
        {
            jj = fullname.lastIndexOf("\\");
        }
        if (jj == -1)
        {
            jj = 0;
        }
        String localName = fullname.substring(jj == 0 ? 0 : jj + 1);
        //System.out.println(fullname.substring(jj + 1));

        int ii = localName.lastIndexOf(".", jj);

        if (ii != -1) // find .
        {
            // return the part after .
            extensionName = localName.substring(ii + 1);
            System.out.println(extensionName);
        }
        return extensionName;
    }

    /**
     * find the file name without path or extension
     * @param fullname String
     * @return String
     */
    public static String findName(String fullname)
    {
        String fileName = null;
        int end = fullname.lastIndexOf(".");
        int begin = 0;
        if (fullname.lastIndexOf("/") != -1)
        {
            begin = fullname.lastIndexOf("/");
        }
        else if (fullname.lastIndexOf("\\") != -1)
        {
            begin = fullname.lastIndexOf("\\");
        }

        if (end != -1) // find .
        {
            // return the part after .
            fileName = fullname.substring(begin, end);
        }
        return fileName;
    }

    /**
     * @param fullname String
     * @param text String
     * @return boolean
     *
     * append text to a file
     *
     * @author Jie Bao
     * @version 2004-09-07
     */
    public static boolean appendFile(String fullname, String text)
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(
                fullname, true));
            out.write(text);
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            return false;
        }
        return true;
    }

    public static boolean isFileExists(String fullname)
    {
        boolean exists = (new File(fullname)).exists();
        return exists;
    }

    // Returns the contents of the file in a byte array.
    // 2003-10 Jie Bao
    public static byte[] getBytesFromFile(File file) throws IOException
    {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE)
        {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[ (int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               &&
               (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file " +
                                  file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    /**
     * Save to temporary file
     * @param prefix String - could be null
     * @param suffix String - could be null
     * @param contens String - to write
     * @param deleteOnExit boolean - if delete the file on exit
     * @return File
     * @author Jie Bao
     * @since 2005-03-09
     */
    public static File writeToTempFile(String prefix, String suffix,
                                       String contens, boolean deleteOnExit)
    {
        try
        {
            // Create temp file.
            if (prefix == null)
            {
                prefix = "temporary";
            }
            File temp = File.createTempFile(prefix, suffix);

            // Delete temp file when program exits.
            if (deleteOnExit)
            {
                temp.deleteOnExit();
            }

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(temp));
            out.write(contens);
            out.close();
            return temp;
        }
        catch (IOException e)
        {
            return null;
        }

    }

}
