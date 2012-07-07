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
package illarion.mapedit.render;

import illarion.mapedit.data.Map;
import illarion.mapedit.gui.MapPanel;

import java.awt.*;

/**
 * This is the base class off all classes that want to draw on the map.
 *
 * @author Tim
 */
public abstract class AbstractMapRenderer implements Comparable<AbstractMapRenderer> {

    /**
     * The render manager.
     */
    private final RendererManager manager;
    /**
     * The panel, to draw the map on.
     */
    private final MapPanel mapPanel;

    /**
     * Creates a new map renderer
     *
     * @param mapPanel The panel, to draw the map on.
     */
    public AbstractMapRenderer(final MapPanel mapPanel) {
        this.manager = RendererManager.getInstance();
        this.mapPanel = mapPanel;
    }

    /**
     * Returns the rectangle, that needs to be redrawn.
     *
     * @return the render rectangle.
     */
    protected Rectangle getRenderRectangle() {
        Rectangle dirty = mapPanel.getDirtyRegion();
        if (dirty == null)
            return mapPanel.getVisibleRect();
        return mapPanel.getVisibleRect().intersection(dirty);
    }

    /**
     * @return the map zoom.
     */
    protected float getZoom() {
        return manager.getZoom();
    }

    /**
     * @return the x translation of the map.
     */
    protected int getTranslateX() {
        return manager.getTranslationX();
    }

    /**
     * the y translation of the map.
     *
     * @return
     */
    protected int getTranslateY() {
        return manager.getTranslationY();
    }

    /**
     * @return The height of a tile.
     */
    protected float getTileHeight() {
        return manager.getTileHeight();
    }

    /**
     * @return The width of a tile.
     */
    protected float getTileWidth() {
        return manager.getTileWidth();
    }

    /**
     * The map holds all tile, warp and item-data.
     *
     * @return the map to draw.
     */
    protected Map getMap() {
        return mapPanel.getMap();
    }

    /**
     * In this method all rendering should be done.
     *
     * @param graphics the graphics object.
     */
    public abstract void renderMap(final Graphics2D graphics);

    /**
     * Returns a value. The renderer with the lowest value will be rendered first.
     *
     * @return the render priority
     */
    protected abstract int getRenderPriority();

    /**
     * This method is for sorting the renderers in the correct order.
     *
     * @param o
     * @return
     */
    @Override
    public final int compareTo(final AbstractMapRenderer o) {
        final int i = getRenderPriority(), j = o.getRenderPriority();
        if (i < j) {
            return -1;
        } else if (i == j) {
            return 0;
        } else {
            return 1;
        }
    }
}
