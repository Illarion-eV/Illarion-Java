/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.imagepacker;

/**
 * This interface defined in general elements that located on a texture along
 * with all required informations about them.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface TextureElement {
    /**
     * Get the height of this texture element.
     * 
     * @return the height of the texture element
     */
    int getHeight();

    /**
     * Get the width of this texture element.
     * 
     * @return the width of the texture element
     */
    int getWidth();

    /**
     * The X coordinate of the origin of this texture element.
     * 
     * @return the x coordinate of this texture element
     */
    int getX();

    /**
     * The Y coordinate of the origin of this texture element.
     * 
     * @return the y coordinate of this texture element
     */
    int getY();
}
