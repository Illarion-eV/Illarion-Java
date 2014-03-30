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
package illarion.client.world;

/**
 * This Enumerator contains the possible value for the movement methods of a character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CharMovementMode {
    /**
     * This constant means that no movement is done. The character is only turning around or warping.
     */
    None,

    /**
     * This movement mode means that the character is walking.
     */
    Walk,

    /**
     * This constant means that the character is running.
     */
    Run,

    /**
     * This constant means that the character is being pushed.
     */
    Push
}
