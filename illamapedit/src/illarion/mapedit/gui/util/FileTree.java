/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.util;

import illarion.mapedit.data.MapIO;
import illarion.mapedit.events.menu.MapOpenEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Fredrik K
 */
public class FileTree extends JTree {
    private static final Color SELECTED_COLOR = new Color(115,164,209);

    private class FileTreeMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(final MouseEvent e) {
            final int selRow = getClosestRowForLocation(e.getX(), e.getY());
            if(selRow == -1) {
                return;
            }
            final Rectangle bounds = getRowBounds(selRow);
            final boolean outside = (e.getX() < bounds.getX()) || (e.getX() > (bounds.getX() + bounds.getWidth()));

            final boolean onRow = (e.getY() >= bounds.getY()) && (e.getY() < (bounds.getY() + bounds.getHeight()));
            if (onRow) {
                if(outside) {
                    setSelectionRow(selRow);
                    if(e.getClickCount() == 2) {
                        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                        if (node == null) {
                            return;
                        }
                        if (isCollapsed(selRow)) {
                            expandRow(selRow);
                        } else {
                            collapseRow(selRow);
                        }
                    }
                }
                if(e.getClickCount() == 2) {
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                    if (node == null) {
                        return;
                    }
                    if (node.isLeaf()) {
                        EventBus.publish(node.getUserObject());
                    }
                }
            }
        }
    }

    public static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        private static final int ICON_SIZE = 16;

        public MyTreeCellRenderer() {
            setBackgroundNonSelectionColor(null);
            final ResizableIcon iconOpen = ImageLoader.getResizableIcon("fileopen");
            iconOpen.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));
            final ResizableIcon iconMap = ImageLoader.getResizableIcon("mapedit64");
            iconMap.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));
            setLeafIcon(iconMap);
            setClosedIcon(iconOpen);
            setOpenIcon(iconOpen);
            setBorderSelectionColor(null);
            setBackgroundSelectionColor(null);
        }

        @Override
        @Nullable
        public Color getBackground() {
            return null;
        }
    }

    public FileTree() {
        setCellRenderer(new MyTreeCellRenderer());
        setRootVisible(false);
        setShowsRootHandles(true);
        setEditable(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        addMouseListener(new FileTreeMouseAdapter());
    }

    public static boolean isMapFile(final File node) {
        return node.isDirectory() || node.getName().endsWith(MapIO.EXT_TILE);
    }

    private static MutableTreeNode scan(final File node) {
        if (node.isDirectory()) {
            return getDirectoryTreeNode(node);
        }
        final MapOpenEvent leaf = new MapOpenEvent(node.getParent(), node.getName().substring(0,
                node.getName().length() - MapIO.EXT_TILE.length()));
        return new DefaultMutableTreeNode(leaf);
    }

    private static MutableTreeNode getDirectoryTreeNode(final File node) {
        final DefaultMutableTreeNode ret = new DefaultMutableTreeNode(node.getName());
        final File[] files = node.listFiles();
        if (files == null) {
            return ret;
        }
        sortFiles(files);
        for (final File child : files) {
            if (isMapFile(child)) {
                ret.add(scan(child));
            }
        }
        return ret;
    }

    private static void sortFiles(final File[] files) {
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(final File o1, final File o2) {
                if (o1.isDirectory() ^ o2.isDirectory()) {
                    if (o1.isDirectory()) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            }
        });
    }

    public void setDirectory(final File file) {
        if (isMapFile(file)) {
            setModel(new DefaultTreeModel(scan(file)));
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        final int fromRow = getRowForPath( getSelectionPath());
        if (fromRow != -1) {
            final Rectangle fromBounds = getRowBounds(fromRow);
            if (fromBounds != null) {
                g.setColor(SELECTED_COLOR);
                g.fillRect(0, fromBounds.y, getWidth(), fromBounds.height);
            }
        }
        setOpaque(false);
        super.paintComponent(g);
        setOpaque(false);
    }
}
