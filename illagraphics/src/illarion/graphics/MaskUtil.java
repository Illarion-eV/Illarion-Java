/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

/**
 * A mask is a area that limits the render area to the mask or to the area
 * outside the mask. This can be used for cutting the graphics in a not
 * rectangular way.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface MaskUtil {
    /**
     * Start defining a mask. All following render operations will describe the
     * shape of the mask.
     */
    void defineMask();

    /**
     * Limit the area that is changed by render operations to the area outside
     * of the mask. All following render operations only become visible in case
     * they are not on the mask.
     */
    void drawOffMask();

    /**
     * Limit the area that is changed by render operations to the area on the
     * mask. All following render operations only become visible in case they
     * are on the mask.
     */
    void drawOnMask();

    /**
     * Finish the definition of the mask. All following operations will draw
     * again to the image buffer and become visible on the screen.
     */
    void finishDefineMask();

    /**
     * Reset the mask. This removes the defined mask from the buffer. All render
     * operations act normal again.
     */
    void resetMask();
}
