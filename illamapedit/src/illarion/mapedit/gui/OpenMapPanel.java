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
package illarion.mapedit.gui;

import illarion.common.config.ConfigChangedEvent;
import illarion.mapedit.data.MapIO;
import illarion.mapedit.events.CloseMapEvent;
import illarion.mapedit.events.GlobalActionEvents;
import illarion.mapedit.events.UpdateMapListEvent;
import illarion.mapedit.events.menu.MapOpenEvent;
import illarion.mapedit.events.menu.MapSelectedEvent;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Fredrik K
 */
public class OpenMapPanel extends JPanel {
    private static final FilenameFilter FILTER_TILES = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, @Nonnull final String name) {
            return name.endsWith(MapIO.EXT_TILE);
        }
    };
    private static final int ICON_SIZE = 24;
    private static final int PREFERRED_BUTTON_CELL_WIDTH = 10;
    private static final int PREFERRED_MAP_CELL_WIDTH = 150;
    private static final int PREFERRED_RENDER_CELL_WIDTH = 20;
    private final JPanel panel;
    private final JToggleButton openButton;
    private final JToggleButton renderButton;
    private final JList fileList;
    private JTable openTable;
    private OpenMapTableModel openTableModel;

    public OpenMapPanel() {
        super(new BorderLayout());
        AnnotationProcessor.process(this);
        panel = new JPanel(new GridLayout(0,1));

        final ResizableIcon iconRender = ImageLoader.getResizableIcon("render");
        iconRender.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));
        final ResizableIcon iconOpen = ImageLoader.getResizableIcon("fileopen");
        iconOpen.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));

        openButton = new JToggleButton();
        openButton.setIcon(iconOpen);

        renderButton = new JToggleButton();
        renderButton.setIcon(iconRender);

        fileList = new JList();
    }

    private void initMaps() {
        final File dir = loadFileList();
        fileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(@Nonnull final ListSelectionEvent e) {
                if (e.getValueIsAdjusting() && (fileList.getSelectedValue() != null)) {
                    EventBus.publish(new MapOpenEvent(dir.getPath(), (String) fileList.getSelectedValue()));
                    fileList.clearSelection();
                }
            }
        });
    }

    private File loadFileList() {
        final File dir = MapEditorConfig.getInstance().getMapFolder();

        final String[] maps = dir.list(FILTER_TILES);
        if (maps != null) {
            for (int i = 0; i < maps.length; ++i) {
                maps[i] = maps[i].substring(0, maps[i].length() - MapIO.EXT_TILE.length());
            }
            fileList.setListData(maps);
        }
        return dir;
    }

    private void initOpenMaps() {
        openTableModel = new OpenMapTableModel();
        openTable = new JTable(openTableModel);
        final JButton closeButton = new JButton(ImageLoader.getImageIcon("close"));
        final OpenMapTableCellEditor editor = new OpenMapTableCellEditor(closeButton);
        openTable.getColumnModel().getColumn(2).setCellEditor(editor);
        openTable.getColumnModel().getColumn(2).setCellRenderer(editor);
        openTable.getColumnModel().getColumn(0).setPreferredWidth(PREFERRED_RENDER_CELL_WIDTH);
        openTable.getColumnModel().getColumn(1).setPreferredWidth(PREFERRED_MAP_CELL_WIDTH);
        openTable.getColumnModel().getColumn(2).setPreferredWidth(PREFERRED_BUTTON_CELL_WIDTH);

        closeButton.setActionCommand(GlobalActionEvents.CLOSE_MAP);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                final int row = openTable.convertRowIndexToModel(openTable.getEditingRow());
                EventBus.publish(new CloseMapEvent(row));
            }
        });

        openTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final ListSelectionModel selectionModel = openTable.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                final ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
                if (!listSelectionModel.isSelectionEmpty()) {
                    EventBus.publish(new MapSelectedEvent(openTable.getSelectedRow()));
                }
            }
        });
    }

    public void init() {
        panel.setPreferredSize(new Dimension(180, 0));
        panel.add(new JList(new String[]{"apa", "bepa"}));

        renderButton.setSelected(true);
        renderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showActiveComponents();
            }
        });

        openButton.setSelected(true);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                showActiveComponents();
            }
        });

        final JToolBar itemActions = new JToolBar(JToolBar.VERTICAL);
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
            panel.add(new JScrollPane(fileList));
        }
        panel.setVisible(panel.getComponentCount() > 0);
        panel.revalidate();
        panel.repaint();
    }

    @EventSubscriber
    public void onUpdateMapList(@Nonnull final UpdateMapListEvent e) {
        openTableModel.setTableData(e.getMaps());
        EventBus.publish(new MapSelectedEvent(e.getSelectedIndex()));
    }

    @EventTopicSubscriber(topic = MapEditorConfig.MAPEDIT_FOLDER)
    public void onConfigChanged(final String topic, @Nonnull final ConfigChangedEvent event) {
        fileList.removeAll();
        loadFileList();
    }
}
