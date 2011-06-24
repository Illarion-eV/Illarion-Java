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
package illarion.mapedit.map;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import illarion.mapedit.MapEditor;
import illarion.mapedit.graphics.Tile;

import illarion.common.graphics.ItemInfo;
import illarion.common.util.Location;
import illarion.common.util.Rectangle;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.LightTracer;
import illarion.graphics.common.LightingMap;

/**
 * This class stores that map that got already load.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MapStorage implements LightingMap {
    /**
     * The singleton instance of this class.
     */
    private static final MapStorage INSTANCE = new MapStorage();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MapStorage.class);

    /**
     * The light tracer that handles all the slights on all maps load into this
     * storage.
     */
    private final LightTracer lightTracer;

    /**
     * The maps that are load into this storage.
     */
    private final java.util.Map<String, Map> loadedMaps;

    /**
     * The map that is currently selected for the editor.
     */
    private Map selectedMap;

    /**
     * The private constructor to ensure that no instances but the singleton
     * instance are created.
     */
    private MapStorage() {
        loadedMaps = new HashMap<String, Map>();
        lightTracer = new LightTracer(this);
        lightTracer.start();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of the class
     */
    public static MapStorage getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean acceptsLight(final Location loc, final int dx, final int dy) {
        final MapTile mapTile = getMapTile(loc);
        if (mapTile == null) {
            return false;
        }
        final Tile tile = mapTile.getPrototypeTile();
        if (tile == null) {
            return false;
        }

        switch (mapTile.getFace()) {
            default: //$FALL-THROUGH$
            case ItemInfo.FACE_ALL:
                return true;

            case ItemInfo.FACE_W:
                return dx >= 0;

            case ItemInfo.FACE_SW:
                return (dy - dx) < 0;

            case ItemInfo.FACE_S:
                return dy <= 0;
        }
    }

    /**
     * Add a map to the map storage and display it on the screen.
     * 
     * @param newMap the new map to show
     */
    public void addMap(final Map newMap) {
        synchronized (loadedMaps) {
            loadedMaps.put(newMap.getMapName(), newMap);
        }

        try {
            while (!MapEditor.getDisplay().activateBatchMode()) {
                try {
                    Thread.sleep(30);
                } catch (final InterruptedException ex) {
                    // nothing
                }
                Thread.yield();
            }

            lightTracer.pause();
            newMap.showMap();
            MapEditor.getDisplay().finishBatchMode();
//            MapEditor.getMainFrame().getRightToolbar().getMapSelector()
//                .addMap(newMap.getMapName());
            calculateMapSize();
        } finally {
            MapEditor.getDisplay().finishBatchMode();
            lightTracer.start();
            lightTracer.renderLights();
        }
    }

    @Override
    public int blocksView(final Location loc) {
        final MapTile mapTile = getMapTile(loc);
        if (mapTile == null) {
            return 0;
        }
        final Tile tile = mapTile.getPrototypeTile();
        if (tile == null) {
            return 0;
        }
        if (tile.isOpaque()) {
            return 100;
        }
        return mapTile.getBlocksView();
    }

    /**
     * Calculate the needed display size of the map.
     */
    public void calculateMapSize() {
        final Rectangle rect = Rectangle.getInstance();
        rect.set(0, 0, 0, 0);

        for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
            final Rectangle tempRect = entry.getValue().getRenderRectangle();
            rect.add(tempRect);
            tempRect.recycle();
        }

//        MapEditor.getMainFrame().getRenderArea().setVirtualSize(rect);
        rect.recycle();
    }

    /**
     * Create a new map.
     * 
     * @param name the name of the map, excluding the indicator for the level
     *            that is added here
     * @param posX the x coordinate of the map origin
     * @param posY the y coordinate of the map origin
     * @param posZ the level of the map
     * @param width the width of the map in tiles
     * @param height the height of the map in tiles
     * @return <code>null</code> in case everything went well, else a error
     *         message
     */
    @SuppressWarnings("nls")
    public String createMap(final String name, final int posX, final int posY,
        final int posZ, final int width, final int height) {
        if (name.length() < 4) {
            return "Name of the map is too short";
        }
        final String mapName = name + "_" + Integer.toString(posZ);
        if (loadedMaps.containsKey(mapName)) {
            return "Name of the map is already taken";
        }
        if ((width < 1) || (height < 1)) {
            return "Width and height must not be smaller then 1";
        }
        final String message = "Creating map: " + mapName;
//        MapEditor.getMainFrame().getMessageLine().addMessage(message);

        final Location newMapLocation = Location.getInstance();
        newMapLocation.setSC(posX, posY, posZ);
        final Map newMap =
            new Map(mapName, newMapLocation, new Dimension(width, height));
        newMapLocation.recycle();

        synchronized (loadedMaps) {
            loadedMaps.put(mapName, newMap);
        }

        try {
            while (!MapEditor.getDisplay().activateBatchMode()) {
                try {
                    Thread.sleep(30);
                } catch (final InterruptedException ex) {
                    // nothing
                }
                Thread.yield();
            }

            lightTracer.pause();
            newMap.showMap();
            MapEditor.getDisplay().finishBatchMode();
//            MapEditor.getMainFrame().getRightToolbar().getMapSelector()
//                .addMap(mapName);
            calculateMapSize();
        } finally {
            MapEditor.getDisplay().finishBatchMode();
//            MapEditor.getMainFrame().getMessageLine().removeMessage(message);
            lightTracer.start();
            lightTracer.renderLights();
        }

        return null;
    }

    /**
     * Get the names of the maps that were changed since the last save.
     * 
     * @return the maps saved since the last change
     */
    public String[] getChangedMaps() {
        final ArrayList<String> changedMaps = new ArrayList<String>();
        for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
            if (entry.getValue().isChanged()) {
                changedMaps.add(entry.getKey());
            }
        }
        if (changedMaps.isEmpty()) {
            return null;
        }
        return changedMaps.toArray(new String[changedMaps.size()]);
    }

    /**
     * Get the light tracer that is used to calculate all lights.
     * 
     * @return the light tracer
     */
    public LightTracer getLightTracer() {
        return lightTracer;
    }

    /**
     * Get a map tile at one specified location. This will check all load maps
     * for the required location.
     * 
     * @param loc the location where the map tile is searched
     * @return the map tile found or <code>null</code>
     */
    public MapTile getMapTile(final Location loc) {
        synchronized (loadedMaps) {
            for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
                final MapTile tile = entry.getValue().getTile(loc);
                if (tile != null) {
                    return tile;
                }
            }
        }
        return null;
    }

    /**
     * The map that is selected to be the map that is currently edited.
     * 
     * @return the selected map
     */
    public Map getSelectedMap() {
        return selectedMap;
    }

    /**
     * Load one map into the storage.
     * 
     * @param mapName the name of the map to load
     */
    @SuppressWarnings("nls")
    public void loadMap(final String mapName) {
        if (loadedMaps.containsKey(mapName)) {
            return;
        }
        final String message = "Loading map: " + mapName;
        try {
//            MapEditor.getMainFrame().getMessageLine().addMessage(message);
            final Map newMap = new Map(mapName);

            synchronized (loadedMaps) {
                loadedMaps.put(mapName, newMap);
            }
            while (!MapEditor.getDisplay().activateBatchMode()) {
                try {
                    Thread.sleep(30);
                } catch (final InterruptedException ex) {
                    // nothing
                }
                Thread.yield();
            }
            lightTracer.pause();
            newMap.showMap();
            MapEditor.getDisplay().finishBatchMode();
            calculateMapSize();
//            MapEditor.getMainFrame().getRightToolbar().getMapSelector()
//                .addMap(mapName);
        } catch (final IllegalArgumentException ex) {
            LOGGER.error("Loading map failed", ex);
        } catch (final NullPointerException ex) {
            LOGGER.error("Loading map failed", ex);
        } finally {
            MapEditor.getDisplay().finishBatchMode();
//            MapEditor.getMainFrame().getMessageLine().removeMessage(message);
            lightTracer.start();
            lightTracer.renderLights();
        }
    }

    @Override
    @SuppressWarnings("nls")
    public void renderLights() {
        final String message = "Rendering Lights";
//        MapEditor.getMainFrame().getMessageLine().addMessage(message);
        synchronized (loadedMaps) {
            for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
                entry.getValue().renderLights();
            }
        }
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
//        MapEditor.getMainFrame().getMessageLine().removeMessage(message);
    }

    @Override
    @SuppressWarnings("nls")
    public void resetLights() {
        final String message = "Resetting Lights";
//        MapEditor.getMainFrame().getMessageLine().addMessage(message);
        synchronized (loadedMaps) {
            for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
                entry.getValue().resetLights();
            }
        }
