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
import illarion.mapedit.util.SwingLocation;

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
    public void renderMap(final Map map, final Rectangle viewport, final Graphics2D g) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int z = map.getZ();

        final AffineTransform transform = g.getTransform();

        g.translate(0, getTileHeight() + 1);

        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= height; ++x) {
            g.drawLine(
                    SwingLocation.displayCoordinateX(x, 0, z),
                    SwingLocation.displayCoordinateY(x, 0, z),
                    SwingLocation.displayCoordinateX(x, width, z),
                    SwingLocation.displayCoordinateY(x, width, z));
        }

        for (int y = 0; y <= width; ++y) {
            g.drawLine(
                    SwingLocation.displayCoordinateX(0, y, z),
                    SwingLocation.displayCoordinateY(0, y, z),
                    SwingLocation.displayCoordinateX(height, y, z),
                    SwingLocation.displayCoordinateY(height, y, z));
        }


        g.setTransform(transform);

    }

    @Override
    protected int getRenderPriority() {
        return 6;
    }
}
