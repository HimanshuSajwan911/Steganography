package steganography;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Himanshu Sajwan.
 */
public class AudioSteganography {

    public void encode(String source_full_path, String destination_full_path, int key, String message) throws IOException {
    
        Path path = Paths.get(source_full_path);
        byte[] audio_bytes = Files.readAllBytes(path);
        
        addKey(audio_bytes, key);
        
    }
    
    private void addKey(byte[] source, int key){
        
    }

}
