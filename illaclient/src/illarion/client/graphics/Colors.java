/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * A storage of some predefined colors that are used in the client on a regular
 * base.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 */
public enum Colors {
    /**
     * Represents the color black.
     */
    black(0.f, 0.f, 0.f),

    /**
     * Represents the color blue.
     */
    blue(0.7f, 0.8f, 1.f),

    /**
     * Represents the color dark blue.
     */
    darkBlue(0.f, 0.2f, 0.8f),

    /**
     * Represents the color gray.
     */
    gray(0.6f, 0.6f, 0.6f),

    /**
     * Represents the color green.
     */
    green(0.f, 0.8f, 0.2f),

    /**
     * Represents the color red.
     */
    red(1.f, 0.3f, 0.3f),

    /**
     * Represents the color white.
     */
    white(1.f, 1.f, 1.f),

    /**
     * Represents the color yellow.
     */
    yellow(1.f, 1.f, 0.2f);

    /**
     * The instance of sprite color representing the current color.
     */
    private transient final SpriteColor color;

    /**
     * Create a new color with a specified set of values.
     * 
     * @param redPart the red share of the color between 0 and 1
     * @param greenPart the green share of the color between 0 and 1
     * @param bluePart the blue share of the color between 0 and 1
     */
    private Colors(final float redPart, final float greenPart,
        final float bluePart) {
        color = Graphics.getInstance().getSpriteColor();
        color.set(redPart, greenPart, bluePart);
        color.setAlpha(SpriteColor.COLOR_MAX);
    }

    /**
     * Get the sprite color instance representing the current color.
     * 
     * @return the sprite color instance with the values of this color
     */
    public final SpriteColor getColor() {
        return color;
    }
}
