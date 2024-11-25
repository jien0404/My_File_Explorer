import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.Stack;

public class FileExplorer extends JFrame {
    private DirectoryTree directoryTree;
    private JTextArea fileInfoArea;
    private JToolBar toolBar;
    private JButton backButton;
    private Stack<File> directoryStack;
    private File currentDirectory;

    public FileExplorer() {
        setTitle("Enhanced File Explorer");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLookAndFeel();

        directoryStack = new Stack<>();

        // Tạo cây thư mục
        currentDirectory = new File(System.getProperty("user.home"));
        directoryTree = new DirectoryTree(currentDirectory, this);

        // Tạo khu vực hiển thị thông tin tệp
        fileInfoArea = new JTextArea();
        fileInfoArea.setEditable(false);
        fileInfoArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        fileInfoArea.setLineWrap(true);
        fileInfoArea.setWrapStyleWord(true);

        // Tạo thanh công cụ (Toolbar)
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(e -> {
            if (!directoryStack.isEmpty()) {
                currentDirectory = directoryStack.pop();
                directoryTree.updateDirectoryTree(currentDirectory);
            }
        });
        toolBar.add(backButton);

        // Sử dụng JSplitPane để chia màn hình
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(directoryTree.getTree()), new JScrollPane(fileInfoArea));
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);

        // Thêm các thành phần vào JFrame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    public void updateFileInfo(String info) {
        fileInfoArea.setText(info);
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
