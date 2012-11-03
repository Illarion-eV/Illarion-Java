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
import illarion.mapedit.data.MapItem;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.loaders.ItemLoader;
import illarion.mapedit.util.SwingLocation;

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
    public void renderMap(final Map map, final Rectangle viewport, final int level, final Graphics2D g) {
        final AffineTransform t = g.getTransform();


        //actual H-Position
        int actualH = 0;
        //the start-w-position of the current diagonal iteration
        int iterationStartW = map.getWidth() - 1;
        //actual W-Position
        int actualW = iterationStartW;
        //iterate diagonal until iterations can reach the nearest tile
        while ((iterationStartW > -map.getHeight()) && (actualH < map.getHeight())) {
            render(actualW, actualH, viewport, map, level, g);
            //iterate diagonal
            actualH++;
            actualW++;
            //iteration will end at max W or at max H
            if ((actualW >= map.getWidth()) || (actualH >= map.getHeight())) {
                //start at the next lower W position
                actualW = --iterationStartW;
                if (actualW < 0) {
                    //in case of the lower-left half start at left side and lower
                    actualH = -actualW;
                    actualW = 0;
                } else {
                    //otherwise start at h = 0
                    actualH = 0;
                }
            }

        }
        g.setTransform(t);
    }

    private void render(final int x, final int y, final Rectangle viewport, final Map map, final int level, final Graphics2D g) {
        final int z = map.getZ() - level;
        final List<MapItem> items = map.getTileAt(x, y).getMapItems();
        if (items.isEmpty()) {
            return;
        }

        final int xdisp = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
        final int ydisp = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
        if (viewport.contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

            final AffineTransform tr = g.getTransform();
            for (final MapItem item : items) {

                final ItemImg img = ItemLoader.getInstance().getTileFromId(item.getId());
                if ((img != null) && (img.getImgs() != null)) {
                    final Image paintImg = img.getImgs()[0];

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

    @Override
    protected int getRenderPriority() {
        return 5;
    }
}
