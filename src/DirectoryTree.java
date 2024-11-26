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

    public DirectoryTree(File rootDirectory, FileExplorer fileExplorer) {
        this.fileExplorer = fileExplorer;
    
        // Tạo JTree và áp dụng các tùy chỉnh
        tree = new JTree(createTreeNode(rootDirectory));
        tree.setFont(new Font("Arial", Font.PLAIN, 18)); // Font lớn hơn
        tree.setRowHeight(30); // Tăng chiều cao hàng
        tree.setCellRenderer(new CustomTreeCellRenderer());
    
        // Thêm các sự kiện
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                File selectedFile = (File) selectedNode.getUserObject();
                fileExplorer.updatePathField(selectedFile.getAbsolutePath());
            }
        });
    
        tree.addMouseListener(new MouseAdapter() {
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
    
}
