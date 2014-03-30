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
import java.awt.image.BufferedImage;

/**
 * @author Tim
 */
public class MusicRenderer extends AbstractMapRenderer {
    private static final int XOFFSET = 30;
    private static final int YOFFSET = 20;

    @Nonnull
    private final Image image;

    /**
     * Creates a new map renderer
     */
    public MusicRenderer(final RendererManager manager) {
        super(manager);
        image = resizeImage((BufferedImage) ImageLoader.getImage("sound"), 16, 16);
    }

    @Override
    public void renderMap(
            @Nonnull final Map map,
            @Nonnull final Rectangle viewport,
            final int level,
            @Nonnull final Graphics2D g) {
        final int width = map.getWidth();
        final int height = map.getHeight();
        final int z = map.getZ() - level;
        final AffineTransform transform = g.getTransform();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                final int id = map.getTileAt(x, y).getMusicID();
                if (id == 0) {
                    continue;
                }
                final int xdisp = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
                final int ydisp = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
                if (viewport.contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                                      (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {

                    g.drawImage(image, xdisp + (int) (XOFFSET * getZoom()), ydisp + (int) (YOFFSET * getZoom()), null);
                    g.setColor(Color.RED);
                    g.drawString(Integer.toString(id), xdisp + (int) (XOFFSET * getZoom()),
                                 ydisp + (int) (YOFFSET * getZoom()));
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
        return Lang.getMsg("renderer.Sound");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("sound");
    }

    @Override
    public boolean isDefaultOn() {
        return false;
    }
}
