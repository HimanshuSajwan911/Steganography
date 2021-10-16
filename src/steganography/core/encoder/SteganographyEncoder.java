package steganography.core.encoder;

import steganography.core.exceptions.InsufficientMemoryException;

/**
 * @author Himanshu Sajwan.
 */
public class SteganographyEncoder {

    /**
     * Insert all the bits of source byte array (starting from <B>source_start_position</B> till <B>source_end_position</B>)
     * in
     * <B>LSB</B> position of target byte array (starting from <B>target_position</B>).
     *
     * @param target byte array in which bytes are to be inserted.
     * @param target_position starting position for target array, where bits
     * are to be inserted.
     * @param source byte array from where bytes are to be inserted in target
     * byte array.
     * @param source_start_position starting position of source byte array for bytes that are to be encoded.
     * @param source_end_position ending position of source byte array till bytes to be encoded.
     *
     * @throws InsufficientMemoryException
    *
     */
    public static void addBits(byte[] target, int target_position, byte[] source, int source_start_position, int source_end_position) throws InsufficientMemoryException {

        int source_length = source_end_position - source_start_position;
        
        if (target.length < (source_length * 8 + target_position)) {
            throw new InsufficientMemoryException();
        }

        // loop to access all bytes of source array.
        for (int i = source_start_position; i < source_end_position; i++) {
            byte data = source[i];

            // loop to extract all bits of byte "data".
            for (int j = 7; j >= 0; j--, target_position++) {
                target[target_position] = (byte) ((target[target_position] & 0xFE) | ((data >>> j) & 1));
            }

        }

    }

}
