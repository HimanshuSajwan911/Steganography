package steganography;

import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import static steganography.core.Steganography.KEY_SIZE;
import static steganography.core.Steganography.LENGTH_SIZE;
import static steganography.core.Steganography.addKey;
import static steganography.core.Steganography.addLength;
import static steganography.core.Steganography.addMessage;
import steganography.core.exceptions.InsufficientMemoryException;
import static steganography.core.filehandling.Reader.readBytes;
import static steganography.core.filehandling.Writer.writeBytes;

/**
 * @author Himanshu Sajwan.
 */

public class DocumentSteganography {
    
    
    
    /*
        ----------------------------------------Encoding part starts here----------------------------------------
    */

    /**
     * Function to encode 
     * 
     * @param source_full_path
     * @param destination_full_path
     * @param key
     * @param message
     * @throws IOException
     * @throws InsufficientMemoryException
     * @throws UnsupportedAudioFileException
     */

    public void encode(String source_full_path, String destination_full_path, int key, String message) throws IOException, InsufficientMemoryException, UnsupportedAudioFileException {

        byte[] audio_bytes = readBytes(source_full_path);

        byte[] Message = message.getBytes();

        int position = 0;
        
        // adding secert key to audio byte.
        addKey(audio_bytes, position, key);

        // key took (KEY_SIZE * 8) positions ie (4 bytes * 8) = 32 bits position.
        position += KEY_SIZE * 8;
        
        // adding message length to audio byte.
        addLength(audio_bytes, position, Message.length);
        
        // length took (LENGTH_SIZE * 8) positions ie (4 bytes * 8) = 32 bits position.
        position += LENGTH_SIZE * 8;

        // adding message.
        addMessage(audio_bytes, position, Message);
        
        // saving audio_byte array as a file.
        writeBytes(audio_bytes, destination_full_path);

    }
   
    
}
