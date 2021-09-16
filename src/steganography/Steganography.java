package steganography;

import SteganographyExceptions.InsufficientBitsException;
import SteganographyExceptions.InsufficientMemoryException;

/**
 * @author Himanshu Sajwan.
 */
public class Steganography {

    private int Current_Position;

    /**
     * Returns value if Image saving was successful after hiding message.
     */
    public static final int SUCCESSFUL = 1;

    /**
     * Returns value if Image saving was unsuccessful after hiding message.
     */
    public static final int UNSUCCESSFUL = 0;

    /**
     * Returns value if Image saving was canceled.
     */
    public static final int CANCELED = -1;

    /**
     * Returns byte array of <B>amount</B> number of bytes extracted from <B>position</B> of <B>source</B> byte array.
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

        for (int i = position, pos = 0; i < amount; i++, pos++) {

            byte retrieved_byte = 0;

            for (int j = 0; j < 7; j++, position++) {

                byte source_byte = source[position];
                retrieved_byte = (byte) ((retrieved_byte << 1) | (source_byte & 1));

            }
            
            result[pos] = retrieved_byte;
            
        }
 
        return result;
    }

    
    /**
     * Insert all the bits of source byte array (starting from source_position) in
     * <B>LSB</B> position of target byte array (starting from target_position).
     * 
     * @param target byte array in which bytes are to be inserted.
     * @param target_position starting position for target array from where bits are to be inserted.
     * @param source byte array from where bytes are to be inserted in target byte array.
     *
     * @throws InsufficientMemoryException
    **/
    public static void addBits(byte[] target, int target_position, byte[] source) throws InsufficientMemoryException {

        if (target.length < (source.length * 8 + target_position)) {
            throw new InsufficientMemoryException();
        }

        // loop to access all bytes of source array.
        for (int i = 0; i < source.length; i++) {
            byte data = source[i];

            // loop to extract all bits of byte "data".
            for (int j = 7; j >= 0; j--, target_position++) {
                target[target_position] = (byte) ((target[target_position] & 0xFE) | ((data >> j) & 1));
            }

        }

    }

}
