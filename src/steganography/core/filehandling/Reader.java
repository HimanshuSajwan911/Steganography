package steganography.core.filehandling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Himanshu Sajwan.
 */

public class Reader {

    /**
     * Reads file from given <B>source</B> and returns byte array of that file.
     * 
     * @param source path of file to be read.
     * @return byte array of source file.
     * @throws IOException 
     */
    public static byte[] readBytes(String source) throws IOException{
        return Files.readAllBytes(Paths.get(source));
    }
  
}
