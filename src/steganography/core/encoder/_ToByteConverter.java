package steganography.core.encoder;

/**
 * @author Himanshu Sajwan.
 */

public class _ToByteConverter {

    /**
     * Convert int <B>value</B> to byte array.
     * 
     * @param value that is to be converted to byte array.
     * @return byte array of converted int.
     */
    public static byte[] intToByte(int value){
        
        byte[] byte_array = new byte[4];

        byte_array[0] = (byte) ((value >>> 24) & 0xFF);
        byte_array[1] = (byte) ((value >>> 16) & 0xFF);
        byte_array[2] = (byte) ((value >>> 8)  & 0xFF);
        byte_array[3] = (byte) ((value)        & 0xFF);
        
        return byte_array;
    }
    
}
