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
    private JTextField searchField;
    private JButton searchButton;

    private boolean isSearching = false;

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

        // Hộp tìm kiếm
        searchField = new JTextField(20);
        searchField.setFont(new Font("Consolas", Font.PLAIN, 16));
        searchField.addActionListener(e -> {
            if (isSearching) {
                // Nếu đang trong chế độ tìm kiếm, thoát khỏi tìm kiếm
                exitSearchMode();
            } else {
                // Nếu không, thực hiện tìm kiếm
                searchFiles(searchField.getText());
            }
        });

        // Nút tìm kiếm với biểu tượng
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("resources\\icons\\search_icon.png"));
        Image scaledSearchIcon = searchIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        searchButton = new JButton(new ImageIcon(scaledSearchIcon));
        searchButton.setPreferredSize(new Dimension(30, 30));

        // Bỏ nền và đường viền
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false); // Bỏ viền khi nút được chọn
        searchButton.setFocusable(false); // Bỏ khả năng tiêu điểm

        searchButton.addActionListener(e -> {
            if (isSearching) {
                // Nếu đang trong chế độ tìm kiếm, thoát khỏi tìm kiếm
                exitSearchMode();
            } else {
                // Nếu không, thực hiện tìm kiếm
                searchFiles(searchField.getText());
            }
        });

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

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        navigationPanel.add(backButton, BorderLayout.WEST);
        navigationPanel.add(pathField, BorderLayout.CENTER);
        navigationPanel.add(searchPanel, BorderLayout.EAST); // Thêm hộp tìm kiếm

        // Thêm thanh menu tính năng vào phía trên cùng
        JToolBar actionToolBar = createToolBar();
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(navigationPanel, BorderLayout.NORTH); // Phần điều hướng
        topPanel.add(actionToolBar, BorderLayout.SOUTH);    // Thanh chức năng

        // Thêm các thành phần vào JFrame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(directoryTree.getTree()), BorderLayout.CENTER);
    }

    private void searchFiles(String searchTerm) {
        directoryTree.filterFiles(searchTerm);
        // Đổi icon thành icon exit
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("resources\\icons\\exit.png"));
        Image scaledSearchIcon = searchIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        searchButton.setIcon(new ImageIcon(scaledSearchIcon));
        isSearching = true; // Đánh dấu là đang tìm kiếm
    }

    private void exitSearchMode() {
        searchField.setText(""); // Xóa nội dung hộp tìm kiếm
        directoryTree.updateDirectoryTree(currentDirectory); // Quay lại thư mục trước đó
        ImageIcon searchIcon = new ImageIcon(getClass().getResource("resources\\icons\\search_icon.png"));
        Image scaledSearchIcon = searchIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        searchButton.setIcon(new ImageIcon(scaledSearchIcon));
        isSearching = false; // Đánh dấu là không còn tìm kiếm
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

    private JToolBar createToolBar() { 
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
    
        JButton addFolderButton = new JButton("Add Folder");
        JButton addFileButton = new JButton("Add File");
        JButton renameButton = new JButton("Rename");
        JButton deleteButton = new JButton("Delete");
    
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        addFolderButton.setFont(buttonFont);
        addFileButton.setFont(buttonFont);
        renameButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);

        addFolderButton.setToolTipText("Create a new folder");
        addFileButton.setToolTipText("Create a new file");
        renameButton.setToolTipText("Rename the selected item");
        deleteButton.setToolTipText("Delete the selected item");
    
        // // Set button icons
        // addFolderButton.setIcon(new ImageIcon("/resources/icons/folder.png"));
        // addFileButton.setIcon(new ImageIcon("/resources/icons/file.png"));
        // renameButton.setIcon(new ImageIcon("resources\\icons\\rename.png"));
        // deleteButton.setIcon(new ImageIcon("resources\\icons\\delete.png"));
    
        toolBar.add(addFolderButton);
        toolBar.addSeparator();
        toolBar.add(addFileButton);
        toolBar.addSeparator();
        toolBar.add(renameButton);
        toolBar.addSeparator();
        toolBar.add(deleteButton);
    
        addFolderButton.addActionListener(e -> directoryTree.handleAddFolder());
        addFileButton.addActionListener(e -> directoryTree.handleAddFile());
        renameButton.addActionListener(e -> directoryTree.handleRename());
        deleteButton.addActionListener(e -> directoryTree.handleDelete());
    
        return toolBar;
    }    
}