import javax.swing.*;
import java.awt.*;
import java.util.Stack;
import java.io.File;

public class FileExplorer extends JFrame {
    private DirectoryTree directoryTree;
    private JTextArea fileInfoArea;
    private JButton backButton;
    private Stack<File> directoryStack;
    private File currentDirectory;

    public FileExplorer() {
        setTitle("Simple File Explorer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        directoryStack = new Stack<>();

        // Tạo cây thư mục
        currentDirectory = new File(System.getProperty("user.home"));
        directoryTree = new DirectoryTree(currentDirectory, this);

        // Tạo khu vực hiển thị thông tin tệp
        fileInfoArea = new JTextArea();
        fileInfoArea.setEditable(false);

        // Tạo nút Back
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (!directoryStack.isEmpty()) {
                currentDirectory = directoryStack.pop();
                directoryTree.updateDirectoryTree(currentDirectory);
            }
        });

        // Thêm các thành phần vào JFrame
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(directoryTree.getTree()), BorderLayout.WEST);
        getContentPane().add(new JScrollPane(fileInfoArea), BorderLayout.CENTER);
    }

    public void updateFileInfo(String info) {
        fileInfoArea.setText(info);
    }

    public void pushToStack(File directory) {
        directoryStack.push(directory);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileExplorer explorer = new FileExplorer();
            explorer.setVisible(true);
        });
    }
}
