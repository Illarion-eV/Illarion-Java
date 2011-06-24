/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

/**
 * This interface is used for every object that is reusable. Means that
 * instances of the object are stored for later usage and in case there are
 * "old" object available those are used instead of new ones.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface Reusable extends javolution.lang.Reusable {
    /**
     * Mark this object as unused and store it for later usage.
     */
    void recycle();
}
