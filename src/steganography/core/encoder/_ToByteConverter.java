package steganography.core.encoder;

/**
 * @author Himanshu Sajwan.
 */

public class _ToByteConverter {

    /**
     * Convert int <B>value</B> to byte array.
     * 
     * @param value that is to be converted to byte array.
     * @return byte array of int value.
     */
    public static byte[] intToByte(int value){
        
        byte[] byte_array = new byte[4];

        byte_array[0] = (byte) ((value >>> 24) & 0xFF);
        byte_array[1] = (byte) ((value >>> 16) & 0xFF);
        byte_array[2] = (byte) ((value >>> 8)  & 0xFF);
        byte_array[3] = (byte) ((value)        & 0xFF);
        
        return byte_array;
    }
    
    
    /**
     * Convert float <B>value</B> to byte array.
     * 
     * @param value that is to be converted to byte array.
     * @return byte array of float value.
     */
    public static byte[] floatToByte(float value){
        
        return intToByte(Float.floatToRawIntBits(value));
    }
    
    
    /**
     * Convert long <B>value</B> to byte array.
     * 
     * @param value that is to be converted to byte array.
     * @return byte array of long value.
     */
    public static byte[] longToByte(long value){
        
        byte[] byte_array = new byte[8];
        
        byte_array[0] = (byte) ((value >>> 56) & 0xFF);
        byte_array[1] = (byte) ((value >>> 48) & 0xFF);
        byte_array[2] = (byte) ((value >>> 40) & 0xFF);
        byte_array[3] = (byte) ((value >>> 32) & 0xFF);
        byte_array[4] = (byte) ((value >>> 24) & 0xFF);
        byte_array[5] = (byte) ((value >>> 16) & 0xFF);
        byte_array[6] = (byte) ((value >>> 8)  & 0xFF);
        byte_array[7] = (byte) ((value)        & 0xFF);
        
        return byte_array;
    }
    
    
    /**
     * Convert double <B>value</B> to byte array.
     * 
     * @param value that is to be converted to byte array.
     * @return byte array of double value.
     */
    public static byte[] doubleToByte(double value){
        
        return longToByte(Double.doubleToRawLongBits(value));
    }
    
}
