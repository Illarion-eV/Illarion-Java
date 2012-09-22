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
import illarion.mapedit.resource.loaders.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Tim
 */
public class MusicRenderer extends AbstractMapRenderer {

    private static Image img;
    private static final int XOFFSET = 20;
    private static final int YOFFSET = 10;

    /**
     * Creates a new map renderer
     */
    public MusicRenderer(final RendererManager manager) {
        super(manager);
        img = ImageLoader.getImage("/sound.png");
    }

    @Override
    public void renderMap(final Graphics2D g) {
        final Map map = getMap();
        final int width = map.getWidth();
        final int height = map.getHeight();

        final AffineTransform transform = g.getTransform();

        g.translate(getTranslateX(), getTranslateY());
        g.scale(getZoom(), getZoom());

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (map.getTileAt(x, y).getMusicID() == 0) {
                    continue;
                }
                final int xdisp = Location.displayCoordinateX(x, y, 0);
                final int ydisp = -Location.displayCoordinateY(x, y, 0);
                if (getRenderRectangle().contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                        (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

                    g.drawImage(img, xdisp + (int) (XOFFSET * getZoom()), ydisp + (int) (YOFFSET * getZoom()), null);

                }
            }
        }
        g.setTransform(transform);
    }

    @Override
    protected int getRenderPriority() {
        return 5;
    }
}
