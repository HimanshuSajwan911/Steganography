package steganography.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import static steganography.core.decoder.ByteTo_Converter.byteToInt;
import static steganography.core.decoder.ByteTo_Converter.byteToLong;
import static steganography.core.decoder.SteganographyDecoder.extractByte;
import static steganography.core.encoder._ToByteConverter.intToByte;
import static steganography.core.encoder._ToByteConverter.longToByte;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.exceptions.UnsupportedFileException;
import steganography.core.filehandling.Filters;
import static steganography.core.filehandling.Writer.skip;
import static steganography.core.encoder.SteganographyEncoder.insertBits;

/**
 * @author Himanshu Sajwan.
 */

public class Steganography {

    /**
     * Specifies the size of KEY in byte.
     */
    public static final int KEY_SIZE_BYTE = 4; 
    
    /**
     * Specifies the size of KEY in bits.
     */
    public static final int KEY_SIZE_BIT = 32;
    
    /**
     * Specifies the size of Message Length in byte.
     */
    public static final int LENGTH_SIZE_BYTE = 8;
    
    /**
     * Specifies the size of Message Length in bits.
     */
    public static final int LENGTH_SIZE_BIT = 64;
    
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
    
    /**
     * Number of bytes to read from source.
     */
    protected int SOURCE_BUFFER_SIZE;
    
    /**
     * Number of bytes to read from data.
     */
    protected int DATA_BUFFER_SIZE;
    
    /**
     * Position from where to write data file in source file.
     */
    protected int offset;
    
    public Steganography(){
        // setting default value for SOURCE_BUFFER_SIZE.
        SOURCE_BUFFER_SIZE = MB; // 1 MB
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8); // 128 KB
    }
    
    /**
     * Set capacity of <B>SOURCE_BUFFER_SIZE</B> and accordingly calculate and set capacity of 
     * <B>DATA_BUFFER_SIZE</B> as <code>(SOURCE_BUFFER_SIZE / 8)</code>.
     * 
     * @param capacity number of bytes to read at a time.
     */
    public void setBufferCapacity(int capacity){
        SOURCE_BUFFER_SIZE = capacity;
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8);
    }
    
    
    /**
     * Set value of offset.
     * offset means from which position to start writing data file in source file.
     * eg if offset = 50 writing of data file will start from 50th position or byte,
     * ie 0 - 49 bytes will remain unchanged.
     * 
     * @param offset integer value.
     */
    public void setOffset(int offset){
        if(offset > 0){
            this.offset = offset;
        }
    }
    
    /*
        =========================================================================================================
        |                                       Encoding part starts here                                       |
        =========================================================================================================
    */
    
   
    /**
     * Encode file from <B>sourceFile_full_path</B> location
     * with file from <B>dataFile_full_path</B> starting from <B>offset</B> position and save this encoded file to <B>destinationFile_full_path</B> location.
     * 
     * @param sourceFile_full_path location of source Document file.
     * @param dataFile_full_path location of data file that is to be encoded.
     * @param destinationFile_full_path location to save encoded Document file.
     * @param key to secure encoded file with a 32 bit size integer.
     * 
     * @throws InsufficientMemoryException
     * @throws IOException
     * @throws UnsupportedAudioFileException 
     */
    public void encode(String sourceFile_full_path, String dataFile_full_path, String destinationFile_full_path, int key) throws InsufficientMemoryException, IOException, UnsupportedFileException{
        
        File src_file = new File(sourceFile_full_path);
        File data_file = new File(dataFile_full_path);
        
        if(!src_file.exists()){
            throw new FileNotFoundException("(The system cannot find the source file specified)");
        }
        
        if(!data_file.exists()){
            throw new FileNotFoundException("(The system cannot find the data file specified)");
        }
        
        // length of data file.
        long data_file_length = new File(dataFile_full_path).length();

        // checking if space available for data file + key(32 bits) + length(64 bits) from offset position.
        if (src_file.length() < (data_file_length * 8) + KEY_SIZE_BIT + LENGTH_SIZE_BIT + offset) {
            throw new InsufficientMemoryException("not enough space in source file!!");
        }
        
        
        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileInputStream  data_input_Stream   = new FileInputStream(dataFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
           
            // to store source byte stream.
            byte[] source;
            
            // to store data byte stream.
            byte[] data; 
            
            // skips offset amount of bytes from modifying.
            skip(source_input_Stream, output_Stream, offset);
            
            // ----------------------------adding key start--------------------------//
            source = new byte[KEY_SIZE_BIT];
            
            // reading 32 bytes.
            source_input_Stream.read(source);
            
            // inserting 32 bit key in LSB of 32 bytes.
            insertInteger(source, 0, key);
            
            // writing these encoded 32 bytes to output file.
            output_Stream.write(source);
            
            // ----------------------------adding key ends--------------------------//
            
            
            // ----------------------------adding length start--------------------------//
            source = new byte[LENGTH_SIZE_BIT];
            
            // reading 64 bytes.
            source_input_Stream.read(source);
            
            // inserting 64 bit length in LSB of 64 bytes.
            insertLong(source, 0, data_file_length);
            
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
                    insertBits(source, 0, source.length, data, 0, noOfDataBytes);
                }
                
                output_Stream.write(source, 0, noOfSourceBytes);
                
            }
 
            // ----------------------------adding data ends--------------------------//
        } 
        
    }
    
    

    /**
     * Adds all bytes of <B>message</B> byte array in <B>LSB</B> position of bytes of <B>source</B> byte array
     * starting from <B>position</B> position.
     * 
     * @param source byte array in LSB of whose key is to be inserted.
     * @param position from where insertion is supposed to start.
     * @param message byte array that is to be inserted.
     * 
     * @throws InsufficientMemoryException 
     */
    public static void addMessage(byte[] source, int position, byte[] message) throws InsufficientMemoryException {
        insertBits(source, position, source.length, message, 0, message.length);
    }
   
    /*
        ---------------------------------------------------------------------------------------------------------
        |                                       Encoding part ends here                                         |
        ---------------------------------------------------------------------------------------------------------
    */
    
    
    
    /*
        =========================================================================================================
        |                                       Decoding part starts here                                       |
        =========================================================================================================
    */
    
    
    
    
    public static int getKey(byte[] source, int position, int key_size) throws InsufficientBitsException{
        byte[] key_bytes = extractByte(source, position, key_size);
        
        int res =  byteToInt(key_bytes);
        
        return res;
    }
    
    public static long getMessageLength(byte[] source, int position, int length_size) throws InsufficientBitsException{
        byte[] length_bytes = extractByte(source, position, length_size);
        
        return byteToLong(length_bytes);
    }
    
    public static byte[] getMessage(byte[] source, int position, int message_length) throws InsufficientBitsException{
        return extractByte(source, position, message_length);
    }
    
    /*
        ---------------------------------------------------------------------------------------------------------
        |                                       Decoding part ends here                                         |
        ---------------------------------------------------------------------------------------------------------
    */
    
    
    
}
