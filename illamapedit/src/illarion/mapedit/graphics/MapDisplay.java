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
package illarion.mapedit.graphics;

import java.util.List;

import javolution.context.ConcurrentContext;
import javolution.util.FastComparator;
import javolution.util.FastTable;

import illarion.mapedit.MapEditor;
import illarion.mapedit.gui.awt.SplashScreen;

import illarion.common.util.FastMath;
import illarion.common.util.Rectangle;

import illarion.graphics.GraphicResolution;
import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;
import illarion.graphics.common.TextureLoader;

/**
 * The map display that is used to control the settings of the display and is
 * used to render the map itself.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MapDisplay implements RenderTask {
    /**
     * Settings for the display of the grid. This constants are used to
     * determine how the grid are displayed.
     * 
     * @author Martin Karing
     * @since 0.99
     */
    public static enum GridDisplay {
        /**
         * Hide the grid.
         */
        hide,

        /**
         * Show the grid.
         */
        show;
    }

    /**
     * Settings for the display of the items. This constants are used to
     * determine how the items are displayed.
     * 
     * @author Martin Karing
     * @since 0.99
     */
    public static enum ItemDisplay {
        /**
         * Hide the items.
         */
        hide,

        /**
         * Show the items.
         */
        show;
    }

    /**
     * Settings for the display of the levels. This constants are used to
     * determine how the levels are displayed.
     * 
     * @author Martin Karing
     * @since 0.99
     */
    public static enum LevelDisplay {
        /**
         * Assume all levels as 0, so all maps get flatten out to the same
         * level.
         */
        nullLevel,

        /**
         * Handle the levels in the way they are really.
         */
        realLevel;
    }

    /**
     * Setting for the display of special conditions. This constants are used to
     * determine how the map is displayed.
     * 
     * @author Martin Karing
     * @since 0.99
     */
    public static enum SpecialDisplay {
        /**
         * Render the display for the blocked tiles on the map.
         */
        blocked,

        /**
         * Render and display the lights on the map.
         */
        light,

        /**
         * Show the map in the normal way.
         */
        none;
    }

    /**
     * Settings for the display of the tiles. This constants are used to
     * determine how the tiles are displayed.
     * 
     * @author Martin Karing
     * @since 0.99
     */
    public static enum TileDisplay {
        /**
         * Show the tiles as full graphics.
         */
        full,

        /**
         * Hide the tiles.
         */
        hide,

        /**
         * Show the tiles as simple colors.
         */
        simple;
    }

    /**
     * This comparator is used to order the display list in case it is needed.
     * 
     * @author Martin Karing
     * @since 0.99
     * @version 0.99
     */
    private static final class DisplayListComparator extends
        FastComparator<DisplayItem> {
        /**
         * The serialization UID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * Public constructor to allow the parent class creating instances.
         */
        public DisplayListComparator() {
            // nothing to do
        }

        /**
         * Test of two objects are equal. This returns <code>true</code> also in
         * case both objects are <code>null</code>.
         */
        @Override
        public boolean areEqual(final DisplayItem o1, final DisplayItem o2) {
            return (o1 == o2);
        }

        /**
         * Compare two instances of the object and there relative offset that is
         * needed for ordering this objects.
         */
        @Override
        public int compare(final DisplayItem o1, final DisplayItem o2) {
            return (o2.getZOrder() - o1.getZOrder());
        }

        /**
         * Get the hash code of a object.
         */
        @Override
        public int hashCodeOf(final DisplayItem obj) {
            if (obj == null) {
                return 0;
            }
            return obj.hashCode();
        }
    }

    private final List<DisplayItem> addItems = new FastTable<DisplayItem>();

    private boolean batchMode = false;

    private final List<DisplayItem> deleteItems = new FastTable<DisplayItem>();

    /**
     * The list of display items that are part of the current map.
     */
    private final FastTable<DisplayItem> display;

    /**
     * This comperator is used to sort the display list properly.
     */
    private final DisplayListComparator displayListComperator;

    /**
     * The X drawing offset that needs to be applied to display the required
     * area correctly.
     */
    private int offsetX;

    /**
     * The y drawing offset that needs to be applied to display the required
     * area correctly;
     */
    private int offsetY;

    /**
     * The rectangle of the last render run to find out if that changed and
     * compile the optimized display list again.
     */
    private final Rectangle oldViewportRect = Rectangle.getInstance();

    /**
     * Flag to determine of the optimized display list is dirty or not.
     */
    private boolean optDirty;

    /**
     * The optimized list of display items that are part of the current map.
     */
    private final List<DisplayItem> optDisplay;

    /**
     * This flag stores if the sorting of the main display list is corrupted.
     */
    private boolean orderDirty = false;

    /**
     * Display settings for the items.
     */
    private GridDisplay settingsGrid = GridDisplay.show;

    /**
     * Display settings for the items.
     */
    private ItemDisplay settingsItem = ItemDisplay.show;

    /**
     * Display settings for the levels.
     */
    private LevelDisplay settingsLevel = LevelDisplay.realLevel;

    /**
     * Display settings for special functions.
     */
    private SpecialDisplay settingsSpecial = SpecialDisplay.none;

    /**
     * Display settings for the tiles.
     */
    private TileDisplay settingsTile = TileDisplay.full;

    /**
     * The rectangle of the current viewport. All objects inside the rectangle
     * need to be drawn.
     */
    private final Rectangle viewportRect = Rectangle.getInstance();

    /**
     * The zoom value of the map.
     */
    private float zoom;

    /**
     * Default constructor that prepares all data to store the display items
     * that are needed to render.
     */
    public MapDisplay() {
        displayListComperator = new DisplayListComparator();
        display =
            new FastTable<DisplayItem>()
                .setValueComparator(displayListComperator);
        optDisplay = new FastTable<DisplayItem>();
        optDirty = true;
        zoom = 1.f;
    }

    public boolean activateBatchMode() {
        if (batchMode) {
            return false;
        }
        batchMode = true;
        return true;
    }

    /**
     * Add a item to the list of display items.
     * 
     * @param item the item that is needed to be added to the list
     */
    public void add(final DisplayItem item) {
        if (batchMode) {
            addItems.add(item);
            return;
        }
        synchronized (display) {
            insertSorted(item);
            optDirty = true;
        }
    }

    public void finishBatchMode() {
        if (!batchMode) {
            return;
        }
        boolean didSomething = false;
        synchronized (display) {
            if (!deleteItems.isEmpty()) {
                if (deleteItems.size() == display.size()) {
                    display.clear();
                } else {
                    display.removeAll(deleteItems);
                }
                deleteItems.clear();
                didSomething = true;
                optDirty = true;
            }
            if (!addItems.isEmpty()) {

                display.addAll(addItems);
                addItems.clear();
                orderDirty = true;
                didSomething = true;
                optDirty = true;
            }
        }
        batchMode = false;
        if (didSomething) {
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .repaint();
        }
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Get the display settings for showing the grid.
     * 
     * @return the display constant for the grid
     */
    public GridDisplay getSettingsGrid() {
        return settingsGrid;
    }

    /**
     * Get the display settings for showing the items.
     * 
     * @return the display constant for the items
     */
    public ItemDisplay getSettingsItem() {
        return settingsItem;
    }

    /**
     * Get the settings value for showing the levels.
     * 
     * @return the settings value for the level display
     */
    public LevelDisplay getSettingsLevel() {
        return settingsLevel;
    }

    /**
     * Get the display settings for showing the map.
     * 
     * @return the display constant for the map
     */
    public SpecialDisplay getSettingsSpecial() {
        return settingsSpecial;
    }

    /**
     * Get the display settings for showing the tiles.
     * 
     * @return the display constant for the tiles
     */
    public TileDisplay getSettingsTile() {
        return settingsTile;
    }

    /**
     * Get the zoom value of the map.
     * 
     * @return The new zoom value of the map
     */
    public float getZoom() {
        return zoom;
    }

    /**
     * Test if the graphic is inside the viewport and needs to be rendered.
     * 
     * @param testRect the rectangle to test
     * @return <code>true</code> in case the graphic is inside the rectangle and
     *         needs to be drawn
     */
    public boolean isInsideViewport(final Rectangle testRect) {
        if (viewportRect.isEmpty() || (testRect == null)) {
            return true;
        }
        return viewportRect.intersects(testRect);
    }

    /**
     * Remove one item from the display list.
     * 
     * @param item the item that is needed to be removed from the display list
     */
    public void remove(final DisplayItem item) {
        if (batchMode) {
            final int addItemIndex = addItems.indexOf(item);
            if (addItemIndex > -1) {
                addItems.remove(addItemIndex);
            } else {
                deleteItems.add(item);
            }
            return;
        }
        synchronized (display) {
            display.remove(item);
            optDirty = true;
        }
    }

    /**
     * Render the display items to the display.
     * 
     * @param delta the time since the last render run
     * @return <code>true</code> in case it shall be rendered again at the next
     *         run
     */
    @Override
    public boolean render(final int delta) {

        viewportRect.set(offsetX, offsetY, (int) (Graphics.getInstance()
            .getRenderDisplay().getRenderArea().getWidth() / zoom),
            (int) (Graphics.getInstance().getRenderDisplay().getRenderArea()
                .getHeight() / zoom));

        if (!viewportRect.equals(oldViewportRect)) {
            optDirty = true;
        }
        oldViewportRect.set(viewportRect);

        Graphics.getInstance().getRenderDisplay().applyScaling(zoom);
        Graphics.getInstance().getRenderDisplay()
            .applyOffset(-offsetX, -offsetY);

        if (optDirty) {
            synchronized (display) {
                reorder();
                optDisplay.clear();
                DisplayItem item;
                for (int i = 0, n = display.size(); i < n; i++) {
                    item = display.get(i);
                    if (item.draw()) {
                        optDisplay.add(item);
                    }
                }
                optDirty = false;
            }
        } else {
            for (int i = 0, n = optDisplay.size(); i < n; i++) {
                optDisplay.get(i).draw();
            }
        }

        Graphics.getInstance().getRenderDisplay().resetOffset();
        Graphics.getInstance().getRenderDisplay().resetScaling();
        return true;
    }

    /**
     * Set the x coordinate of the offset that is used to draw.
     * 
     * @param newOffset the new offset value
     */
    public void setOffsetX(final int newOffset) {
        if (offsetX != newOffset) {
            offsetX = newOffset;
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .repaint();
        }
    }

    /**
     * Set the y coordinate of the offset that is used to draw.
     * 
     * @param newOffset the new offset value
     */
    public void setOffsetY(final int newOffset) {
        if (offsetY != newOffset) {
            offsetY = newOffset;
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .repaint();
        }
    }

    /**
     * Set the display settings for showing the grid.
     * 
     * @param newValue the new settings value
     */
    public void setSettingsGrid(final GridDisplay newValue) {
        if ((newValue == null) || (newValue == settingsGrid)) {
            return;
        }
        synchronized (display) {
            settingsGrid = newValue;
        }
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }

    /**
     * Set the display settings for showing the items.
     * 
     * @param newValue the new settings value
     */
    public void setSettingsItem(final ItemDisplay newValue) {
        if ((newValue == null) || (newValue == settingsItem)) {
            return;
        }
        synchronized (display) {
            settingsItem = newValue;
        }
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }

    /**
     * Set the display settings for showing the levels.
     * 
     * @param newValue the new settings value
     */
    public void setSettingsLevel(final LevelDisplay newValue) {
        if ((newValue == null) || (newValue == settingsLevel)) {
            return;
        }
        synchronized (display) {
            settingsLevel = newValue;
        }
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }

    /**
     * Set the display settings for showing the map.
     * 
     * @param newValue the new settings value
     */
    public void setSettingsSpecial(final SpecialDisplay newValue) {
        if ((newValue == null) || (newValue == settingsSpecial)) {
            return;
        }
        synchronized (display) {
            settingsSpecial = newValue;
        }
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }

    /**
     * Set the display settings for showing the tiles.
     * 
     * @param newValue the new settings value
     */
    public void setSettingsTile(final TileDisplay newValue) {
        if ((newValue == null) || (newValue == settingsTile)) {
            return;
        }
        synchronized (display) {
            settingsTile = newValue;
        }
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }

    /**
     * Set the new value for the zoom of the map.
     * 
     * @param zoom the zoom of the map
     */
    public void setZoom(final float newZoom) {
        if (FastMath.abs(newZoom - zoom) > FastMath.FLT_EPSILON) {
            zoom = newZoom;
            //MapEditor.getMainFrame().getRenderArea().reportZoomChanged();
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .repaint();
        }
    }

    /**
     * Begin rendering the graphics and load all required data.
     */
    public void startRendering() {
        final GraphicResolution res = new GraphicResolution(300, 300, 32, 60);
        Graphics.getInstance().getRenderDisplay().setDisplayMode(res);

        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            public boolean render(final int delta) {
                Graphics.getInstance().getRenderDisplay().startRendering();
                return false;
            }
        });

        final MapDisplay thisDisplay = this;

