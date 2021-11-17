package steganography;

import steganography.core.Steganography;
import static steganography.core.Steganography.MB;

/**
 * @author Himanshu Sajwan.
 */

public class ImageSteganography extends Steganography{

    public ImageSteganography(){
        // setting default value for SOURCE_BUFFER_SIZE.
        SOURCE_BUFFER_SIZE = MB; // 1 MB
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8); // 128 KB
    }
    
    
   
    
}
