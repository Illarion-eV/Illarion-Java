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

import illarion.client.IllaClient;
import illarion.client.world.World;
import illarion.common.graphics.Layers;
import illarion.common.graphics.MapConstants;
import illarion.common.types.Location;
import org.apache.log4j.Logger;
import org.illarion.engine.Engine;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.effects.FogEffect;

import javax.annotation.Nonnull;

/**
 * The map display manager stores and manages all objects displayed on the map. It takes care for rendering the objects
 * in the proper order, for animations of the entire map and it manages the current location of the avatar.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class MapDisplayManager
        implements AnimatedMove {

    /**
     * Offset of the tiles due the perspective of the map view.
     */
    public static final int TILE_PERSPECTIVE_OFFSET = 3;

    private boolean active;

    // scrolling offset
    @Nonnull
    private final MoveAnimation ani;

    @Nonnull
    private final FadingCorridor corridor;

    private int dL;
    private int dX;
    private int dY;

    private int elevation;

    @Nonnull
    private final MoveAnimation levelAni;

    @Nonnull
    private final Location origin;

    private static final Logger LOGGER = Logger.getLogger(MapDisplayManager.class);

    /**
     * The scene the game is displayed in.
     */
    @Nonnull
    private final Scene gameScene;

    public MapDisplayManager(@Nonnull final Engine engine) {
        active = false;
        ani = new MoveAnimation(this);

        corridor = FadingCorridor.getInstance();
        origin = new Location();

        gameScene = engine.getAssets().createNewScene();

        dX = 0;
        dY = 0;

        levelAni = new MoveAnimation(new AnimatedMove() {
            @Override
            public void animationFinished(final boolean ok) {
                // nothing to do here
            }

            @Override
            public void setPosition(final int x, final int y, final int z) {
                dL = y;
            }
        });
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

    /**
     * Animate the movement of the game map
     *
     * @param dir
     * @param speed
     * @param run
     */
    public void animate(final int dir, final int speed, final boolean run) {
        // remember move dir and
        int mod = 1;
        if (run) {
            mod = 2;
        }
        switch (dir) {
            case Location.DIR_NORTH:
                // animate map
                ani.start(0, 0, MapConstants.STEP_X * mod, -MapConstants.STEP_Y * mod, speed);
                break;
            case Location.DIR_NORTHEAST:
                // animate map
                ani.start(0, 0, 0, -MapConstants.TILE_H * mod, speed);
                break;
            case Location.DIR_EAST:
                // animate map
                ani.start(0, 0, -MapConstants.STEP_X * mod, -MapConstants.STEP_Y * mod, speed);
                break;
            case Location.DIR_SOUTHEAST:
                // animate map
                ani.start(0, 0, -MapConstants.TILE_W * mod, 0, speed);
                break;
            case Location.DIR_SOUTH:
                // animate map
                ani.start(0, 0, -MapConstants.STEP_X * mod, MapConstants.STEP_Y * mod, speed);
                break;
            case Location.DIR_SOUTHWEST:
                // animate map
                ani.start(0, 0, 0, MapConstants.TILE_H * mod, speed);
                break;
            case Location.DIR_WEST:
                // animate map
                ani.start(0, 0, MapConstants.STEP_X * mod, MapConstants.STEP_Y * mod, speed);
                break;
            case Location.DIR_NORTHWEST:
                // animate map
                ani.start(0, 0, MapConstants.TILE_W, 0, speed);
                break;
            default:
                animationFinished(false);

        }
        // start separate Elevation animation
        final int fromElevation = elevation;
        elevation = World.getMap().getElevationAt(World.getPlayer().getLocation());
        if (elevation != fromElevation) {
            levelAni.start(0, -fromElevation, 0, -elevation, speed);
        }

        // adjust Z-order after update
        final Avatar playerAvatar = World.getPlayer().getCharacter().getAvatar();
        if (playerAvatar != null) {
            gameScene.updateElementLocation(playerAvatar);
        }
    }

    /**
     * Map movement is complete
     *
     * @param ok
     */
    @Override
    public void animationFinished(final boolean ok) {
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
        av.setScreenPos(origin.getDcX() - dX, (origin.getDcY() - dY) + dL, origin.getDcZ(), Layers.CHARS);
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Update the display entries.
     *
     * @param container the container that holds the game
     * @param delta     the time in milliseconds since the last update
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

        gameScene.update(container, delta);
    }

    private boolean fogEnabled;

    /**
     * Render all visible map items
     *
     * @param c the game container the map is rendered in
     */
    public void render(@Nonnull final GameContainer c) {
        if (!active) {
            return;
        }

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

        final Camera camera = Camera.getInstance();
        gameScene.render(c.getEngine().getGraphics(), -camera.getViewportOffsetX(), -camera.getViewportOffsetY());
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
        // origin.setSC(location.scX, location.scY, 0);
        origin.set(location);
        ani.stop();
        levelAni.stop();
        elevation = World.getMap().getElevationAt(origin);
        dX = 0;
        dY = 0;
        dL = -elevation;
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
