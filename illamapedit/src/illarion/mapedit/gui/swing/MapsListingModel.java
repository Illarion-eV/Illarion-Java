package illarion.mapedit.gui.swing;

import java.util.Collection;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import javolution.util.FastTable;

import org.jdesktop.swingx.treetable.TreeTableModel;

import illarion.mapedit.Lang;
import illarion.mapedit.database.MapData;
import illarion.mapedit.database.MapDatabase;

import illarion.common.util.Location;

class MapsListingModel implements TreeTableModel {
    private static enum TreeNodeLevel {
        root, folders, maps, files;
    }

    private static final class TreeNode {
        private final TreeNodeLevel level;
        private final TreeNode parent;
        private List<TreeNode> children;
        private final Object element;

        public TreeNode(final TreeNodeLevel nodeLevel,
            final TreeNode parentNode, final Object nodeElement) {
            level = nodeLevel;
            parent = parentNode;
            element = nodeElement;
            if (parent != null) {
                parent.addChild(this);
            }
        }

        private void addChild(final TreeNode childNode) {
            if (children == null) {
                children = new FastTable<TreeNode>();
            }
            children.add(childNode);
        }

        public boolean isLeaf() {
            return (children == null);
        }

        public TreeNodeLevel getLevel() {
            return level;
        }

        public TreeNode getParent() {
            return parent;
        }

        public int getChildrenCount() {
            if (children == null) {
                return 0;
            }
            return children.size();
        }

        public TreeNode getChildByIndex(final int index) {
            if (children == null) {
                return null;
            }
            return children.get(index);
        }

        public int getIndexOfChild(final TreeNode child) {
            if (children == null) {
                return -1;
            }
            return children.indexOf(child);
        }

        public Object getAttachedElement() {
            return element;
        }
    }

    /**
     * The label that is displayed at the root of the tree. Only in case this
     * object contains anything but <code>null</code> the model with bother to
     * display anything else.
     */
    private TreeNode root = null;

    /**
     * This function will load a map database and prepare it to be displayed in
     * the listing view using this model.
     * 
     * @param db the database to load
     */
    @SuppressWarnings("unused")
    void loadDatabase(final MapDatabase db) {
        cleanup();

        if (db == null) {
            return;
        }

        root = new TreeNode(TreeNodeLevel.root, null, db.getDirectory());

        Collection<MapData> maps = db.getAllMaps();
        for (MapData map : maps) {
            // get the ground folder ready
            final String groupName = map.getGroupName();

            TreeNode groupNode = null;
            final int currentGroupCount = root.getChildrenCount();
            for (int i = 0; i < currentGroupCount; i++) {
                final String currentGroupName =
                    (String) root.getChildByIndex(i).getAttachedElement();;
                if ((currentGroupName == null && groupName == null)
                    || (groupName != null && groupName
                        .equals(currentGroupName))) {
                    groupNode = root.getChildByIndex(i);
                    break;
                }
            }
            if (groupNode == null) {
                groupNode =
                    new TreeNode(TreeNodeLevel.folders, root, groupName);
            }

            final TreeNode mapNode =
                new TreeNode(TreeNodeLevel.maps, groupNode, map);

            new TreeNode(TreeNodeLevel.files, mapNode, map.getTileFileName());
            new TreeNode(TreeNodeLevel.files, mapNode, map.getItemFileName());
            new TreeNode(TreeNodeLevel.files, mapNode, map.getWarpFileName());
            new TreeNode(TreeNodeLevel.files, mapNode, map.getMetaFileName());
        }

        reportTreeLayoutChanged();
    }

    /**
     * This function is used to cleanup the model. In case a new database is
     * load the old values first need to be cleaned, before anything new is
     * load.
     */
    private void cleanup() {
        root = null;
    }

    /**
     * Get the root node of the tree.
     */
    @Override
    public Object getRoot() {
        return root;
    }

    /**
     * Get the children with a specified index on a specified node.
     */
    @Override
    public Object getChild(final Object parent, final int index) {
        final TreeNode node = (TreeNode) parent;
        return node.getChildByIndex(index);
    }

    /**
     * Get the amount of children a node contains.
     */
    @Override
    public int getChildCount(final Object parent) {
        final TreeNode node = (TreeNode) parent;
        return node.getChildrenCount();
    }

