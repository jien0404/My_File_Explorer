import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeSelectionListener; // Thêm dòng này
import java.io.File;

public class DirectoryTree extends JTree {
    private DefaultMutableTreeNode rootNode;

    public DirectoryTree(File rootDirectory) {
        rootNode = createTreeNode(rootDirectory);
        setModel(new DefaultTreeModel(rootNode));
    }

    private DefaultMutableTreeNode createTreeNode(File directory) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(directory);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file);
                node.add(childNode);
                if (file.isDirectory()) {
                    childNode.add(createTreeNode(file)); // Đệ quy thêm thư mục con
                }
            }
        }
        return node;
    }

    public void updateDirectoryTree(File directory) {
        rootNode = createTreeNode(directory);
        setModel(new DefaultTreeModel(rootNode));
    }

    public File getSelectedFile() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
        if (selectedNode != null) {
            return (File) selectedNode.getUserObject();
        }
        return null;
    }

    // Thêm phương thức này để thêm TreeSelectionListener
    public void addTreeSelectionListener(TreeSelectionListener listener) {
        super.addTreeSelectionListener(listener);
    }
}