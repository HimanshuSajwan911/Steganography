package steganography;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import static steganography.core.Steganography.KEY_SIZE_BIT;
import static steganography.core.Steganography.KEY_SIZE_BYTE;
import static steganography.core.Steganography.LENGTH_SIZE_BIT;
import static steganography.core.Steganography.LENGTH_SIZE_BYTE;
import static steganography.core.Steganography.addKey;
import static steganography.core.Steganography.addMessageLength;
import static steganography.core.Steganography.getKey;
import static steganography.core.Steganography.getMessage;
import static steganography.core.Steganography.getMessageLength;
import static steganography.core.encoder.SteganographyEncoder.addBits;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.filehandling.Filters;
import static steganography.core.filehandling.Writer.skip;

/**
 * @author Himanshu Sajwan.
 */

public class AudioSteganography {

    private int SOURCE_BUFFER_SIZE;
    private int DATA_BUFFER_SIZE;
    
    /**
     * Size of 1 KB in Bytes.
     */
    public static final int KB = 1024;
    
    /**
     * Size of 1 MB int Bytes.
     */
    public static final int MB = 1048576;
    
    /**
     * Size of 1 GB in Bytes.
     */
    public static final int GB = 1073741824;
    
    public AudioSteganography(){
        SOURCE_BUFFER_SIZE = MB; // 1 MB
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8); // 128 KB
    }
    
    /**
     * Set capacity of <B>SOURCE_BUFFER_SIZE</B> and accordingly calculate capacity of 
     * <B>DATA_BUFFER_SIZE</B> as <code>(SOURCE_BUFFER_SIZE / 8)</code>.
     * 
     * @param capacity number of bytes to read at a time.
     */
    public void setBufferCapacity(int capacity){
        SOURCE_BUFFER_SIZE = capacity;
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8);
    }
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */
    
    public void encode(String sourceFile_full_path,String dataFile_full_path, String destinationFile_full_path, int key) throws InsufficientMemoryException, IOException, UnsupportedAudioFileException{
        
        File src_file = new File(sourceFile_full_path);
        File data_file = new File(dataFile_full_path);
        
        long length = data_file.length();
        
        if(src_file.length() < data_file.length() * 8){
            throw new InsufficientMemoryException("not enough space in source file!!");
        }
        
        String extension = Filters.getFileExtension(src_file);
        
        switch(extension){
           
            case "wav": encodeWav(sourceFile_full_path, dataFile_full_path, destinationFile_full_path, key, length);
                        break;
                                
                       
                        
            default:    throw new UnsupportedAudioFileException("Given file format is not yet supported.");
          
        }
        
    }
    
    private void encodeWav(String sourceFile_full_path,String dataFile_full_path, String destinationFile_full_path, int key, long length) throws IOException, InsufficientMemoryException, UnsupportedAudioFileException {

        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileInputStream  data_input_Stream   = new FileInputStream(dataFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
            
            byte[] source; // to store source byte stream.
            
            byte[] data; // to store data byte stream.
            
            int position = 44;
            
            // skips modifying header.
            skip(source_input_Stream, output_Stream, position);
            
            // ----------------------------adding key start--------------------------//
            source = new byte[KEY_SIZE_BIT];
            
            // reading 32 bytes.
            source_input_Stream.read(source);
            
            // inserting 32 bit key in LSB of 32 bytes.
            addKey(source, 0, key);
            
            // writing these encoded 32 bytes to output file.
            output_Stream.write(source);
            
            // ----------------------------adding key ends--------------------------//
            
            
            // ----------------------------adding length start--------------------------//
            source = new byte[LENGTH_SIZE_BIT];
            
            // reading 64 bytes.
            source_input_Stream.read(source);
            
            // inserting 64 bit length in LSB of 64 bytes.
            addMessageLength(source, 0, length);
            
            // writing these encoded 64 bytes to output file.
            output_Stream.write(source);
            
            
            // ----------------------------adding length ends--------------------------//
            
            
            // ----------------------------adding data starts--------------------------//
            data = new byte[DATA_BUFFER_SIZE];
            source = new byte[SOURCE_BUFFER_SIZE];
            
            int noOfSourceBytes, noOfDataBytes;
            
            // while source has bytes.
            while ((noOfSourceBytes = source_input_Stream.read(source)) > 0) {
               
                // if data bytes exists.
                if((noOfDataBytes = data_input_Stream.read(data)) > 0){
                    addBits(source, 0, source.length, data, 0, noOfDataBytes);
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
    
     
    public void decode(String sourceFile_full_path, String destinationFile_full_path, int key) throws UnsupportedAudioFileException, IOException, FileNotFoundException, InsufficientBitsException, InvalidKeyException{
        
        String extension = Filters.getFileExtension(new File(sourceFile_full_path));
        
        switch(extension){
           
            case "wav": decodeWav(sourceFile_full_path,destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedAudioFileException("given audio file format is not yet supported.");
          
        }
        
        
    }
    
    private void decodeWav(String sourceFile_full_path, String destinationFile_full_path, int key) throws FileNotFoundException, IOException, InsufficientBitsException, InvalidKeyException{
        
        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
            
            byte[] source; // to store source byte stream.
            
            int position = 44;
            
            // skips source header.
            skip(source_input_Stream, null, position);
            
            // ----------------------------decoding key start--------------------------//
            source = new byte[KEY_SIZE_BIT];
            
            // reading 32 bytes.
            source_input_Stream.read(source);
            
            // extracting 4 byte (32 bit) key from LSB of 32 bytes.
            int extracted_key = getKey(source, 0, KEY_SIZE_BYTE);
            
            if(extracted_key != key){
                throw new InvalidKeyException();
            }
            
            position += KEY_SIZE_BIT;
            
            // ----------------------------decoding key ends--------------------------//
            
            
            // ----------------------------decoding length start--------------------------//
            source = new byte[LENGTH_SIZE_BIT];
            
            // reading 64 bytes.
            source_input_Stream.read(source);
            
            // extracting 8 byte (64 bit) length from LSB of 64 bytes.
            long length = getMessageLength(source, 0, LENGTH_SIZE_BYTE);
            
            // writing these encoded 64 bytes to output file.
            
            position += LENGTH_SIZE_BIT;
            
            // ----------------------------decoding length ends--------------------------//
            
            
            // ----------------------------decoding data starts--------------------------//
            source = new byte[SOURCE_BUFFER_SIZE];
            
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
