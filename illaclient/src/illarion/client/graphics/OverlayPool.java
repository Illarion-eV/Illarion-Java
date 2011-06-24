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
package illarion.client.graphics;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.util.FastTable;

import illarion.client.graphics.particle.Particle;
import illarion.client.graphics.particle.ParticlePool;

/**
 * This special particle pool is rendered on top of everything else. Its used
 * for particle effects that are displayed as overlay over the entire map.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class OverlayPool implements ParticlePool {
    /**
     * The list of particles that are a part of this pool.
     */
    private final FastTable<Particle> pool;

    /**
     * This is the lock used to protect the access on the pool.
     */
    private final ReadWriteLock poolLock;

    /**
     * Create a new instance of the overlay pool. That should only be done by
     * the map display manager that takes care of rendering the map.
     */
    OverlayPool() {
        pool = new FastTable<Particle>();
        poolLock = new ReentrantReadWriteLock();
    }

    /**
     * Add a single particle to the pool of particles.
     */
    @Override
    public void addParticleToPool(final Particle addPart) {
        poolLock.writeLock().lock();
        try {
            pool.add(addPart);
        } finally {
            poolLock.writeLock().unlock();
        }
    }

    /**
     * Clear all particles from this pool.
     */
    @Override
    public void clearPool() {
        poolLock.writeLock().lock();
        try {
            while (!pool.isEmpty()) {
                pool.removeLast().kill();
            }
        } finally {
            poolLock.writeLock().unlock();
        }
    }

    /**
     * Draw all particles assigned to this pool.
     */
    public void draw() {
        final int particleCount = pool.size();
        if (particleCount == 0) {
            return;
        }
        poolLock.readLock().lock();
        try {
            for (int i = 0; i < particleCount; i++) {
                pool.get(i).render();
            }
        } finally {
            poolLock.readLock().unlock();
        }
    }

    /**
     * Remove one particle from this pool that won't be rendered any longer.
     */
    @Override
    public void removeParticleFromPool(final Particle remPart) {
        poolLock.writeLock().lock();
        try {
            pool.remove(remPart);
        } finally {
            poolLock.writeLock().unlock();
        }
    }

}
