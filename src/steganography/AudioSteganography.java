package steganography;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import static steganography.encoder.SteganographyEncoder.addBits;
import static steganography.encoder._ToByteConverter.intToByte;
import steganography.exceptions.InsufficientMemoryException;

/**
 * @author Himanshu Sajwan.
 */

public class AudioSteganography {

    public void encode(String source_full_path, String destination_full_path, int key, String message) throws IOException, InsufficientMemoryException {

        Path path = Paths.get(source_full_path);
        byte[] audio_bytes = Files.readAllBytes(path);

        // adding secert key to audio file.
        addKey(audio_bytes, key);

    }

    private void addKey(byte[] source, int key) throws InsufficientMemoryException {
        byte[] keyBytes = intToByte(key);

        int starting = 44;

        addBits(source, starting, keyBytes);

    }

    /**
     * Generates an audio file from the stream. The file must be a WAV file.
     *
     * @param data the byte array
     * @param outputFile the file in which to write the audio data could not be
     * written onto the file
     */
    public static void generateWavFile(byte[] data, File outputFile) {
        try {

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
            if (outputFile.getName().endsWith("wav")) {
                int nb = AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE,
                        new FileOutputStream(outputFile));

            } else {
                throw new RuntimeException("Unsupported encoding " + outputFile);
            }
        } catch (IOException | RuntimeException | UnsupportedAudioFileException e) {
            throw new RuntimeException("could not generate file: " + e);
        }
    }

}
