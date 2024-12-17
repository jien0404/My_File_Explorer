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
        setTitle("File Explorer");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLookAndFeel();

        directoryStack = new Stack<>();

        // Hiển thị danh sách ổ đĩa mặc định (My Computer)
        currentDirectory = null;
        directoryTree = new DirectoryTree(currentDirectory, this);

        // Tạo thanh điều hướng (North)
        JPanel navigationPanel = new JPanel(new BorderLayout());
        backButton = createNavigationButton("← Back");
        
        pathField = new JTextField("My Computer");
        pathField.setEditable(true);
        pathField.setFont(new Font("Consolas", Font.PLAIN, 16));
        pathField.setBorder(BorderFactory.createCompoundBorder(
            pathField.getBorder(), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Hộp tìm kiếm
        searchField = new JTextField(20);
        searchField.setFont(new Font("Consolas", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            searchField.getBorder(), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Placeholder text cho search field
        searchField.setText("Files and folders");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Files and folders")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Files and folders");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchField.addActionListener(e -> searchFiles(searchField.getText()));

        // Nút tìm kiếm với biểu tượng
        searchButton = createSearchButton();
        searchButton.addActionListener(e -> {
            if (isSearching) {
                exitSearchMode();
            } else {
                searchFiles(searchField.getText());
            }
        });

        // Lắng nghe sự kiện Enter ở pathField
        pathField.addActionListener(e -> navigateToPath());

        // Tạo panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Thêm các thành phần vào navigation panel
        navigationPanel.add(backButton, BorderLayout.WEST);
        navigationPanel.add(pathField, BorderLayout.CENTER);
        navigationPanel.add(searchPanel, BorderLayout.EAST);

        // Tạo toolbar chức năng
        JToolBar actionToolBar = createToolBar();
        
        // Panel trên cùng
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(navigationPanel, BorderLayout.NORTH);
        topPanel.add(actionToolBar, BorderLayout.SOUTH);

        // Cấu hình layout cho JFrame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(directoryTree.getTree()), BorderLayout.CENTER);

        // Sự kiện back button
        backButton.addActionListener(e -> {
            if (!directoryStack.isEmpty()) {
                currentDirectory = directoryStack.pop();
                directoryTree.updateDirectoryTree(currentDirectory);
                updatePathField(currentDirectory != null ? 
                    currentDirectory.getAbsolutePath() : "My Computer");
            } else {
                currentDirectory = null;
                directoryTree.updateDirectoryTree(null);
                updatePathField("My Computer");
            }
        });
    }

    // Phương thức tạo nút back với style
    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        
        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(true);
                button.setBackground(new Color(200, 220, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(false);
            }
        });
        
        return button;
    }

    // Phương thức tạo nút tìm kiếm
    private JButton createSearchButton() {
        JButton button = new JButton("🔍");
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    // Điều hướng tới đường dẫn
    private void navigateToPath() {
        String enteredPath = pathField.getText().trim();
        File enteredDirectory = new File(enteredPath);

        if (enteredDirectory.exists() && enteredDirectory.isDirectory()) {
            pushToStack(currentDirectory);
            currentDirectory = enteredDirectory;
            directoryTree.updateDirectoryTree(currentDirectory);
            updatePathField(currentDirectory.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, 
                "Đường dẫn không hợp lệ:\n" + enteredPath, 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            updatePathField(currentDirectory != null ? 
                currentDirectory.getAbsolutePath() : "My Computer");
        }
    }

    // Tìm kiếm files
    private void searchFiles(String searchTerm) {
        if (searchTerm.equals("Files and folders...")) return;
        
        directoryTree.filterFiles(searchTerm);
        searchButton.setText("✖");
        isSearching = true;
    }

    // Thoát chế độ tìm kiếm
    private void exitSearchMode() {
        searchField.setText("Files and folders...");
        searchField.setForeground(Color.GRAY);
        directoryTree.updateDirectoryTree(currentDirectory);
        searchButton.setText("🔍");
        isSearching = false;
    }

    // Các phương thức hỗ trợ
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
    
    // Cài đặt giao diện
    private void setLookAndFeel() {
        try {
            // Sử dụng giao diện hiện đại
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            // Tùy chỉnh màu sắc
            UIDefaults defaults = UIManager.getLookAndFeelDefaults();
            defaults.put("activeCaption", new Color(41, 128, 185)); 
            defaults.put("control", new Color(240, 240, 240)); 
            
            // Font chữ hiện đại
            UIManager.put("Tree.font", new FontUIResource(new Font("Segoe UI", Font.PLAIN, 14)));
            UIManager.put("Button.font", new FontUIResource(new Font("Segoe UI", Font.BOLD, 14)));
            UIManager.put("TextField.font", new FontUIResource(new Font("Consolas", Font.PLAIN, 14)));
        } catch (Exception ex) {
            System.err.println("Không thể áp dụng giao diện");
        }
    }

    // Tạo toolbar chức năng
    private JToolBar createToolBar() { 
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(240, 240, 240));

        // Tạo các nút chức năng
        JButton addFolderButton = createToolbarButton("Thêm Thư Mục", "Add Folder");
        JButton addFileButton = createToolbarButton("Thêm Tệp", "Add File");
        JButton renameButton = createToolbarButton("Đổi Tên", "Rename");
        JButton deleteButton = createToolbarButton("Xóa", "Delete");

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

    // Phương thức hỗ trợ tạo nút toolbar
    private JButton createToolbarButton(String tooltip, String text) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(true);
                button.setBackground(new Color(200, 220, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(false);
            }
        });
        
        return button;
    }
}