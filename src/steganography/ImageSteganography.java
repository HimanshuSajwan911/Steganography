package steganography;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import steganography.core.Steganography;
import static steganography.core.Steganography.MB;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.exceptions.UnsupportedImageFileException;
import static steganography.core.util.Files.getFileExtension;

/**
 * @author Himanshu Sajwan.
 */

public class ImageSteganography extends Steganography{

    public ImageSteganography(){
        // setting default value for SOURCE_BUFFER_SIZE.
        SOURCE_BUFFER_SIZE = MB; // 1 MB
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8); // 128 KB
    }
    
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */
    
    /**
     * Encode Image file with a 32 bit <B>key</B> from <B>sourceFile_full_path</B> location
     * with file from <B>dataFile_full_path</B> starting from <B>offset</B> position and 
     * save this encoded Image file to <B>destinationFile_full_path</B> location.
     * 
     * @param sourceFile_full_path location of source Image file.
     * @param dataFile_full_path location of data file that is to be encoded.
     * @param destinationFile_full_path location to save encoded Image file.
     * @param key to secure encoded file with a 32 bit size integer.
     * 
     * @throws InsufficientMemoryException
     * @throws IOException
     * @throws UnsupportedImageFileException 
     */
    public void encode(String sourceFile_full_path, String dataFile_full_path, String destinationFile_full_path, int key) throws InsufficientMemoryException, IOException, UnsupportedImageFileException{
        
        File src_file = new File(sourceFile_full_path);
        File data_file = new File(dataFile_full_path);
        
        if(!src_file.exists()){
            throw new FileNotFoundException("(The system cannot find the source file specified)");
        }
        
                
        if(!data_file.exists()){
            throw new FileNotFoundException("(The system cannot find the data file specified)");
        }
        
        if(src_file.length() < data_file.length() * 8){
            throw new InsufficientMemoryException("not enough space in source file!!");
        }
        
        
        String extension = getFileExtension(src_file);
        
                
        switch(extension){
           
            case "png": encodePNG(sourceFile_full_path, dataFile_full_path, destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedImageFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    

    /*
        ________________________________________Encoding part ends here_________________________________________
    */
    
    
    /*
        ----------------------------------------Decoding part starts here----------------------------------------
    */
    
     /**
      * Decode Image file with a 32 bit <B>key</B> from <B>sourceFile_full_path</B> location
      * starting from provided offset position  and 
      * save this encoded file to <B>destinationFile_full_path</B> location.
      * 
      * @param sourceFile_full_path location of encoded Image file.
      * @param destinationFile_full_path location to save decoded file.
      * @param key to decode file with a 32 bit size integer. 
      * 
      * @throws IOException
      * @throws FileNotFoundException
      * @throws InsufficientBitsException
      * @throws InvalidKeyException
      * @throws UnsupportedImageFileException 
      */
    public void decode(String sourceFile_full_path, String destinationFile_full_path, int key) throws IOException, FileNotFoundException, InsufficientBitsException, InvalidKeyException, UnsupportedImageFileException{
        
        if(!new File(sourceFile_full_path).exists()){
            throw new FileNotFoundException("(The system cannot find the source file specified)");
        }
        
        String extension = getFileExtension(new File(sourceFile_full_path));
        
        switch(extension){
           
            case "png": decodePNG(sourceFile_full_path,destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedImageFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    
    
    
    
    /*
        ________________________________________Decoding part ends here_________________________________________
    */
    
}
