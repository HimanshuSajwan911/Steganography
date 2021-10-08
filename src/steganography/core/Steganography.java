
package steganography.core;

import static steganography.core.decoder.ByteTo_Converter.byteToInt;
import static steganography.core.decoder.SteganographyDecoder.extractByte;
import static steganography.core.encoder.SteganographyEncoder.addBits;
import static steganography.core.encoder._ToByteConverter.intToByte;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;

/**
 * @author Himanshu Sajwan.
 */

public class Steganography {

    private static int KEY_SIZE = 4; // 4 byte (32 bit) key.
    private static int LENGTH_SIZE = 4; // 4 byte (32 bit) message length.
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */
    
    public static void addKey(byte[] source, int position, int key) throws InsufficientMemoryException {
        byte[] keyBytes = intToByte(key);

        addBits(source, position, keyBytes);
    }

    public static void addLength(byte[] source, int position, int length) throws InsufficientMemoryException {
        byte[] lengthBytes = intToByte(length);

        addBits(source, position, lengthBytes);
    }

    public static void addMessage(byte[] source, int position, byte[] message) throws InsufficientMemoryException {
        addBits(source, position, message);
    }
    
    /*
        ________________________________________Encoding part ends here_________________________________________
    */
    
    
    /*
        ----------------------------------------Decoding part starts here----------------------------------------
    */
    
    public static int getKey(byte[] source, int position, int key_size) throws InsufficientBitsException{
        byte[] key_bytes = extractByte(source, position, key_size);
        
        int res =  byteToInt(key_bytes);
        
        return res;
    }
    
    public static int getMessageLength(byte[] source, int position, int length_size) throws InsufficientBitsException{
        byte[] length_bytes = extractByte(source, position, length_size);
        
        return byteToInt(length_bytes);
    }
    
    public static String getMessage(byte[] source, int position, int message_length) throws InsufficientBitsException{
        return new String(extractByte(source, position, message_length));
    }
    
    /*
        ________________________________________Decoding part ends here_________________________________________
    */
    
    
    
}
