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

/**
 * This interface defined in general elements that located on a texture along with all required information about them.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
abstract class TextureElement {
    /**
     * The height of the texture element
     */
    def int height

    /**
     * The width of the texture element
     */
    def int width

    /**
     * The X coordinate of the origin of this texture element.
     */
    def int x

    /**
     * The Y coordinate of the origin of this texture element.
     */
    def int y

    TextureElement() {}

    TextureElement(final int x, final int y, final int height, final int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
}