//        MapEditor.getMainFrame().getMessageLine().removeMessage(message);
    }

    /**
     * Save all currently load maps in case its needed.
     */
    @SuppressWarnings("nls")
    public void saveAllMaps() {
        final String message = "Saving all maps";
//        MapEditor.getMainFrame().getMessageLine().addMessage(message);

        synchronized (loadedMaps) {
            for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
                entry.getValue().save();
            }
        }
//        MapEditor.getMainFrame().getMessageLine().removeMessage(message);
    }

    @Override
    public void setLight(final Location loc, final SpriteColor color) {
        final MapTile mapTile = getMapTile(loc);
        if (mapTile == null) {
            return;
        }
        mapTile.addLight(color);
    }

    /**
     * Set the map that is supposed to be the selected working copy from now on.
     * 
     * @param mapName the name of the map that is selected
     */
    public void setSelectedMap(final String mapName) {
        final Map newSelectedMap = loadedMaps.get(mapName);
        if (newSelectedMap == selectedMap) {
            return;
        }
        selectedMap = newSelectedMap;
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
//        MapEditor.getMainFrame().getMenubar().validateHistory();
    }

    /**
     * Unload all currently load maps.
     */
    @SuppressWarnings("nls")
    public void unloadAllMaps() {
        if (loadedMaps.isEmpty()) {
            return;
        }

        final String message = "Removing all maps";
//        MapEditor.getMainFrame().getMessageLine().addMessage(message);

        while (!MapEditor.getDisplay().activateBatchMode()) {
            try {
                Thread.sleep(30);
            } catch (final InterruptedException ex) {
                // nothing
            }
            Thread.yield();
        }

        synchronized (loadedMaps) {
            for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
                entry.getValue().hideMap();
//                MapEditor.getMainFrame().getRightToolbar().getMapSelector()
//                    .deleteMap(entry.getKey());
            }
            loadedMaps.clear();
        }

        MapEditor.getDisplay().finishBatchMode();
        calculateMapSize();
