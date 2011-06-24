/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics.particle.emitter;

import java.util.Random;

import illarion.client.graphics.EffectPool;
import illarion.client.graphics.particle.Particle;
import illarion.client.graphics.particle.ParticleSystem;
import illarion.client.world.Game;
import illarion.client.world.GameMap;
import illarion.client.world.MapTile;
import illarion.client.world.Weather;

import illarion.common.util.FastMath;
import illarion.common.util.Location;
import illarion.common.util.Range;
import illarion.common.util.Rectangle;

import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * Emitter for the rain particles that are needed to show the rain on the map.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class WeatherRainEmitter extends AbstractParticleEmitter {
    /**
     * The minimal and maximal amount of drops spawned at one spawn time.
     */
    private static final Range DROP_COUNT = new Range(3, 150);

    /**
     * The maximal amount of drops spawned at one time.
     */
    private static final Range DROP_MAX_AMOUNT = new Range(50, 2000);

    /**
     * The minimal and the maximal size of the falling water particles.
     */
    private static final Range DROP_SIZE = new Range(8, 15);

    /**
     * The chosen drop size devided with this value in order to get the real
     * value that is needed.
     */
    private static final float DROP_SIZE_DIV = 10.f;

    /**
     * The minimal and maximal time between two spawns.
     */
    private static final Range DROP_SPAWN = new Range(120, 5);

    /**
     * The minimal and the maximal speed in x-y direction.
     */
    private static final Range SPEED_HORZ = new Range(0, 60);

    /**
     * The chosen horizontal speed by {@link #SPEED_HORZ} is divided by this
     * value.
     */
    private static final float SPEED_HORZ_DIV = 100.f;

    /**
     * The minimal and the maximal falling speed of a water particle.
     */
    private static final Range SPEED_VERT = new Range(50, 190);

    /**
     * The chosen vertical speed by {@link SPEED_VERT} is divided by this value.
     */
    private static final float SPEED_VERT_DIV = 100.f;

    /**
     * The size of the splashes when a rain drop hits the ground.
     */
    private static final Range SPLASH_SIZE = new Range(1, 7);

    /**
     * The tile level border above the player that was calculated for the last
     * player location and is used to poll the drops.
     */
    private int borderAbove;

    /**
     * The tile level border below the player that was calculated for the last
     * player location and is used to poll the drops.
     */
    private int borderBelow;

    /**
     * The tile row border at the bottom side that was calculated for the last
     * player location and is used to poll the drops.
     */
    private int borderBtm;

    /**
     * The tile column border at the left side that was calculated for the last
     * player location and is used to poll the drops.
     */
    private int borderLeft;

    /**
     * The tile column border at the right side that was calculated for the last
     * player location and is used to poll the drops.
     */
    private int borderRight;

    /**
     * The tile row border at the top side that was calculated for the last
     * player location and is used to poll the drops.
     */
    private int borderTop;

    /**
     * The drawer instance that is used to drawn the water drops of the rain.
     */
    private final Drawer drawer = Graphics.getInstance().getDrawer();

    /**
     * Size of a particle in the moment of its creation.
     */
    private float initialSize = 1.f;

    /**
     * The game map that is used to select the tiles the rain is falling on.
     */
    private final GameMap map = Game.getMap();

    /**
     * The Sprite color instance that holds the color of the rain drops.
     */
    private final SpriteColor rainColor = Graphics.getInstance()
        .getSpriteColor();

    /**
     * This value has to be set to true in order to ensure that the rain is
     * calculated and drawn. Setting it to false will disable all activities of
     * this Emitter.
     */
    private boolean renderRain = true;

    /**
     * The random value generator that is used for this rain emitter.
     */
    private final Random rnd = new Random();

    /**
     * The current target speed of one drop in x direction. This is also the
     * initial speed of a particle.
     */
    private float speedX;

    /**
     * The current target speed of one drop in y direction. This is also the
     * initial speed of a particle.
     */
    private float speedY;

    /**
     * The current target speed of one drop in z direction. This is also the
     * initial speed of a particle.
     */
    private float speedZ;

    /**
     * The current calculated value for the splash size that shall be rendered.
     */
    private int splashSize;

    /**
     * The time in ms until the next spawn of the drops.
     */
    private int timeToNextSpawn;

    /**
     * Reference to the weather handler that is used to get the weather
     * informations that effect the particles.
     */
    private final Weather weather;

    /**
     * Constructor for the Rain Emitter of the weather system.
     * 
     * @param parentSystem The particle system that is the parent of this
     *            particle emitter and supplies the needed particles
     * @param weatherParent the weather handler that started this emitter
     */
    public WeatherRainEmitter(final ParticleSystem parentSystem,
        final Weather weatherParent) {
        super(parentSystem);
        weather = weatherParent;
        rnd.setSeed(System.nanoTime());
    }

    /**
     * Render the rain particles that are handles by this emitter. All particles
     * are drawn as a string from their last location to the new one.
     * 
     * @param renderParticle the particle that shall be rendered
     */
    @Override
    public void render(final Particle renderParticle) {
        if (!renderRain) {
            return;
        }
        final float posX = renderParticle.getPosX();
        final float posY = renderParticle.getPosY();
        final float posZ = renderParticle.getPosZ();

        final float partSpeedX = renderParticle.getSpeedX();
        final float partSpeedY = renderParticle.getSpeedY();
        final float partSpeedZ = renderParticle.getSpeedZ();

        final int dcX1 = Location.displayCoordinateX(posX, posY, posZ);
        final int dcY1 = Location.displayCoordinateY(posX, posY, posZ);

        final int dcX2 =
            Location.displayCoordinateX(posX + partSpeedX, posY + partSpeedY,
                posZ + partSpeedZ);
        final int dcY2 =
            Location.displayCoordinateY(posX + partSpeedX, posY + partSpeedY,
                posZ + partSpeedZ);

        final float width = renderParticle.getSize();

        // if ((dcX1 > MapDisplayManager.MAP_WIDTH)
        // || (dcY1 < ClientWindow.SCREEN_HEIGHT
        // - MapDisplayManager.MAP_HEIGHT)) {
        // return;
        // }
        //
        // if ((dcX2 > MapDisplayManager.MAP_WIDTH)
        // || (dcY2 < ClientWindow.SCREEN_HEIGHT
        // - MapDisplayManager.MAP_HEIGHT)) {
        // return;
        // }

        drawer.drawLine(dcX1, dcY1, dcX2, dcY2, width, rainColor);

        // draw the splash in case it hits the ground
        if (renderParticle.getLifetime() == 0) {
            drawer.drawLine(dcX1, dcY1, dcX1 + splashSize, dcY1 + splashSize,
                width, rainColor);
            drawer.drawLine(dcX1, dcY1, dcX1 - splashSize, dcY1 + splashSize,
                width, rainColor);
        }
    }

    /**
     * Update the emitter. This makes the emitter preparing all values needed
     * for the particle update so this works very fast and it takes care for
     * spawning new particles on the map that fall down to the ground.
     * 
     * @param delta the time since the last update of this emitter
     */
    @Override
    public void updateEmitter(final int delta) {
        if (!weather.isOutside()) {
            renderRain = false;
            return;
        }
        renderRain = true;

        final int percStrength = weather.getPrecStrength();
        final int percType = weather.getPrecType();
        final int wind = weather.getWind();

        if ((percStrength <= 0) || (percType != Weather.RAIN)) {
            renderRain = false;
            return;
        }

        // set the location aboth the player.
        final Location playerLoc = Game.getPlayer().getLocation();
        setLocation(playerLoc.getScX(), playerLoc.getScY(), playerLoc.getScZ());

        // calculate the initial and the target speed of the rain drops
        speedZ = SPEED_VERT.getInterpolated(percStrength) / SPEED_HORZ_DIV;

        final int dirMod = (int) Math.signum(wind);
        speedX =
            SPEED_HORZ.getInterpolated(FastMath.abs(wind)) / SPEED_VERT_DIV;
        speedX *= dirMod;
        speedY = speedX;

        // setup the color of the rain drops
        rainColor.set(Game.getWeather().getAmbientLight());
        rainColor.multiply(0.8f, 0.8f, 1.f);
        rainColor.setAlpha(0.3f);

        splashSize = SPLASH_SIZE.getInterpolated(percStrength);

        initialSize = DROP_SIZE.getInterpolated(percStrength) / DROP_SIZE_DIV;

        final Rectangle clipping = Game.getMap().getClipping();
        borderLeft = playerLoc.getCol() + clipping.getLeft();
        borderRight = playerLoc.getCol() + clipping.getRight();
        borderTop = playerLoc.getRow() + clipping.getTop();
        borderBtm = playerLoc.getRow() + clipping.getBottom();
        borderAbove = playerLoc.getScZ() + 2;
        borderBelow = playerLoc.getScZ() - 2;

        MapTile tile = null;

        int spawnDrops =
            DROP_MAX_AMOUNT.getInterpolated(percStrength) - particleCount;
        spawnDrops =
            Math.min(spawnDrops, DROP_COUNT.getInterpolated(percStrength));
        spawnDrops = Math.max(0, spawnDrops);

        if (spawnDrops == 0) {
            return;
        }

        final Location loc = Location.getInstance();
        while (true) {
            if (timeToNextSpawn > delta) {
                timeToNextSpawn -= delta;
                loc.recycle();
                return;
            }
            timeToNextSpawn += DROP_SPAWN.getInterpolated(percStrength);

            for (int i = 0; i < spawnDrops; ++i) {
                final int col =
                    rnd.nextInt(borderRight - borderLeft) + borderLeft;
                final int row = rnd.nextInt(borderTop - borderBtm) + borderBtm;

                loc.setMC(col, row);
                for (int z = borderAbove; z >= borderBelow; --z) {
                    loc.setSC(loc.getScX(), loc.getScY(), z);
                    tile = map.getMapAt(loc);
                    if (tile != null) {
                        final Particle addedPart = system.requestParticle();
                        addedPart.setSpeed(speedX, speedY, speedZ);
                        addedPart.setLocation(
                            loc.getScX()
                                + (((float) rnd.nextGaussian() * 2) - 1),
                            loc.getScY()
                                + (((float) rnd.nextGaussian() * 2) - 1),
                            loc.getScZ() + ((borderTop - row) * 0.4f) + 1);
                        addedPart.bind(this);
                        addedPart.insertIntoPool(tile.getParticlePool());
                        addedPart.setSize(initialSize);
                        addedPart.setLifetime(1);
                        addedPart.activate();
                        particleCount++;
                        break;
                    }
                }
            }
            loc.recycle();
        }
    }

    /**
     * Move a rain drop along its path and render it on the next location. In
     * case it hits the ground the particle needs to be removed.
     * 
     * @param updateParticle the particle that needs to be updated
     * @param delta the time since the last update
     * @return <code>true</code> in case the particle stays alive,
     *         <code>false</code> if not
     */
    @Override
    public boolean updateParticle(final Particle updateParticle,
        final int delta) {
        if (!renderRain) {
            return false;
        }
        final float timeMod = delta / 50.f;

        // current location
        final float posX = updateParticle.getPosX();
        final float posY = updateParticle.getPosY();
        final float posZ = updateParticle.getPosZ();

        final EffectPool partPool = (EffectPool) updateParticle.getPool();
        final Location poolLocation = partPool.getParent().getLocation();

        if (posZ <= poolLocation.getScZ()) {
            return false;
        }

        // current speed
        final float partSpeedX = updateParticle.getSpeedX();
        final float partSpeedY = updateParticle.getSpeedY();
        final float partSpeedZ = updateParticle.getSpeedZ();

        // new speed
        final float newSpeedX = partSpeedX + ((speedX - partSpeedX) / 2);
        final float newSpeedY = partSpeedY + ((speedY - partSpeedY) / 2);
        final float newSpeedZ = partSpeedZ + ((speedZ - partSpeedZ) / 2);

        // new location
        final float newPosX = posX - (newSpeedX * timeMod);
        final float newPosY = posY - (newSpeedY * timeMod);
        final float newPosZ =
            Math.max(posZ - (newSpeedZ * timeMod),
                poolLocation.getScZ() - 0.01f);

        if (newPosZ < poolLocation.getScZ()) {
            updateParticle.setLifetime(0);
        }

        // moving the particle along the path by the speed
        updateParticle.setLocation(newPosX, newPosY, newPosZ);
        updateParticle.setSpeed(newSpeedX, newSpeedY, newSpeedZ);

        final Location loc = Location.getInstance();
        // check the map tile pool and change it in case its needed.
        loc.setSC(FastMath.round(newPosX), FastMath.round(newPosY),
            FastMath.round(newPosZ));
        if ((poolLocation.getScX() != loc.getScX())
            || (poolLocation.getScY() != loc.getScY())) {
            if (loc.getCol() < borderLeft) {
                loc.setMC((loc.getCol() + borderRight) - borderLeft,
                    loc.getRow());
            } else if (loc.getCol() > borderRight) {
                loc.setMC(loc.getCol() - borderRight - borderLeft,
                    loc.getRow());
            }

            if (loc.getRow() < borderBtm) {
                loc.setMC(loc.getCol(), (loc.getRow() + borderTop) - borderBtm);
            } else if (loc.getRow() > borderTop) {
                loc.setMC(loc.getCol(), loc.getRow() - borderTop - borderBtm);
            }

            updateParticle.setLocation(loc.getScX() + (newPosX % 1),
                loc.getScY() + (newPosY % 1), newPosZ);

            MapTile tile = null;
            boolean foundNewTile = false;
            for (int z = borderAbove; z >= borderBelow; --z) {
                loc.setSC(loc.getScX(), loc.getScY(), z);
                tile = map.getMapAt(loc);
                if (tile != null) {
                    updateParticle.insertIntoPool(tile.getParticlePool());
                    foundNewTile = true;
                    break;
                }
            }

            if (!foundNewTile) {
                loc.recycle();
                return false;
            }
        }
        loc.recycle();
        return true;
    }

}
