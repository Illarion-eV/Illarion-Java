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

import java.util.List;

import javolution.context.ConcurrentContext;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.client.graphics.particle.emitter.AbstractParticleEmitter;

/**
 * The ParticleSystem causes the general updates of all particles and manages
 * the currently existing emitters.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ParticleSystem {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(ParticleSystem.class);

    /**
     * The particle factory that is used to create and store the particles.
     */
    private final ParticleFactory particleFactory;

    /**
     * The list of particles that are currently in use. When updating all this
     * particles need to be updated.
     */
    private final FastTable<Particle> particleInUse;

    /**
     * The list of emitters that are currently working.
     */
    private final List<AbstractParticleEmitter> workingEmitters;

    /**
     * The constructor of the ParticleSystem. This constructs a whole new
     * ParticleSystem that handles Emitters and particles.
     */
    public ParticleSystem() {
        particleInUse = new FastTable<Particle>();
        workingEmitters = new FastTable<AbstractParticleEmitter>();
        particleFactory = new ParticleFactory(this);
    }

    /**
     * Add a emitter to the particle system. This emitter will be updated from
     * now on.
     * 
     * @param newEmitter the emitter to add
     */
    public void addEmitter(final AbstractParticleEmitter newEmitter) {
        workingEmitters.add(newEmitter);
    }

    /**
     * Get the amount of particles that are currently active.
     * 
     * @return the amount of active particles
     */
    public int getParticleCount() {
        return particleInUse.size();
    }

    /**
     * Remove a emitter from the list of currently active emitters.
     * 
     * @param newEmitter the emitter to remove
     */
    public void removeEmitter(final AbstractParticleEmitter newEmitter) {
        workingEmitters.remove(newEmitter);
    }

    /**
     * Get a particle object. Either take one out of the storage or create a new
     * particle.
     * 
     * @return the particle that can be used now
     */
    public Particle requestParticle() {
        return particleFactory.object();
    }

    /**
     * Trigger a update to all particles. Modify their locations, speed,
     * lifetime and so on.
     * 
     * @param delta the time offset since the last update
     */
    public void update(final int delta) {
        final int emitterCount = workingEmitters.size();
        for (int i = 0; i < emitterCount; i++) {
            workingEmitters.get(i).updateEmitter(delta);
        }

        update(delta, particleInUse);
    }

    /**
     * Activate a particle so its handled by the particle system.
     * 
     * @param part the particle to activate
     */
    void activateParticle(final Particle part) {
        particleInUse.add(part);
    }

    /**
     * This is the special update implementation that is used to update all
     * particles that are currently active in the particle system concurrent.
     * 
     * @param delta the time since the last update
     * @param list the list of particles that is handled
     */
    void update(final int delta, final FastTable<Particle> list) {
        final int size = list.size();
        if (size == 0) {
            return;
        }
        if (size < 100) {
            // in case there are less then 100 particles in the list: just
            // perform the update!
            final FastTable<Particle> result = FastTable.newInstance();
            Particle currentPart;
            for (int i = 0; i < size; i++) {
                currentPart = list.get(i);
                if (currentPart.update(delta)) {
                    result.add(currentPart);
                } else {
                    currentPart.clean();
                    particleFactory.recycle(currentPart);
                }
            }

            // Only update the entire old list in case there were any changes
            // done to this list.
            final int resultSize = result.size();
            if (size > resultSize) {
                for (int i = 0; i < resultSize; i++) {
                    list.set(i, result.get(i));
                }
                while (list.size() > resultSize) {
                    list.removeLast();
                }
            }
            FastTable.recycle(result);
        } else {
            // Split the particle list in two parts and operate on each part
            // independent.
            final FastTable<Particle> table1 = FastTable.newInstance();
            final FastTable<Particle> table2 = FastTable.newInstance();

            ConcurrentContext.enter();
            try {
                ConcurrentContext.execute(new Runnable() {
                    @Override
                    public void run() {
                        table1.addAll(list.subList(0, size / 2));
                        update(delta, table1); // Recursive.
                    }
                });
                ConcurrentContext.execute(new Runnable() {
                    @Override
                    public void run() {
                        table2.addAll(list.subList(size / 2, size));
                        update(delta, table2); // Recursive.
                    }
                });
            } finally {
                ConcurrentContext.exit();
            }

            final int table1Size = table1.size();
            final int table2Size = table2.size();

            if (size > (table1Size + table2Size)) {
                for (int i = 0; i < table1Size; i++) {
                    list.set(i, table1.get(i));
                }
                for (int i = 0; i < table2Size; i++) {
                    list.set(i + table1Size, table2.get(i));
                }
                while (list.size() > (table1Size + table2Size)) {
                    list.removeLast();
                }
            }

            table1.clear();
            table2.clear();
            FastTable.recycle(table1);
            FastTable.recycle(table2);
        }
    }

    /**
     * Put a particle back into the storage so the object is used again later.
     * 
     * @param part the particle that is not in use anymore
     */
    protected void recycle(final Particle part) {
        particleInUse.remove(part);
        particleFactory.recycle(part);
    }
}
