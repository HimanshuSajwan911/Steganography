package steganography.core.encoder;

import static steganography.core.encoder._ToByteConverter.intToByte;
import static steganography.core.encoder._ToByteConverter.longToByte;
import steganography.core.exceptions.InsufficientMemoryException;

/**
 * @author Himanshu Sajwan.
 */

public class SteganographyEncoder {

    /**
     * Insert all the bits of source byte array (starting from <B>source_start_position</B> till <B>source_end_position</B>)
     * in
     * <B>LSB</B> position of target byte array (starting from <B>target_start_position</B> till <B>target_end_position</B>).
     *
     * @param target byte array in which bytes are to be inserted.
     * @param target_start_position starting position for target array, where bits
     * are to be inserted.
     * @param target_end_position ending position for target array, till where bits
     * are to be inserted.
     * @param source byte array from where bytes are to be inserted in target
     * byte array.
     * @param source_start_position starting position of source byte array for bytes that are to be encoded.
     * @param source_end_position ending position of source byte array till bytes to be encoded.
     *
     * @throws InsufficientMemoryException
     *
     */
    public static void insertBits(byte[] target, int target_start_position, int target_end_position, byte[] source, int source_start_position, int source_end_position) throws InsufficientMemoryException {

        int source_length = source_end_position - source_start_position;
        int target_length = target_end_position - target_start_position;
        
        if (target_length < (source_length * 8 + target_start_position)) {
            throw new InsufficientMemoryException();
        }

        // loop to access all bytes of source array.
        for (int i = source_start_position; i < source_end_position; i++) {
            byte data = source[i];

            // loop to extract all bits of byte "data".
            for (int j = 7; j >= 0; j--, target_start_position++) {
                target[target_start_position] = (byte) ((target[target_start_position] & 0xFE) | ((data >>> j) & 1));
            }

        }

    }

    /**
     * Inserts a integer(32 bits) <B>"value"</B> in <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>position</B> position.
     * 
     * @param source byte array in LSB of whose, integer is to be inserted.
     * @param position from where insertion is supposed to start.
     * @param value 32 bit integer value that is to be inserted.
     * 
     * @throws InsufficientMemoryException 
     */
    public static void insertInteger(byte[] source, int position, int value) throws InsufficientMemoryException {
        byte[] intBytes = intToByte(value);

        insertBits(source, position, position + Integer.SIZE , intBytes, 0, intBytes.length);
    }

    /**
     * Inserts a long(64 bits) <B>"value"</B> in <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>position</B> position.
     * 
     * @param source byte array in LSB of whose, long is to be inserted.
     * @param position from where insertion is supposed to start.
     * @param value 64 bit long value that is to be inserted.
     * 
     * @throws InsufficientMemoryException 
     */
    public static void insertLong(byte[] source, int position, long value) throws InsufficientMemoryException {
        byte[] longBytes = longToByte(value);

        insertBits(source, position, position + Long.SIZE, longBytes, 0, longBytes.length);
    }
    
    
}
