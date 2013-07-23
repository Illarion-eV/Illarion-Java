/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2013 - Illarion e.V.
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

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.util.SwingLocation;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * @author Fredrik K
 */
public class ItemDataRenderer extends AbstractMapRenderer {
    private static final int XOFFSET = 9;
    private static final int YOFFSET = 11;

    private final Image image;

    public ItemDataRenderer(final RendererManager manager) {
        super(manager);
        image = resizeImage((BufferedImage) ImageLoader.getImage("info"), 16, 16);
    }

    @Override
    public void renderMap(final Map map, final Rectangle viewport, final int level, final Graphics2D g) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int z = map.getZ() - level;
        final AffineTransform transform = g.getTransform();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                final MapTile tile = map.getTileAt(x, y);
                if ((tile == null) || tile.isMapItemsDataEmpty()) {
                    continue;
                }
                final int xDisplay = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
                final int yDisplay = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
                if (viewport.contains((xDisplay * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                        (yDisplay * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {
                    g.drawImage(image, xDisplay + (int) (XOFFSET * getZoom()), yDisplay + (int) (YOFFSET * getZoom()), null);
                }
            }
        }
        g.setTransform(transform);
    }

    @Override
    protected int getRenderPriority() {
        return 8;
    }

    @Override
    public String getLocalizedName() {
        return  Lang.getMsg("renderer.ItemData");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("info");
    }

    @Override
    public boolean isDefaultOn() {
        return false;
    }
}
