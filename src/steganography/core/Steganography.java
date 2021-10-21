
package steganography.core;

import static steganography.core.decoder.ByteTo_Converter.byteToInt;
import static steganography.core.decoder.ByteTo_Converter.byteToLong;
import static steganography.core.decoder.SteganographyDecoder.extractByte;
import static steganography.core.encoder.SteganographyEncoder.addBits;
import static steganography.core.encoder._ToByteConverter.intToByte;
import static steganography.core.encoder._ToByteConverter.longToByte;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;

/**
 * @author Himanshu Sajwan.
 */

public class Steganography {

    /**
     * Specifies the size of KEY in byte.
     */
    public static final int KEY_SIZE_BYTE = 4; 
    
    /**
     * Specifies the size of KEY in bits.
     */
    public static final int KEY_SIZE_BIT = 32;
    
    /**
     * Specifies the size of Message Length in byte.
     */
    public static final int LENGTH_SIZE_BYTE = 8;
    
    /**
     * Specifies the size of Message Length in bits.
     */
    public static final int LENGTH_SIZE_BIT = 64;
    
    
    /*
        =========================================================================================================
        |                                       Encoding part starts here                                       |
        =========================================================================================================
    */
    
    public static void addKey(byte[] source, int position, int key) throws InsufficientMemoryException {
        byte[] keyBytes = intToByte(key);

        addBits(source, position, position + KEY_SIZE_BIT , keyBytes, 0, keyBytes.length);
    }

    public static void addMessageLength(byte[] source, int position, long length) throws InsufficientMemoryException {
        byte[] lengthBytes = longToByte(length);

        addBits(source, position, position + LENGTH_SIZE_BIT, lengthBytes, 0, lengthBytes.length);
    }

    public static void addMessage(byte[] source, int position, byte[] message) throws InsufficientMemoryException {
        addBits(source, position, source.length, message, 0, message.length);
    }
   
    /*
        ---------------------------------------------------------------------------------------------------------
        |                                       Encoding part ends here                                         |
        ---------------------------------------------------------------------------------------------------------
    */
    
    
    
    /*
        =========================================================================================================
        |                                       Decoding part starts here                                       |
        =========================================================================================================
    */
    
    public static int getKey(byte[] source, int position, int key_size) throws InsufficientBitsException{
        byte[] key_bytes = extractByte(source, position, key_size);
        
        int res =  byteToInt(key_bytes);
        
        return res;
    }
    
    public static long getMessageLength(byte[] source, int position, int length_size) throws InsufficientBitsException{
        byte[] length_bytes = extractByte(source, position, length_size);
        
        return byteToLong(length_bytes);
    }
    
    public static byte[] getMessage(byte[] source, int position, int message_length) throws InsufficientBitsException{
        return extractByte(source, position, message_length);
    }
    
    /*
        ---------------------------------------------------------------------------------------------------------
        |                                       Decoding part ends here                                         |
        ---------------------------------------------------------------------------------------------------------
    */
    
    
    
}
