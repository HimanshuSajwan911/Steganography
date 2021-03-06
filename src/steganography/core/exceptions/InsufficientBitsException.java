package steganography.core.exceptions;

/**
 * @author Himanshu Sajwan.
 */

/**
 * InsufficientBitsException Exception class.
 */
public class InsufficientBitsException extends InsufficientException{
    
    public InsufficientBitsException() {
        super("Insufficient Bits");
    }

    public InsufficientBitsException(String msg) {
        super(msg);
    }

}
