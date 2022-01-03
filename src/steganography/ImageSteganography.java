package steganography;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import java.io.IOException;
import steganography.core.Steganography;
import static steganography.core.Steganography.KEY_SIZE_BIT;
import static steganography.core.Steganography.LENGTH_SIZE_BIT;
import static steganography.core.Steganography.MB;
import static steganography.core.decoder.SteganographyDecoder.extractByte;
import static steganography.core.decoder.SteganographyDecoder.extractInteger;
import static steganography.core.decoder.SteganographyDecoder.extractLong;
import static steganography.core.encoder.SteganographyEncoder.insertBits;
import static steganography.core.encoder._ToByteConverter.intToByte;
import static steganography.core.encoder._ToByteConverter.longToByte;
import steganography.core.exceptions.InsufficientBytesException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.exceptions.UnsupportedImageFileException;
import static steganography.core.util.Files.getFileExtension;
import steganography.core.util.PNG;

/**
 * @author Himanshu Sajwan.
 */

public class ImageSteganography extends Steganography{

    public ImageSteganography(){
        // setting default value for SOURCE_BUFFER_SIZE.
        setBufferCapacity(MB);
    }
    
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */
    
    /**
     * Encode Image file with a 32 bit <B>key</B> from <B>sourceFile_full_path</B> location
     * with file from <B>dataFile_full_path</B> starting from <B>OFFSET</B> position and 
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
    
    public void encodePNG(String sourceFile_full_path,String dataFile_full_path, String destinationFile_full_path, int key) throws FileNotFoundException, IOException, InsufficientMemoryException{
        
        try (
            FileInputStream  data_input_Stream   = new FileInputStream(dataFile_full_path);
        ) {
            
            // length of data file.
            long data_file_length = new File(dataFile_full_path).length();
            
            int position = getOffset();
            PNG png = new PNG(sourceFile_full_path);
            
            BufferedImage png_image = png.readPNG(sourceFile_full_path);
            byte[] source = png.getImageByte(png_image);
            
            int source_length = source.length;
            
            if (source_length  < (data_file_length * 8) + KEY_SIZE_BIT + LENGTH_SIZE_BIT + getOffset()) {
                throw new InsufficientMemoryException("not enough space in source file!!");
            }

            byte[] intBytes = intToByte(key);
            insertBits(source, position, source.length , intBytes, 0, intBytes.length);
            position += 32;
            
            byte[] longBytes = longToByte(data_file_length);

            insertBits(source, position, source.length, longBytes, 0, longBytes.length);
            position += 64;
            
            // ----------------------------adding data starts--------------------------//
            
            byte[] data = new byte[(int)data_file_length];
            data_input_Stream.read(data);
            insertBits(source, position, source.length, data, 0, (int) data_file_length);
 
            // ----------------------------adding data ends--------------------------//
            
            // writing image
            ImageIO.write(png_image, "PNG", new File(destinationFile_full_path));
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
      *  starting from provided OFFSET position  and 
      *  save this encoded file to <B>destinationFile_full_path</B> location.
      * 
      * @param sourceFile_full_path location of encoded Image file.
      * @param destinationFile_full_path location to save decoded file.
      * @param key to decode file with a 32 bit size integer. 
      * 
      * @throws IOException
      * @throws FileNotFoundException
      * @throws InsufficientBytesException
      * @throws InvalidKeyException
      * @throws UnsupportedImageFileException 
      */
    public void decode(String sourceFile_full_path, String destinationFile_full_path, int key) throws IOException, FileNotFoundException, InvalidKeyException, UnsupportedImageFileException, InsufficientBytesException{
        
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
    
    public void decodePNG(String sourceFile_full_path, String destinationFile_full_path, int key) throws FileNotFoundException, IOException, InvalidKeyException, InsufficientBytesException{
        
        try (
            FileOutputStream  output_Stream = new FileOutputStream(destinationFile_full_path);
            ){
            
            int position = getOffset();
           
            PNG png = new PNG(sourceFile_full_path);
            
            BufferedImage png_image = png.readPNG(sourceFile_full_path);
            byte[] source = png.getImageByte(png_image);
            
            // decoding key.
            int extracted_key = extractInteger(source, position);

            if (extracted_key != key) {
                throw new InvalidKeyException();
            }

            position += 32;

            // decoding message length.
            long length = extractLong(source, position);

            position += 64;

            // decoding message data
            byte[] extracted_data = extractByte(source, position, (int) length);

            // writing extracted data to output file.
            output_Stream.write(extracted_data);

        }

    }

    /*
        ________________________________________Decoding part ends here_________________________________________
     */
}