    /**
     * Check if a node is a leaf. Means that is has no children.
     */
    @Override
    public boolean isLeaf(final Object node) {
        final TreeNode treeNode = (TreeNode) node;
        return treeNode.isLeaf();
    }

    /**
     * Change the value of a path.
     * 
     * @throws IllegalAccessError in all cases, as this is not allowed in this
     *             list
     */
    @Override
    public void valueForPathChanged(final TreePath path, final Object newValue) {
        throw new IllegalAccessError();
    }

    /**
     * Get the child index of a special child.
     */
    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        if (parent == null || child == null) {
            return -1;
        }

        try {
            final TreeNode parentNode = (TreeNode) parent;
            final TreeNode childNode = (TreeNode) child;

            return parentNode.getIndexOfChild(childNode);
        } catch (final Exception e) {
            // nothing
        }
        return -1;
    }

    /**
     * The list of listeners that are used to monitor changes of the table.
     */
    private List<TreeModelListener> listeners;

    /**
     * Add a tree model listener.
     */
    @Override
    public void addTreeModelListener(final TreeModelListener l) {
        if (listeners == null) {
            listeners = FastTable.newInstance();
        }
        listeners.add(l);
    }

    /**
     * Notify all listeners that the tree layout heavily changed.
     */
    private void reportTreeLayoutChanged() {
        if (listeners != null) {
            final TreeModelEvent event =
                new TreeModelEvent(root, (TreePath) null);
            for (TreeModelListener l : listeners) {
                l.treeStructureChanged(event);
            }
        }
    }

    /**
     * Remove a tree model listener.
     */
    @Override
    public void removeTreeModelListener(final TreeModelListener l) {
        if (listeners == null) {
            listeners.remove(l);
            if (listeners.isEmpty()) {
                if (listeners instanceof FastTable) {
                    FastTable.recycle((FastTable<?>) listeners);
                }
                listeners = null;
            }
        }
    }

    /**
     * The object type that is displayed in each column.
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Location.class;
            case 2:
                return Integer.class;
            case 3:
                return Integer.class;
            default:
                return String.class;
        }
    }

    /**
     * The count of columns of this table.
     */
    @Override
    public int getColumnCount() {
        return 4;
    }

    /**
     * The name of the columns of this table.
     */
    @Override
    public String getColumnName(final int column) {
        switch (column) {
            case 0:
                return Lang.getMsg(MapsListingModel.class, "Columns.Title"); //$NON-NLS-1$
            case 1:
                return Lang.getMsg(MapsListingModel.class, "Columns.Location"); //$NON-NLS-1$
            case 2:
                return Lang.getMsg(MapsListingModel.class, "Columns.Width"); //$NON-NLS-1$
            case 3:
                return Lang.getMsg(MapsListingModel.class, "Columns.Height"); //$NON-NLS-1$
            default:
                return null;
        }
    }

    @Override
    public int getHierarchicalColumn() {
        return 0;
    }

    /**
     * Get the label of a node in a specified column.
     */
    @Override
    public Object getValueAt(final Object node, final int column) {
        if (!(node instanceof TreeNode)) {
            throw new IllegalArgumentException();
        }

        final TreeNode treeNode = (TreeNode) node;

        switch (treeNode.getLevel()) {
            case root: //$FALL-THROUGH$
            case files:
                if (column == 0) {
                    return treeNode.getAttachedElement();
                }
                return null;
            case folders:
                if (column == 0) {
                    final Object element = treeNode.getAttachedElement();
                    if (element == null) {
                        return Lang
                            .getMsg(MapsListingModel.class, "NullGroup"); //$NON-NLS-1$ 
                    }
                    return treeNode.getAttachedElement();
                }
                return null;
            case maps:
                final MapData data = (MapData) treeNode.getAttachedElement();
                switch (column) {
                    case 0:
                        return data.getMapName();
                    case 1:
                        return data.getOrigin();
                    case 2:
                        return Integer.valueOf(data.getMapWidth());
                    case 3:
                        return Integer.valueOf(data.getMapHeight());
                    default:
                        return null;
                }
        }

        return null;
    }

    @Override
    public boolean isCellEditable(final Object node, final int column) {
        return false;
    }

    @Override
    public void setValueAt(final Object value, final Object node,
        final int column) {
        throw new IllegalAccessError();
    }

}
