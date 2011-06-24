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

/**
 * Particle Pools are the main handlers for rendering operations of the
 * particles. This pooles ensure that the particles are rendered at the needed
 * locations at the correct time.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public interface ParticlePool {
    /**
     * Add a particle to this pool. All particles are rendered at the very same
     * time.
     * 
     * @param addPart the particle that shall be added to this particle pool
     */
    void addParticleToPool(Particle addPart);

    /**
     * Clearing the pool means no particles are drawn anymore from this pool and
     * the particles go back to the Particle System.
     */
    void clearPool();

    /**
     * Remove a particle from this pool. This needs to be done in case a
     * particle changes to pool as it happens when particles on the map move
     * across tiles. Also this needs to be done in case a particle dies and a
     * new one needs to be set up.
     * 
     * @param remPart the particle that shall be removed from this pool
     */
    void removeParticleFromPool(Particle remPart);
}
