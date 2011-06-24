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

import illarion.client.graphics.particle.Particle;
import illarion.client.graphics.particle.ParticleSystem;

/**
 * A Particle Emitter controls and creates the particles that have a reference
 * to this Emitter.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public abstract class AbstractParticleEmitter {
    /**
     * Amount of particles handled by this Emitter.
     */
    protected int particleCount;

    /**
     * The particle system that is the parent of this emitter and the source of
     * the particles this emitter sends out.
     */
    protected final ParticleSystem system;

    /**
     * Default constructor for a particle emitter.
     * 
     * @param parentSystem the parten Particle System that supplies the
     *            particles this emitter sends out
     */
    protected AbstractParticleEmitter(final ParticleSystem parentSystem) {
        particleCount = 0;

        system = parentSystem;
        parentSystem.addEmitter(this);
    }

    public void particleDied() {
        particleCount--;
    }

    public abstract void render(final Particle renderParticle);

    /**
     * Update the Emitter. Sending out more particles is done by this.
     */
    public abstract void updateEmitter(final int delta);

    /**
     * Update a particle. Updating the location of the particle is done by this.
     * 
     * @param updateParticle the particle that needs to be updated
     * @param delta the time in ms since the last update
     * @return true in case the particle stays alive, false if not
     */
    public abstract boolean updateParticle(final Particle updateParticle,
        final int delta);

    protected void setLocation(final float x, final float y, final float z) {
    }
}
