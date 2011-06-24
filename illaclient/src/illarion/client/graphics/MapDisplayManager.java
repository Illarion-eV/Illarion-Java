/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import javolution.util.FastComparator;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.client.ClientWindow;
import illarion.client.graphics.particle.ParticlePool;
import illarion.client.world.Game;
import illarion.client.world.GameMap;

import illarion.common.graphics.Layers;
import illarion.common.graphics.MapConstants;
import illarion.common.util.Location;

import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * The map display manager stores and manages all objects displayed on the map.
 * It takes care for rendering the objects in the proper order, for animations
 * of the entire map and it manages the current location of the avatar.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class MapDisplayManager implements AnimatedMove {
    /**
     * This comparator is used to order the display list in case it is needed.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
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
    
    /**
     * The X coordinate of the center of the map. At this location the avatar is
     * displayed.
     */
    public static final int MAP_CENTER_X = ClientWindow.getInstance()
        .getScreenWidth() / 2;

    /**
     * The Y coordinate of the center of the map. At this location the avatar is
     * displayed.
     */
    public static final int MAP_CENTER_Y = ClientWindow.getInstance()
        .getScreenHeight() / 2;

    /**
     * The height of the visible map. All tiles outside this height are not
     * rendered to the screen.
     */
    @Deprecated
    public static final int MAP_HEIGHT = 11 * MapConstants.TILE_H;

    /**
     * The width of the visible map. All tiles outside this width are not
     * rendered to the screen.
     */
    @Deprecated
    public static final int MAP_WIDTH = 11 * MapConstants.TILE_W;

    private static final Drawer DRAWER = Graphics.getInstance().getDrawer();

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger
        .getLogger(MapDisplayManager.class);

    /**
     * The height in pixels the GUI laps over the map at the bottom of the map
     * display.
     */
    @Deprecated
    private static final int MAP_GUI_BOTTOM_OVERLAP = 12;

    boolean displayListDirty = false;

    private boolean active;

    // scrolling offset
    private final MoveAnimation ani;

    private final FadingCorridor corridor;
    private final FastTable<DisplayItem> display;
    private final DisplayListComparator displayListComperator;

    private int dL;
    private int dX;
    private int dY;

    private int elevation;

    private final SpriteColor fadeOutColor = Graphics.getInstance()
        .getSpriteColor();

    private final MoveAnimation levelAni;

    private final Location origin;

    /**
     * The particle pool that is used to draw particles as overlays on top of
     * the map.
     */
    private final OverlayPool overlayParticlePool;

    public MapDisplayManager() {
        active = false;
        ani = new MoveAnimation(this);

        display = new FastTable<DisplayItem>();
        displayListComperator = new DisplayListComparator();
        display.setValueComparator(displayListComperator);
        corridor = FadingCorridor.getInstance();
        origin = new Location();

        dX = 0;
        dY = 0;

        levelAni = new MoveAnimation(new AnimatedMove() {
            @Override
            public void animationFinished(final boolean ok) {
                // nothing to do here
            }

            @Override
            public void setPosition(final int x, final int y, final int z) {
                dL = y;
            }
        });
        elevation = 0;
        dL = 0;
        fadeOutColor.set(SpriteColor.COLOR_MIN);
        overlayParticlePool = new OverlayPool();
    }

    /**
     * Make item visible on screen
     * 
     * @param item
     */
    @SuppressWarnings("nls")
    public void add(final DisplayItem item) {
        if (item == null) {
            assert false : "Trying to add NULL displayItem";
            return;
        }

        synchronized (display) {
            insertSorted(item);
            // if (!display.add(item)) {
            // LOGGER.warn("duplicate display entry " + item);
            // } else {
            // displayListDirty = true;
            // }
        }
    }

    /**
     * Animate the movement of the game map
     * 
     * @param dir
     * @param speed
     * @param run
     */
    public void animate(final int dir, final int speed, final boolean run) {
        // remember move dir and
        int mod = 1;
        if (run) {
            mod = 2;
        }
        switch (dir) {
            case Location.DIR_NORTH:
                // animate map
                ani.start(0, 0, MapConstants.STEP_X * mod,
                    -MapConstants.STEP_Y * mod, speed);
                break;
            case Location.DIR_NORTHEAST:
                // animate map
                ani.start(0, 0, 0, -MapConstants.TILE_H * mod, speed);
                break;
            case Location.DIR_EAST:
                // animate map
                ani.start(0, 0, -MapConstants.STEP_X * mod,
                    -MapConstants.STEP_Y * mod, speed);
                break;
            case Location.DIR_SOUTHEAST:
                // animate map
                ani.start(0, 0, -MapConstants.TILE_W * mod, 0, speed);
                break;
            case Location.DIR_SOUTH:
                // animate map
                ani.start(0, 0, -MapConstants.STEP_X * mod,
                    MapConstants.STEP_Y * mod, speed);
                break;
            case Location.DIR_SOUTHWEST:
                // animate map
                ani.start(0, 0, 0, MapConstants.TILE_H * mod, speed);
                break;
            case Location.DIR_WEST:
                // animate map
                ani.start(0, 0, MapConstants.STEP_X * mod, MapConstants.STEP_Y
                    * mod, speed);
                break;
            case Location.DIR_NORTHWEST:
                // animate map
                ani.start(0, 0, MapConstants.TILE_W, 0, speed);
                break;
            default:
                animationFinished(false);

        }
        // start separate Elevation animation
        final int fromElevation = elevation;
        elevation =
            Game.getMap().getElevationAt(Game.getPlayer().getLocation());
        if (elevation != fromElevation) {
            levelAni.start(0, fromElevation, 0, elevation, speed);
        }

        // adjust Z-order after update
        if (Game.getAvatar() != null) {
            readd(Game.getAvatar());
        }
    }

    /**
     * Map movement is complete
     * 
     * @param ok
     */
    @Override
    public void animationFinished(final boolean ok) {
        // move graphical player position to new location
        setLocation(Game.getPlayer().getLocation());

        // remove surplus tiles from the map
        // Game.getMap().clipMap();
    }

    public int getElevation() {
        return elevation;
    }

    /**
     * Get the particle pool that is used to render the particles as overlay
     * over the entire map.
     * 
     * @return the particle pool that is used to render overlays
     */
    public ParticlePool getOverlayParticlePool() {
        return overlayParticlePool;
    }

    public int getWorldX(final int x) {
        return ((x - MAP_CENTER_X) + origin.getDcX()) - dX;
    }

    public int getWorldY(final int y) {
        return ((y - MAP_CENTER_Y) + origin.getDcY()) - dY;
    }

    /**
     * Fix avatar's position in the middle of the screen and Z-Order
     * 
     * @param av
     */
    public void glueAvatarToOrigin(final Avatar av) {
        av.setScreenPos(origin.getDcX() - dX, (origin.getDcY() - dY) + dL,
            origin.getDcZ(), Layers.CHARS);
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Display lookat text
     * 
     * @param x screen coordinates
     * @param y
     * @param text
     */
    public void lookAt(int x, int y, final String text) {
        x += (MAP_CENTER_X - origin.getDcX()) + dX;
        y += (MAP_CENTER_Y - origin.getDcY()) + dY;
        // y += MAP_CENTER_Y + dY;

        // Tooltip tip = Tooltip.create();
        // tip.initText(text);
        // tip.show(x, y + MapConstants.TILE_H);
        // tip.setColor(Colors.white);
        // Gui.getInstance().addToolTip(tip);

    }

    /**
     * Remove and add a item again. This has to be done in case the value of a
     * item changed.
     * 
     * @param item the display item to remove and add again
     */
    public void readd(final DisplayItem item) {
        if (item == null) {
            assert false : "Trying to add NULL displayItem";
            return;
        }

        synchronized (display) {
            display.remove(item);
            insertSorted(item);
        }
    }

    /**
     * Remove item from screen
     * 
     * @param item
     */
    public void remove(final DisplayItem item) {
        synchronized (display) {
            display.remove(item);
        }
    }

    /**
     * Render all visible map items
     * 
     * @param delta the delta time since the last render operation
     * @param width the width of the area the map is rendered in
     * @param height the height of the area the map is rendered in
     */
    public void render(final int delta, final int width, final int height) {
        if (!active) {
            return;
        }

        final int centerX = width >> 1;
        final int centerY = height >> 1;

        final int offX = (centerX - origin.getDcX()) + dX;
        final int offY = (centerY - origin.getDcY()) + dY;

        ClientWindow.getInstance().getRenderDisplay().applyOffset(offX, offY);

        final Avatar av = Game.getAvatar();
        if (av != null) {
            glueAvatarToOrigin(av);
            corridor.setCorridor(av);
        }

        Camera.getInstance().setViewport(-offX, -offY, width, height);

        synchronized (display) {
            DisplayItem currentItem;
            synchronized (GameMap.LIGHT_LOCK) {
                // draw all items
                final int itemCount = display.size();
                for (int i = 0; i < itemCount; i++) {
                    currentItem = display.get(i);
                    currentItem.update(delta);
                    currentItem.draw();
                }
            }
        }

        overlayParticlePool.draw();

        ClientWindow.getInstance().getRenderDisplay().resetOffset();

        if (fadeOutColor.getAlphai() > 0) {
            fadeOutColor.setAlpha(AnimationUtility.approach(
                fadeOutColor.getAlphai(), 0, 0, SpriteColor.COLOR_MAX, delta));

            DRAWER.drawRectangle(0, 0, width, height, fadeOutColor);
        }
    }

    /**
     * Display spoken text. Temporary solution.
     * 
     * @param x
     * @param y
     * @param text
     * @param mode
     */
    public void sayText(int x, int y, final String text, final int mode) {
        x += (MAP_CENTER_X - origin.getDcX()) + dX;
        y += (MAP_CENTER_Y - origin.getDcY()) + dY;

        // Tooltip tip = Tooltip.create();
        // tip.initText(text);
        // tip.show(x, y);
        // tip.setColor(SpeechMode.getColor(mode));
        // todo: layout the spoken text properly
        // Gui.getInstance().addToolTip(tip);
    }

    public void setActive(final boolean active) {
        if (!active) {
            fadeOutColor.setAlpha(SpriteColor.COLOR_MAX);
        }
        this.active = active;
    }

    /*
     * public int getScreenX(int x) { return x + MAP_CENTER_X - origin.dcX + dX;
     * } public int getScreenY(int y) { return y + MAP_CENTER_Y - origin.dcY +
     * dY; }
     */

    /*
     * public void showMarker(int x, int y, int z) { showMarker = marker;
     * //marker. }
     */

    /**
     * Move the map origin to a new location
     * 
     * @param location
     */
    public void setLocation(final Location location) {
        // origin.setSC(location.scX, location.scY, 0);
        origin.set(location);
        ani.stop();
        final Avatar avatar = Game.getAvatar();
        if (avatar != null) {
            avatar.animationFinished(false);
        }
        elevation = Game.getMap().getElevationAt(origin);
        dL = elevation;
        dX = 0;
        dY = 0;
    }

    /**
     * Scroll map
     * 
     * @param x
     * @param y
     */
    @Override
    public void setPosition(final int x, final int y, final int z) {
        dX = x;
        dY = y;
        dL = z;

        // Gui.getInstance().getManager().notifyMovement();
    }

    /**
     * Show spoken text as tooltip
     * 
     * @param text
     * @param mode
     */
    public void showText(final String text, final int mode, final int x,
        final int y) {
        // convert to screen coordinates
        final Location tempLoc = Location.getInstance();
        tempLoc.setSC(x, y, origin.getScZ());

        sayText(tempLoc.getDcX(), tempLoc.getDcY(), text, mode);
        tempLoc.recycle();
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
}
