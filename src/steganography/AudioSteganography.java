package steganography;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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
import static steganography.core.decoder.ByteTo_Converter.byteToInt;
import static steganography.core.decoder.ByteTo_Converter.byteToLong;
import static steganography.core.encoder.SteganographyEncoder.addBits;
import static steganography.core.encoder._ToByteConverter.intToByte;
import static steganography.core.decoder.SteganographyDecoder.extractByte;
import static steganography.core.encoder.SteganographyEncoder.addBits;
import static steganography.core.encoder._ToByteConverter.longToByte;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.filehandling.Filters;
import static steganography.core.filehandling.Reader.readBytes;
import static steganography.core.filehandling.Writer.skip;
import static steganography.core.filehandling.Writer.writeBytes;

/**
 * @author Himanshu Sajwan.
 */

public class AudioSteganography {

    private static final int SOURCE_BUFFER_SIZE = 4096; // 4KB
    private static final int DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8); // 4KB
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */
    
    public void encode(String sourceFile_full_path,String dataFile_full_path, String destinationFile_full_path, int key) throws InsufficientMemoryException, IOException, UnsupportedAudioFileException{
        
        File src_file = new File(sourceFile_full_path);
        File data_file = new File(dataFile_full_path);
        
        long length = data_file.length();
        
        if(src_file.length() < data_file.length() * 8){
            throw new InsufficientMemoryException("not enough space in source file");
        }
        
        String extension = Filters.getFileExtension(src_file);
        
        switch(extension){
           
            case "wav": encodeWav(sourceFile_full_path, dataFile_full_path, destinationFile_full_path, key, length);
                        break;
                                
                       
                        
            default:    throw new UnsupportedAudioFileException("given audio file format is not yet supported.");
          
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
            
            
            
            int noOfSourceBytes, noOfDataBytes;
            
            long MessageLength = length * 8;
            source = new byte[8];
            
            while(MessageLength > 0){
                noOfSourceBytes = source_input_Stream.read(source);
                
                byte[] ret = getMessage(source, 0, 1);
                
                output_Stream.write(ret);
                MessageLength -= 8;
            }
            
            // ----------------------------decoding data ends--------------------------//
        } 
        
    }
    
    
    /*
        ________________________________________Decoding part ends here_________________________________________
    */
    
    
}
