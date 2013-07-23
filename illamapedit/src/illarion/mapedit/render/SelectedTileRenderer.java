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
import illarion.mapedit.gui.MapEditorConfig;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.util.SwingLocation;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * This class renders a border around the selected tiles
 *
 * @author Fredrik K
 */
public class SelectedTileRenderer extends AbstractMapRenderer {

    /**
     * Creates a new SelectedTileRenderer
     */
    public SelectedTileRenderer(final RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(@Nonnull final Map map, final Rectangle viewport, final int level, @Nonnull final Graphics2D g) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int z = map.getZ() - level;
        final AffineTransform transform = g.getTransform();

        g.translate(0, getTileHeight() + 1);
        g.setColor(Color.ORANGE);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                final int mapX = x + map.getX();
                final int mapY = y + map.getY();
                if (map.isActiveTile(x, y)) {
                    g.setColor(Color.YELLOW);
                    drawLine(mapX, mapY, mapX, mapY + 1, z, g);
                    drawLine(mapX + 1, mapY, mapX + 1, mapY + 1, z, g);
                    drawLine(mapX, mapY, mapX + 1, mapY, z, g);
                    drawLine(mapX, mapY + 1, mapX + 1, mapY + 1, z, g);
                    g.setColor(Color.ORANGE);
                } else if (MapEditorConfig.getInstance().isShowPosition() && map.isPositionAtTile(x, y)) {
                    g.setColor(Color.CYAN);
                    drawLine(mapX, mapY, mapX, mapY + 1, z, g);
                    drawLine(mapX + 1, mapY, mapX + 1, mapY + 1, z, g);
                    drawLine(mapX, mapY, mapX + 1, mapY, z, g);
                    drawLine(mapX, mapY + 1, mapX + 1, mapY + 1, z, g);
                    g.setColor(Color.ORANGE);
                } else if (map.isSelected(x, y)) {
                    if (!map.isSelected(x-1, y)) {
                        drawLine(mapX, mapY, mapX, mapY + 1, z, g);
                    }
                    if (!map.isSelected(x+1, y)) {
                        drawLine(mapX + 1, mapY, mapX + 1, mapY + 1, z, g);
                    }
                    if (!map.isSelected(x, y-1)) {
                        drawLine(mapX, mapY, mapX + 1, mapY, z, g);
                    }
                    if (!map.isSelected(x, y+1)) {
                        drawLine(mapX, mapY + 1, mapX + 1, mapY + 1, z, g);
                    }
                }
            }
        }

        g.setTransform(transform);
    }

    private void drawLine(final int fromX, final int fromY, final int toX, final int toY, final int z, @Nonnull final Graphics2D g) {
        g.drawLine(
                SwingLocation.displayCoordinateX(fromX, fromY, z),
                SwingLocation.displayCoordinateY(fromX, fromY, z),
                SwingLocation.displayCoordinateX(toX, toY, z),
                SwingLocation.displayCoordinateY(toX, toY, z));
    }

    @Override
    protected int getRenderPriority() {
        return 5;
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("renderer.SelectedTiles");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("viewGrid");
    }

    @Override
    public boolean isDefaultOn() {
        return true;
    }
}
