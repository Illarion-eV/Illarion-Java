/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
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

import illarion.mapedit.events.ItemInspectorSelectedEvent;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

/**
 * @author Fredrik K
 */
public class ItemDataPanel extends JPanel {
    @Nonnull
    private final ItemDataFields dataFields;
    @Nonnull
    private final ItemDataTable dataTable;

    public ItemDataPanel() {
        super(new BorderLayout());
        AnnotationProcessor.process(this);

        dataFields = new ItemDataFields();
        dataTable = new ItemDataTable();

        add(dataFields, BorderLayout.NORTH);
        add(dataTable, BorderLayout.CENTER);
    }

    public void clearDataList() {
        dataTable.clearDataList();
        dataFields.clearFields();
    }

    @EventSubscriber
    public void onItemInspectorSelected(@Nonnull final ItemInspectorSelectedEvent e) {
        dataTable.setDataList(e.getItem());
        dataFields.setData(e.getItem());
    }
}
