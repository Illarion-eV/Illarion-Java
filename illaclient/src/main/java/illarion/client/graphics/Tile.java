/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.client.input.DoubleClickOnMapEvent;
import illarion.client.resources.Resource;
import illarion.client.resources.TileFactory;
import illarion.client.resources.data.TileTemplate;
import illarion.client.world.MapGroup;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.graphics.MapVariance;
import illarion.common.graphics.TileInfo;
import illarion.common.types.Location;
import org.apache.log4j.Logger;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.input.Button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents one tile on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("ClassNamingConvention")
public class Tile extends AbstractEntity<TileTemplate> implements Resource {
    @Nullable
    private final FrameAnimation animation;

    @Nullable
    private Overlay overlay;

    private final boolean variants;

    @Nonnull
    private final MapTile parentTile;

    public Tile(final TileTemplate template, @Nonnull final MapTile parentTile) {
        super(template);

        if (template.getAnimationSpeed() > 0) {
            // start animation right away. All tiles of this type will share it
            animation = template.getSharedAnimation();
            variants = false;
        } else if (template.getFrames() > 1) { // a tile with variants
            variants = true;
            animation = null;
        } else {
            animation = null;
            variants = false;
        }
        this.parentTile = parentTile;
    }

    public static Tile create(final int id, @Nonnull final Location loc, final MapTile parentTile) {
        return create(id, loc.getScX(), loc.getScY(), parentTile);
    }

    /**
     * Create a tile and its overlay. Assign variant.
     *
     * @param id complex id
     * @param x  the x coordinate where the tile is supposed to be created
     * @param y  the y coordinate of the location where the tile is supposed to be created
     * @return the created instance of the tile
     */
    @SuppressWarnings("nls")
    private static Tile create(final int id, final int x, final int y, @Nonnull final MapTile parentTile) {
        // instantiate tile
        final TileTemplate tileTemplate = TileFactory.getInstance().getTemplate(TileInfo.getBaseID(id));
        final Tile tile = new Tile(tileTemplate, parentTile);
        // if it is a variants tile, set coordinates
        if (tile.variants) {
            tile.setVariant(x, y);
        }

        if (TileInfo.hasOverlay(id)) {
            tile.setOverlay(Overlay.create(TileInfo.getOverlayID(id), TileInfo.getShapeId(id), tile));
        }

        return tile;
    }

    /**
     * Draw tile and its overlay
     *
     * @param g the graphics object that is used to render the tile.
     */
    @Override
    public void render(@Nonnull final Graphics g) {
        final MapTile obstructingTile = parentTile.getObstructingTile();
        if ((obstructingTile != null) && obstructingTile.isOpaque()) {
            // do not render tiles that are not visible for sure
            return;
        }

        super.render(g);
        if (overlay != null) {
            overlay.render(g);
        }
    }

    @Override
    public void markAsRemoved() {
        super.markAsRemoved();
        if (overlay != null) {
            overlay.markAsRemoved();
        }
    }

    /**
     * Assign overlay to tile
     *
     * @param overlay the overlay
     */
    public void setOverlay(@Nullable final Overlay overlay) {
        this.overlay = overlay;
        setFadingCorridorEffectEnabled(overlay == null);
    }

    @Override
    public void setScreenPos(final int x, final int y, final int z,
                             final int layer) {
        super.setScreenPos(x, y, z, layer);
        if (overlay != null) {
            overlay.setScreenPos(x, y, z, layer);
        }
    }

    @Override
    public void show() {
        final MapGroup group = parentTile.getMapGroup();
        if ((group != null) && group.isHidden()) {
            setAlphaTarget(0);
            setAlpha(0);
            setFadingCorridorEffectEnabled(false);
        } else {
            setFadingCorridorEffectEnabled(overlay == null);
        }
        super.show();
        if (animation != null) {
            animation.addTarget(this, true);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (animation != null) {
            animation.removeTarget(this);
        }
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        final MapGroup group = parentTile.getMapGroup();
        if ((group != null) && group.isHidden()) {
            setAlphaTarget(0);
            setFadingCorridorEffectEnabled(false);
        } else {
            setFadingCorridorEffectEnabled(overlay == null);
        }
        super.update(container, delta);
        if (overlay != null) {
            overlay.update(container, delta);
        }
    }

    /**
     * The instance of the logging class for this class.
     */
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOGGER = Logger.getLogger(Tile.class);

    @Override
    public boolean isEventProcessed(@Nonnull final GameContainer container, final int delta,
                                    @Nonnull final SceneEvent event) {
        if (!parentTile.isAtPlayerLevel()) {
            return false;
        }

        if (event instanceof DoubleClickOnMapEvent) {
            final DoubleClickOnMapEvent clickEvent = (DoubleClickOnMapEvent) event;
            if (clickEvent.getKey() != Button.Left) {
                return false;
            }
            if (!isMouseInInteractionRect(clickEvent.getX(), clickEvent.getY())) {
                return false;
            }

            World.getPlayer().getMovementHandler().walkTo(parentTile.getLocation());
            return true;
        }
        return false;
    }

    /**
     * Determine the graphical variant from the x/y coordinates
     *
     * @param x the x coordinate used to get the variant
     * @param y the y coordinate used to get the variant
     */
    protected final void setVariant(final int x, final int y) {
        setFrame(MapVariance.getTileFrameVariance(x, y, getTemplate().getFrames()));
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation of the parent light fetches the light of the parent tile in order to ensure that the same
     * color value is used.
     */
    @Override
    @Nonnull
    protected Color getParentLight() {
        return parentTile.getLight();
    }
}
