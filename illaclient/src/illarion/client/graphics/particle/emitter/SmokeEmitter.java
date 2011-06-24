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

import illarion.common.util.Location;

/**
 * The main use of this emitter is to produce smoke that raises up from a spot
 * and moves regarding to the wind in the game environment.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class SmokeEmitter extends AbstractParticleEmitter {
    /**
     * The location of the emitter.
     */
    private final Location emitterPos;

    /**
     * Create a smoke particle emitter. Calculate and render smoke that raises
     * up from a single spot. The location object is copied for the internal
     * storage.
     * 
     * @param parentSystem the particle system that supplies this emitter with
     *            particles and triggers the updates
     * @param loc the location on the map where the particles are emitted
     */
    public SmokeEmitter(final ParticleSystem parentSystem, final Location loc) {
        super(parentSystem);
        emitterPos = new Location(loc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * illarion.client.graphics.particle.emitter.AbstractParticleEmitter#render
     * (illarion.client.graphics.particle.Particle)
     */
    @Override
    public void render(final Particle renderParticle) {
        // TODO Auto-generated method stub

    }

    /**
     * Set the new position of the emitter. To save this information a copy of
     * the location object is used for internal storage.
     * 
     * @param loc the new location of the emitter
     */
    public void setLocation(final Location loc) {
        emitterPos.set(loc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see illarion.client.graphics.particle.emitter.AbstractParticleEmitter#
     * updateEmitter(int)
     */
    @Override
    public void updateEmitter(final int delta) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see illarion.client.graphics.particle.emitter.AbstractParticleEmitter#
     * updateParticle(illarion.client.graphics.particle.Particle, int)
     */
    @Override
    public boolean updateParticle(final Particle updateParticle,
        final int delta) {
        // TODO Auto-generated method stub
        return false;
    }

}
