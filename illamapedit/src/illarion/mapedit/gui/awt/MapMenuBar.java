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

import java.awt.Desktop;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import org.apache.log4j.Logger;

import illarion.mapedit.MapEditor;
import illarion.mapedit.map.Map;
import illarion.mapedit.map.MapStorage;
import illarion.mapedit.map.export.CopyStorage;
import illarion.mapedit.map.optimize.MapOptimizer;
import illarion.mapedit.map.optimize.WorkingCopyMap;
import illarion.mapedit.tools.AbstractTool;
import illarion.mapedit.tools.ToolCopyArea;
import illarion.mapedit.tools.ToolPaste;

import illarion.common.config.ConfigDialog;
import illarion.common.config.entries.CheckEntry;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.entries.NumberEntry;
import illarion.common.util.MessageSource;

import illarion.graphics.Graphics;

/**
 * This is the main menu bar of the main frame of the map editor. This class
 * takes care for loading all entries of the menu bar and setting everything up
 * correctly.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class MapMenuBar extends MenuBar {
    /**
     * The serialization UID of this menu bar.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The "Insert" Menu Item
     */
    private final MenuItem insertItem;

    /**
     * The "Redo" Menu Item.
     */
    private final MenuItem redoItem;

    /**
     * The "Uudo" Menu Item.
     */
    private final MenuItem undoItem;

    /**
     * Constructor of the menu bar that also fills the bar with all its
     * contents.
     */
    @SuppressWarnings("nls")
    public MapMenuBar() {
        super();

        final Menu fileMenu = new Menu("File");

        final MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final ConfigDialog dialog = new ConfigDialog();
                dialog.setConfig(MapEditor.getConfig());
                dialog.setMessageSource(new MessageSource() {
                    @Override
                    public String getMessage(final String key) {
                        if (key.equals("illarion.common.config.gui.Title")) {
                            return "Options";
                        } else if (key
                            .equals("illarion.common.config.gui.Save")) {
                            return "Save";
                        } else if (key
                            .equals("illarion.common.config.gui.Cancel")) {
                            return "Cancel";
                        } else if (key
                            .equals("illarion.common.config.gui.directory.Title")) {
                            return "Choose directory";
                        } else if (key
                            .equals("illarion.common.config.gui.directory.Browse")) {
                            return "Browse";
                        } else if (key
                            .equals("illarion.common.config.gui.file.Title")) {
                            return "Choose file";
                        } else if (key
                            .equals("illarion.common.config.gui.file.Browse")) {
                            return "Browse";
                        } else {
                            return key;
                        }
                    }
                });
                dialog.setDisplaySystem(ConfigDialog.DISPLAY_AWT);

                final ConfigDialog.Page page =
                    new ConfigDialog.Page("General");
                page.addEntry(new ConfigDialog.Entry("Map Directory",
                    new DirectoryEntry("mapDir", null)));
                page.addEntry(new ConfigDialog.Entry("Global History",
                    new CheckEntry("globalHist")));
                page.addEntry(new ConfigDialog.Entry("History Entries",
                    new NumberEntry("historyLength", 0, 10000)));
                dialog.addPage(page);

                dialog.show();
            }
        });
        fileMenu.add(settingsItem);

        final MenuItem saveItem = new MenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapEditor.saveConfiguration();
                MapStorage.getInstance().saveAllMaps();
            }
        });
        fileMenu.add(saveItem);

        final MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapEditor.exit();
            }
        });
        fileMenu.add(exitItem);

        add(fileMenu);

        final Menu editMenu = new Menu("Edit");

        undoItem = new MenuItem("Undo");
        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Map map = MapStorage.getInstance().getSelectedMap();
                if (map != null) {
                    map.getHistory().undo();
                }
            }
        });
        editMenu.add(undoItem);

        redoItem = new MenuItem("Redo");
        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Map map = MapStorage.getInstance().getSelectedMap();
                if (map != null) {
                    map.getHistory().redo();
                }
            }
        });
        editMenu.add(redoItem);

        editMenu.addSeparator();

        final MenuItem copyItem = new MenuItem("Copy");
        copyItem.addActionListener(new ActionListener() {
            private final AbstractTool tool = new ToolCopyArea();

            @Override
            public void actionPerformed(final ActionEvent e) {
                //MapEditor.getMainFrame().getToolbar().setOtherTool("Copy");
                tool.activateTool();
            }
        });
        editMenu.add(copyItem);

        insertItem = new MenuItem("Paste");
        insertItem.addActionListener(new ActionListener() {
            private final AbstractTool tool = new ToolPaste();

            @Override
            public void actionPerformed(final ActionEvent e) {
                //MapEditor.getMainFrame().getToolbar().setOtherTool("Paste");
                tool.activateTool();
            }
        });

        editMenu.add(insertItem);
        editMenu.addSeparator();

        final MenuItem repaintItem = new MenuItem("Repaint map");
        repaintItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapStorage.getInstance().calculateMapSize();
                Graphics.getInstance().getRenderDisplay().getRenderArea()
                    .repaint();
            }
        });
        editMenu.add(repaintItem);

        final MenuItem calcOverlayItem = new MenuItem("Recalculate overlays");
        calcOverlayItem.addActionListener(new ActionListener() {
            private static final String msg = "Recalculating Overlays";

            @Override
            public void actionPerformed(final ActionEvent e) {
                final Map map = MapStorage.getInstance().getSelectedMap();
                if (map != null) {
                   // MapEditor.getMainFrame().getMessageLine().addMessage(msg);
                    map.recalculateOverlays();
                    //MapEditor.getMainFrame().getMessageLine()
                    //    .removeMessage(msg);
                }
            }
        });
        editMenu.add(calcOverlayItem);

        final MenuItem optimizeItem = new MenuItem("Optimize Map");
        optimizeItem.addActionListener(new ActionListener() {
            private final MapOptimizer opti = new MapOptimizer();

            @Override
            public void actionPerformed(final ActionEvent e) {
                final Map map = MapStorage.getInstance().getSelectedMap();

                if (map == null) {
                    // no maü selected, do not do anything.
                    return;
                }
                final WorkingCopyMap workMap = new WorkingCopyMap(map);
                opti.optimize(workMap);

                MapStorage.getInstance().unloadMap(map);

                final int mapCount = workMap.getMapCount();
                for (int i = 0; i < mapCount; i++) {
                    MapStorage.getInstance().addMap(workMap.getMap(i));
                }

            }
        });
        editMenu.add(optimizeItem);

        add(editMenu);

        final Menu mapsMenu = new Menu("Maps");

        final MenuItem createMapItem = new MenuItem("Create new map");
        createMapItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new NewMapDialog().setVisible(true);
            }
        });
        mapsMenu.add(createMapItem);

        final MenuItem loadMapsItem = new MenuItem("Load maps");
        loadMapsItem.addActionListener(new ActionListener() {
            private MapLoader loader;

            @Override
            public void actionPerformed(final ActionEvent e) {
                if (loader == null) {
                    loader = new MapLoader();
                }
                loader.setVisible(true);
            }
        });
        mapsMenu.add(loadMapsItem);
        mapsMenu.addSeparator();

        final MenuItem openFolder = new MenuItem("Open maps folder");
        openFolder.addActionListener(new ActionListener() {
            private final Logger LOGGER = Logger.getLogger(this.getClass());

            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(
                        MapEditor.getConfig().getFile("mapDir").toURI());
                } catch (final IOException ex) {
                    LOGGER.error("Can't open desktop", ex);
                }
            }
        });
        mapsMenu.add(openFolder);

        mapsMenu.addSeparator();

        final MenuItem saveAllMaps = new MenuItem("Save all maps");
        saveAllMaps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapStorage.getInstance().saveAllMaps();
            }
        });
        mapsMenu.add(saveAllMaps);

        final MenuItem closeAllMapsItem = new MenuItem("Close all maps");
        closeAllMapsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MapStorage.getInstance().unloadAllMaps();
            }
        });
        mapsMenu.add(closeAllMapsItem);

        add(mapsMenu);
        validateHistory();
        validateInsert();
    }

    /**
     * Update the history navigation buttons according to the currently
     * activated map history.
     */
    public void validateHistory() {
        final Map map = MapStorage.getInstance().getSelectedMap();
        if (map == null) {
            undoItem.setEnabled(false);
            redoItem.setEnabled(false);
        } else {
            undoItem.setEnabled(map.getHistory().canUndo());
            redoItem.setEnabled(map.getHistory().canRedo());
        }
    }

    /**
     * Update the insert menu item so its only activated in case there is some
     * data avaiable to insert.
     */
    public void validateInsert() {
        insertItem
            .setEnabled(CopyStorage.getInstance().getCopiedMap() != null);
    }
}
