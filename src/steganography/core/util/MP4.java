package steganography.core.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static steganography.core.Steganography.MB;
import static steganography.core.decoder.ByteTo_Converter.byteToLong;

/**
 * @author Himanshu Sajwan.
 */

/**
 * Class to process MP4 files.
 */
public class MP4 {

    private int mdat_POSITION;
    private long mdat_SIZE;
    private boolean IS_MP4;
    
    public MP4(String source) throws IOException{
        process(source);
    }
    
    public final void process(String sourceFile) throws FileNotFoundException, IOException{
        
        try ( FileInputStream  source_input_Stream = new FileInputStream(sourceFile) ) {
            
            int noOfSourceBytes;
            int SOURCE_BUFFER_SIZE = MB;
            
            boolean found = false;
            
            byte[] source = new byte[SOURCE_BUFFER_SIZE];
            
            while (!found && (noOfSourceBytes = source_input_Stream.read(source)) > 0) {

                for (int i = 0; i < noOfSourceBytes - 4; i++) {
                    // 'mdat' atom found.
                    if (source[i] == 'm' && source[i + 1] == 'd' && source[i + 2] == 'a' && source[i + 3] == 't') {
                        IS_MP4 = true;
                        byte[] source_length_bytes = new byte[8];
                        source_length_bytes[0] = 0;
                        source_length_bytes[1] = 0;
                        source_length_bytes[2] = 0;
                        source_length_bytes[3] = 0;

                        // copying 4 bytes written before 'mdat' atom ie number of bytes in mdat atom.
                        for (int j = 4, loc = i - 4; j < 8; j++) {
                            source_length_bytes[j] = source[loc++];
                        }
                        
                        mdat_SIZE = byteToLong(source_length_bytes);
                        found = true;
                        break;
                    }
                    else{
                        mdat_POSITION++;
                    }
                }

            }
        }
        
    }

    public int getMdat_position() {
        return mdat_POSITION;
    }

    public long getMdat_SIZE() {
        return mdat_SIZE;
    }

    public boolean isMP4(){
        return IS_MP4;
    }
    
}
