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
import illarion.mapedit.data.MapIO;
import illarion.mapedit.events.GlobalActionEvents;
import illarion.mapedit.events.UpdateMapListEvent;
import illarion.mapedit.events.menu.MapOpenEvent;
import illarion.mapedit.events.menu.MapSelectedEvent;
import illarion.mapedit.gui.util.MapComboBoxModel;
import illarion.mapedit.resource.loaders.ImageLoader;
import javolution.util.FastList;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventBusAction;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * @author Tim
 */
public class MapFileBand extends JRibbonBand {
    private static final FilenameFilter FILTER_TILES = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, @Nonnull final String name) {
            return name.endsWith(MapIO.EXT_TILE);
        }
    };
    @Nonnull
    private final MapComboBoxModel model;

    public MapFileBand() {
        super(Lang.getMsg("gui.maps"), null);

        AnnotationProcessor.process(this);

        final File dir = MapEditorConfig.getInstance().getMapFolder();

        final String[] maps = dir.list(FILTER_TILES);
        final JList list;
        if (maps != null) {
            for (int i = 0; i < maps.length; ++i) {
                maps[i] = maps[i].substring(0, maps[i].length() - MapIO.EXT_TILE.length());
            }
            list = new JList(maps);
        } else {
            list = new JList();
        }
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(@Nonnull final ListSelectionEvent e) {
                if (e.getValueIsAdjusting() && (list.getSelectedValue() != null)) {
                    EventBus.publish(new MapOpenEvent(dir.getPath(), (String) list.getSelectedValue()));
                    list.clearSelection();
                }
            }
        });

        model = new MapComboBoxModel(null);

        final JComboBox mapSelector = new JComboBox(model);
        final JButton close = new JButton(ImageLoader.getImageIcon("close"));
        close.setActionCommand(GlobalActionEvents.CLOSE_MAP);
        close.addActionListener(new EventBusAction(GlobalActionEvents.CLOSE_MAP, null));

        mapSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(@Nonnull final ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                EventBus.publish(new MapSelectedEvent(mapSelector.getSelectedIndex()));
            }
        });


        addRibbonComponent(new JRibbonComponent(new JScrollPane(list)));
        addRibbonComponent(new JRibbonComponent(mapSelector));
        addRibbonComponent(new JRibbonComponent(close));

        final List<RibbonBandResizePolicy> resize = new FastList<RibbonBandResizePolicy>();
        resize.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));


        setResizePolicies(resize);

    }

    @EventSubscriber
    public void onUpdateMapList(@Nonnull final UpdateMapListEvent e) {
        model.updateList(e.getMaps());

        model.setSelectedItem(model.getElementAt(e.getSelectedIndex()));
        EventBus.publish(new MapSelectedEvent(e.getSelectedIndex()));
    }
}
