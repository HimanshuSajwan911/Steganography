package steganography;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import steganography.core.Steganography;
import static steganography.core.Steganography.KEY_SIZE_BIT;
import static steganography.core.Steganography.LENGTH_SIZE_BIT;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.exceptions.UnsupportedVideoFileException;
import steganography.core.filehandling.Filters;
import static steganography.core.filehandling.Writer.skip;
import steganography.core.util.MP4;
import static steganography.core.encoder.SteganographyEncoder.insertBits;

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
    
    public void encode(String sourceFile_full_path, String dataFile_full_path, String destinationFile_full_path, int key) throws InsufficientMemoryException, IOException, UnsupportedVideoFileException{
        
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
                                
                       
                        
            default:    throw new UnsupportedVideoFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    
    private void encodeMP4(String sourceFile_full_path,String dataFile_full_path, String destinationFile_full_path, int key) throws IOException, InsufficientMemoryException, UnsupportedVideoFileException {

        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileInputStream  data_input_Stream   = new FileInputStream(dataFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
            
            // length of data file.
            long data_file_length = new File(dataFile_full_path).length();
            
            int noOfSourceBytes, noOfDataBytes;
           
            MP4 mp4 = new MP4(sourceFile_full_path);
            
            int position = mp4.getMdat_position();
            long source_length = mp4.getMdat_SIZE();
             
            if (source_length < (data_file_length * 8) + KEY_SIZE_BIT + LENGTH_SIZE_BIT + offset) {
                throw new InsufficientMemoryException("not enough space in source file!!");
            }

            // skips modifying source header.
            skip(source_input_Stream, output_Stream, position + offset);
             
            // adding key.
            encodeKey(source_input_Stream, output_Stream, key);
            
            // adding message length.
            encodeMessageLength(source_input_Stream, output_Stream, data_file_length);
            
            
            // ----------------------------adding data starts--------------------------//
            
            // to store source byte stream.
            byte[] source = new byte[SOURCE_BUFFER_SIZE];
            
            // to store data byte stream.
            byte[] data = new byte[DATA_BUFFER_SIZE];
            
            // while source has bytes.
            while ((noOfSourceBytes = source_input_Stream.read(source)) > 0) {
               
                // if data bytes exists.
                if((noOfDataBytes = data_input_Stream.read(data)) > 0){
                    insertBits(source, 0, source.length, data, 0, noOfDataBytes);
                }
                
                output_Stream.write(source, 0, noOfSourceBytes);
                
            }
 
            // ----------------------------adding data ends--------------------------//
        } 

    }
    


    /*
        ________________________________________Encoding part ends here_________________________________________
    */
    
    
    /*
        ----------------------------------------Decoding part starts here----------------------------------------
    */
    
     
    public void decode(String sourceFile_full_path, String destinationFile_full_path, int key) throws UnsupportedVideoFileException, IOException, FileNotFoundException, InsufficientBitsException, InvalidKeyException{
        
        if(!new File(sourceFile_full_path).exists()){
            throw new FileNotFoundException("(The system cannot find the source file specified)");
        }
        
        String extension = Filters.getFileExtension(new File(sourceFile_full_path));
        
        switch(extension){
           
            case "mp4": decodeMP4(sourceFile_full_path,destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedVideoFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    
    
    private void decodeMP4(String sourceFile_full_path, String destinationFile_full_path, int key) throws FileNotFoundException, IOException, InsufficientBitsException, InvalidKeyException{
        
        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
            
            MP4 mp4 = new MP4(sourceFile_full_path);
            
            int position = mp4.getMdat_position();
            long source_length = mp4.getMdat_SIZE();
             
            // if not enough data to extract ie KEY_SIZE_BIT (32 bytes) and LENGTH_SIZE_BIT (64 bytes).
            if(source_length < KEY_SIZE_BIT + LENGTH_SIZE_BIT + offset){
                throw new InsufficientBitsException("not enough data in source file!!");
            }
            
            // skips source header.
            skip(source_input_Stream, null, position + offset);
            
            // decoding key.
            int extracted_key = getKey(source_input_Stream);
            
            if(extracted_key != key){
                throw new InvalidKeyException();
            }
            
            // decoding message length.
            long length = getMessageLength(source_input_Stream);
            
            
            // ----------------------------decoding data starts--------------------------//
            
            // to store source byte stream.
            byte[] source = new byte[SOURCE_BUFFER_SIZE];
            
            int extract_length;
            
            while(length > 0){
                
                if(length <= DATA_BUFFER_SIZE){
                    extract_length = (int)length;
                }
                else{
                    extract_length = DATA_BUFFER_SIZE;
                }
                source_input_Stream.read(source);
                
                byte[] extracted_data = getMessage(source, 0,  extract_length);
                
                output_Stream.write(extracted_data);
                length -= extract_length;
            }
            
            // ----------------------------decoding data ends--------------------------//
        } 
        
    }
    
    /*
        ________________________________________Decoding part ends here_________________________________________
    */
    
    
}
