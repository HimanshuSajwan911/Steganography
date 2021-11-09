package steganography.core.decoder;

import static steganography.core.decoder.ByteTo_Converter.byteToInt;
import static steganography.core.decoder.ByteTo_Converter.byteToLong;
import steganography.core.exceptions.InsufficientBitsException;

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
     * @return byte array of extracted bytes.
     * @throws InsufficientBitsException
     */
    public static byte[] extractByte(byte[] source, int position, int amount) throws InsufficientBitsException {

        if(amount < 1){
            throw new InsufficientBitsException("number of byte cannot be less than 1.");
        }
        
        // if source array does not contain enough bits.
        if ((amount * 8 + position) > source.length) {
            throw new InsufficientBitsException("source does not contain specified bytes.");
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
     * Extracts a integer(32 bits) value from <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>"position"</B> position.
     * 
     * @param source byte array from LSB of whose, integer value is to be extracted.
     * @param position from where extraction is suppose to start.
     * 
     * @return integer value.
     * @throws InsufficientBitsException 
     */
    public static int extractInteger(byte[] source, int position) throws InsufficientBitsException{
        byte[] integer_bytes = extractByte(source, position, (Integer.SIZE / 8));
        
        int res =  byteToInt(integer_bytes);
        
        return res;
    }
    
    /**
     * Extracts a long(64 bits) value from <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>position</B> position.
     * 
     * @param source byte array from LSB of whose, long value is to be extracted.
     * @param position from where extraction is suppose to start.
     * 
     * @return long value.
     * @throws InsufficientBitsException 
     */
    public static long extractLong(byte[] source, int position) throws InsufficientBitsException{
        byte[] long_bytes = extractByte(source, position, (Long.SIZE / 8));
        
        return byteToLong(long_bytes);
    }
    
}
