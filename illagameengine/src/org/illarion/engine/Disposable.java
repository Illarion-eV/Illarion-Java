/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
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
