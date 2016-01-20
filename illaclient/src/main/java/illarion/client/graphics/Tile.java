/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
import illarion.common.types.Direction;
import illarion.common.types.Rectangle;
import illarion.common.types.ServerCoordinate;
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
import java.util.Collection;
import java.util.EnumSet;

/**
 * This class represents one tile on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("ClassNamingConvention")
public class Tile extends AbstractEntity<TileTemplate> implements Resource {
    /**
     * The instance of the logging class for this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Tile.class);
    /**
     * The animation that is applied to this tile or {@code null} in case there is none.
     */
    @Nullable
    private final FrameAnimation animation;
    /**
     * The parent map tile reference.
     */
    @Nonnull
    private final MapTile parentTile;
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
    private int showHighlight;
    private TileLightEffect tileLightEffect;
    @Nullable
    private Color topColor;
    @Nullable
    private Color leftColor;
    @Nullable
    private Color rightColor;
    @Nullable
    private Color bottomColor;

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
            ServerCoordinate location = parentTile.getCoordinates();
            setFrame(MapVariance.getTileFrameVariance(location.getX(), location.getY(), template.getFrames()));
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

    @Override
    public int getHighlight() {
        return showHighlight;
    }

    @Override
    protected void renderSprite(@Nonnull Graphics g, int x, int y, @Nonnull Color light,
                                @Nonnull TextureEffect... effects) {
        Color centerLight = parentTile.getLight();
        if ((topColor != null) && (leftColor != null) && (rightColor != null) && (bottomColor != null)) {
            g.drawTileSprite(getTemplate().getSprite(), x, y, topColor, bottomColor, leftColor, rightColor, centerLight,
                             getCurrentFrame(), effects);
            if (overlay != null) {
                g.drawTileSprite(overlay.getSprite(), x, y, topColor, bottomColor, leftColor, rightColor, centerLight,
                                 overlayShape, effects);
            }
        } else {
            g.drawTileSprite(getTemplate().getSprite(), x, y, centerLight, centerLight, centerLight, centerLight,
                             centerLight, getCurrentFrame(), effects);
            if (overlay != null) {
                g.drawTileSprite(overlay.getSprite(), x, y, centerLight, centerLight, centerLight, centerLight,
                                 centerLight, overlayShape, effects);
            }
        }
    }

    /**
     * Draw tile and its overlay
     *
     * @param graphics the graphics object that is used to render the tile.
     */
    @Override
    public void render(@Nonnull Graphics graphics) {
        if (performRendering()) {
            MapTile obstructingTile = parentTile.getObstructingTile();
            if ((obstructingTile != null) && obstructingTile.isOpaque()) {
                return;
            }

            super.render(graphics);
            showHighlight = 0;
        }
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        parentTile.updateColor(delta);
        if (tileLightEffect == null) {
            try {
                tileLightEffect = container.getEngine().getAssets().getEffectManager().getTileLightEffect(true);
            } catch (EngineException ignored) {
            }
        }
        if (parentTile.isHidden()) {
            setAlphaTarget(0);
            setFadingCorridorEffectEnabled(false);
        } else {
            setFadingCorridorEffectEnabled(true);
        }
        super.update(container, delta);

        if (parentTile.hasLightGradient()) {
            topColor = getCornerColor(topColor, EnumSet.of(Direction.North, Direction.NorthEast, Direction.East));
            leftColor = getCornerColor(leftColor, EnumSet.of(Direction.North, Direction.NorthWest, Direction.West));
            bottomColor = getCornerColor(bottomColor, EnumSet.of(Direction.West, Direction.SouthWest, Direction.South));
            rightColor = getCornerColor(rightColor, EnumSet.of(Direction.East, Direction.SouthEast, Direction.South));
        } else {
            topColor = null;
            leftColor = null;
            bottomColor = null;
            rightColor = null;
        }
    }

    @Override
    public boolean isEventProcessed(@Nonnull GameContainer container, int delta, @Nonnull SceneEvent event) {
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

            log.debug("Single click on tile at {}", parentTile.getCoordinates());

            TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
            handler.walkTo(parentTile.getCoordinates(), parentTile.isBlocked() ? 1 : 0);
            handler.assumeControl();
            return true;
        }

        if (event instanceof CurrentMouseLocationEvent) {
            CurrentMouseLocationEvent moveEvent = (CurrentMouseLocationEvent) event;
            if (!isMouseInInteractionRect(moveEvent.getX(), moveEvent.getY())) {
                return false;
            }
            World.getPlayer().getMovementHandler().getTargetMouseMovementHandler()
                 .walkTo(parentTile.getCoordinates(), parentTile.isBlocked() ? 1 : 0);

            if (!moveEvent.isHighlightHandled()) {
                showHighlight = 1;
                moveEvent.setHighlightHandled(true);
            }
            return true;
        }

        return false;
    }

    @Override
    public void hide() {
        super.hide();
        if (animation != null) {
            animation.removeTarget(this);
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
     * <p>
     * This implementation of the parent light fetches the light of the parent tile in order to ensure that the same
     * color value is used.
     */
    @Override
    @Nonnull
    protected Color getParentLight() {
        return parentTile.getLight();
    }

    @Nonnull
    private Color getCornerColor(@Nullable Color storage, @Nonnull Collection<Direction> sourceDirections) {
        Color usedStorage = storage;
        if (usedStorage == null) {
            usedStorage = new Color(Color.BLACK);
        }

        usedStorage.setColor(parentTile.getLight());
        for (Direction sourceDirection : sourceDirections) {
            Color directionLight = parentTile.getLight(sourceDirection);
            usedStorage.add((directionLight == null) ? parentTile.getLight() : directionLight);
        }
        usedStorage.multiply(1.f / (sourceDirections.size() + 1));
        return usedStorage;
    }
}
