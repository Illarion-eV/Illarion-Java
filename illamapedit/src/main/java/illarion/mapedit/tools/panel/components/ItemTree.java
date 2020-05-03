/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.tools.panel.components;

import illarion.mapedit.events.ItemSelectedEvent;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.loaders.ItemGroupLoader;
import illarion.mapedit.resource.loaders.ItemLoader;
import illarion.mapedit.tools.panel.cellrenderer.ItemTreeCellRenderer;
import javolution.util.FastTable;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.List;

/**
 * @author Tim
 */
public class ItemTree extends JScrollPane {

    private static class ItemGroupModel implements TreeModel {

        private static final String ROOT = "Items";
        @Nonnull
        private final List<ItemGroup> groups;

        private ItemGroupModel(@Nonnull ItemImg... items) {
            ItemGroupLoader gl = ItemGroupLoader.getInstance();
            groups = new FastTable<>();

            for (ItemImg i : items) {
                boolean existedGroup = false;
                for (ItemGroup ig : groups) {
                    if (ig.getId() == i.getEditorGroup()) {
                        ig.add(i);
                        existedGroup = true;
                        break;
                    }
                }
                if (existedGroup) {
                    continue;
                }
                ItemGroup gr = new ItemGroup(i.getEditorGroup(), gl.getGroupName(i.getEditorGroup()));
                gr.add(i);
                groups.add(gr);
            }
            Collections.sort(groups, (group1, group2) -> {
                String g1 = group1.getName();
                if (g1 == null) {
                    g1 = "";
                }
                String g2 = group2.getName();
                if (g2 == null) {
                    g2 = "";
                }
                return g1.compareToIgnoreCase(g2);
            });
        }

        @Nonnull
        @Override
        public Object getRoot() {
            return ROOT;
        }

        @Override
        public Object getChild(@Nonnull Object parent, int index) {
            if (parent == getRoot()) {
                return groups.get(index);
            }
            if (parent instanceof ItemGroup) {
                return ((ItemGroup) parent).getItems().get(index);
            }
            throw new IllegalArgumentException("Parent must be ROOT or instanceof ItemGroup and not: " + parent);
        }

        @Override
        public int getChildCount(@Nonnull Object parent) {
            if (parent == getRoot()) {
                return groups.size();
            }
            if (parent instanceof ItemGroup) {
                return ((ItemGroup) parent).getItems().size();
            }
            if (parent instanceof ItemImg) {
                return 0;
            }
            throw new IllegalArgumentException("Parent must be ROOT or instanceof ItemGroup and not: " + parent);
        }

        @Override
        public boolean isLeaf(Object node) {
            return node instanceof ItemImg;
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {

        }

        @Override
        public int getIndexOfChild(@Nonnull Object parent, Object child) {
            if (parent == getRoot()) {
                return groups.indexOf(child);
            }
            if (parent instanceof ItemGroup) {
                return ((ItemGroup) parent).getItems().indexOf(child);
            }
            return 0;
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {

        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {

        }
    }

    private static class ItemGroup {
        @Nonnull
        private final List<ItemImg> items;
        private final String name;
        private final int id;

        private ItemGroup(int id, String name) {
            this.id = id;
            items = new FastTable<>();
            this.name = name;
        }

        @Nonnull
        public List<ItemImg> getItems() {
            return items;
        }

        public void add(ItemImg item) {
            items.add(item);
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        @Nonnull
        @Override
        public String toString() {
            return name;
        }
    }

    public ItemTree() {
        JTree tree = new JTree(new ItemGroupModel(ItemLoader.getInstance().getItems()));
        tree.setToggleClickCount(1);
        setViewportView(tree);
        tree.setEditable(false);
        tree.setCellEditor(null);
        tree.setScrollsOnExpand(false);
        tree.setCellRenderer(new ItemTreeCellRenderer(getBackground()));
        tree.setSelectionModel(new DefaultTreeSelectionModel());
        tree.addTreeSelectionListener(e -> {
            Object[] o = e.getPath().getPath();
            if (o[o.length - 1] instanceof ItemImg) {
                EventBus.publish(new ItemSelectedEvent((ItemImg) o[o.length - 1]));
            }
        });
    }
}
