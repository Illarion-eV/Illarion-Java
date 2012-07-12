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
package illarion.mapedit.gui;

import illarion.mapedit.events.TileSelectedEvent;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.resource.loaders.TileLoader;
import org.bushe.swing.event.EventBus;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * @author Tim
 */
public class TileSelector extends JFrame {


    private final JList<TileImg> tileList;


    public TileSelector() {
        setDefaultLookAndFeelDecorated(false);

        tileList = new JList<TileImg>(TileLoader.getInstance().getTiles());
        tileList.setCellRenderer(new TileImgCellRenderer());
        tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getFirstIndex() == e.getLastIndex()) {
                    EventBus.publish(new TileSelectedEvent(tileList.getSelectedValue()));
                }
            }
        });

        add(new JScrollPane(tileList), BorderLayout.CENTER);
        pack();
        doLayout();
        setVisible(true);

    }
}
