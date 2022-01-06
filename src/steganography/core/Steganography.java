package steganography.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import static steganography.core.SteganographyNew.KEY_SIZE_BIT;
import static steganography.core.SteganographyNew.LENGTH_SIZE_BIT;
import static steganography.core.decoder.SteganographyDecoder.extractByte;
import static steganography.core.decoder.SteganographyDecoder.extractDouble;
import static steganography.core.decoder.SteganographyDecoder.extractFloat;
import static steganography.core.decoder.SteganographyDecoder.extractInteger;
import static steganography.core.decoder.SteganographyDecoder.extractLong;
import static steganography.core.encoder.SteganographyEncoder.insertByte;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidSecurityException;
import steganography.core.exceptions.UnsupportedFileException;
import static steganography.core.util.Files.skip;
import static steganography.core.encoder.SteganographyEncoder.insertInteger;
import static steganography.core.encoder.SteganographyEncoder.insertLong;
import static steganography.core.encoder.SteganographyEncoder.insertDouble;
import static steganography.core.encoder.SteganographyEncoder.insertFloat;
import static steganography.core.encoder.SteganographyEncoder.insertInteger;
import static steganography.core.encoder.SteganographyEncoder.insertLong;
import steganography.core.exceptions.InsufficientBytesException;
import steganography.core.exceptions.InsufficientException;
import steganography.core.exceptions.UnsupportedSecurityTypeException;
import steganography.core.exceptions.UnsupportedVideoFileException;
import static steganography.core.util.Files.skip;
import static steganography.core.util.Util.getClassName;

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
     * Encode file with a <B>security</B>, from <B>sourceFile_full_path</B> location
     * with file from <B>dataFile_full_path</B> starting from <B>OFFSET</B> position and save this encoded file to <B>destinationFile_full_path</B> location.
     * 
     * @param sourceFile_full_path location of source file.
     * @param dataFile_full_path location of data file that is to be encoded.
     * @param destinationFile_full_path location to save encoded file.
     * @param security to secure encoded cover file with password(text password) or key(integer or floating value).
     * 
     * @throws IOException
     * @throws UnsupportedFileException
     * @throws UnsupportedSecurityTypeException
     * @throws InsufficientBytesException
     * @throws InsufficientMemoryException
     */
    public void encode(String sourceFile_full_path, String dataFile_full_path, String destinationFile_full_path, Object security) throws IOException, UnsupportedFileException, UnsupportedSecurityTypeException, InsufficientBytesException, InsufficientMemoryException{
        
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
            
            // setting security on output file.
            setSecurity(source_input_Stream, output_Stream, security);
            
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
                    insertByte(source, 0, source.length, data, 0, noOfDataBytes);
                }
                
                output_Stream.write(source, 0, noOfSourceBytes);
                
            }
 
            // ----------------------------adding data ends--------------------------//
        } 
        
    }
    
    /**
     * Secures cover file with password(text password) or key(integer or floating value).
     * 
     * @param source FileInputStream object of cover file.
     * @param output FileOutputStream object for resultant file.
     * @param security Object of password or key. 
     * 
     * @throws IOException
     * @throws InsufficientBytesException
     * @throws InsufficientMemoryException 
     * @throws UnsupportedSecurityTypeException
     */
    protected void setSecurity(FileInputStream source, FileOutputStream output, Object security) throws IOException, InsufficientBytesException, InsufficientMemoryException, UnsupportedSecurityTypeException{
        
        String class_name = getClassName(security);
        
        switch (class_name) {
            
            case "String": {
                String password = security.toString();
                encodeString(source, output, password);
                break;
            }
            
            case "Integer": {
                int key = Integer.parseInt(security.toString());
                encodeInteger(source, output, key);
                break;
            }
            
            case "Float": {
                float key = Float.parseFloat(security.toString());
                encodeFloat(source, output, key);
                break;
            }
            
            case "Long": {
                long key = Long.parseLong(security.toString());
                encodeLong(source, output, key);
                break;
            }

            case "Double": {
                double key = Double.parseDouble(security.toString());
                encodeDouble(source, output, key);
                break;
            }
           
            default: throw new UnsupportedSecurityTypeException("Security not defined for " + class_name);
            
        }
    }
    
    
    protected void encodeString(FileInputStream source, FileOutputStream output, String string) throws InsufficientMemoryException, IOException{
        
        int string_length = string.length();
        encodeInteger(source, output, string_length);
        
        byte[] buffer = new byte[string_length * 8];
        
        // reading string_length number of bytes.
        source.read(buffer);
        
        byte[] string_bytes = string.getBytes();
        
        insertByte(buffer, 0, buffer.length, string_bytes, 0, string_bytes.length);
        
        // writing encode string_length number of bytes.
        output.write(buffer);
    }
    
    protected void encodeInteger(FileInputStream source, FileOutputStream output, int value) throws InsufficientMemoryException, IOException{
        
        byte[] buffer = new byte[Integer.SIZE];
        
        // reading 32 bytes.
        source.read(buffer);

        // inserting 32 bit integer value in LSB of 32 bytes.
        insertInteger(buffer, 0, value);

        // writing these encoded 32 bytes to output file.
        output.write(buffer);
    }
    
    protected void encodeFloat(FileInputStream source, FileOutputStream output, float value) throws InsufficientMemoryException, IOException{
        
        byte[] buffer = new byte[Float.SIZE];
        
        // reading 32 bytes.
        source.read(buffer);

        // inserting 32 bit float value in LSB of 32 bytes.
        insertFloat(buffer, 0, value);

        // writing these encoded 32 bytes to output file.
        output.write(buffer);
    }
    
    protected void encodeLong(FileInputStream source, FileOutputStream output, long value) throws InsufficientMemoryException, IOException{
        
        byte[] buffer = new byte[Long.SIZE];
        
        // reading 64 bytes.
        source.read(buffer);

        // inserting 64 bit lobg value in LSB of 64 bytes.
        insertLong(buffer, 0, value);

        // writing these encoded 64 bytes to output file.
        output.write(buffer);
    }
    
    protected void encodeDouble(FileInputStream source, FileOutputStream output, double value) throws InsufficientMemoryException, IOException{
       
        byte[] buffer = new byte[Double.SIZE];
        
        // reading 64 bytes.
        source.read(buffer);

        // inserting 64 bit double value in LSB of 64 bytes.
        insertDouble(buffer, 0, value);

        // writing these encoded 64 bytes to output file.
        output.write(buffer);
    }
    
    protected void encodeMessageLength(FileInputStream source, FileOutputStream output, long length) throws InsufficientMemoryException, IOException {
        encodeLong(source, output, length);
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
    public void addMessage(byte[] source, int position, byte[] message) throws InsufficientMemoryException {
        insertByte(source, position, source.length, message, 0, message.length);
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
     * Decode a file with a <B>security</B>, from
     * <B>sourceFile_full_path</B> location starting from provided OFFSET
     * position and save this decoded file to <B>destinationFile_full_path</B>
     * location.
     *
     * @param sourceFile_full_path location of encoded file.
     * @param destinationFile_full_path location to save decoded file.
     * @param security to decode file with password(text password) or key(integer or floating value).
     *
     * @throws UnsupportedFileException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InvalidSecurityException
     * @throws InsufficientBytesException
     * @throws UnsupportedSecurityTypeException
     * @throws InsufficientMemoryException
     */
    public void decode(String sourceFile_full_path, String destinationFile_full_path, Object security) throws UnsupportedFileException, IOException, FileNotFoundException, InvalidSecurityException, InsufficientBytesException, UnsupportedSecurityTypeException, InsufficientMemoryException{
        
        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
            
            // skips decoding OFFSET number of bytes.
            skip(source_input_Stream, null, OFFSET);
            
            // verifying security on input file.
            verifySecurity(source_input_Stream, security);
            
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
    
    /**
     * Verifies security for file with password(text password) or 
     * key(integer or floating value).
     *
     * @param source FileInputStream object of cover file.
     * @param output FileOutputStream object for resultant file.
     * @param security Object of password or key.
     *
     * @throws IOException
     * @throws InsufficientBytesException
     * @throws InsufficientMemoryException
     * @throws UnsupportedSecurityTypeException
     * @throws InvalidSecurityException
     */
    protected void verifySecurity(FileInputStream source, Object security) throws IOException, InsufficientBytesException, InsufficientMemoryException, UnsupportedSecurityTypeException, InvalidSecurityException {

        String class_name = security.getClass().getSimpleName();
        
        switch (class_name) {
            case "String": {
                String password = security.toString();
                
                int extracted_length = decodeInteger(source);
                
                if(extracted_length != password.length()){
                    throw new InvalidSecurityException("Invalid password!");
                }
                
                String extracted_password = decodeString(source, extracted_length);

                if(!extracted_password.equals(password)){
                    throw new InvalidSecurityException("Invalid password!");
                }
                
                break;
            }
            
            case "Integer": {
                int key = Integer.parseInt(security.toString());
                
                int extracted_key = decodeInteger(source);
                
                if(extracted_key != key){
                    throw new InvalidSecurityException("Invalid key!");
                }
                
                break;
            }
            
            case "Float": {
                float key = Float.parseFloat(security.toString());
                
                float extracted_key = decodeFloat(source);
                
                if(extracted_key != key){
                    throw new InvalidSecurityException("Invalid key!");
                }
                
                break;
            }
            
            case "Long": {
                long key = Long.parseLong(security.toString());
                
                long extracted_key = decodeLong(source);
                
                if(extracted_key != key){
                    throw new InvalidSecurityException("Invalid key!");
                }
                
                break;
            }

            case "Double": {
                double key = Double.parseDouble(security.toString());
                
                double extracted_key = decodeDouble(source);
                
                if(extracted_key != key){
                    throw new InvalidSecurityException("Invalid key!");
                }
                
                break;
            }
           
            default: throw new UnsupportedSecurityTypeException("Security not defined for " + class_name);
            
        }
        
    }
    
    
    protected String decodeString(FileInputStream source, int length) throws IOException, InsufficientBytesException{
        
        byte[] source_bytes = new byte[length * 8];
        
        // reading source_bytes number of bytes.
        source.read(source_bytes);

        byte[] extracted_string_bytes = extractByte(source_bytes, 0, length);

        return new String(extracted_string_bytes);
    }
    
    protected int decodeInteger(FileInputStream source) throws IOException, InsufficientBytesException{
        
        byte[] buffer = new byte[Integer.SIZE];
            
        // reading 32 bytes.
        source.read(buffer);
        
        // extracting 4 byte (32 bit) integer from LSB of 32 bytes.
        return extractInteger(buffer, 0);
    }
    
    protected float decodeFloat(FileInputStream source) throws IOException, InsufficientBytesException{
        
        byte[] buffer = new byte[Float.SIZE];
            
        // reading 32 bytes.
        source.read(buffer);
        
        // extracting 4 byte (32 bit) float from LSB of 32 bytes.
        return extractFloat(buffer, 0);
    }
    
    protected long decodeLong(FileInputStream source) throws IOException, InsufficientBytesException{
        
        byte[] buffer = new byte[Long.SIZE];
            
        // reading 64 bytes.
        source.read(buffer);
        
        // extracting 8 byte (64 bit) long from LSB of 64 bytes.
        return extractLong(buffer, 0);
    }
    
    protected double decodeDouble(FileInputStream source) throws IOException, InsufficientBytesException{
        
        byte[] buffer = new byte[Double.SIZE];
            
        // reading 64 bytes.
        source.read(buffer);
        
        // extracting 8 byte (64 bit) double from LSB of 64 bytes.
        return extractDouble(buffer, 0);
    }
    
    protected long getMessageLength(FileInputStream source) throws IOException, InsufficientBytesException{
        return decodeLong(source);
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
