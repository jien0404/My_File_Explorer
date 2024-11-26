import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class DirectoryTree {
    private JTree tree;
    private FileExplorer fileExplorer;
    private JPopupMenu contextMenu;

    public DirectoryTree(File rootDirectory, FileExplorer fileExplorer) {
        this.fileExplorer = fileExplorer;
        tree = new JTree(createTreeNode(rootDirectory));
        tree.setFont(new Font("Arial", Font.PLAIN, 18));
        tree.setRowHeight(30);
        tree.setCellRenderer(new CustomTreeCellRenderer());

        // Thêm các sự kiện
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                File selectedFile = (File) selectedNode.getUserObject();
                fileExplorer.updatePathField(selectedFile.getAbsolutePath());
            }
        });
    
        // Tạo menu ngữ cảnh
        contextMenu = new JPopupMenu();
        JMenu addSubMenu = new JMenu("Add New");
        addSubMenu.setIcon(loadIcon("/resources/icons/add.png", 20, 20));

        JMenuItem addFolderMenuItem = new JMenuItem("Add Folder");
        addFolderMenuItem.setIcon(loadIcon("/resources/icons/folder.png", 20, 20));

        JMenuItem addFileMenuItem = new JMenuItem("Add File");
        addFileMenuItem.setIcon(loadIcon("/resources/icons/file.png", 20, 20));

        addSubMenu.add(addFolderMenuItem);
        addSubMenu.add(addFileMenuItem);

        JMenuItem renameMenuItem = new JMenuItem("Rename");
        renameMenuItem.setIcon(loadIcon("resources\\icons\\rename.png", 20, 20));
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setIcon(loadIcon("resources\\icons\\delete.png", 20, 20));
    
        contextMenu.add(addSubMenu);
        contextMenu.add(renameMenuItem);
        contextMenu.add(deleteMenuItem);
    
        // Thêm sự kiện chuột phải
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    contextMenu.show(tree, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        File selectedFile = (File) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        if (selectedFile.isDirectory()) {
                            fileExplorer.pushToStack(fileExplorer.getCurrentDirectory());
                            fileExplorer.setCurrentDirectory(selectedFile);
                            updateDirectoryTree(selectedFile);
                            fileExplorer.updatePathField(selectedFile.getAbsolutePath());
                        } else {
                            FileOperations.openFile(selectedFile, fileExplorer);
                        }
                    }
                }
            }
        });
    
        // Xử lý hành động
        addFolderMenuItem.addActionListener(e -> handleAddFolder());
        addFileMenuItem.addActionListener(e -> handleAddFile());
        renameMenuItem.addActionListener(e -> handleRename());
        deleteMenuItem.addActionListener(e -> handleDelete());
    }
        

    public JTree getTree() {
        return tree;
    }

    public DefaultMutableTreeNode getSelectedNode() {
        TreePath selectedPath = tree.getSelectionPath();
        if (selectedPath == null) {
            return null;
        }
        return (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
    }
    

    public void updateDirectoryTree(File directory) {
        tree.setModel(new DefaultTreeModel(createTreeNode(directory)));
        if (directory == null) {
            fileExplorer.updatePathField("My Computer");
        }
    }
    

    private DefaultMutableTreeNode createTreeNode(File directory) {
        DefaultMutableTreeNode rootNode;
        if (directory == null) {
            // Hiển thị danh sách ổ đĩa
            rootNode = new DefaultMutableTreeNode("My Computer");
            File[] roots = File.listRoots();
            if (roots != null) {
                for (File root : roots) {
                    rootNode.add(new DefaultMutableTreeNode(root));
                }
            }
        } else {
            // Hiển thị nội dung thư mục
            rootNode = new DefaultMutableTreeNode(directory);
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    rootNode.add(new DefaultMutableTreeNode(file));
                }
            }
        }
        return rootNode;
    }
    
    public Icon loadIcon(String path, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(path));
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path);
            return null;
        }
    }
    

    // Bộ renderer tùy chỉnh để hiển thị biểu tượng
    private static class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        private final Icon folderIcon = scaleIcon(UIManager.getIcon("FileView.directoryIcon"), 24);
        private final Icon fileIcon = scaleIcon(UIManager.getIcon("FileView.fileIcon"), 24);
    
        // Phương thức scale icon
        private static Icon scaleIcon(Icon icon, int size) {
            if (icon instanceof ImageIcon) {
                Image img = ((ImageIcon) icon).getImage();
                Image newImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH); // Scale icon
                return new ImageIcon(newImg);
            }
            return icon;
        }
    
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    
            // Lấy thông tin node để hiển thị icon phù hợp
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof File file) {
                if (file.isDirectory()) {
                    setIcon(folderIcon); // Icon thư mục
                } else {
                    setIcon(fileIcon); // Icon tập tin
                }
            }
            return c;
        }
    }
    
    public void handleAddFolder() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            File selectedFile = (File) selectedNode.getUserObject();
            if (selectedFile.isDirectory()) {
                String newFolderName = JOptionPane.showInputDialog("Enter name for new folder:");
                if (newFolderName != null && !newFolderName.trim().isEmpty()) {
                    File newFolder = new File(selectedFile, newFolderName);
                    if (newFolder.mkdir()) {
                        JOptionPane.showMessageDialog(null, "Folder created successfully!");
                        updateDirectoryTree(fileExplorer.getCurrentDirectory());
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create folder.");
                    }
                }
            }
        }
    }

    public void handleAddFile() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            File selectedFile = (File) selectedNode.getUserObject();
            if (selectedFile.isDirectory()) {
                // Danh sách các loại tệp
                String[] fileTypes = {".txt", ".jpg", ".pdf", ".docx", ".png"};
    
                // Tạo JPanel để chứa các thành phần nhập liệu
                JPanel panel = new JPanel(new GridLayout(2, 2));
                JTextField fileNameField = new JTextField(15);
                JComboBox<String> fileTypeComboBox = new JComboBox<>(fileTypes);
    
                panel.add(new JLabel("Enter name for new file (without extension):"));
                panel.add(fileNameField);
                panel.add(new JLabel("Select file type:"));
                panel.add(fileTypeComboBox);
    
                int result = JOptionPane.showConfirmDialog(null, panel, "Create New File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String newFileName = fileNameField.getText().trim();
                    String selectedType = (String) fileTypeComboBox.getSelectedItem();
    
                    if (!newFileName.isEmpty() && selectedType != null) {
                        File newFile = new File(selectedFile, newFileName + selectedType);
                        try {
                            if (newFile.createNewFile()) {
                                JOptionPane.showMessageDialog(null, "File created successfully!");
                                updateDirectoryTree(fileExplorer.getCurrentDirectory());
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to create file.");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter a valid name for the file.");
                    }
                }
            }
        }
    }

    public void handleRename() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            File selectedFile = (File) selectedNode.getUserObject();
            String newName = JOptionPane.showInputDialog("Enter new name:", selectedFile.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                File renamedFile = new File(selectedFile.getParent(), newName);
                if (selectedFile.renameTo(renamedFile)) {
                    JOptionPane.showMessageDialog(null, "Renamed successfully!");
                    updateDirectoryTree(fileExplorer.getCurrentDirectory());
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to rename.");
                }
            }
        }
    }

    public void handleDelete() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            File selectedFile = (File) selectedNode.getUserObject();
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (deleteRecursively(selectedFile)) {
                    JOptionPane.showMessageDialog(null, "Deleted successfully!");
                    updateDirectoryTree(fileExplorer.getCurrentDirectory());
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete.");
                }
            }
        }
    }
    
    private boolean deleteRecursively(File file) {
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
    
}
