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

import illarion.client.graphics.Camera;
import illarion.client.graphics.particle.Particle;
import illarion.client.graphics.particle.ParticleSystem;
import illarion.client.world.Game;
import illarion.client.world.Weather;

import illarion.common.util.FastMath;
import illarion.common.util.Range;

import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * This is a particle emitter used to display the rain in the game as overlay to
 * the map. It does not handle the rain volumic, just as overlay.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class WeatherRainEmitter2 extends AbstractParticleEmitter {
    /**
     * This is the alpha value of the rain color that shall be set regarding the
     * strength of the rain.
     */
    private static final Range COLOR_ALPHA = new Range(40, 220);

    /**
     * The minimal and maximal amount of drops spawned at one spawn time.
     */
    private static final Range DROP_COUNT = new Range(3, 150);

    /**
     * The maximal amount of drops spawned at one time.
     */
    private static final Range DROP_MAX_AMOUNT = new Range(50, 1000);

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
    private static final Range SPEED_HORZ = new Range(0, 10);

    /**
     * The chosen horizontal speed by {@link #SPEED_HORZ} is divided by this
     * value.
     */
    private static final float SPEED_HORZ_DIV = 100.f;

    /**
     * The minimal and the maximal falling speed of a water particle.
     */
    private static final Range SPEED_VERT = new Range(4, 30);

    /**
     * The chosen vertical speed by {@link SPEED_VERT} is divided by this value.
     */
    private static final float SPEED_VERT_DIV = 100.f;

    /**
     * The size of the splashes when a rain drop hits the ground.
     */
    private static final Range SPLASH_SIZE = new Range(1, 7);

    /**
     * The Sprite color instance that holds the color of the rain drops.
     */
    private final SpriteColor rainColor;

    /**
     * This flag stores if the rain is currently rendered or not.
     */
    private boolean renderRain;

    /**
     * The random value generator that is used for this rain emitter.
     */
    private final Random rnd;

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

    public WeatherRainEmitter2(final ParticleSystem parentSystem,
        final Weather weatherParent) {
        super(parentSystem);
        weather = weatherParent;
        renderRain = false;
        rainColor = Graphics.getInstance().getSpriteColor();
        timeToNextSpawn = 0;

        rnd = new Random(System.nanoTime());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * illarion.client.graphics.particle.AbstractParticleEmitter#render(illarion
     * .client.graphics.particle.Particle)
     */
    @Override
    public void render(final Particle renderParticle) {
        if (!renderRain) {
            return;
        }

        final Drawer drawer = Graphics.getInstance().getDrawer();

        final int posX = (int) renderParticle.getPosX();
        final int posY = (int) renderParticle.getPosY();

        final int partSpeedX = (int) renderParticle.getSpeedX();
        final int partSpeedY = (int) renderParticle.getSpeedY();

        final float width = renderParticle.getSize();

        final int targetX = posX + partSpeedX;
        final int targetY = posY + partSpeedY;

        drawer.drawLine(posX, posY, targetX, targetY, width, rainColor);

        // draw the splash in case it hits the ground
        if (renderParticle.getLifetime() == 0) {
            drawer.drawLine(posX, posY, posX + splashSize, posY + splashSize,
                width, rainColor);
            drawer.drawLine(posX, posY, posX - splashSize, posY + splashSize,
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

        // setup the color of the rain drops
        rainColor.set(Game.getWeather().getAmbientLight());
        rainColor.multiply(0.7f, 0.8f, 1.f);
        rainColor.setAlpha(COLOR_ALPHA.getInterpolated(percStrength));

        splashSize = SPLASH_SIZE.getInterpolated(percStrength);

        /*
         * now updating the values the emitter requires to update the partices
         * is done. Following are the required lines to create new particles
         * that are spawned.
         */

        if (timeToNextSpawn > delta) {
            timeToNextSpawn -= delta;
            return;
        }

        timeToNextSpawn += DROP_SPAWN.getInterpolated(percStrength);

        int spawnDrops =
            DROP_MAX_AMOUNT.getInterpolated(percStrength) - particleCount;
        spawnDrops =
            Math.min(spawnDrops, DROP_COUNT.getInterpolated(percStrength));
        spawnDrops = Math.max(0, spawnDrops);

        if (spawnDrops == 0) {
            return;
        }

        // calculate the initial and the target speed of the rain drops
        final int viewportWidth = Camera.getInstance().getViewportWidth();
        final int viewportHeight = Camera.getInstance().getViewportHeight();

        final int offsetX = Camera.getInstance().getViewportOffsetX();
        final int offsetY = Camera.getInstance().getViewportOffsetY();

        final float speedY =
            (SPEED_VERT.getInterpolated(percStrength) / SPEED_HORZ_DIV)
                * viewportHeight;

        final int dirMod = FastMath.sign(wind);
        final float speedX =
            (SPEED_HORZ.getInterpolated(FastMath.abs(wind)) / SPEED_VERT_DIV)
                * dirMod * viewportWidth;

        final float initialSize =
            DROP_SIZE.getInterpolated(percStrength) / DROP_SIZE_DIV;

        for (int i = 0; i < spawnDrops; ++i) {
            final int startX = rnd.nextInt(viewportWidth) + offsetX;
            final int targetY = rnd.nextInt(viewportHeight) + offsetY;

            final Particle addedPart = system.requestParticle();
            addedPart.setSize(initialSize);
            addedPart.setLocation(startX, viewportHeight + offsetY, 0);
            addedPart.setSpeed(speedX, speedY, 0);
            addedPart.insertIntoPool(Game.getDisplay()
                .getOverlayParticlePool());
            addedPart.setLifetime(targetY);
            addedPart.bind(this);
            addedPart.activate();
            particleCount++;
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
        if (!renderRain || (updateParticle.getLifetime() == 0)) {
            return false;
        }
        final float timeMod = delta / 50.f;

        final float lifetime = updateParticle.getLifetime();

        // current location
        final float posX = updateParticle.getPosX();
        final float posY = updateParticle.getPosY();

        final float partSpeedX = updateParticle.getSpeedX();
        final float partSpeedY = updateParticle.getSpeedY();

        float newPosX = posX - (partSpeedX * timeMod);
        float newPosY = posY - (partSpeedY * timeMod);

        if (newPosY < lifetime) {
            newPosY = lifetime;
            updateParticle.setLifetime(0);
        }

        final int viewportWidth = Camera.getInstance().getViewportWidth();
        final int offsetX = Camera.getInstance().getViewportOffsetX();

        if (newPosX < offsetX) {
            newPosX = viewportWidth + newPosX;
        } else if (newPosX > (viewportWidth + offsetX)) {
            newPosX = newPosX - viewportWidth;
        }

        updateParticle.setLocation(newPosX, newPosY, 0);

        return true;
    }

}
