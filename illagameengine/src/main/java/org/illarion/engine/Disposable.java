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
package org.illarion.engine;

/**
 * This interface is implemented by all classes that require to be disposed. Those are usually objects with native
 * references or that hold any kind of memory that is not cleaned by the garbage collector of java.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Disposable {
    /**
     * Calling this function renders the object unusable and cleans this object up to make it ready for disposal.
     */
    void dispose();
}
