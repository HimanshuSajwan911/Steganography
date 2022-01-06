
package steganography.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Himanshu Sajwan.
 */

public class Files {

    /**
     * Skips ie reads <B>amount</B> number of bytes from <B>input</B> InputStream and writes those bytes to <B>output</B> OutputStream.
     * 
     * @param input InputStream object from where bytes are to be read.
     * @param output OutputStream object to where bytes are to be written.
     * @param amount number of bytes that to be read and written.
     * @throws IOException 
     */
    public static void skip(InputStream input, OutputStream output, int amount) throws IOException{
        byte[] b = new byte[amount];
        
        if (input != null) {
            input.read(b);
        }
        
        if (output != null) {
            output.write(b);
        }
    }
    
    /**
     * Returns extension of file.
     * 
     * @param file path of file.
     * @return extension ie (String after . ) if exists else empty String.
     */
    public static String getFileExtension(File file) {
        String s = file.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            return s.substring(i + 1).toLowerCase();
        }
        return "";
    }
    
    
    
}
