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

import illarion.mapedit.gui.MapPanel;

import java.awt.*;

/**
 * @author Tim
 */
public abstract class AbstractMapRenderer {

    private static final int DEFAULT_ZOOM = 32;
    private static final int MIN_RENDER_TILE_SIZE = 10;

    private final int zoom;
    private final MapPanel mapPanel;

    public AbstractMapRenderer(final MapPanel mapPanel) {
        this(mapPanel, DEFAULT_ZOOM);
    }

    public AbstractMapRenderer(final MapPanel mapPanel, final int zoom) {
        this.mapPanel = mapPanel;
        this.zoom = zoom;
    }

    protected Rectangle getRenderRectangle() {
        return mapPanel.getVisibleRect().intersection(mapPanel.getDirtyRegion());
    }

    protected int getZoom() {
        return zoom;
    }

    public abstract void renderMap(Graphics2D graphics);


}
