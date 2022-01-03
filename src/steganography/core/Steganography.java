package steganography.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import static steganography.core.decoder.SteganographyDecoder.extractByte;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.exceptions.UnsupportedFileException;
import static steganography.core.util.Files.skip;
import static steganography.core.encoder.SteganographyEncoder.insertBits;
import static steganography.core.encoder.SteganographyEncoder.insertInteger;
import static steganography.core.encoder.SteganographyEncoder.insertLong;
import static steganography.core.decoder.SteganographyDecoder.extractInteger;
import static steganography.core.decoder.SteganographyDecoder.extractLong;
import steganography.core.exceptions.InsufficientBytesException;
import steganography.core.exceptions.UnsupportedVideoFileException;

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
    private int SOURCE_BUFFER_SIZE;
    
    /**
     * Number of bytes to read from data.
     */
    private int DATA_BUFFER_SIZE;
    
    /**
     * Position from where to write data file in source file.
     */
    private int OFFSET;
    
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
    public final void setBufferCapacity(int capacity){
        SOURCE_BUFFER_SIZE = capacity;
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8);
    }
    
    
    public final int getSourceBufferSize() {
        return SOURCE_BUFFER_SIZE;
    }

    public final int getDataBufferSize() {
        return DATA_BUFFER_SIZE;
    }
    
    /**
     * Set value of OFFSET.
     * OFFSET means from which position to start writing data file in source file.
     * eg if OFFSET = 50 writing of data file will start from 50th position/byte,
     * ie 0 - 49 bytes will remain unchanged.
     * 
     * @param offset integer value.
     */
    public final void setOffset(int offset){
        if(offset > 0){
            this.OFFSET = offset;
        }
    }
    
    public final int getOffset() {
        return OFFSET;
    }
    
    /*
        =========================================================================================================
        |                                       Encoding part starts here                                       |
        =========================================================================================================
    */
    
   
    /**
     * Encode file from <B>sourceFile_full_path</B> location
     * with file from <B>dataFile_full_path</B> starting from <B>OFFSET</B> position and save this encoded file to <B>destinationFile_full_path</B> location.
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

        // checking if space available for data file + key(32 bits) + length(64 bits) from OFFSET position.
        if (src_file.length() < (data_file_length * 8) + KEY_SIZE_BIT + LENGTH_SIZE_BIT + OFFSET) {
            throw new InsufficientMemoryException("not enough space in source file!!");
        }
        
        
        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileInputStream  data_input_Stream   = new FileInputStream(dataFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
           
            // skips OFFSET amount of bytes from modifying.
            skip(source_input_Stream, output_Stream, OFFSET);
            
            // adding key.
            encodeKey(source_input_Stream, output_Stream, key);
            
            // adding message length.
            encodeMessageLength(source_input_Stream, output_Stream, data_file_length);
            
            
            // ----------------------------adding data starts--------------------------//
            // to store source byte stream.
            byte[] source = new byte[SOURCE_BUFFER_SIZE];
            
            // to store data byte stream.
            byte[] data = new byte[DATA_BUFFER_SIZE];
            
            
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
    
    protected void encodeKey(FileInputStream source, FileOutputStream output, int key) throws InsufficientMemoryException, IOException{
        byte[] buffer = new byte[KEY_SIZE_BIT];
        
        // reading 32 bytes.
        source.read(buffer);

        // inserting 32 bit key in LSB of 32 bytes.
        insertInteger(buffer, 0, key);

        // writing these encoded 32 bytes to output file.
        output.write(buffer);
    }
    
    protected void encodeMessageLength(FileInputStream source, FileOutputStream output, long length) throws InsufficientMemoryException, IOException {
        byte[] buffer = new byte[LENGTH_SIZE_BIT];

        // reading 64 bytes.
        source.read(buffer);

        // inserting 64 bit length in LSB of 64 bytes.
        insertLong(buffer, 0, length);

        // writing these encoded 64 bytes to output file.
        output.write(buffer);
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
    
    /**
     * Decode a file with a 32 bit <B>key</B> from
     * <B>sourceFile_full_path</B> location starting from provided OFFSET
     * position and save this decoded file to <B>destinationFile_full_path</B>
     * location.
     *
     * @param sourceFile_full_path location of encoded file.
     * @param destinationFile_full_path location to save decoded file.
     * @param key to decode file with a 32 bit size integer.
     *
     * @throws UnsupportedVideoFileException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InsufficientBytesException
     * @throws InvalidKeyException
     */
    public void decode(String sourceFile_full_path, String destinationFile_full_path, int key) throws UnsupportedFileException, IOException, FileNotFoundException, InvalidKeyException, InsufficientBytesException{
        
        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
            
            // skips modifying OFFSET number of bytes.
            skip(source_input_Stream, null, OFFSET);
            
            // decoding key.
            int extracted_key = getKey(source_input_Stream);
            
            if(extracted_key != key){
                throw new InvalidKeyException();
            }
            
            // decoding message length.
            long length = getMessageLength(source_input_Stream);
            
            
            // ----------------------------decoding message data starts--------------------------//
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
    
    protected int getKey(FileInputStream source) throws IOException, InsufficientBytesException{
        byte[] buffer = new byte[KEY_SIZE_BIT];
            
        // reading 32 bytes.
        source.read(buffer);
        
        // extracting 4 byte (32 bit) key from LSB of 32 bytes.
        return extractInteger(buffer, 0);
    }
    
    protected long getMessageLength(FileInputStream source) throws IOException, InsufficientBytesException{
        byte[] buffer = new byte[LENGTH_SIZE_BIT];
            
        // reading 64 bytes.
        source.read(buffer);
            
        // extracting 8 byte (64 bit) length from LSB of 64 bytes.
        return extractLong(buffer, 0);
    }
    
    
    public byte[] getMessage(byte[] source, int position, int message_length) throws InsufficientBytesException{
        return extractByte(source, position, message_length);
    }
    
    /*
        ---------------------------------------------------------------------------------------------------------
        |                                       Decoding part ends here                                         |
        ---------------------------------------------------------------------------------------------------------
    */
    
    
    
}
