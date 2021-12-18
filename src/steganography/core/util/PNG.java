
package steganography.core.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import static steganography.core.Steganography.MB;
import static steganography.core.decoder.ByteTo_Converter.byteToInt;

/**
 * @author Himanshu Sajwan.
 */

public class PNG {

    boolean IS_PNG = false;
    private int HEIGHT, WIDTH, IDAT_Position, IDAT_Count;
    private long IEND_Position, Current_Position;
    private ArrayList<Pair<Long, Integer>> ALL_IDAT_Position;
    
    public PNG(String source) throws IOException{
        this.ALL_IDAT_Position = new ArrayList<>();
        process(source);
    }
    
    public final void process(String sourceFile) throws FileNotFoundException, IOException{
        try ( FileInputStream  source_input_Stream = new FileInputStream(sourceFile) ) {
            
            int noOfSourceBytes;
            int SOURCE_BUFFER_SIZE = MB;
            
            boolean found = false;
            
            byte[] source = new byte[SOURCE_BUFFER_SIZE];
            
            while (!found && (noOfSourceBytes = source_input_Stream.read(source)) > 0) {

                for(int i = 0; i < noOfSourceBytes - 4; i++, Current_Position++){
                    
                    if(!IS_PNG && source[i] == 'P' && source[i + 1] == 'N' && source[i + 2] == 'G'){
                        IS_PNG = true;
                        Current_Position += 2;
                        i += 2;
                    }
                    
                    // IHDR chunck found.
                    else if(source[i] == 'I' && source[i + 1] == 'H' && source[i + 2] == 'D' && source[i + 3] == 'R'){
                        i += 3;
                        Current_Position += 3;
                        
                        byte[] four_bytes = new byte[4];
                        
                        // extracting image width.
                        for(int j = 0; j < 4; j++){
                            four_bytes[j] = source[i + 1];
                            Current_Position++;
                            i++;
                        }
                        WIDTH = byteToInt(four_bytes);
                        
                        // extracting image height.
                        for(int j = 0; j < 4; j++){
                            four_bytes[j] = source[i + 1];
                            Current_Position++;
                            i++;
                        }
                        HEIGHT = byteToInt(four_bytes);
                        
                    }
                    
                    // IDAT (Image data) found.
                    else if(source[i] == 'I' && source[i + 1] == 'D' && source[i + 2] == 'A' && source[i + 3] == 'T'){
                        
                        int l = i - 4;
                        byte[] length_arr = new byte[4];
                        for(int j = 0; j < 4; j++, l++){
                            length_arr[j] = source[l];
                        }
                        
                        int length = byteToInt(length_arr);
                        
                        i += 3;
                        
                        if(IDAT_Count == 0){
                            IDAT_Position =  (int) (Current_Position);
                        }
                        
                        ALL_IDAT_Position.add(new Pair(Current_Position, length));
                        
                        IDAT_Count++;
                        Current_Position += 3;
                    }
                    
                    // IEND (Image data End)found.
                    else if(source[i] == 'I' && source[i + 1] == 'E' && source[i + 2] == 'N' && source[i + 3] == 'D'){
                        IEND_Position = Current_Position;
                        found = true;
                        break;
                    }
                    
                }
                
                Current_Position += 4;
            }
        }
    }

    // function to read image from given location and return BufferedImage.
    public BufferedImage readPNG(String url) throws IOException {
        return ImageIO.read(new File(url));
    }
    
    // function to convert given BufferedImage into pure RGB.
    public BufferedImage user_space(BufferedImage image) {
        BufferedImage user_space_image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = user_space_image.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose();
        return user_space_image;
    }
    
    // this function return byte array of supplied image.
    public byte[] getImageByte(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    public boolean isPNG() {
        return IS_PNG;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getIDAT_Position() {
        return IDAT_Position;
    }

    public long getIEND_Position() {
        return IEND_Position;
    }

    public int getIDAT_Count() {
        return IDAT_Count;
    }

    public ArrayList<Pair<Long, Integer>> getALL_IDAT_Position() {
        return ALL_IDAT_Position;
    }
    
    
}

