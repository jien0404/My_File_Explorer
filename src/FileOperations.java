import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class FileOperations {
    public static void openFile(File file, JFrame parent) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            showErrorDialog("Cannot open file or directory: " + file.getAbsolutePath(), parent);
        } catch (UnsupportedOperationException ex) {
            showErrorDialog("This operation is not supported on your platform.", parent);
        } catch (Exception ex) {
            showErrorDialog("An unexpected error occurred: " + ex.getMessage(), parent);
        }
    }

    public static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteRecursively(f);
                }
            }
        }
        return file.delete();
    }

    private static void showErrorDialog(String message, JFrame parent) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
