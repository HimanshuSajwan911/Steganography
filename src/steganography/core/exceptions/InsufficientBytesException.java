package steganography.core.exceptions;

/**
 * @author Himanshu Sajwan.
 */

/**
 * InsufficientBytesException Exception class.
 */
public class InsufficientBytesException extends Exception {

    public InsufficientBytesException() {
        super("Insufficient Bytes");
    }

    public InsufficientBytesException(String msg) {
        super(msg);
    }

}
