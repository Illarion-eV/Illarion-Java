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

    private static final int DEFAULT_TILE_SIZE = 32;
    private static final int MIN_RENDER_TILE_SIZE = 10;

    private final int sizePerTile;
    private final MapPanel mapPanel;

    public AbstractMapRenderer(final MapPanel mapPanel) {
        this(mapPanel, DEFAULT_TILE_SIZE);
    }

    public AbstractMapRenderer(final MapPanel mapPanel, final int sizePerTile) {
        this.mapPanel = mapPanel;
        this.sizePerTile = sizePerTile;
    }

    protected Rectangle getRenderRectangle() {
        return mapPanel.getVisibleRect();
    }

    protected int getSizePerTile() {
        return sizePerTile;
    }

    public abstract void renderMap(Graphics2D graphics);


}
