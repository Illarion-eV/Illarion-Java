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
package illarion.client.util;

import illarion.client.resources.Resource;

/**
 * This utility class is used to wrap a object together with a ID.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class IdWrapper<T> implements Resource {
    /**
     * The object that is stored in this instance.
     */
    private final T object;

    /**
     * The ID that is connected with the object.
     */
    private final int id;

    /**
     * Constructor that allows to set the ID and the object that are supposed to
     * be linked together.
     *
     * @param id     the ID
     * @param object the object
     */
    public IdWrapper(final int id, final T object) {
        this.id = id;
        this.object = object;
    }

    /**
     * Get the ID of this object.
     *
     * @return the Id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the wrapped object.
     *
     * @return the object
     */
    public T getObject() {
        return object;
    }
}
