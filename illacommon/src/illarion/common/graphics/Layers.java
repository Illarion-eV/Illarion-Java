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
package illarion.common.graphics;

/**
 * Utility class to store the constants that mark the graphical layers of the
 * different elements. This layer values influence the order of the items they
 * are rendered in. The lower the layer number the sooner the object is
 * rendered. A object with a low layer number will be below a object with a high
 * layer number.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class Layers {
    /**
     * The character layer. All character, NPCs and monsters are rendered on
     * this layer.
     */
    public static final int CHARS = 320;

    /**
     * The depth of a layer in case a object is behind another object. This is
     * mainly needed as the location calculation.
     */
    public static final int DISTANCE = 50;

    /**
     * The effect layer. All graphical effects are rendered with this layer.
     */
    public static final int EFFECTS = 321;

    /**
     * The item layer. Means all object in the game are rendered with this
     * layer.
     */
    public static final int ITEM = 302;

    /**
     * The layer per level. Means how much is added or subtracted from or to
     * other layer levels in case a object is a level below or above the player
     * character.
     */
    public static final int LEVEL = 500;

    /**
     * The marker layer. Means using or attacking markers. They are rendered
     * right on top of the tiles but below everything else.
     */
    public static final int MARKER = 301;

    /**
     * The tile layer. Since there is nothing below the tiles, the tiles get the
     * lowest layer number.
     */
    public static final int TILE = 0;

    /**
     * The private constructor, used to ensure that nothing creates a instance
     * of this utility class.
     */
    private Layers() {
        // this constructor only blocks that nothing can create a instance of
        // this class.
    }
}
