package steganography.core.decoder;

import steganography.core.exceptions.InsufficientBytesException;

/**
 * @author Himanshu Sajwan.
 */

public class ByteTo_Converter {

    /**
     * Converts byte array to int from index <B>"starting"</B> to index <B>"starting + 3"</B>.
     *
     * @param source array containing bytes that needs to be converted to int.
     * <P>(must contain at least 4 bytes.)</P>
     * 
     * @return int value of byte array.
     *
     * @throws InsufficientBytesException
     */
    public static int byteToInt(byte[] source, int starting) throws InsufficientBytesException {

        if ((source.length - starting) < 4) {
            throw new InsufficientBytesException("requires at least 4 bytes.");
        }

        return source[starting] << 24 | (source[starting + 1] & 0xFF) << 16 | (source[starting + 2] & 0xFF) << 8 | (source[starting + 3] & 0xFF);
    }
    
    /**
     * Converts byte array to int from index 0 to index 3.
     *
     * @param source array containing bytes that needs to be converted to int.
     * <P>(must contain at least 4 bytes.)</P>
     * 
     * @return int value of byte array.
     *
     * @throws InsufficientBytesException
     */
    public static int byteToInt(byte[] source) throws InsufficientBytesException {
        
        return byteToInt(source, 0);
    }
    
    /**
     * Converts byte array to float from index <B>"starting"</B> to index <B>"starting + 3"</B>.
     *
     * @param source array containing bytes that needs to be converted to float.
     * <P>(must contain at least 4 bytes.)</P>
     * 
     * @return float value of byte array.
     *
     * @throws InsufficientBytesException
     */
    public static float byteToFloat(byte[] source, int starting) throws InsufficientBytesException {
        
        return Float.intBitsToFloat(byteToInt(source, starting));
    }
    
    /**
     * Converts byte array to float from index 0 to index 3.
     *
     * @param source array containing bytes that needs to be converted to float.
     * <P>(must contain at least 4 bytes.)</P>
     * 
     * @return float value of byte array.
     *
     * @throws InsufficientBytesException
     */
    public static float byteToFloat(byte[] source) throws InsufficientBytesException{
        
        return byteToFloat(source, 0);
    }
    
    
    /**
     * Converts byte array to long from index <B>"starting"</B> to index <B>"starting + 7"</B>.
     *
     * @param source array containing bytes that needs to be converted to long.
     * 
     * <P>(must contain at least 8 bytes.)</P>
     * 
     * @return long value of byte array.
     *
     * @throws InsufficientBytesException
     */
    public static long byteToLong(byte[] source, int starting) throws InsufficientBytesException {

        if ((source.length - starting)  < 8) {
            throw new InsufficientBytesException("requires at least 8 bytes.");
        }
        
        long byte0 = source[starting];
        long byte1 = source[starting + 1];
        long byte2 = source[starting + 2];
        long byte3 = source[starting + 3];
        long byte4 = source[starting + 4];
        long byte5 = source[starting + 5];
        long byte6 = source[starting + 6];
        long byte7 = source[starting + 7];

        return byte0 << 56 | (byte1 & 0xFF) << 48 | (byte2 & 0xFF) << 40 | (byte3 & 0xFF) << 32 | 
              (byte4 & 0xFF) << 24 | (byte5 & 0xFF) << 16 | (byte6 & 0xFF) << 8 | (byte7 & 0xFF);  
        
    }
    
    /**
     * Converts byte array to long from index 0 to index 7.
     *
     * @param source array containing bytes that needs to be converted to long.
     * 
     * <P>(must contain at least 8 bytes.)</P>
     * 
     * @return long value of byte array.
     *
     * @throws InsufficientBytesException
     */
    public static long byteToLong(byte[] source) throws InsufficientBytesException {

        return byteToLong(source, 0);
    }

}
