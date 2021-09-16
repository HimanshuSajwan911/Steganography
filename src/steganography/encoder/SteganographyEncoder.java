package steganography.encoder;

import steganography.exceptions.InsufficientMemoryException;

/**
 * @author Himanshu Sajwan.
 */
public class SteganographyEncoder {

    /**
     * Insert all the bits of source byte array (starting from source_position)
     * in
     * <B>LSB</B> position of target byte array (starting from target_position).
     *
     * @param target byte array in which bytes are to be inserted.
     * @param target_position starting position for target array from where bits
     * are to be inserted.
     * @param source byte array from where bytes are to be inserted in target
     * byte array.
     *
     * @throws InsufficientMemoryException
    *
     */
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
