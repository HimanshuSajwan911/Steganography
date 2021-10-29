package steganography;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import steganography.core.Steganography;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.filehandling.Filters;

/**
 * @author Himanshu Sajwan.
 */

public class VideoSteganography extends Steganography{

    public VideoSteganography(){
        // setting default value for SOURCE_BUFFER_SIZE.
        SOURCE_BUFFER_SIZE = MB; // 1 MB
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8); // 128 KB
    }
    
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */
    
    public void encode(String sourceFile_full_path, String dataFile_full_path, String destinationFile_full_path, int key) throws InsufficientMemoryException, IOException, UnsupportedAudioFileException{
        
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
        
       // check if video format is supported.
        
        String extension = Filters.getFileExtension(src_file);
        
        switch(extension){
           
            case "mp4": encodeMP4(sourceFile_full_path, dataFile_full_path, destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedAudioFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    
    
    


    /*
        ________________________________________Encoding part ends here_________________________________________
    */
    
    
    /*
        ----------------------------------------Decoding part starts here----------------------------------------
    */
    
     
    public void decode(String sourceFile_full_path, String destinationFile_full_path, int key) throws UnsupportedAudioFileException, IOException, FileNotFoundException, InsufficientBitsException, InvalidKeyException{
        
        if(!new File(sourceFile_full_path).exists()){
            throw new FileNotFoundException("(The system cannot find the source file specified)");
        }
        
        String extension = Filters.getFileExtension(new File(sourceFile_full_path));
        
        switch(extension){
           
            case "wav": decodeWav(sourceFile_full_path,destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedAudioFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    
        
    }
    
    
    /*
        ________________________________________Decoding part ends here_________________________________________
    */
    
    
}
