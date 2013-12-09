/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Build Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.imagepacker;

import javax.annotation.Nonnull;

/**
 * This class is used to define the empty space on a texture atlas.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class Space extends TextureElement {
    /**
     * Default constructor
     */
    Space(final int x, final int y, final int height, final int width) {
        super(x, y, height, width);
    }

    /**
     * Check if a sprite fits into the space.
     *
     * @param s the sprite to test
     * @return <code>true</code> in case the sprite fits into the space
     */
    public boolean isFittingInside(@Nonnull final TextureElement s) {
        return (s.height <= height) && (s.width <= width);
    }

    /**
     * Get the size of this space in pixels.
     */
    final long size = height * width
}
