/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import illarion.mapedit.data.MapTile;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.util.SwingLocation;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * @author Fredrik K
 */
public class AnnotationRenderer extends AbstractMapRenderer {
    private static final int XOFFSET = 50;
    private static final int YOFFSET = 11;

    @Nonnull
    private final Image image;

    public AnnotationRenderer(RendererManager manager) {
        super(manager);
        image = resizeImage((BufferedImage) ImageLoader.getImage("annotation"), 16, 16);
    }

    @Override
    public void renderMap(
            @Nonnull Map map,
            @Nonnull Rectangle viewport,
            int level,
            @Nonnull Graphics2D g) {
        int width = map.getWidth();
        int height = map.getHeight();
        int z = map.getZ() - level;
        AffineTransform transform = g.getTransform();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                MapTile tile = map.getTileAt(x, y);
                if ((tile == null) || (!tile.hasAnnotation() && !tile.hasItemAnnotation())) {
                    continue;
                }

                int xDisplay = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
                int yDisplay = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
                if (isInViewport(viewport, xDisplay, yDisplay)) {
                    int drawX = xDisplay + (int) (XOFFSET * getZoom());
                    int drawY = yDisplay + (int) (YOFFSET * getZoom());
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
        return Lang.getMsg("renderer.Annotation");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("annotation");
    }

    @Override
    public boolean isDefaultOn() {
        return false;
    }
}
