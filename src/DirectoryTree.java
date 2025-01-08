import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class DirectoryTree {
    private JTree tree;
    private FileExplorer fileExplorer;
    private DefaultMutableTreeNode rootNode;

    public DirectoryTree(File rootDirectory, FileExplorer fileExplorer) {
        this.fileExplorer = fileExplorer;
        rootNode = createTreeNode(rootDirectory);
        tree = new JTree(rootNode);
        // tree = new JTree(createTreeNode(rootDirectory));
        tree.setFont(new Font("Arial", Font.PLAIN, 18));
        tree.setRowHeight(30);
        tree.setCellRenderer(new CustomTree());

        // Thêm các sự kiện
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                File selectedFile = (File) selectedNode.getUserObject();
                fileExplorer.updatePathField(selectedFile.getAbsolutePath());
            }
        });

        // Tạo và thêm menu ngữ cảnh
        ContextMenu contextMenu = new ContextMenu(this);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    contextMenu.showMenu(tree, e.getX(), e.getY());
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
            rootNode = new DefaultMutableTreeNode("My Computer");
            File[] roots = File.listRoots();
            if (roots != null) {
                for (File root : roots) {
                    rootNode.add(new DefaultMutableTreeNode(root));
                }
            }
        } else {
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
                if (FileOperations.deleteRecursively(selectedFile)) {
                    JOptionPane.showMessageDialog(null, "Deleted successfully!");
                    updateDirectoryTree(fileExplorer.getCurrentDirectory());
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete.");
                }
            }
        }
    }

    public void filterFiles(String searchTerm) {
        File currentDirectory = fileExplorer.getCurrentDirectory();
        DefaultMutableTreeNode newRootNode = new DefaultMutableTreeNode("Search Results");
        filterFilesRecursively(createTreeNode(currentDirectory), newRootNode, searchTerm);
        
        if (newRootNode.getChildCount() == 0) {
            newRootNode.add(new DefaultMutableTreeNode("No results found."));
        }
        
        tree.setModel(new DefaultTreeModel(newRootNode));
    }
    
    private void filterFilesRecursively(DefaultMutableTreeNode currentNode, DefaultMutableTreeNode newRootNode, String searchTerm) {
        for (int i = 0; i < currentNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) currentNode.getChildAt(i);
            File file = (File) childNode.getUserObject();
            
            // Tìm kiếm chỉ theo tên tệp
            boolean matchName = file.getName().toLowerCase().contains(searchTerm.toLowerCase());
            
            if (matchName) {
                newRootNode.add(new DefaultMutableTreeNode(file));
            }
            
            // Nếu là thư mục, tìm kiếm đệ quy
            if (file.isDirectory()) {
                filterFilesRecursively(createTreeNode(file), newRootNode, searchTerm);
            }
        }
    }
}
