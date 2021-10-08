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
     * Function to encode Document from <B>source_full_path</B> location 
     * with <B>Message</b> message and save this encoded document to <B>destination_full_path</B> location.
     * 
     * @param source_full_path location of source Document file.
     * @param destination_full_path location to save encoded Document file.
     * @param key to secure encoded file with a 32 bit size integer.
     * @param message to be encoded into the Document file.
     * 
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
