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
