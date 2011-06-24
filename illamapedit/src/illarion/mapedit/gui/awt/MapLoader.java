/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import illarion.mapedit.MapEditor;
import illarion.mapedit.map.MapStorage;

/**
 * The map loader is supposed to display a interface to choose the maps to load.
 * It reads the avaiable maps from the map folder selected.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class MapLoader extends Dialog {
    /**
     * The serialization UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The file name filter that is used to find the valid files for the maps.
     */
    private final FilenameFilter mapFileSearchFilter;

    /**
     * The list that shows the maps that can be load.
     */
    private final List mapList;

    /**
     * Constructor for the dialog that prepares the display of the loading
     * screen correctly.
     */
    @SuppressWarnings("nls")
    public MapLoader() {
        super(MapEditor.getMainFrame(), "Load map", true);

        final Panel content = new Panel(new BorderLayout(5, 5));
        content.add(new Label("Select the maps you want to load. "
            + "Selecting multiple entries is possible."), BorderLayout.NORTH);

        mapList = new List(10, true);
        content.add(mapList, BorderLayout.CENTER);

        final Panel buttonPanel =
            new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        final Button okButton = new Button("Load maps");
        final Button cancelButton = new Button("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        content.add(buttonPanel, BorderLayout.SOUTH);
        add(content);

        pack();
        validate();

        setLocation(100, 100);

        final Dimension prefSize = getPreferredSize();
        prefSize.width = 300;
        setPreferredSize(prefSize);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String[] selectedMaps = getMapList().getSelectedItems();
                setVisible(false);

                for (final String selectedMap : selectedMaps) {
                    MapStorage.getInstance().loadMap(selectedMap);
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                setVisible(false);
            }
        });

        mapFileSearchFilter = new FilenameFilter() {
            private final String itemFile = ".items.txt";
            private final String tileFile = ".tiles.txt";

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(itemFile) || name.endsWith(tileFile);
            }
        };
    }

    /**
     * Overwritten set visible method to populate the list as soon as the map
     * loader goes visible.
     * 
     * @param visible the new value for the visible flag
     */
    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            populateMaplist();
        }

        super.setVisible(visible);

        if (!visible) {
            mapList.removeAll();
        }
    }

    /**
     * Get the list of maps that is used to select the map to load.
     * 
     * @return the list where the maps are shown and selected
     */
    List getMapList() {
        return mapList;
    }

    /**
     * Read the map directory and fill the map list with the required data.
     */
    @SuppressWarnings("nls")
    private void populateMaplist() {
        final File mapDir = MapEditor.getConfig().getFile("mapDir");

        mapList.removeAll();

        if (!mapDir.isDirectory()) {
            return;
        }

        final ArrayList<String> validMapNames = new ArrayList<String>();
        final String[] mapFiles = mapDir.list(mapFileSearchFilter);

        for (final String mapFile : mapFiles) {
            final String mapName = mapFile.substring(0, mapFile.length() - 10);

            if (validMapNames.contains(mapName)) {
                mapList.add(mapName);
            } else {
                validMapNames.add(mapName);
            }
        }
    }
}
