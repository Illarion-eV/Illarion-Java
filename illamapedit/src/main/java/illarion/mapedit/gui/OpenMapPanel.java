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
package illarion.mapedit.gui;

import illarion.common.config.ConfigChangedEvent;
import illarion.mapedit.events.CloseMapEvent;
import illarion.mapedit.events.GlobalActionEvents;
import illarion.mapedit.events.UpdateMapListEvent;
import illarion.mapedit.events.menu.MapSelectedEvent;
import illarion.mapedit.events.menu.SetFolderEvent;
import illarion.mapedit.gui.util.FileTree;
import illarion.mapedit.gui.util.OpenMapTableCellEditor;
import illarion.mapedit.gui.util.OpenMapTableModel;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

/**
 * @author Fredrik K
 */
public class OpenMapPanel extends JPanel {
    private static final int ICON_SIZE = 24;
    private static final int PREFERRED_BUTTON_CELL_WIDTH = 10;
    private static final int PREFERRED_MAP_CELL_WIDTH = 150;
    private static final int PREFERRED_RENDER_CELL_WIDTH = 20;
    @Nonnull
    private final JPanel panel;
    @Nonnull
    private final JToggleButton openButton;
    @Nonnull
    private final JToggleButton renderButton;
    private JTable openTable;
    private OpenMapTableModel openTableModel;
    @Nonnull
    private final FileTree tree;

    public OpenMapPanel() {
        super(new BorderLayout());
        AnnotationProcessor.process(this);
        panel = new JPanel(new GridLayout(0, 1));

        ResizableIcon iconRender = ImageLoader.getResizableIcon("render");
        iconRender.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));
        ResizableIcon iconOpen = ImageLoader.getResizableIcon("fileopen");
        iconOpen.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));

        openButton = new JToggleButton();
        openButton.setIcon(iconOpen);

        renderButton = new JToggleButton();
        renderButton.setIcon(iconRender);

        tree = new FileTree();
    }

    private void initMaps() {
        Path mapFolder = MapEditorConfig.getInstance().getMapFolder();
        if (mapFolder != null) {
            tree.setDirectory(mapFolder);
        }
    }

    private void initOpenMaps() {
        openTableModel = new OpenMapTableModel();
        openTable = new JTable(openTableModel);
        JButton closeButton = new JButton(ImageLoader.getImageIcon("close"));
        OpenMapTableCellEditor editor = new OpenMapTableCellEditor(closeButton);
        openTable.getColumnModel().getColumn(2).setCellEditor(editor);
        openTable.getColumnModel().getColumn(2).setCellRenderer(editor);
        openTable.getColumnModel().getColumn(0).setPreferredWidth(PREFERRED_RENDER_CELL_WIDTH);
        openTable.getColumnModel().getColumn(1).setPreferredWidth(PREFERRED_MAP_CELL_WIDTH);
        openTable.getColumnModel().getColumn(2).setPreferredWidth(PREFERRED_BUTTON_CELL_WIDTH);

        closeButton.setActionCommand(GlobalActionEvents.CLOSE_MAP);
        closeButton.addActionListener(actionEvent -> {
            int row = openTable.convertRowIndexToModel(openTable.getEditingRow());
            EventBus.publish(new CloseMapEvent(row));
        });

        openTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel selectionModel = openTable.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
            if (!listSelectionModel.isSelectionEmpty()) {
                EventBus.publish(new MapSelectedEvent(openTable.getSelectedRow()));
            }
        });
    }

    public void init() {
        panel.setPreferredSize(new Dimension(180, 0));

        renderButton.setSelected(true);
        renderButton.addActionListener(e -> showActiveComponents());

        openButton.setSelected(true);
        openButton.addActionListener(e -> showActiveComponents());

        JToolBar itemActions = new JToolBar(JToolBar.VERTICAL);
        itemActions.setFloatable(false);
        itemActions.add(renderButton);
        itemActions.add(openButton);
        add(itemActions, BorderLayout.LINE_START);
        add(panel, BorderLayout.CENTER);
        initMaps();
        initOpenMaps();
        showActiveComponents();
    }

    private void showActiveComponents() {
        panel.removeAll();
        if (renderButton.isSelected()) {
            panel.add(new JScrollPane(openTable));
        }
        if (openButton.isSelected()) {
            panel.add(new JScrollPane(tree));
        }
        panel.setVisible(panel.getComponentCount() > 0);
        panel.revalidate();
        panel.repaint();
    }

    @EventSubscriber
    public void onUpdateMapList(@Nonnull UpdateMapListEvent e) {
        openTableModel.setTableData(e.getMaps());
        EventBus.publish(new MapSelectedEvent(e.getSelectedIndex()));
    }

    @EventTopicSubscriber(topic = MapEditorConfig.MAPEDIT_FOLDER)
    public void onConfigChanged(String topic, @Nonnull ConfigChangedEvent event) {
        tree.setDirectory(MapEditorConfig.getInstance().getMapFolder());
    }

    @EventSubscriber
    public void onFolderList(@Nonnull SetFolderEvent e) {
        tree.setDirectory(e.getFile());
    }
}
