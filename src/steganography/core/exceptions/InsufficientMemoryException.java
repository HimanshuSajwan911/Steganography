package steganography.core.exceptions;

/**
 * @author Himanshu Sajwan.
 */

/** 
 * InsufficientMemoryException Exception class.
 */
public class InsufficientMemoryException extends Exception {

    public InsufficientMemoryException() {
        super("Insufficient Memory");
    }

    public InsufficientMemoryException(String msg) {
        super(msg);
    }

}
