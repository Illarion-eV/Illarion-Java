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
package illarion.client.graphics.particle;

import illarion.client.graphics.particle.emitter.AbstractParticleEmitter;

/**
 * This class represents one particle. It does nothing itself but saving the
 * current state of the particle so the emitter and the particle system can
 * handle this particle correctly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Particle {
    /**
     * The emitter that is controlling this particle.
     */
    private transient AbstractParticleEmitter emitter;

    /**
     * The current lifetime of the particle.
     */
    private int lifetime;

    /**
     * The pool this particle is a member of.
     */
    private transient ParticlePool pool;

    /**
     * The X coordinate of the position of the particle in the game.
     */
    private float posX;

    /**
     * The Y coordinate of the position of the particle in the game.
     */
    private float posY;

    /**
     * The Z coordinate of the position of the particle in the game.
     */
    private float posZ;

    /**
     * The size of the particle.
     */
    private float size;

    /**
     * The current speed of the particle along the X-axis on the game map.
     */
    private float speedX;

    /**
     * The current speed of the particle along the Y-axis on the game map.
     */
    private float speedY;

    /**
     * The current speed of the particle along the Z-axis on the game map.
     */
    private float speedZ;

    /**
     * The Particle System that is the owner of this Particle.
     */
    private transient final ParticleSystem system;

    /**
     * Default constructor. Create a particle at position 0 0 0 that does not
     * move.
     * 
     * @param parentSystem the ParticleSystem that owns this particle
     */
    public Particle(final ParticleSystem parentSystem) {
        posX = 0;
        posY = 0;
        posZ = 0;

        speedX = 0;
        speedY = 0;
        speedZ = 0;

        size = 0;
        lifetime = 0;

        system = parentSystem;
        emitter = null;
        pool = null;
    }

    public void activate() {
        if (emitter == null) {
            throw new IllegalStateException(
                "Can't activate particle without emitter.");
        }
        system.activateParticle(this);
    }

    /**
     * Bind the particle to a emitter. This emitter has to handle the actions of
     * this particle.
     * 
     * @param handler the particle emitter that takes care for this particle
     */
    public void bind(final AbstractParticleEmitter handler) {
        emitter = handler;
    }

    /**
     * Similar to kill, it just does not notify the particle system about the
     * reset of the data.
     */
    public void clean() {
        posX = 0;
        posY = 0;
        posZ = 0;

        speedX = 0;
        speedY = 0;
        speedZ = 0;

        size = 0;
        lifetime = 0;

        if (emitter != null) {
            emitter.particleDied();
            emitter = null;
        }

        if (pool != null) {
            pool.removeParticleFromPool(this);
        }
        pool = null;
    }

    public int getLifetime() {
        return lifetime;
    }

    public ParticlePool getPool() {
        return pool;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getPosZ() {
        return posZ;
    }

    public float getSize() {
        return size;
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public float getSpeedZ() {
        return speedZ;
    }

    /**
     * Insert the particle into a particle pool that triggers the rendering
     * operations.
     * 
     * @param newPool the pool this particle is bind to
     */
    public void insertIntoPool(final ParticlePool newPool) {
        if (pool != null) {
            if (pool.equals(newPool)) {
                return;
            }
            pool.removeParticleFromPool(this);
        }

        pool = newPool;
        newPool.addParticleToPool(this);
    }

    /**
     * Kill a particle object, so reset all values and place it back in the
     * particle system storage for later usage.
     */
    public void kill() {
        clean();
    }

    public void reduceLifetime(final int reduceTime) {
        lifetime -= reduceTime;
    }

    /**
     * Render this particle by using the particle emitter.
     */
    public void render() {
        final AbstractParticleEmitter emitterCopy = emitter;
        if (emitterCopy != null) {
            emitterCopy.render(this);
        }
    }

    public void setLifetime(final int newLifetime) {
        lifetime = newLifetime;
    }

    public void setLocation(final float newPosX, final float newPosY,
        final float newPosZ) {
        posX = newPosX;
        posY = newPosY;
        posZ = newPosZ;
    }

    public void setSize(final float size) {
        if (size > 0) {
            this.size = size;
        }
    }

    public void setSpeed(final float newSpeedX, final float newSpeedY,
        final float newSpeedZ) {
        speedX = newSpeedX;
        speedY = newSpeedY;
        speedZ = newSpeedZ;
    }

    /**
     * Update this particle. Update all values of this particle regarding the
     * delta time since the last update.
     * 
     * @param delta the time in ms since the last update
     * @return <code>true</code> of the particle shall survive, else
     *         <code>false</code>
     */
    protected boolean update(final int delta) {
        final AbstractParticleEmitter emitterCopy = emitter;
        return (emitterCopy != null)
            && emitterCopy.updateParticle(this, delta);
    }
}
