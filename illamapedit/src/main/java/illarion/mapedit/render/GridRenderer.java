/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.render;

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.util.SwingLocation;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * This method renders the grid, to see the tiles better.
 *
 * @author Tim
 */
public class GridRenderer extends AbstractMapRenderer {

    /**
     * Creates a new map renderer
     */
    public GridRenderer(final RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(
            @Nonnull final Map map,
            final Rectangle viewport,
            final int level,
            @Nonnull final Graphics2D g) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int z = map.getZ() - level;
        final AffineTransform transform = g.getTransform();

        g.translate(0, getTileHeight() + 1);

        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= width; ++x) {
            g.drawLine(SwingLocation.displayCoordinateX(x + map.getX(), map.getY(), z),
                       SwingLocation.displayCoordinateY(x + map.getX(), map.getY(), z),
                       SwingLocation.displayCoordinateX(x + map.getX(), height + map.getY(), z),
                       SwingLocation.displayCoordinateY(x + map.getX(), height + map.getY(), z));
        }
        for (int y = 0; y <= height; ++y) {
            g.drawLine(SwingLocation.displayCoordinateX(map.getX(), y + map.getY(), z),
                       SwingLocation.displayCoordinateY(map.getX(), y + map.getY(), z),
                       SwingLocation.displayCoordinateX(width + map.getX(), y + map.getY(), z),
                       SwingLocation.displayCoordinateY(width + map.getX(), y + map.getY(), z));
        }

        g.setTransform(transform);
    }

    @Override
    protected int getRenderPriority() {
        return 4;
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("renderer.Grid");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("viewGrid");
    }

    @Override
    public boolean isDefaultOn() {
        return false;
    }
}
