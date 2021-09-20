package steganography.core.filehandling;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

}
