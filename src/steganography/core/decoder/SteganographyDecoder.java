package steganography.core.decoder;

import static steganography.core.decoder.ByteTo_Converter.byteToFloat;
import static steganography.core.decoder.ByteTo_Converter.byteToInt;
import static steganography.core.decoder.ByteTo_Converter.byteToLong;
import steganography.core.exceptions.InsufficientBytesException;

/**
 * @author Himanshu Sajwan.
 */
public class SteganographyDecoder {

    /**
     * Returns byte array of <B>"amount"</B> number of bytes, 
     * extracted from <I>LSB</I> position of each byte 
     * of <B>source</B> byte array starting from <B>"position"</B> position.
     * 
     * @param source byte array which contains bytes to be extracted.
     * @param position from where bytes are to be extracted.
     * @param amount number of bytes to be extracted.
     * 
     * @return byte array of extracted bytes.
     * 
     * @throws InsufficientBytesException
     */
    public static byte[] extractByte(byte[] source, int position, int amount) throws InsufficientBytesException {

        if(amount < 1){
            throw new InsufficientBytesException("number of byte cannot be less than 1.");
        }
        
        // if source array does not contain enough bits.
        if ((amount * 8 + position) > source.length) {
            throw new InsufficientBytesException("source does not contain specified bytes.");
        }
        
        byte[] result = new byte[amount];

        for (int i = 0, pos = 0; i < amount; i++, pos++) {

            byte retrieved_byte = 0;

            // loop to extract 8 LSB bits from 8 byte values.
            for (int j = 0; j < 8; j++, position++) {

                byte source_byte = source[position];
                retrieved_byte = (byte) ((retrieved_byte << 1) | (source_byte & 1));

            }
            
            result[pos] = retrieved_byte;
            
        }
 
        return result;
    }
    
    /**
     * Extracts a 32 bits integer value from <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>"position"</B> position.
     * 
     * @param source byte array from LSB of whose, integer value is to be extracted.
     * @param position from where extraction is suppose to start.
     * 
     * @return integer value.
     * 
     * @throws InsufficientBytesException
     */
    public static int extractInteger(byte[] source, int position) throws InsufficientBytesException{
        byte[] integer_bytes = extractByte(source, position, (Integer.BYTES));
        
        int res =  byteToInt(integer_bytes);
        
        return res;
    }
    
    /**
     * Extracts a 32 bits float value from <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>"position"</B> position.
     * 
     * @param source byte array from LSB of whose, float value is to be extracted.
     * @param position from where extraction is suppose to start.
     * 
     * @return float value.
     * 
     * @throws InsufficientBytesException
     */
    public static float extractFloat(byte[] source, int position) throws InsufficientBytesException{
        byte[] float_bytes = extractByte(source, position, (Float.BYTES));
        
        float res =  byteToFloat(float_bytes);
        
        return res;
    }
    
    /**
     * Extracts a 64 bits long value from <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>position</B> position.
     * 
     * @param source byte array from LSB of whose, long value is to be extracted.
     * @param position from where extraction is suppose to start.
     * 
     * @return long value.
     * 
     * @throws InsufficientBytesException
     */
    public static long extractLong(byte[] source, int position) throws InsufficientBytesException{
        byte[] long_bytes = extractByte(source, position, (Long.BYTES));
        
        return byteToLong(long_bytes);
    }
    
}
