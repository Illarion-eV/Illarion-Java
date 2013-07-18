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
package illarion.mapedit.tools.panel;

import illarion.mapedit.data.MapItem;
import illarion.mapedit.events.*;
import illarion.mapedit.tools.panel.components.ItemDataTable;
import illarion.mapedit.tools.panel.components.ItemInspectorList;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Fredrik K
 */
public class DataPanel extends JPanel {
    private final ItemInspectorList itemPanel = new ItemInspectorList();
    private final ItemDataTable dataPanel = new ItemDataTable();

    public DataPanel() {
        super(new GridLayout(2, 1)) ;
        AnnotationProcessor.process(this);

        add(itemPanel);
        add(dataPanel);
    }

    public void setItems(final Collection<MapItem> items) {
        itemPanel.setDataList(items);
        dataPanel.clearDataList();
    }

    @EventSubscriber
    public void onItemInspectorSelected(@Nonnull final ItemInspectorSelectedEvent e) {
        dataPanel.setDataList(e.getItem().getItemData());
    }

    @EventSubscriber
    public void onItemDataChanged(@Nonnull final ItemItemDataChangedEvent e) {
        itemPanel.getSelectedItem().getItemData().set(e.getRow(),e.getData());
    }

    @EventSubscriber
    public void onItemDataRemoved(@Nonnull final ItemDataRemovedEvent e) {
        itemPanel.getSelectedItem().getItemData().remove(e.getIndex());
    }

    @EventSubscriber
    public void onItemDataAdded(@Nonnull final ItemDataAddedEvent e) {
        itemPanel.getSelectedItem().getItemData().add(e.getData());
    }

    @EventSubscriber
    public void onItemsUpdated(@Nonnull final ItemsUpdatedEvent e) {
        List<MapItem> items = new ArrayList<MapItem>();
        if (e.getItems() != null) {
            items = e.getItems();
        }
        setItems(items);
    }
}
