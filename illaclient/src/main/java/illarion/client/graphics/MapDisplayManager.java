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
package illarion.client.graphics;

import illarion.client.IllaClient;
import illarion.client.input.CurrentMouseLocationEvent;
import illarion.client.world.World;
import illarion.client.world.characters.CharacterAttribute;
import illarion.common.types.DisplayCoordinate;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.effects.FogEffect;
import org.illarion.engine.graphic.effects.GrayScaleEffect;
import org.illarion.engine.input.Input;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nullable
    private DisplayCoordinate origin;

    /**
     * The scene the game is displayed in.
     */
    @Nonnull
    private final Scene gameScene;

    public MapDisplayManager(@Nonnull Engine engine) {
        active = false;

        corridor = FadingCorridor.getInstance();

        gameScene = engine.getAssets().createNewScene();
    }

    /**
     * Get the game scene that is managed by this display manager.
     *
     * @return the game scene
     */
    @Nonnull
    @Contract(pure = true)
    public Scene getGameScene() {
        return gameScene;
    }

    public int getWorldX(int x) {
        if (origin == null) {
            throw new IllegalStateException("Origin of the map display is not set.");
        }
        return (x - getMapCenterX()) + origin.getX();
    }

    public int getWorldY(int y) {
        if (origin == null) {
            throw new IllegalStateException("Origin of the map display is not set.");
        }
        return (y - getMapCenterY()) + origin.getY();
    }

    private static int getMapCenterX() {
        GameContainer window = IllaClient.getInstance().getContainer();
        return window.getWidth() >> 1;
    }

    private static int getMapCenterY() {
        GameContainer window = IllaClient.getInstance().getContainer();
        return window.getHeight() >> 1;
    }

    /**
     * Fix avatar's position in the middle of the screen and Z-Order
     *
     * @param av
     */
    public void glueAvatarToOrigin(@Nonnull Avatar av) {
        //av.setScreenPos(origin.getDcX() - dX, (origin.getDcY() - dY) + dL, origin.getDcZ());
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
    public void update(@Nonnull GameContainer container, int delta) {
        if (!active) {
            return;
        }
        if (origin == null) {
            throw new IllegalStateException("Can't update the display without a set origin.");
        }

        int centerX = container.getWidth() >> 1;
        int centerY = container.getHeight() >> 1;

        int offX = centerX - origin.getX();
        int offY = centerY - origin.getY();

        Avatar av = World.getPlayer().getCharacter().getAvatar();
        if (av != null) {
            glueAvatarToOrigin(av);
            corridor.setCorridor(av);
        }

        Camera.getInstance().setViewport(-offX, -offY, container.getWidth(), container.getHeight());

        Input engineInput = container.getEngine().getInput();
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
    private void updateDeadView(@Nonnull GameContainer c) {
        int hitPoints = World.getPlayer().getCharacter().getAttribute(CharacterAttribute.HitPoints);
        if (hitPoints == 0) {
            if (!deadViewEnabled) {
                try {
                    GrayScaleEffect effect = c.getEngine().getAssets().getEffectManager()
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
                    GrayScaleEffect effect = c.getEngine().getAssets().getEffectManager()
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
    private void updateFog(@Nonnull GameContainer c) {
        float fog = World.getWeather().getFog();
        if (fog > 0.f) {
            try {
                FogEffect effect = c.getEngine().getAssets().getEffectManager().getFogEffect(true);
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
                FogEffect effect = c.getEngine().getAssets().getEffectManager().getFogEffect(true);
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
    public void render(@Nonnull GameContainer c) {
        if (!active) {
            return;
        }

        Camera camera = Camera.getInstance();
        gameScene.render(c.getEngine().getGraphics(), camera.getViewportOffsetX(), camera.getViewportOffsetY());
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Move the map origin to a new location
     *
     * @param location
     */
    public void setLocation(@Nonnull DisplayCoordinate location) {
        origin = location;
    }

    @Override
    public void animationStarted() {
    }

    /**
     * Map movement is complete
     */
    @Override
    public void animationFinished(boolean finished) {
    }

    /**
     * Animation implementation. Does the same as {@link #setLocation}
     */
    @Override
    public void setPosition(@Nonnull DisplayCoordinate position) {
        setLocation(position);
    }
}
