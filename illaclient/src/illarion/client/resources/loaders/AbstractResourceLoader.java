/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.resources.loaders;

import illarion.client.resources.Resource;
import illarion.client.resources.ResourceFactory;

import java.util.concurrent.Callable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractResourceLoader<T extends Resource> implements Callable<ResourceFactory<T>> {
    /**
     * The factory that is supposed to store the load objects.
     */
    private ResourceFactory<T> targetFactory;

    /**
     * Get the target factory that is set.
     *
     * @return the target factory of this loader
     */
    protected final ResourceFactory<T> getTargetFactory() {
        return targetFactory;
    }

    /**
     * Check if this resource loader has a assigned target factory that is
     * supposed to receive the data.
     *
     * @return <code>true</code> in case this resource loader has a assigned
     *         factory
     */
    protected final boolean hasTargetFactory() {
        return targetFactory != null;
    }

    /**
     * Set the resource factory that will take the data.
     *
     * @param factory the factory that will take the data
     */
    public final AbstractResourceLoader<T> setTarget(final ResourceFactory<T> factory) {
        if (hasTargetFactory()) {
            throw new IllegalStateException(
                    "Changing the target factory once set is not allowed");
        }
        targetFactory = factory;
        return this;
    }
}
