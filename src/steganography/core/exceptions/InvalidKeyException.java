package steganography.core.exceptions;

/**
 * @author Himanshu Sajwan.
 */

/**
 * InvalidKeyException Exception class.
 */
public class InvalidKeyException extends Exception{
    
    public InvalidKeyException(){
        super("Invalid Key!");
    }
    
    public InvalidKeyException(String msg){
        super(msg);
    }
    
}
