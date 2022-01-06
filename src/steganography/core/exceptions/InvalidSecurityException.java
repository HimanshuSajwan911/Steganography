package steganography.core.exceptions;

/**
 * @author Himanshu Sajwan.
 */

/**
 * InvalidSecurityException Exception class.
 */
public class InvalidSecurityException extends Exception {

    public InvalidSecurityException() {
        super("Invalid Security!");
    }

    public InvalidSecurityException(String msg) {
        super(msg);
    }

}
