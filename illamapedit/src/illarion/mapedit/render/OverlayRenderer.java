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
import illarion.mapedit.data.MapTile;
import illarion.mapedit.resource.Overlay;
import illarion.mapedit.resource.loaders.OverlayLoader;
import illarion.mapedit.util.SwingLocation;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Tim
 */
public class OverlayRenderer extends AbstractMapRenderer {

    /**
     * Creates a new map renderer
     */
    public OverlayRenderer(final RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(final Map map, final Rectangle viewport, final int level, final Graphics2D g) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int z = map.getZ() - level;
        final AffineTransform transform = g.getTransform();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                final int xdisp = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
                final int ydisp = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
                if (viewport.contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                        (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {
                    final MapTile tile = map.getTileAt(x, y);
                    final Overlay o = OverlayLoader.getInstance().getOverlayFromId(tile.getOverlayID());

                    if (o != null) {
                        final AffineTransform tr = g.getTransform();
                        if (getZoom() > getMinZoom()) {
                            final Image img = o.getImgs()[tile.getShapeID() - 1];
                            if (img != null) {
                                g.translate(xdisp, ydisp);
                                g.drawImage(img,
                                        0,
                                        0, null);
                            }
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
        return 4;
    }
}
