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
package illarion.common.util;

import javax.annotation.Nullable;

/**
 * Like the name suggests a object source is a source for objects. Its basically
 * a method to access a list with a String as key.
 *
 * @param <T> The type of the object that is handled by this source
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ObjectSource<T> {
    /**
     * Check if there is a object bound to a key.
     *
     * @param key the key to test
     * @return <code>true</code> in case some object is bound to this key
     */
    boolean containsObject(String key);

    /**
     * Return a object to the source once its not used anymore.
     *
     * @param key the key that was used to request the object
     * @param object the object itself
     */
    void disposeObject(String key, T object);

    /**
     * Get a object assigned to the type.
     *
     * @param key the key of the object to fetch
     * @return the object that is assigned to the key or <code>null</code> in
     * case no object is bound to the set key
     */
    @Nullable
    T getObject(String key);
}
