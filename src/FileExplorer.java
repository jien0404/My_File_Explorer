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
        setTitle("My File Explorer");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLookAndFeel();

        directoryStack = new Stack<>();

        // Hiển thị danh sách ổ đĩa mặc định (My Computer)
        currentDirectory = null;
        directoryTree = new DirectoryTree(currentDirectory, this);

        // Tạo thanh điều hướng (North)
        JPanel navigationPanel = new JPanel(new BorderLayout());
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(e -> {
            if (!directoryStack.isEmpty()) {
                currentDirectory = directoryStack.pop();
                directoryTree.updateDirectoryTree(currentDirectory);
                updatePathField(currentDirectory.getAbsolutePath());
            } else {
                currentDirectory = null;
                directoryTree.updateDirectoryTree(null);
                updatePathField("My Computer");
            }
        });

        pathField = new JTextField("My Computer");
        pathField.setEditable(true); // Cho phép chỉnh sửa
        pathField.setFont(new Font("Consolas", Font.PLAIN, 16));

        // Lắng nghe sự kiện Enter
        pathField.addActionListener(e -> {
            String enteredPath = pathField.getText().trim();
            File enteredDirectory = new File(enteredPath);

            if (enteredDirectory.exists() && enteredDirectory.isDirectory()) {
                pushToStack(currentDirectory); // Lưu thư mục hiện tại vào ngăn xếp
                currentDirectory = enteredDirectory;
                directoryTree.updateDirectoryTree(currentDirectory);
                updatePathField(currentDirectory.getAbsolutePath());
            } else {
                // Hiển thị thông báo lỗi nếu đường dẫn không hợp lệ
                JOptionPane.showMessageDialog(this, 
                    "The entered path is invalid or not a directory:\n" + enteredPath, 
                    "Invalid Path", 
                    JOptionPane.ERROR_MESSAGE);
                updatePathField(currentDirectory != null ? currentDirectory.getAbsolutePath() : "My Computer");
            }
        });

        navigationPanel.add(backButton, BorderLayout.WEST);
        navigationPanel.add(pathField, BorderLayout.CENTER);

        // Thêm thanh menu tính năng vào phía trên cùng
        JPanel actionPanel = createActionPanel(); // Tạo thanh chức năng
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(navigationPanel, BorderLayout.NORTH); // Phần điều hướng
        topPanel.add(actionPanel, BorderLayout.SOUTH);    // Thanh chức năng

        // Thêm các thành phần vào JFrame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(directoryTree.getTree()), BorderLayout.CENTER);
    }

    public void updatePathField(String path) {
        pathField.setText(path);
    }    

    public void pushToStack(File directory) {
        directoryStack.push(directory);
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }
    
    public void setCurrentDirectory(File directory) {
        this.currentDirectory = directory;
    }
    
    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Tree.font", new FontUIResource(new Font("Arial", Font.PLAIN, 14)));
        } catch (Exception ex) {
            System.err.println("Failed to apply Look and Feel");
        }
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Tạo các nút chức năng
        JButton addFolderButton = new JButton("Add Folder");
        JButton addFileButton = new JButton("Add File");
        JButton renameButton = new JButton("Rename");
        JButton deleteButton = new JButton("Delete");
    
        // Đặt font chữ và icon
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        addFolderButton.setFont(buttonFont);
        addFileButton.setFont(buttonFont);
        renameButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);

        addFolderButton.setToolTipText("Create a new folder");
        addFileButton.setToolTipText("Create a new file");
        renameButton.setToolTipText("Rename the selected item");
        deleteButton.setToolTipText("Delete the selected item");
    
        // Thêm các nút vào panel
        actionPanel.add(addFolderButton);
        actionPanel.add(addFileButton);
        actionPanel.add(renameButton);
        actionPanel.add(deleteButton);

        renameButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Bật/tắt khi một mục được chọn trong DirectoryTree
        directoryTree.getTree().addTreeSelectionListener(e -> {
            boolean isItemSelected = directoryTree.getSelectedNode() != null;
            renameButton.setEnabled(isItemSelected);
            deleteButton.setEnabled(isItemSelected);
        });
    
        // Xử lý sự kiện
        addFolderButton.addActionListener(e -> directoryTree.handleAddFolder());
        addFileButton.addActionListener(e -> directoryTree.handleAddFile());
        renameButton.addActionListener(e -> directoryTree.handleRename());
        deleteButton.addActionListener(e -> directoryTree.handleDelete());
    
        return actionPanel;
    }
}