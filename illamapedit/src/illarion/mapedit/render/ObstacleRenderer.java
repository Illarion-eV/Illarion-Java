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
import illarion.mapedit.data.MapItem;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.resource.loaders.ItemLoader;
import illarion.mapedit.util.SwingLocation;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author Fredrik K
 */
public class ObstacleRenderer extends AbstractMapRenderer {
    private static final int XOFFSET = 30;
    private static final int YOFFSET = -3;

    private final Image image;

    public ObstacleRenderer(final RendererManager manager) {
        super(manager);
        image = resizeImage((BufferedImage) ImageLoader.getImage("messagebox_critical"), 16, 16);
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
                if (tile == null) {
                    continue;
                }
                final List<MapItem> items = tile.getMapItems();
                if (items == null) {
                    continue;
                }
                boolean obstacle = false;
                for (final MapItem item : items) {
                    final ItemImg img = ItemLoader.getInstance().getTileFromId(item.getId());
                    obstacle |= img.isObstacle();
                }
                if (obstacle) {
                    continue;
                }

                final int xDisplay = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
                final int yDisplay = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
                if (isInViewport(viewport, xDisplay, yDisplay)) {
                    final int drawX = xDisplay + (int) (XOFFSET * getZoom());
                    final int drawY = yDisplay + (int) (YOFFSET * getZoom());
                    g.drawImage(image, drawX, drawY, null);
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
        return  Lang.getMsg("renderer.Obstacle");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("messagebox_critical");
    }

    @Override
    public boolean isDefaultOn() {
        return false;
    }
}
