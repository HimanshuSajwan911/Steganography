
package gui.image;

import gui.stegano.Steganography;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import steganography.core.exceptions.InsufficientBitsException;
import steganography.core.exceptions.InsufficientMemoryException;
import steganography.core.exceptions.InvalidKeyException;
import steganography.core.exceptions.UnsupportedFileException;
import static steganography.core.util.Files.setImageFileExtension;

/**
 * @author Himanshu Sajwan.
 */

public class ImageSteganography extends gui.stegano.Steganography{

    public static void main(String[] args) {
        new ImageSteganography().setVisible(true);
    }
    
    protected void openCoverFile() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Open Image File.");
        chooser.setAcceptAllFileFilterUsed(false);
        setImageFileExtension(chooser);

        int selection = chooser.showOpenDialog(null);

        if (selection == JFileChooser.APPROVE_OPTION) {
            COVER_FILE = chooser.getSelectedFile();
            if (COVER_FILE.exists()) {
                CoverFilePath.setText(COVER_FILE.getAbsolutePath());
                CoverFilePath.setToolTipText(COVER_FILE.getAbsolutePath());
            } 
            else {
                COVER_FILE = null;
                JOptionPane.showMessageDialog(null, "File does not exists!", "ERROR!!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    protected void setDestination() throws IOException {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Save Encoded File.");
        
        chooser.setAcceptAllFileFilterUsed(false);
        setImageFileExtension(chooser);
        
        int selection = chooser.showSaveDialog(null);

        if (selection == JFileChooser.APPROVE_OPTION) {
            DESTINATION_FILE = chooser.getSelectedFile();
            if (DESTINATION_FILE.exists()) {
                
                int choice = JOptionPane.showConfirmDialog(null, "File already exists. \nDo you want to replace it??", "Confirm Save??", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    DESTINATION_FILE = null;
                }
                else{
                    DESTINATION_FILE.delete();
                    DESTINATION_FILE.createNewFile();
                    DestinationPath.setText(DESTINATION_FILE.getAbsolutePath());
                    DestinationPath.setToolTipText(DESTINATION_FILE.getAbsolutePath());
                }
            } 
            else {
                DESTINATION_FILE.createNewFile();
                DestinationPath.setText(DESTINATION_FILE.getAbsolutePath());
                DestinationPath.setToolTipText(DESTINATION_FILE.getAbsolutePath());
            }
        }
    }
    
    protected int encode(){
        
        if(KEY == -1){
            JOptionPane.showMessageDialog(null, "Input a key", "Missing Key", JOptionPane.ERROR_MESSAGE);
            return 1;
        }
        
        if(COVER_FILE == null){
            JOptionPane.showMessageDialog(null, "Input Location for Cover File", "Missing Cover File", JOptionPane.ERROR_MESSAGE);
            return 2;
        }
        
        if(DATA_FILE == null){
            JOptionPane.showMessageDialog(null, "Input Location for Data File", "Missing Data File", JOptionPane.ERROR_MESSAGE);
            return 3;
        }
        
        if(DESTINATION_FILE == null){
            JOptionPane.showMessageDialog(null, "Input Location for Destination File", "Missing Destination File", JOptionPane.ERROR_MESSAGE);
            return 4;
        }
        
        steganography.ImageSteganography image_steganography = new steganography.ImageSteganography();
        
        if(OFFSET != -1){
            image_steganography.setOffset(OFFSET);
        }
        
        if(BUFFER_CAPACITY != -1){
            image_steganography.setBufferCapacity(BUFFER_CAPACITY);
        }
        
        try {
            image_steganography.encode(COVER_FILE.getPath(), DATA_FILE.getPath(), DESTINATION_FILE.getPath(), KEY);
            
            JOptionPane.showMessageDialog(null, "Successfully Encoded", "SUCCESSFUL", JOptionPane.INFORMATION_MESSAGE);
            
            return 0;
        } 
        catch (InsufficientMemoryException | IOException | UnsupportedFileException ex) {
            
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR!!", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Steganography.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        return -1;
    }
    
    protected int decode(){
        
        if(KEY == -1){
            JOptionPane.showMessageDialog(null, "Input a key", "Missing Key", JOptionPane.ERROR_MESSAGE);
            return 1;
        }
        
        if(COVER_FILE == null){
            JOptionPane.showMessageDialog(null, "Input Location for Source File", "Missing Source File", JOptionPane.ERROR_MESSAGE);
            return 2;
        }
        
        if(DESTINATION_FILE == null){
            JOptionPane.showMessageDialog(null, "Input Location for Destination File", "Missing Destination File", JOptionPane.ERROR_MESSAGE);
            return 4;
        }
        
        steganography.ImageSteganography image_steganography = new steganography.ImageSteganography();
       
        if(OFFSET != -1){
            image_steganography.setOffset(OFFSET);
        }
        
        if(BUFFER_CAPACITY != -1){
            image_steganography.setBufferCapacity(BUFFER_CAPACITY);
        }
        
        try {
            image_steganography.decode(COVER_FILE.getPath(), DESTINATION_FILE.getPath(), KEY);
            JOptionPane.showMessageDialog(null, "Successfully Decoded", "SUCCESSFUL", JOptionPane.INFORMATION_MESSAGE);
            return 0;
        } 
        catch (UnsupportedFileException | IOException | InsufficientBitsException | InvalidKeyException ex) {
            
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR!!", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Steganography.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        return -1;
    }
    
}
