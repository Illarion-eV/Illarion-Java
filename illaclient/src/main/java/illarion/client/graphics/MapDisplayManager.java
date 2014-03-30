/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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

import illarion.client.IllaClient;
import illarion.client.input.CurrentMouseLocationEvent;
import illarion.client.world.World;
import illarion.client.world.characters.CharacterAttribute;
import illarion.common.types.Location;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.effects.FogEffect;
import org.illarion.engine.graphic.effects.GrayScaleEffect;
import org.illarion.engine.input.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * The map display manager stores and manages all objects displayed on the map. It takes care for rendering the objects
 * in the proper order, for animations of the entire map and it manages the current location of the avatar.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class MapDisplayManager implements AnimatedMove {

    /**
     * Offset of the tiles due the perspective of the map view.
     */
    public static final int TILE_PERSPECTIVE_OFFSET = 3;

    private boolean active;

    @Nonnull
    private final FadingCorridor corridor;

    private int dL;
    private int dX;
    private int dY;

    private int elevation;

    @Nonnull
    private final Location origin;

    private static final Logger LOGGER = LoggerFactory.getLogger(MapDisplayManager.class);

    /**
     * The scene the game is displayed in.
     */
    @Nonnull
    private final Scene gameScene;

    public MapDisplayManager(@Nonnull final Engine engine) {
        active = false;

        corridor = FadingCorridor.getInstance();
        origin = new Location();

        gameScene = engine.getAssets().createNewScene();

        dX = 0;
        dY = 0;
        elevation = 0;
        dL = 0;
    }

    /**
     * Get the game scene that is managed by this display manager.
     *
     * @return the game scene
     */
    @Nonnull
    public Scene getGameScene() {
        return gameScene;
    }

    public void updateElevation() {
        if (!moveAnimationInProgress) {
            setLocation(World.getPlayer().getLocation());
        }
    }

    private boolean moveAnimationInProgress;

    @Override
    public void animationStarted() {
        moveAnimationInProgress = true;
    }

    /**
     * Map movement is complete
     *
     * @param ok
     */
    @Override
    public void animationFinished(final boolean ok) {
        moveAnimationInProgress = false;
        // move graphical player position to new location
        setLocation(World.getPlayer().getLocation());

        // remove surplus tiles from the map
        // Game.getMap().clipMap();
    }

    public int getElevation() {
        return elevation;
    }

    public int getWorldX(final int x) {
        return ((x - getMapCenterX()) + origin.getDcX()) - dX;
    }

    public int getWorldY(final int y) {
        return ((y - getMapCenterY()) + origin.getDcY()) - dY;
    }

    private static int getMapCenterX() {
        final GameContainer window = IllaClient.getInstance().getContainer();
        return window.getWidth() >> 1;
    }

    private static int getMapCenterY() {
        final GameContainer window = IllaClient.getInstance().getContainer();
        return window.getHeight() >> 1;
    }

    /**
     * Fix avatar's position in the middle of the screen and Z-Order
     *
     * @param av
     */
    public void glueAvatarToOrigin(@Nonnull final Avatar av) {
        av.setScreenPos(origin.getDcX() - dX, (origin.getDcY() - dY) + dL, origin.getDcZ());
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Update the display entries.
     *
     * @param container the container that holds the game
     * @param delta the time in milliseconds since the last update
     */
    public void update(@Nonnull final GameContainer container, final int delta) {
        if (!active) {
            return;
        }

        final int centerX = container.getWidth() >> 1;
        final int centerY = container.getHeight() >> 1;

        final int offX = (centerX - origin.getDcX()) + dX;
        final int offY = (centerY - origin.getDcY()) + dY - dL;

        final Avatar av = World.getPlayer().getCharacter().getAvatar();
        if (av != null) {
            glueAvatarToOrigin(av);
            corridor.setCorridor(av);
        }

        Camera.getInstance().setViewport(-offX, -offY, container.getWidth(), container.getHeight());

        final Input engineInput = container.getEngine().getInput();
        gameScene.publishEvent(new CurrentMouseLocationEvent(engineInput.getMouseX(), engineInput.getMouseY()));
        gameScene.update(container, delta);
        updateFog(container);
        updateDeadView(container);
    }

    /**
     * This flag stores if the fog effect was already applied to the scene.
     */
    private boolean fogEnabled;

    /**
     * This flag stores if the gray scale filter that is applied in case the character is dead was already enabled.
     */
    private boolean deadViewEnabled;

    /**
     * Update the graphical effects applied in case the character died.
     *
     * @param c the game container
     */
    private void updateDeadView(@Nonnull final GameContainer c) {
        final int hitPoints = World.getPlayer().getCharacter().getAttribute(CharacterAttribute.HitPoints);
        if (hitPoints == 0) {
            if (!deadViewEnabled) {
                try {
                    final GrayScaleEffect effect = c.getEngine().getAssets().getEffectManager()
                            .getGrayScaleEffect(true);
                    gameScene.addEffect(effect);
                    deadViewEnabled = true;
                } catch (EngineException e) {
                    // error activating gray scale
                }
            }
        } else {
            if (deadViewEnabled) {
                try {
                    final GrayScaleEffect effect = c.getEngine().getAssets().getEffectManager()
                            .getGrayScaleEffect(true);
                    gameScene.removeEffect(effect);
                    deadViewEnabled = false;
                } catch (EngineException e) {
                    // error activating gray scale
                }
            }
        }
    }

    /**
     * Update the graphical effect that shows the fog on the map.
     *
     * @param c the game container
     */
    private void updateFog(@Nonnull final GameContainer c) {
        final float fog = World.getWeather().getFog();
        if (fog > 0.f) {
            try {
                final FogEffect effect = c.getEngine().getAssets().getEffectManager().getFogEffect(true);
                effect.setDensity(fog);
                if (!fogEnabled) {
                    gameScene.addEffect(effect);
                    fogEnabled = true;
                }
            } catch (EngineException e) {
                // error activating fog
            }
        } else if (fogEnabled) {
            try {
                final FogEffect effect = c.getEngine().getAssets().getEffectManager().getFogEffect(true);
                gameScene.removeEffect(effect);
                fogEnabled = false;
            } catch (EngineException e) {
                // error activating fog
            }
        }
    }

    /**
     * Render all visible map items
     *
     * @param c the game container the map is rendered in
     */
    public void render(@Nonnull final GameContainer c) {
        if (!active) {
            return;
        }

        final Camera camera = Camera.getInstance();
        gameScene.render(c.getEngine().getGraphics(), camera.getViewportOffsetX(), camera.getViewportOffsetY());
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * Move the map origin to a new location
     *
     * @param location
     */
    public void setLocation(@Nonnull final Location location) {
        origin.set(location);
        elevation = World.getMap().getElevationAt(origin);
        dX = 0;
        dY = 0;
        dL = -elevation;

        World.getMap().getMiniMap().setPlayerLocation(location);
    }

    /**
     * Scroll map
     *
     * @param x
     * @param y
     */
    @Override
    public void setPosition(final int x, final int y, final int z) {
        if ((dX == x) && (dY == y) && (dL == z)) {
            return;
        }

        dX = x;
        dY = y;
        dL = z;
    }
}
