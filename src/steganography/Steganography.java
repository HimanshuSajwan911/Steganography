package steganography;

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

    private void addBits(byte[] target, byte[] source) throws InsufficientMemoryException {

        if (target.length < source.length * 8) {
            throw new InsufficientMemoryException();
        }

        // loop to access all bytes of source array.
        for (int i = 0; i < source.length; i++) {
            byte data = source[i];

            // loop to extract all bits of data byte.
            for (int j = 7; j >= 0; j--, Current_Position++) {
                target[Current_Position] = (byte) ((target[Current_Position] & 0xFE) | ((data >> j) & 1));
            }

        }

    }

}
