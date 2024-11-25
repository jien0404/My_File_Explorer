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
        tree = new JTree(createTreeNode(rootDirectory));
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                File selectedFile = (File) selectedNode.getUserObject();
                fileExplorer.updateFileInfo(selectedFile.getAbsolutePath());
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
                            fileExplorer.pushToStack(rootDirectory);
                            updateDirectoryTree(selectedFile);
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
    }

    private DefaultMutableTreeNode createTreeNode(File directory) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(directory);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                rootNode.add(new DefaultMutableTreeNode(file));
            }
        }
        return rootNode;
    }

    // Bộ renderer tùy chỉnh để hiển thị biểu tượng
    private static class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
        private final Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
        private final Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof File file) {
                if (file.isDirectory()) {
                    setIcon(folderIcon);
                } else {
                    setIcon(fileIcon);
                }
            }
            return c;
        }
    }
}
