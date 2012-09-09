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

import illarion.mapedit.Lang;
import illarion.mapedit.events.ItemSelectedEvent;
import illarion.mapedit.events.TileSelectedEvent;
import illarion.mapedit.gui.cellrenderer.ItemImgCellRenderer;
import illarion.mapedit.gui.cellrenderer.TileImgCellRenderer;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.resource.loaders.ItemLoader;
import illarion.mapedit.resource.loaders.TileLoader;
import org.bushe.swing.event.EventBus;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Tim
 */
public class ObjectSelector extends JDialog {


    private final JList<TileImg> tileList;
    private final JList<ItemImg> itemList;

    public ObjectSelector() {
        super(MainFrame.getInstance());
        setDefaultLookAndFeelDecorated(false);
        final JTabbedPane tab = new JTabbedPane();

        tileList = new JList<TileImg>(TileLoader.getInstance().getTiles());
        tileList.setCellRenderer(new TileImgCellRenderer());
        tileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                EventBus.publish(new TileSelectedEvent(tileList.getSelectedValue()));
            }
        });

        itemList = new JList<ItemImg>(ItemLoader.getInstance().getTiles());
        itemList.setCellRenderer(new ItemImgCellRenderer());
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                EventBus.publish(new ItemSelectedEvent(itemList.getSelectedValue()));
            }
        });

        add(tab);

        tab.add(Lang.getMsg("gui.selector.tile"), new JScrollPane(tileList));
        tab.add(Lang.getMsg("gui.selector.item"), new JScrollPane(itemList));
        pack();
        doLayout();
        setVisible(true);

    }
}
