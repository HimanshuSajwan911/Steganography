package steganography.decoder;

/**
 * @author Himanshu Sajwan.
 */

public class ByteTo_Converter {

    public static int byteToInt(byte[] source){
        
        if(source.length != 4){
            throw new IllegalArgumentException("int have only 4 bytes.");
        }
        
        int result = 0;
        
        for(int i = 0; i < 4; i++){
            
            byte data = source[i];
            
            for(int j = 7; j >= 0; j--){
                result = (result << 1) | (data & 1);
            }
        }
        
        return result;
    } 
    
}
