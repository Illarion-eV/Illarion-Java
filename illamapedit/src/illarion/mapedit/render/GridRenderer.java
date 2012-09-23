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

import illarion.common.util.Location;
import illarion.mapedit.data.Map;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * This method renders the grid, to see the tiles better.
 *
 * @author Tim
 */
public class GridRenderer extends AbstractMapRenderer {


    /**
     * Creates a new map renderer
     */
    public GridRenderer(final RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(final Graphics2D g) {
        final Map map = getMap();
        final int width = map.getWidth();
        final int height = map.getHeight();


        final AffineTransform transform = g.getTransform();

        g.translate(getTranslateX(), getTranslateY() + ((getTileHeight() + 1) * getZoom()));
        g.scale(getZoom(), getZoom());
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= width; ++x) {
            g.drawLine(
                    Location.displayCoordinateX(x, 0, 0),
                    Location.displayCoordinateY(x, 0, 0),
                    Location.displayCoordinateX(x, width, 0),
                    Location.displayCoordinateY(x, width, 0));
        }

        for (int y = 0; y <= height; ++y) {
            g.drawLine(
                    Location.displayCoordinateX(0, y, 0),
                    Location.displayCoordinateY(0, y, 0),
                    Location.displayCoordinateX(height, y, 0),
                    Location.displayCoordinateY(height, y, 0));
        }

        g.setTransform(transform);

    }

    @Override
    protected int getRenderPriority() {
        return 6;
    }
}
