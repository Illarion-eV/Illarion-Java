/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.client.Debug;
import illarion.client.IllaClient;
import illarion.common.types.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * This helper class is used to hide out all parts of the game map that are not needed to be viewed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Camera {
    /**
     * The singleton instance of this class.
     */
    private static final Camera INSTANCE = new Camera();

    /**
     * This rectangle stores the viewport of the camera. Anything outside of this viewport does not need to be drawn.
     */
    private final Rectangle viewport;

    /**
     * This is a list of dirty areas on the screen that require a update.
     */
    private final List<Rectangle> dirtyAreas;

    /**
     * A list of areas that were rendered.
     */
    private final List<Rectangle> renderedAreas;

    /**
     * If this flag is set {@code true} the list of dirty areas is ignored and every image is rendered again.
     */
    private boolean fullUpdate;

    /**
     * This counter counts how many areas are marked dirty during one run.
     */
    private int dirtyAreaCounter;

    /**
     * Private constructor to avoid any instances being created but the singleton instance.
     */
    private Camera() {
        viewport = new Rectangle();
        dirtyAreas = new ArrayList<Rectangle>();
        renderedAreas = new ArrayList<Rectangle>();
        fullUpdate = false;
        dirtyAreaCounter = 0;
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static Camera getInstance() {
        return INSTANCE;
    }

    /**
     * Get the height of the viewport.
     *
     * @return the height of the viewport
     */
    public int getViewportHeight() {
        return viewport.getHeight();
    }

    /**
     * Get the x offset applied to the viewport.
     *
     * @return the x offset
     */
    public int getViewportOffsetX() {
        return viewport.getX();
    }

    /**
     * Get the y offset applied to the viewport.
     *
     * @return the y offset
     */
    public int getViewportOffsetY() {
        return viewport.getY();
    }

    /**
     * Get the width of the viewport.
     *
     * @return the width of the viewport
     */
    public int getViewportWidth() {
        return viewport.getWidth();
    }

    /**
     * This function causes the dirty areas array to be cleared. This should be done once all dirty areas are rendered
     * again.
     */
    public void clearDirtyAreas() {
        synchronized (dirtyAreas) {
            dirtyAreas.clear();
        }
        renderedAreas.clear();
        fullUpdate = false;
        dirtyAreaCounter = 0;
    }

    /**
     * This function marks a area of the screen as dirty. This means that the next render loop has to update this part
     * of the screen.
     *
     * @param dirtyArea the area that was marked dirty
     */
    public void markAreaDirty(final Rectangle dirtyArea) {
        if (fullUpdate || (dirtyAreaCounter > 200)) {
            markEverythingDirty();
            return;
        }

        dirtyAreaCounter++;

        final Rectangle dirtyRect = new Rectangle();
        dirtyRect.set(dirtyArea);

        synchronized (dirtyAreas) {
            final ListIterator<Rectangle> listItr = dirtyAreas.listIterator();
            while (listItr.hasNext()) {
                final Rectangle currentDirtyArea = listItr.next();
                if (currentDirtyArea.intersects(dirtyRect)) {
                    dirtyRect.add(currentDirtyArea);
                    listItr.remove();
                }
            }

            dirtyAreas.add(dirtyRect);
        }
    }

    public void markAreaRendered(final Rectangle area) {
        if (IllaClient.isDebug(Debug.mapRenderer)) {
            renderedAreas.add(area);
        }
    }

    /**
     * Mark the entire screen dirty. That causes everything to be rendered again.
     */
    public void markEverythingDirty() {
        fullUpdate = true;
    }

    /**
     * Check if a specified area of the screen intersects with any dirty area and return this dirty area.
     *
     * @param testArea the tested area
     * @return the dirty area that intersects with the tested one or {@code null} in case no such area is found
     */
    public Rectangle getDirtyArea(final Rectangle testArea) {
        if (fullUpdate) {
            return viewport;
        }

        synchronized (dirtyAreas) {
            for (int i = 0, dirtyAreasSize = dirtyAreas.size(); i < dirtyAreasSize; i++) {
                final Rectangle dirtArea = dirtyAreas.get(i);
                if (testArea.intersects(dirtArea)) {
                    return dirtArea;
                }
            }
        }

        return null;
    }

    public void clearDirtyAreas(final Graphics g) {
        g.setColor(Color.black);

        if (fullUpdate) {
            fillRenderRect(g, viewport);
            return;
        }

        synchronized (dirtyAreas) {
            for (int i = 0, dirtyAreasSize = dirtyAreas.size(); i < dirtyAreasSize; i++) {
                final Rectangle dirtArea = dirtyAreas.get(i);
                fillRenderRect(g, dirtArea);
            }
        }
    }

    public void renderDebug(final Graphics g) {
        if (IllaClient.isDebug(Debug.mapRenderer)) {
            g.translate(-getViewportOffsetX(), -getViewportOffsetY());
            debugShowUpdateAreas(g);
            debugShowRenderedAreas(g);
            g.translate(getViewportOffsetX(), getViewportOffsetY());
        }
    }

    public void debugShowUpdateAreas(final Graphics g) {
        g.setColor(Color.pink);
        if (fullUpdate) {
            debugRenderRect(g, viewport);
            return;
        }

        synchronized (dirtyAreas) {
            for (int i = 0, dirtyAreasSize = dirtyAreas.size(); i < dirtyAreasSize; i++) {
                final Rectangle dirtArea = dirtyAreas.get(i);
                debugRenderRect(g, dirtArea);
            }
        }
    }

    public void debugShowRenderedAreas(final Graphics g) {
        g.setColor(Color.cyan);

        for (final Rectangle area : renderedAreas) {
            debugRenderRect(g, area);
        }
    }

    private void debugRenderRect(final Graphics g, final Rectangle rectangle) {
        g.drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    private void fillRenderRect(final Graphics g, final Rectangle rectangle) {
        g.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    /**
     * Check if anything that is currently displayed is marked as dirty.
     *
     * @return {@code true} in case some are of the map is dirty and needs to be rendered again
     */
    public boolean isAnythingDirty() {
        return fullUpdate || (getDirtyArea(viewport) != null);
    }

    /**
     * Check if a area is at least partly within a area that needs to be updated at this render run.
     *
     * @param testLocX the x location of the area that needs to be checked for intersection with the clipping area
     * @param testLocY the y location of the area that needs to be checked for intersection with the clipping area
     * @param width    the width of the area that needs to be checked
     * @param height   the height of the area that needs to be checked
     * @return <code>true</code> in case there is no clipping area set or the tested area is at least partly within the
     *         clipping area, if its fully outside <code>false</code> is returned
     */
    public boolean requiresUpdate(final int testLocX, final int testLocY, final int width, final int height) {
        final Rectangle testRect = new Rectangle();
        testRect.set(testLocX, testLocY, width, height);

        return requiresUpdate(testRect);
    }

    /**
     * Check if a area is at least partly within a area that needs to be updated at this render run.
     *
     * @param rect the rectangle to test
     * @return <code>true</code> in case there is no clipping area set or the tested area is at least partly within the
     *         clipping area, if its fully outside <code>false</code> is returned
     */
    public boolean requiresUpdate(final Rectangle rect) {
        if (!viewport.intersects(rect)) {
            return false;
        }

        return fullUpdate || (getDirtyArea(rect) != null);
    }

    /**
     * Set the viewport of the camera. That is needed to check if objects are are inside the camera view or not.
     *
     * @param x      the x coordinate of the origin of the viewport
     * @param y      the y coordinate of the origin of the viewport
     * @param width  the width of the viewport
     * @param height the height of the viewport
     */
    public void setViewport(final int x, final int y, final int width, final int height) {
        viewport.set(x, y, width, height);
    }
}
