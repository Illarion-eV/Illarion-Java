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
package illarion.client.graphics;

import illarion.client.input.ClickOnMapEvent;
import illarion.client.input.CurrentMouseLocationEvent;
import illarion.client.input.PointOnMapEvent;
import illarion.client.resources.OverlayFactory;
import illarion.client.resources.Resource;
import illarion.client.resources.TileFactory;
import illarion.client.resources.data.OverlayTemplate;
import illarion.client.resources.data.TileTemplate;
import illarion.client.world.MapGroup;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.movement.TargetMovementHandler;
import illarion.common.graphics.MapVariance;
import illarion.common.graphics.TileInfo;
import illarion.common.types.Location;
import illarion.common.types.Rectangle;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.graphic.effects.TextureEffect;
import org.illarion.engine.graphic.effects.TileLightEffect;
import org.illarion.engine.input.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /**
     * The animation that is applied to this tile or {@code null} in case there is none.
     */
    @Nullable
    private final FrameAnimation animation;

    /**
     * The template of the overlay that is rendered on top of the tile or {@code null} in case just the plain tile is
     * rendered.
     */
    @Nullable
    private OverlayTemplate overlay;

    /**
     * The shape of the overlay that is rendered on the tile.
     */
    private int overlayShape;

    /**
     * The parent map tile reference.
     */
    @Nonnull
    private final MapTile parentTile;

    private int showHighlight;

    @Override
    public int getHighlight() {
        return showHighlight;
    }

    /**
     * The instance of the logging class for this class.
     */
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOGGER = LoggerFactory.getLogger(Tile.class);

    public Tile(int tileId, @Nonnull MapTile parentTile) {
        this(TileFactory.getInstance().getTemplate(TileInfo.getBaseID(tileId)), tileId, parentTile);
    }

    public Tile(@Nonnull TileTemplate template, int tileId, @Nonnull MapTile parentTile) {
        super(template);

        if (template.getAnimationSpeed() > 0) {
            // start animation right away. All tiles of this type will share it
            animation = template.getSharedAnimation();
        } else if (template.getFrames() > 1) { // a tile with variants
            animation = null;
            Location location = parentTile.getLocation();
            setFrame(MapVariance.getTileFrameVariance(location.getScX(), location.getScY(), template.getFrames()));
        } else {
            animation = null;
        }
        this.parentTile = parentTile;

        if (TileInfo.hasOverlay(tileId)) {
            overlay = OverlayFactory.getInstance().getTemplate(TileInfo.getOverlayID(tileId));
            overlayShape = TileInfo.getShapeId(tileId) - 1;
            setFadingCorridorEffectEnabled(false);
        }
    }

    /**
     * Draw tile and its overlay
     *
     * @param g the graphics object that is used to render the tile.
     */
    @Override
    public void render(@Nonnull Graphics g) {
        if (performRendering()) {
            MapTile obstructingTile = parentTile.getObstructingTile();
            if ((obstructingTile != null) && obstructingTile.isOpaque()) {
                return;
            }

            super.render(g);
            showHighlight = 0;

            /* This is the new light based tile. Sadly the performance of this thing is too poor,
             * so its disabled for now.
            if ((showHighlight != 0) || (tileLightEffect == null) || !parentTile.hasLightGradient()) {
                super.render(g);
                showHighlight = 0;
            } else {
                tileLightEffect.setTopLeftColor(parentTile.getLight(Location.DIR_NORTH));
                tileLightEffect.setTopRightColor(parentTile.getLight(Location.DIR_EAST));
                tileLightEffect.setBottomLeftColor(parentTile.getLight(Location.DIR_WEST));
                tileLightEffect.setBottomRightColor(parentTile.getLight(Location.DIR_SOUTH));
                tileLightEffect.setTopColor(parentTile.getLight(Location.DIR_NORTHEAST));
                tileLightEffect.setBottomColor(parentTile.getLight(Location.DIR_SOUTHWEST));
                tileLightEffect.setLeftColor(parentTile.getLight(Location.DIR_NORTHWEST));
                tileLightEffect.setRightColor(parentTile.getLight(Location.DIR_SOUTHEAST));
                tileLightEffect.setCenterColor(parentTile.getLight());
                renderSprite(g, getDisplayX(), getDisplayY(), Color.WHITE, tileLightEffect);
            }
            */
        }
    }

    @Override
    protected void renderSprite(
            @Nonnull Graphics g, int x, int y, @Nonnull Color light, @Nonnull TextureEffect... effects) {
        super.renderSprite(g, x, y, light, effects);
        if (overlay != null) {
            g.drawSprite(overlay.getSprite(), x, y, light, overlayShape, getScale(), 0.f, effects);
        }
    }

    @Override
    public void show() {
        MapGroup group = parentTile.getMapGroup();
        if ((group != null) && group.isHidden()) {
            setAlphaTarget(0);
            setAlpha(0);
            setFadingCorridorEffectEnabled(false);
        } else {
            setFadingCorridorEffectEnabled(true);
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

    private TileLightEffect tileLightEffect;

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        if (tileLightEffect == null) {
            try {
                tileLightEffect = container.getEngine().getAssets().getEffectManager().getTileLightEffect(true);
            } catch (EngineException ignored) {
            }
        }
        parentTile.updateColor(delta);
        MapGroup group = parentTile.getMapGroup();
        if ((group != null) && group.isHidden()) {
            setAlphaTarget(0);
            setFadingCorridorEffectEnabled(false);
        } else {
            setFadingCorridorEffectEnabled(true);
        }
        super.update(container, delta);
    }

    @Override
    public boolean isEventProcessed(
            @Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
        if (event instanceof PointOnMapEvent) {
            if (!isVisible()) {
                return false;
            }

            PointOnMapEvent pointEvent = (PointOnMapEvent) event;
            if (isMouseInInteractionRect(pointEvent.getX(), pointEvent.getY())) {
                return true;
            }
        }
        if (!parentTile.isAtPlayerLevel()) {
            return false;
        }

        if (event instanceof ClickOnMapEvent) {
            ClickOnMapEvent clickEvent = (ClickOnMapEvent) event;
            if (clickEvent.getKey() != Button.Left) {
                return false;
            }
            if (!isMouseInInteractionRect(clickEvent.getX(), clickEvent.getY())) {
                return false;
            }

            TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
            handler.walkTo(parentTile.getLocation(), 0);
            handler.assumeControl();
            return true;
        }

        if (event instanceof CurrentMouseLocationEvent) {
            CurrentMouseLocationEvent moveEvent = (CurrentMouseLocationEvent) event;
            if (!isMouseInInteractionRect(moveEvent.getX(), moveEvent.getY())) {
                return false;
            }
            World.getPlayer().getMovementHandler().getTargetMouseMovementHandler().walkTo(parentTile.getLocation(), 0);

            if (!moveEvent.isHighlightHandled()) {
                showHighlight = 1;
                moveEvent.setHighlightHandled(true);
            }
            return true;
        }

        return false;
    }

    @Override
    protected boolean isMouseInInteractionRect(int mouseX, int mouseY) {
        int mouseXonDisplay = mouseX + Camera.getInstance().getViewportOffsetX();
        int mouseYonDisplay = mouseY + Camera.getInstance().getViewportOffsetY();

        Rectangle interactionRect = getInteractionRect();
        if (!interactionRect.isInside(mouseXonDisplay, mouseYonDisplay)) {
            return false;
        }

        /* Get the pixel location on the tile. */
        int mouseXonTile = mouseXonDisplay - interactionRect.getLeft();
        int mouseYonTile = mouseYonDisplay - interactionRect.getBottom();

        /* Fold the location into one quarter */
        if (mouseXonTile > 37) {
            mouseXonTile = 75 - mouseXonTile;
        }
        if (mouseYonTile > 18) {
            mouseYonTile = 36 - mouseYonTile;
        }

        /*
         * Check if the mouse is on a opaque pixel. This calculation is only valid as long as the shape of the tiles
         * does not change.
         */
        return (mouseXonTile / 2) >= (18 - mouseYonTile);
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
