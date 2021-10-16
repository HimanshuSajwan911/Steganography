
package steganography.core.filehandling;

import java.io.File;

/**
 * @author Himanshu Sajwan.
 */

public class Filters {

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