//        MapEditor.getMainFrame().getMessageLine()
//            .addMessage("Loading Textures");
        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            private boolean waitedOnce = false;

            @Override
            @SuppressWarnings("nls")
            public boolean render(final int delta) {
                if (!TextureLoader.getInstance().preloadAtlasTextures(false)) {
                    Graphics.getInstance().getRenderDisplay().getRenderArea()
                        .repaint();
                    return true;
                }
                if (!waitedOnce) {
                    Graphics.getInstance().getRenderDisplay().getRenderArea()
                        .repaint();
                    waitedOnce = true;
                    return true;
                }
//                MapEditor.getMainFrame().getMessageLine()
//                    .removeMessage("Loading Textures");
//
//                MapEditor.getMainFrame().getMessageLine()
//                    .addMessage("Loading Objects");
//                ItemFactory.getInstance().init();
//                OverlayFactory.getInstance().init();
//                TileFactory.getInstance().init();
//                MapEditor.getMainFrame().getMessageLine()
//                    .removeMessage("Loading Objects");
//
//                MapEditor.getMainFrame().getMessageLine()
//                    .addMessage("Optimizing Data");
//                AbstractEntity.cleanup();
//                TextureLoader.getInstance().cleanup();
//                MapEditor.getMainFrame().getMessageLine()
//                    .removeMessage("Optimizing Data");

                MapEditor.getMainFrame().invalidate();
                MapEditor.getMainFrame().validate();

                SplashScreen.getInstance().setVisible(false);
                SplashScreen.getInstance().dispose();

                Graphics.getInstance().getRenderManager().addTask(thisDisplay);
                return false;
            }
        });

        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }

    void quickSortDisplayList(final FastTable<DisplayItem> table) {
        final int size = table.size();
        if (size < 100) {
            table.sort(); // Direct quick sort.
        } else {
            // Splits table in two and sort both part concurrently.
            final FastTable<DisplayItem> t1 = FastTable.newInstance();
            final FastTable<DisplayItem> t2 = FastTable.newInstance();
            t1.setValueComparator(displayListComperator);
            t2.setValueComparator(displayListComperator);

            ConcurrentContext.enter();
            try {
                ConcurrentContext.execute(new Runnable() {
                    @Override
                    public void run() {
                        t1.addAll(table.subList(0, size / 2));
                        quickSortDisplayList(t1); // Recursive.
                    }
                });
                ConcurrentContext.execute(new Runnable() {
                    @Override
                    public void run() {
                        t2.addAll(table.subList(size / 2, size));
                        quickSortDisplayList(t2); // Recursive.
                    }
                });
            } finally {
                ConcurrentContext.exit();
            }
            // Merges results.
            for (int i = 0, i1 = 0, i2 = 0; i < size; i++) {
                if (i1 >= t1.size()) {
                    table.set(i, t2.get(i2++));
                } else if (i2 >= t2.size()) {
                    table.set(i, t1.get(i1++));
                } else {
                    final DisplayItem o1 = t1.get(i1);
                    final DisplayItem o2 = t2.get(i2);
                    if (displayListComperator.compare(o1, o2) < 0) {
                        table.set(i, o1);
                        i1++;
                    } else {
                        table.set(i, o2);
                        i2++;
                    }
                }
            }
            FastTable.recycle(t1);
            FastTable.recycle(t2);
        }
    }

    private void insertSorted(final DisplayItem item) {
        int currentStart = 0;
        int currentEnd = display.size() - 1;
        int middle;
        DisplayItem foundItem;
        int compareResult;

        while (currentStart <= currentEnd) {
            middle = currentStart + ((currentEnd - currentStart) >> 1);
            foundItem = display.get(middle);
            compareResult = displayListComperator.compare(foundItem, item);

            if (compareResult < 0) {
                currentStart = middle + 1;
            } else if (compareResult > 0) {
                currentEnd = middle - 1;
            } else {
                display.add(middle, item);
                return;
            }
        }

        display.add(currentStart, item);
    }

    private void reorder() {
        if (orderDirty) {
            orderDirty = false;
            quickSortDisplayList(display);
        }
    }
}
