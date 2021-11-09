package steganography.core.exceptions;

/**
 * @author Himanshu Sajwan.
 */

/**
 * UnsupportedFileException Exception class.
 */
public class UnsupportedFileException extends Exception{

    public UnsupportedFileException() {
        super("Unsupported File");
    }

    public UnsupportedFileException(String msg) {
        super(msg);
    }
    
}
