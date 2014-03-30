/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.resources;

/**
 * This interface is shared by all resource factories and ensures that they
 * start up in the very same way.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ResourceFactory<T extends Resource> {
    /**
     * Initialize the factory.
     */
    void init();

    /**
     * This function is called once loading the data is finished. It can be used to optimize the storage to the
     * current amount of data.
     */
    void loadingFinished();

    /**
     * Store a resource in this factory.
     *
     * @param resource the resource to store
     */
    void storeResource(T resource);
}
