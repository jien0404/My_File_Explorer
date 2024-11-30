import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public class CustomTree extends DefaultTreeCellRenderer {
    private final Icon folderIcon = scaleIcon(UIManager.getIcon("FileView.directoryIcon"), 24);
    private final Icon fileIcon = scaleIcon(UIManager.getIcon("FileView.fileIcon"), 24);

    private static Icon scaleIcon(Icon icon, int size) {
        if (icon instanceof ImageIcon) {
            Image img = ((ImageIcon) icon).getImage();
            Image newImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        }
        return icon;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node.getUserObject() instanceof File file) {
            setIcon(file.isDirectory() ? folderIcon : fileIcon);
        }
        return c;
    }
}