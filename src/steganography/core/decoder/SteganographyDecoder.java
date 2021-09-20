package steganography.core.decoder;

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

            for (int j = 0; j < 7; j++, position++) {

                byte source_byte = source[position];
                retrieved_byte = (byte) ((retrieved_byte << 1) | (source_byte & 1));

            }
            
            result[pos] = retrieved_byte;
            
        }
 
        return result;
    }
    
}
