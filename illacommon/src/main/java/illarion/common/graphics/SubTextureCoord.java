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
package illarion.common.graphics;

/**
 * This class is used to store texture coordinates.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
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

    public SubTextureCoord(
            final String texName,
            final int posX,
            final int posY,
            final int texWidth,
            final int texHeight) {
        name = texName;
        x = posX;
        y = posY;
        width = texWidth;
        height = texHeight;
    }
}
