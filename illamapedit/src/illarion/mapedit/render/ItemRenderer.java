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
import illarion.mapedit.data.MapItem;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.loaders.ItemLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * @author Tim
 */
public class ItemRenderer extends AbstractMapRenderer {
    /**
     * Creates a new map renderer
     */
    public ItemRenderer(final RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(final Graphics2D g) {
        final Map map = getMap();
        final int w = map.getW();
        final int h = map.getH();
        final AffineTransform t = g.getTransform();
        g.translate(getTranslateX(), getTranslateY());
        g.scale(getZoom(), getZoom());
        for (int x = w - 1; x >= 0; --x) {
            for (int y = 0, x2 = x; (x2 <= (w - 1)) && (y < h); ++y, ++x2) {

                final List<MapItem> items = map.getTileAt(x2, y).getMapItems();
                if (items.isEmpty()) {
                    continue;
                }

                final int xdisp = Location.displayCoordinateX(x2, y, 0);
                final int ydisp = -Location.displayCoordinateY(x2, y, 0);

                //TODO: Tidy this up.
                if (getRenderRectangle().contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                        (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

                    final AffineTransform tr = g.getTransform();
                    for (final MapItem item : items) {

                        final ItemImg img = ItemLoader.getInstance().getTileFromId(item.getId());
                        final Image paintImg = img.getImgs()[0];

                        //TODO: Fix item offset with different zoom levels
                        //is it because of integer rounding?

                        // Hack, correct the wrong drawn tiles. Easier than correct math etc.
                        g.translate(getTileWidth(), getTileHeight());
                        g.translate(xdisp, ydisp);
                        g.translate(img.getOffsetX(), -img.getOffsetY());
                        g.translate(-paintImg.getWidth(null) / 2, -paintImg.getHeight(null));

                        g.drawImage(img.getImgs()[0], 0, 0, null);
                        g.setTransform(tr);
                    }
                }
            }
        }

        g.setTransform(t);
    }

    @Override
    protected int getRenderPriority() {
        return 4;
    }
}
