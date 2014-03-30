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
import illarion.mapedit.data.MapTile;
import illarion.mapedit.resource.Overlay;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.resource.loaders.OverlayLoader;
import illarion.mapedit.resource.loaders.TileLoader;
import illarion.mapedit.util.SwingLocation;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * This renderer should render all tiles.
 *
 * @author Tim
 */
public class TileRenderer extends AbstractMapRenderer {

    private static final Color[] TILE_COLORS = {new Color(0, 0, 0), // black
                                                new Color(182, 214, 158), // green
                                                new Color(155, 120, 90), // brown
                                                new Color(175, 183, 165), // gray
                                                new Color(126, 193, 238), // blue
                                                new Color(255, 255, 204), // yellow
                                                new Color(205, 101, 101), // red
                                                new Color(255, 255, 255), // white
                                                new Color(140, 160, 100) // dark green
    };

    /**
     * Render Empty tiles
     */
    private boolean renderEmpty;

    /**
     * Creates a new map renderer
     */
    public TileRenderer(final RendererManager manager) {
        super(manager);
    }

    @Override
    public void renderMap(
            @Nonnull final Map map,
            @Nonnull final Rectangle viewport,
            final int level,
            @Nonnull final Graphics2D g) {
        final int z = map.getZ() - level;
        final AffineTransform transform = g.getTransform();

        for (int x = 0; x < map.getWidth(); ++x) {
            for (int y = 0; y < map.getHeight(); ++y) {
                final int xdisp = SwingLocation.displayCoordinateX(x + map.getX(), y + map.getY(), z);
                final int ydisp = SwingLocation.displayCoordinateY(x + map.getX(), y + map.getY(), z);
                if (viewport.contains((xdisp * getZoom()) + getTranslateX() + (getTileWidth() * getZoom()),
                                      (ydisp * getZoom()) + getTranslateY() + (getTileHeight() * getZoom()))) {
                    final MapTile mt = map.getTileAt(x, y);
                    if (renderEmpty || (mt != null && mt.getId() != 0)) {
                        final TileImg t = TileLoader.getInstance().getTileFromId(mt.getId());
                        if (t != null) {
                            final AffineTransform tr = g.getTransform();
                            if (getZoom() > getMinZoom()) {
                                renderTile(xdisp, ydisp, g, t.getImg()[0]);
                                renderOverlay(g, mt);
                            } else {
                                if (t.getInfo().getMapColor() != 0) {
                                    g.translate(xdisp, ydisp);
                                    g.setColor(TILE_COLORS[t.getInfo().getMapColor()]);
                                    g.fill(getTilePolygon());
                                }
                            }
                            g.setTransform(tr);
                        }
                    }
                }
            }
        }

        g.setTransform(transform);
    }

    private void renderTile(
            final int xDisp,
            final int yDisp,
            @Nonnull final Graphics2D graphics,
            @Nonnull final Image image) {
        graphics.translate(xDisp, yDisp);
        graphics.drawImage(image, 0, 0, null);
    }

    private void renderOverlay(@Nonnull final Graphics2D graphics, @Nonnull final MapTile mapTile) {
        final Overlay o = OverlayLoader.getInstance().getOverlayFromId(mapTile.getOverlayID());
        if (o != null) {
            final Image imageOverlay = o.getImgs()[mapTile.getShapeID() - 1];
            if (imageOverlay != null) {
                graphics.drawImage(imageOverlay, 0, 0, null);
            }
        }
    }

    @Override
    protected int getRenderPriority() {
        return 3;
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("renderer.Tile");
    }

    @Override
    public ResizableIcon getRendererIcon() {
        return ImageLoader.getResizableIcon("file_tiles");
    }

    @Override
    public boolean isDefaultOn() {
        return true;
    }

    @Nonnull
    @Override
    public RibbonElementPriority getPriority() {
        return RibbonElementPriority.TOP;
    }

    public String getEmptyTileLocalizedName() {
        return Lang.getMsg("renderer.EmptyTile");
    }

    public ResizableIcon getEmptyTileRendererIcon() {
        return ImageLoader.getResizableIcon("file_tiles");
    }

    public boolean isEmptyTileDefaultOn() {
        return true;
    }

    public void setRenderEmptyTiles(final boolean renderEmptyTiles) {
        renderEmpty = renderEmptyTiles;
    }

    @Nonnull
    public RibbonElementPriority getEmptyTilePriority() {
        return RibbonElementPriority.TOP;
    }
}
