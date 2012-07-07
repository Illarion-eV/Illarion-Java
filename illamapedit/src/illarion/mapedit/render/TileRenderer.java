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
import illarion.mapedit.gui.MapPanel;
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

    /**
     * Creates a new map renderer
     *
     * @param mapPanel The panel, to draw the map on.
     */
    public TileRenderer(final MapPanel mapPanel) {
        super(mapPanel);
    }

    @Override
    public void renderMap(final Graphics2D g) {
        final Map map = getMap();
        final int width = map.getW();
        final int height = map.getH();
        AffineTransform transform = g.getTransform();
        g.translate(getTranslateX(), getTranslateY());
        g.scale(getZoom(), getZoom());

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int xdisp = Location.displayCoordinateX(x, y, 0);
                int ydisp = Location.displayCoordinateY(x, y, 0);
                if (getRenderRectangle().contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                        (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

                    TileImg t = TileLoader.getInstance().getTileFromId(map.getTileData().getTileAt(x,
                            y).getId());
                    if (t != null) {
                        Image img = t.getImg()[0];
                        if (img != null) {
                            g.drawImage(img,
                                    xdisp,
                                    ydisp, null);
                        }
                    }
                }
            }
        }


        g.setTransform(transform);
    }

    private void paint(final int x, final int y) {

    }

    @Override
    protected int getRenderPriority() {
        return 0;
    }
}
