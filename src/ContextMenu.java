import java.awt.Image;

import javax.swing.*;

public class ContextMenu {
    private JPopupMenu contextMenu;
    private DirectoryTree directoryTree;

    public ContextMenu(DirectoryTree directoryTree) {
        this.directoryTree = directoryTree;
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
        
        // Gán hành động cho các menu item
        addFolderMenuItem.addActionListener(e -> directoryTree.handleAddFolder());
        addFileMenuItem.addActionListener(e -> directoryTree.handleAddFile());
        renameMenuItem.addActionListener(e -> directoryTree.handleRename());
        deleteMenuItem.addActionListener(e -> directoryTree.handleDelete());
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

    public void showMenu(JTree tree, int x, int y) {
        int row = tree.getClosestRowForLocation(x, y);
        tree.setSelectionRow(row);
        contextMenu.show(tree, x, y);
    }
}