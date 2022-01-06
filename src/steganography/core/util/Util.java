package steganography.core.util;

/**
 * @author Himanshu Sajwan.
 */

public class Util {

    /**
     * Finds class name for given object of Object class.
     * eg for int returns Integer, for String returns String.
     * 
     * @param object whose class name to find.
     * 
     * @return class name of object.
     */
    public static String getClassName(Object object){
        return object.getClass().getSimpleName();
    }
    
    
    
}
