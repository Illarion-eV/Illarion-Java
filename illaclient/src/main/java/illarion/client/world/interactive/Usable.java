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
package illarion.client.world.interactive;

/**
 * This interface is implemented by any interactive instance that can be used.
 *
 * @author Fredrik K
 */
public interface Usable {
    /**
     * Perform a use operation on this interactive instance.
     */
    public void use();

    /**
     * Check if the interactive instance is inside the valid using
     * range of the player character.
     *
     * @return {@code true} in case the character is allowed to use
     *                      anything on this tile or the tile itself
     */
    public boolean isInUseRange();

    /**
     * Get the range that the interactive instance can be used at.
     *
     * @return the usable range
     */
    public int getUseRange();
}
