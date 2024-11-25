import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.Stack;

public class FileExplorer extends JFrame {
    private DirectoryTree directoryTree;
    private JTextField pathField;
    private JButton backButton;
    private Stack<File> directoryStack;
    private File currentDirectory;

    public FileExplorer() {
        setTitle("Enhanced File Explorer");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLookAndFeel();

        currentDirectory = null; // Bắt đầu ở danh sách ổ đĩa
        directoryTree = new DirectoryTree(currentDirectory, this);


        directoryStack = new Stack<>();

        // Tạo cây thư mục
        currentDirectory = new File(System.getProperty("user.home"));
        directoryTree = new DirectoryTree(currentDirectory, this);

        // Tạo thanh điều hướng (North)
        JPanel navigationPanel = new JPanel(new BorderLayout());
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        // backButton.addActionListener(e -> {
        //     if (!directoryStack.isEmpty()) {
        //         currentDirectory = directoryStack.pop();
        //         directoryTree.updateDirectoryTree(currentDirectory);
        //         updatePathField(currentDirectory.getAbsolutePath());
        //     }
        // });
        backButton.addActionListener(e -> {
            if (!directoryStack.isEmpty()) {
                currentDirectory = directoryStack.pop();
                directoryTree.updateDirectoryTree(currentDirectory);
                updatePathField(currentDirectory != null ? currentDirectory.getAbsolutePath() : "My Computer");
            } else {
                // Reset về danh sách ổ đĩa
                currentDirectory = null;
                directoryTree.updateDirectoryTree(null);
                updatePathField("My Computer");
            }
        });
        

        pathField = new JTextField(currentDirectory.getAbsolutePath());
        pathField.setEditable(false);
        pathField.setFont(new Font("Consolas", Font.PLAIN, 14));

        navigationPanel.add(backButton, BorderLayout.WEST);
        navigationPanel.add(pathField, BorderLayout.CENTER);

        // Thêm các thành phần vào JFrame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navigationPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(directoryTree.getTree()), BorderLayout.CENTER);
    }

    public void updatePathField(String path) {
        pathField.setText(path);
    }

    public void pushToStack(File directory) {
        directoryStack.push(directory);
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Tree.font", new FontUIResource(new Font("Arial", Font.PLAIN, 14)));
        } catch (Exception ex) {
            System.err.println("Failed to apply Look and Feel");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileExplorer explorer = new FileExplorer();
            explorer.setVisible(true);
        });
    }
}
