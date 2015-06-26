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

import illarion.mapedit.events.TileSelectedEvent;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.resource.loaders.TileLoader;
import illarion.mapedit.tools.panel.cellrenderer.TileImgCellRenderer;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import javax.swing.*;

public class TileList extends JScrollPane {

    @Nonnull
    private final JList<TileImg> tileList;

    public TileList() {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        tileList = new JList<>(TileLoader.getInstance().getTiles());
        tileList.setCellRenderer(new TileImgCellRenderer());
        tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tileList.addListSelectionListener(e -> EventBus.publish(new TileSelectedEvent(tileList.getSelectedValue())));

        setViewportView(tileList);
    }
}
