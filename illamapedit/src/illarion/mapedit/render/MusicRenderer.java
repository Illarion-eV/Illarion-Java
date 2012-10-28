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
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.util.SwingLocation;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Tim
 */
public class MusicRenderer extends AbstractMapRenderer {

    private static final int XOFFSET = 20;
    private static final int YOFFSET = 10;

    private final Image image;

    /**
     * Creates a new map renderer
     */
    public MusicRenderer(final RendererManager manager) {
        super(manager);
        image = ImageLoader.getImage("sound");
    }

    @Override
    public void renderMap(final Map map, final Rectangle viewport, final int level, final Graphics2D g) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int z = map.getZ() - level;
        final AffineTransform transform = g.getTransform();


        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (map.getTileAt(x, y).getMusicID() == 0) {
                    continue;
                }
                final int xdisp = SwingLocation.displayCoordinateX(x, y, z);
                final int ydisp = SwingLocation.displayCoordinateY(x, y, z);
                if (viewport.contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                        (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

                    g.drawImage(image, xdisp + (int) (XOFFSET * getZoom()), ydisp + (int) (YOFFSET * getZoom()), null);

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
