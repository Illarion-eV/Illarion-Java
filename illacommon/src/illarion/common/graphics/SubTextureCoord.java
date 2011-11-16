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
package illarion.common.graphics;

/**
 * This class is used to store texture coordinates.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class SubTextureCoord {
    private final String name;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    
    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SubTextureCoord(final String texName, final int posX, final int posY, final int texWidth, final int texHeight) {
        name = texName;
        x = posX;
        y = posY;
        width = texWidth;
        height = texHeight;
    }
}
