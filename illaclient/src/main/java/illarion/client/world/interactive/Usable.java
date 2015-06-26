/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
    void use();

    /**
     * Check if the interactive instance is inside the valid using
     * range of the player character.
     *
     * @return {@code true} in case the character is allowed to use
     * anything on this tile or the tile itself
     */
    boolean isInUseRange();

    /**
     * Get the range that the interactive instance can be used at.
     *
     * @return the usable range
     */
    int getUseRange();
}