//        MapEditor.getMainFrame().getMessageLine().removeMessage(message);
        setSelectedMap(null);
    }

    /**
     * Remove a map from the loaded storage.
     * 
     * @param map the map to remove
     */
    public void unloadMap(final Map map) {
        unloadMap(map.getMapName());
    }

    /**
     * Unload a map from the screen.
     * 
     * @param mapName the name of the map to unload
     */
    @SuppressWarnings("nls")
    public void unloadMap(final String mapName) {
        if (!loadedMaps.containsKey(mapName)) {
            return;
        }
        final String message = "Removing map: " + mapName;
//        MapEditor.getMainFrame().getMessageLine().addMessage(message);

        while (!MapEditor.getDisplay().activateBatchMode()) {
            try {
                Thread.sleep(30);
            } catch (final InterruptedException ex) {
                // nothing
            }
            Thread.yield();
        }

        synchronized (loadedMaps) {
            for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
                entry.getValue().hideMap();
            }
            final Map deletedMap = loadedMaps.remove(mapName);
            if (deletedMap == selectedMap) {
                setSelectedMap(null);
            }
            for (final Entry<String, Map> entry : loadedMaps.entrySet()) {
                entry.getValue().showMap();
            }
        }
        MapEditor.getDisplay().finishBatchMode();
        calculateMapSize();
//        MapEditor.getMainFrame().getRightToolbar().getMapSelector()
//            .deleteMap(mapName);
//        MapEditor.getMainFrame().getMessageLine().removeMessage(message);
    }
}
