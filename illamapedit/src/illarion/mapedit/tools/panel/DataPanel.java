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
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.tools.panel.components.ItemDataTable;
import illarion.mapedit.tools.panel.components.ItemInspectorList;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Fredrik K
 */
public class DataPanel extends JPanel {
    private final ItemInspectorList itemPanel;
    private final ItemDataTable dataPanel;

    public DataPanel() {
        super(new GridLayout(2, 1)) ;
        AnnotationProcessor.process(this);

        itemPanel = new ItemInspectorList();
        dataPanel = new ItemDataTable();

        add(itemPanel);
        add(dataPanel);
    }

    public void setAnnotation(final String text) {
        itemPanel.setAnnotation(text);
    }

    public void setItems(@Nullable final Collection<MapItem> items, final String annotation) {
        itemPanel.setAnnotation(annotation);
        setItems(items);
    }

    public void setItems(@Nullable final Collection<MapItem> items) {
        Collection<MapItem> mapItems = new ArrayList<MapItem>();
        if (items != null) {
            mapItems = items;
        }
        itemPanel.setDataList(mapItems);
        dataPanel.clearDataList();
    }

    @EventSubscriber
    public void onItemInspectorSelected(@Nonnull final ItemInspectorSelectedEvent e) {
        dataPanel.setAnnotation(e.getItem().getAnnotation());
        dataPanel.setDataList(e.getItem());
    }

    @EventSubscriber
    public void onItemDataChanged(@Nonnull final ItemItemDataChangedEvent e) {
        itemPanel.getSelectedItem().addItemData(e.getRow(), e.getData());
    }

    @EventSubscriber
    public void onItemDataRemoved(@Nonnull final ItemDataRemovedEvent e) {
        itemPanel.getSelectedItem().removeItemData(e.getIndex());
    }

    @EventSubscriber
    public void onItemDataAdded(@Nonnull final ItemDataAddedEvent e) {
        itemPanel.getSelectedItem().addItemData(e.getData());
    }

    @EventSubscriber
    public void onItemsUpdated(@Nonnull final ItemsUpdatedEvent e) {
        List<MapItem> items = new ArrayList<MapItem>();
        if (e.getItems() != null) {
            items = e.getItems();
        }
        setItems(items);
    }

    @EventSubscriber
    public void onItemDataAnnotation(@Nonnull final ItemDataAnnotationEvent e) {
        itemPanel.getSelectedItem().setAnnotation(e.getText());
        EventBus.publish(new RepaintRequestEvent());
    }
}
