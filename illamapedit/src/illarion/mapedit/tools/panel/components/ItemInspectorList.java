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
package illarion.mapedit.tools.panel.components;

import illarion.mapedit.data.MapItem;
import illarion.mapedit.events.ItemInspectorSelectedEvent;
import illarion.mapedit.events.ItemRemoveEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.tools.panel.cellrenderer.MapItemCellRenderer;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * @author Fredrik K
 */
public class ItemInspectorList extends JPanel {
    private final JScrollPane scroll;
    @Nonnull
    private JList<MapItem> dataList;

    public ItemInspectorList() {
        super(new BorderLayout());

        scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scroll, BorderLayout.CENTER);

        final ResizableIcon iconRemove =  ImageLoader.getResizableIcon("edit_remove") ;
        iconRemove.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));

        final JButton removeDataButton = new JButton();
        removeDataButton.setIcon(iconRemove);
        removeDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ItemRemoveEvent(dataList.getSelectedIndex()));
            }
        });

        final JToolBar dataActions = new JToolBar();
        dataActions.setFloatable(false);
        dataActions.add(removeDataButton);

        add(dataActions, BorderLayout.PAGE_END);
    }

    /**
     * Get the selected item in the list
     * @return the selected MapItem
     */
    public MapItem getSelectedItem() {
        return dataList.getSelectedValue();
    }

    /**
     * Set items to show in the list
     * @param itemList A collection of items to show
     */
    public void setDataList(@Nonnull final Collection<MapItem> itemList) {
        dataList = new JList<MapItem>(itemList.toArray(new MapItem[itemList.size()]));
        dataList.setCellRenderer(new MapItemCellRenderer());
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                EventBus.publish(new ItemInspectorSelectedEvent(dataList.getSelectedValue()));
            }
        });

        scroll.setViewportView(dataList);
    }
}
