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
 * @author Tim
 */
public abstract class AbstractMapRenderer implements Comparable<AbstractMapRenderer> {

    private final RendererManager manager;
    private final MapPanel mapPanel;

    public AbstractMapRenderer(final MapPanel mapPanel) {
        this.manager = RendererManager.getInstance();
        this.mapPanel = mapPanel;
    }

    protected Rectangle getRenderRectangle() {
        Rectangle dirty = mapPanel.getDirtyRegion();
        if (dirty == null)
            return mapPanel.getVisibleRect();
        return mapPanel.getVisibleRect().intersection(dirty);
    }

    protected float getZoom() {
        return RendererManager.getInstance().getZoom();
    }

    protected int getTranslateX() {
        return manager.getTranslationX();
    }

    protected int getTranslateY() {
        return manager.getTranslationY();
    }

    protected float getTileHeight() {
        return manager.getTileHeight();
    }

    protected float getTileWidth() {
        return manager.getTileWidth();
    }

    protected Map getMap() {
        return mapPanel.getMap();
    }

    public abstract void renderMap(final Graphics2D graphics);

    protected abstract int getRenderPriority();

    @Override
    public int compareTo(final AbstractMapRenderer o) {
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
