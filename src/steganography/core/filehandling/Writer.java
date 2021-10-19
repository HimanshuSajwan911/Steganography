package steganography.core.filehandling;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Himanshu Sajwan.
 */

public class Writer {

    /**
     * Writes <B>source</B> byte array to <B>destination</B> location.
     *
     * @param source byte array which needs to be written.
     * @param destination location where file needs to be written.
     *
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     *
     */
    public static void writeBytes(byte[] source, String destination) throws FileNotFoundException, IOException {

        try (FileOutputStream stream = new FileOutputStream(destination)) {
            stream.write(source);
        }

    }
    
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

}
