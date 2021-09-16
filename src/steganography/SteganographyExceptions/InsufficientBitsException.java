
package SteganographyExceptions;

/**
 * @author Himanshu Sajwan.
 */

// InsufficientBitsException Exception class
public class InsufficientBitsException extends Exception{

    public InsufficientBitsException() {
        super("Insufficient Bits");
    }

    public InsufficientBitsException(String msg){
        super(msg);
    }
    
}
