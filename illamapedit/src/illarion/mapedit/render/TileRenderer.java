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
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.resource.loaders.TileLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * This renderer should render all tiles.
 *
 * @author Tim
 */
public class TileRenderer extends AbstractMapRenderer {

    private static final Color[] TILE_COLORS = {
            new Color(0, 0, 0), // black
            new Color(182, 214, 158), // green
            new Color(155, 120, 90), // brown
            new Color(175, 183, 165), // gray
            new Color(126, 193, 238), // blue
            new Color(255, 255, 204), // yellow
            new Color(205, 101, 101), // red
            new Color(255, 255, 255), // white
            new Color(140, 160, 100) // dark green
    };

    /**
     * Creates a new map renderer
     */
    public TileRenderer(final RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(final Graphics2D g) {
        final Map map = getMap();
        final int width = map.getW();
        final int height = map.getH();
        final AffineTransform transform = g.getTransform();
        g.translate(getTranslateX(), getTranslateY());
        g.scale(getZoom(), getZoom());
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                final int xdisp = Location.displayCoordinateX(x, y, 0);
                final int ydisp = -Location.displayCoordinateY(x, y, 0);
                if (getRenderRectangle().contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                        (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

                    final TileImg t = TileLoader.getInstance().getTileFromId(map.getTileAt(x,
                            y).getId());
                    if (t != null) {
                        final AffineTransform tr = g.getTransform();
                        if (getZoom() > getMinZoom()) {
                            final Image img = t.getImg()[0];
                            if (img != null) {
                                g.translate(xdisp, ydisp);
                                g.drawImage(img,
                                        0,
                                        0, null);
                            }
                        } else {

                            g.translate(xdisp, ydisp);
                            g.setColor(TILE_COLORS[t.getInfo().getMapColor()]);
                            g.fill(getTilePolygon());

                        }
                        g.setTransform(tr);
                    }
                }
            }
        }


        g.setTransform(transform);
    }

    @Override
    protected int getRenderPriority() {
        return 3;
    }
}
