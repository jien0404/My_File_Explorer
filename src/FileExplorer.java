import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class FileExplorer extends JFrame {
    private JTree directoryTree;
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

        // Tạo cây thư mục gốc
        File root = new File(System.getProperty("user.home"));
        currentDirectory = root;
        directoryTree = new JTree(createTreeNode(root));
        directoryTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) directoryTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                File selectedFile = (File) selectedNode.getUserObject();
                fileInfoArea.setText(selectedFile.getAbsolutePath());
            }
        });

        // Thêm MouseListener
        directoryTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    TreePath path = directoryTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        File selectedFile = (File) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        if (selectedFile.isDirectory()) {
                            directoryStack.push(currentDirectory); // Lưu vị trí hiện tại
                            updateDirectoryTree(selectedFile);
                            currentDirectory = selectedFile; // Cập nhật vị trí hiện tại
                        } else {
                            openFile(selectedFile);
                        }
                    }
                }
            }
        });

        // Tạo khu vực hiển thị thông tin tệp
        fileInfoArea = new JTextArea();
        fileInfoArea.setEditable(false);

        // Tạo nút Back
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (!directoryStack.isEmpty()) {
                currentDirectory = directoryStack.pop(); // Lấy vị trí trước đó
                updateDirectoryTree(currentDirectory); // Cập nhật cây thư mục
            }
        });

        // Thêm các thành phần vào JFrame
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(directoryTree), BorderLayout.WEST);
        getContentPane().add(new JScrollPane(fileInfoArea), BorderLayout.CENTER);
    }

    private DefaultMutableTreeNode createTreeNode(File directory) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(directory);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                rootNode.add(node);
            }
        }
        return rootNode;
    }

    private void updateDirectoryTree(File directory) {
        directoryTree.setModel(new DefaultTreeModel(createTreeNode(directory)));
    }

    private void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            showErrorDialog("Cannot open file or directory: " + file.getAbsolutePath());
        } catch (UnsupportedOperationException ex) {
            showErrorDialog("This operation is not supported on your platform.");
        } catch (Exception ex) {
            showErrorDialog("An unexpected error occurred: " + ex.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileExplorer explorer = new FileExplorer();
            explorer.setVisible(true);
        });
    }
}