package steganography;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import steganography.core.Steganography;
import static steganography.core.Steganography.getMessage;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.exceptions.UnsupportedDocumentFileException;
import static steganography.core.util.Files.getFileExtension;
import static steganography.core.encoder.SteganographyEncoder.insertBits;

/**
 * @author Himanshu Sajwan.
 */

public class DocumentSteganography extends Steganography{

    public DocumentSteganography(){
        // setting default value for SOURCE_BUFFER_SIZE.
        SOURCE_BUFFER_SIZE = MB; // 1 MB
        DATA_BUFFER_SIZE = (SOURCE_BUFFER_SIZE / 8); // 128 KB
    }
    
    
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */

    /**
     * Encode Document from <B>sourceFile_full_path</B> location 
     * with file from <B>dataFile_full_path</B> and save this encoded document to <B>destinationFile_full_path</B> location.
     * 
     * @param sourceFile_full_path location of source Document file.
     * @param dataFile_full_path location of data file that is to be encoded.
     * @param destinationFile_full_path location to save encoded Document file.
     * @param key to secure encoded file with a 32 bit size integer.
     * 
     * @throws IOException
     * @throws InsufficientMemoryException
     * @throws UnsupportedDocumentFileException
     */
    public void encode(String sourceFile_full_path, String dataFile_full_path, String destinationFile_full_path, int key) throws IOException, InsufficientMemoryException, UnsupportedDocumentFileException {
        
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
           
            case "txt": encodeTxt(sourceFile_full_path, dataFile_full_path, destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedDocumentFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    
    public void encodeTxt(String sourceFile_full_path,String dataFile_full_path, String destinationFile_full_path, int key) throws IOException, InsufficientMemoryException, UnsupportedDocumentFileException {

        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileInputStream  data_input_Stream   = new FileInputStream(dataFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {

            // length of data file.
            long data_file_length = new File(dataFile_full_path).length();
            
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
    
    /*
        ----------------------------------------Encoding part ends here----------------------------------------
    */
   
    
    /*
        ----------------------------------------Decoding part starts here----------------------------------------
    */
    
    
    /**
     * Decode Document from <B>source_full_path</B> location using <B>key</B>
     * and return encoded message from file.
     * 
     * @param source_full_path location of source Document file.
     * @param key to access encoded Document file with a 32 bit size integer.
     * 
     * @throws IOException
     * @throws InsufficientBitsException 
     * @throws InvalidKeyException 
     */
    public void decode(String sourceFile_full_path, String destinationFile_full_path, int key) throws IOException, InsufficientBitsException, InvalidKeyException, UnsupportedDocumentFileException{
        
        if(!new File(sourceFile_full_path).exists()){
            throw new FileNotFoundException("(The system cannot find the source file specified)");
        }
        
        String extension = getFileExtension(new File(sourceFile_full_path));
        
        switch(extension){
           
            case "txt": decodeTxt(sourceFile_full_path,destinationFile_full_path, key);
                        break;
                                
                       
                        
            default:    throw new UnsupportedDocumentFileException("'" + extension +"' file format is not yet supported.");
          
        }
        
    }
    
    public void decodeTxt(String sourceFile_full_path, String destinationFile_full_path, int key) throws FileNotFoundException, IOException, InsufficientBitsException, InvalidKeyException{
        
        try (
            FileInputStream  source_input_Stream = new FileInputStream(sourceFile_full_path);
            FileOutputStream output_Stream       = new FileOutputStream(destinationFile_full_path);
        ) {
            
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
        ----------------------------------------Decoding part ends here----------------------------------------
    */
}
