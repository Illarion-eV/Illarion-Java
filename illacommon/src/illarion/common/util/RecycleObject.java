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
 * Interface for a recycle object that can be stored for reusing in a instance
 * of the recycle factory. Object that get often removed and created, such as
 * tiles or items should by recycle objects and use a recycle factory.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public interface RecycleObject extends Cloneable, Reusable {
    /**
     * Set the id of the object.
     * 
     * @param id the new id of the object
     */
    void activate(int id);

    /**
     * Create an empty duplicate of the object.
     * 
     * @return the duplicate of the object
     */
    RecycleObject clone();

    /**
     * Retrieve the id of the recycle object type.
     * 
     * @return id of object type
     */
    int getId();
}
