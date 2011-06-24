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

import javolution.context.ObjectFactory;
import javolution.util.FastTable;

import illarion.client.graphics.particle.Particle;
import illarion.client.graphics.particle.ParticlePool;
import illarion.client.world.Game;
import illarion.client.world.MapTile;

import illarion.common.util.Location;
import illarion.common.util.Reusable;

/**
 * The effect pool handles all particles on one tile. This is needed so the
 * objects are correctly overlaid. Particles should be rendered at the same
 * level as the effects.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class EffectPool implements DisplayItem, ParticlePool, Reusable {
    /**
     * The factory class that creates and buffers EffectPool objects for later
     * reuse.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class EffectPoolFactory extends
        ObjectFactory<EffectPool> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public EffectPoolFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected EffectPool create() {
            return new EffectPool();
        }
    }

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final EffectPoolFactory FACTORY = new EffectPoolFactory();

    /**
     * The value {@link #hideOutSlowdown} has to reach before the pool is really
     * removed from the display list.
     */
    private static final int HIDE_SLOWDOWN_FINISH = 120000;

    /**
     * A counter that counts the time how long the pool was empty. After some
     * time of a empty pool it gets removed from the display list. This is done
     * to avoid that the pools are added and removed all time from the list and
     * cause the need to resort the list all time.
     */
    private int hideOutSlowdown = 0;

    /**
     * The z layer of this pool that is used to determine the layer this object
     * is rendered within.
     */
    private int layerZ;

    /**
     * The map tile this effect pool was created by. The map tile is always the
     * parent of this pool.
     */
    private MapTile parentTile;

    /**
     * The list of particles that are a part of this pool.
     */
    private final FastTable<Particle> pool;

    /**
     * A flag is the pool is currently shown on the display or not.
     */
    private boolean shown = false;

    /**
     * Create a new EffectPool.
     */
    EffectPool() {
        pool = new FastTable<Particle>();
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @param parent the parent tile that needs the effect pool
     * @return the instance of this class that is now ready to be used
     */
    public static EffectPool getInstance(final MapTile parent) {
        final EffectPool retPool = FACTORY.object();
        retPool.parentTile = parent;
        return retPool;
    }

    /**
     * Add one particle to the pool. The render function of the particle will be
     * triggered by this pool and so it will be at the same display layer as
     * this pool. In case the first particle is added this pool is added to the
     * display list automatically.
     * 
     * @param addPart the particle that shall be added to the pool
     */
    @SuppressWarnings("nls")
    @Override
    public void addParticleToPool(final Particle addPart) {
        if (addPart == null) {
            throw new IllegalArgumentException(
                "Added particle must not be null");
        }
        synchronized (pool) {
            if (pool.isEmpty()) {
                show();
            }
            pool.add(addPart);
        }
    }

    /**
     * Clean up the particle pool by killing all particles within this pool and
     * throwing them out of the list back into the particle system.
     */
    @Override
    public void clearPool() {
        synchronized (pool) {
            while (!pool.isEmpty()) {
                pool.removeLast().kill();
            }
        }
        if (shown) {
            hide();
        }
    }

    /**
     * Draw the particles of this pool. This causes that all particles that are
     * within this pool get triggered to draw them selves.
     * 
     * @return true in case the rendering was done successfully, false if not
     */
    @Override
    public boolean draw() {
        final int particleCount = pool.size();
        for (int i = 0; i < particleCount; i++) {
            pool.get(i).render();
        }

        return true;
    }

    /**
     * Get the parent tile of this pool, so the map tile this particle pool was
     * created by.
     * 
     * @return the parent MapTile of this particle pool
     */
    public MapTile getParent() {
        return parentTile;
    }

    /**
     * Get the z layer of the pool that selects the position where the pool
     * needs to be rendered on the display. All particles that are inside this
     * pool are rendered at this layer.
     * 
     * @return the z layer of the particle pool
     */
    @Override
    public int getZOrder() {
        return layerZ;
    }

    /**
     * Remove the particle pool from the display list and ensure that the pool
     * is not rendered anymore.
     */
    @Override
    public void hide() {
        Game.getDisplay().remove(this);
        shown = false;
        parentTile.removeParticlePool();
        recycle();
    }

    /**
     * Recycle the object so it can be used again later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Remove a particle from the pool. The particle will not be rendered
     * anymore out of this pool, also in case the last particle got removed the
     * pool is automatically removed from the display list.
     * 
     * @param remPart the particle that shall be removed from the list
     */
    @Override
    public void removeParticleFromPool(final Particle remPart) {
        synchronized (pool) {
            pool.remove(remPart);

            if (pool.isEmpty()) {
                hide();
            }
        }
    }

    /**
     * Reset the state of this instance to its ready to be used later.
     */
    @Override
    public void reset() {
        clearPool();
    }

    /**
     * Set the location of the pool on the screen.
     * 
     * @param loc the location of the pool on the screen, should be the position
     *            of the parent tile
     * @param typeLayer the layer of the pool where the items shall be displayed
     */
    public void setScreenPos(final Location loc, final int typeLayer) {
        if (shown) {
            final int newLayerZ = loc.getDcZ() - typeLayer;
            if (newLayerZ != layerZ) {
                Game.getDisplay().readd(this);
                layerZ = newLayerZ;
            }
        } else {
            layerZ = loc.getDcZ() - typeLayer;
        }
    }

    /**
     * Add the particle pool to the display list so its rendered at the next
     * run.
     */
    @Override
    public void show() {
        Game.getDisplay().add(this);
        shown = true;
    }

    /**
     * Update the particle pool. This will ensure that the particle pool is
     * removed from the display list in case there are no particles displayed
     * inside anymore.
     * 
     * @param delta the time since the last update in milliseconds
     */
    @Override
    public void update(final int delta) {
        if (pool.isEmpty()) {
            hideOutSlowdown += delta;
            if (hideOutSlowdown >= HIDE_SLOWDOWN_FINISH) {
                hide();
            }
        }
    }
}
