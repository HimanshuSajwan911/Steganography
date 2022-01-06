package steganography.core.exceptions;

/**
 * @author Himanshu Sajwan.
 */

/**
 * InsufficientBytesException Exception class.
 */
public class InsufficientBytesException extends InsufficientException {

    public InsufficientBytesException() {
        super("Insufficient Bytes");
    }

    public InsufficientBytesException(String msg) {
        super(msg);
    }

}
