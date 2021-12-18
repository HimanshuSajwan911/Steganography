
package gui.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * @author Himanshu Sajwan.
 */

public final class FileDropHandler extends TransferHandler {
    
    private File file;
    private boolean set;

    public FileDropHandler() {
        this.set = false;
    }
    
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        for (DataFlavor flavor : support.getDataFlavors()) {
            if (flavor.isFlavorJavaFileListType()) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!this.canImport(support)){
            return false;
        }

        List<File> files;
        try {
            files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        } 
        catch (UnsupportedFlavorException | IOException ex) {
            
            return false;
        }

        set = true;
        file = files.get(0);
        
        return true;
    }

    public void setDrop(JComponent jc){
        jc.setToolTipText(file.getPath());
    }
    
    public File getFile() {
        return file;
    }

    public boolean isSet() {
        return set;
    }
    
    public void setSet(boolean value){
        set = value;
    }
    
}
