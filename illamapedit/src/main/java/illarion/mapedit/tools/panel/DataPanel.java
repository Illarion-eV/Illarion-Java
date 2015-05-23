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
package illarion.mapedit.tools.panel;

import illarion.mapedit.data.MapItem;
import illarion.mapedit.events.*;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.tools.panel.components.ItemDataPanel;
import illarion.mapedit.tools.panel.components.ItemInspectorList;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Fredrik K
 */
public class DataPanel extends JPanel {
    @Nonnull
    private final ItemInspectorList itemPanel;
    @Nonnull
    private final ItemDataPanel dataPanel;

    public DataPanel() {
        super(new GridLayout(2, 1));
        AnnotationProcessor.process(this);

        itemPanel = new ItemInspectorList();
        dataPanel = new ItemDataPanel();

        add(itemPanel);
        add(dataPanel);
    }

    public void setAnnotation(@Nonnull String text) {
        itemPanel.setAnnotation(text);
    }

    public void setItems(@Nullable Collection<MapItem> items, String annotation) {
        itemPanel.setAnnotation(annotation);
        setItems(items);
    }

    public void setItems(@Nullable Collection<MapItem> items) {
        Collection<MapItem> mapItems = new ArrayList<>();
        if (items != null) {
            mapItems = items;
        }
        itemPanel.setDataList(mapItems);
        dataPanel.clearDataList();
    }

    @EventSubscriber
    public void onItemDataChanged(@Nonnull ItemItemDataChangedEvent e) {
        itemPanel.getSelectedItem().addItemData(e.getRow(), e.getData());
    }

    @EventSubscriber
    public void onItemDataRemoved(@Nonnull ItemDataRemovedEvent e) {
        itemPanel.getSelectedItem().removeItemData(e.getIndex());
    }

    @EventSubscriber
    public void onItemDataAdded(@Nonnull ItemDataAddedEvent e) {
        itemPanel.getSelectedItem().addItemData(e.getData());
    }

    @EventSubscriber
    public void onItemsUpdated(@Nonnull ItemsUpdatedEvent e) {
        List<MapItem> items = new ArrayList<>();
        if (e.getItems() != null) {
            items = e.getItems();
        }
        setItems(items);
    }

    @EventSubscriber
    public void onItemDataAnnotation(@Nonnull ItemDataAnnotationEvent e) {
        itemPanel.getSelectedItem().setAnnotation(e.getText());
        EventBus.publish(new RepaintRequestEvent());
    }
}
