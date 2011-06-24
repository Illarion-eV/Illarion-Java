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

import javolution.context.ObjectFactory;

/**
 * This particle factory is used to create and store the instances of the
 * particles that are used in the particle system.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class ParticleFactory extends ObjectFactory<Particle> {
    /**
     * The particle system that is the parent of this factory. All particles
     * managed by this factory refer on this system.
     */
    private final ParticleSystem parentSystem;

    /**
     * Create a instance of this particle factory.
     * 
     * @param parent the particle system that uses this factory.
     */
    public ParticleFactory(final ParticleSystem parent) {
        assert (parent != null) : "Parent must not be null"; //$NON-NLS-1$
        parentSystem = parent;
    }

    /**
     * Create a new instance of the particle.
     */
    @Override
    protected Particle create() {
        return new Particle(parentSystem);
    }
}
