/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.graphic;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is the graphics interface used to draw anything on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface Graphics {
    /**
     * Clean everything that was rendered from the output device.
     */
    void clear();

    /**
     * Set the method used to blend the colors of overlapping areas.
     *
     * @param mode the blending mode
     */
    void setBlendingMode(@Nonnull BlendingMode mode);

    /**
     * Draw a text in its original size to the screen.
     *
     * @param font  the font used to render the text
     * @param text  the text that is rendered to the screen
     * @param color the color of the drawn text
     * @param x     the x coordinate on the screen the text is rendered to
     * @param y     the y coordinate on the screen the text is rendered to
     */
    void drawText(@Nonnull Font font, @Nonnull CharSequence text, @Nonnull Color color, int x, int y);

    /**
     * Draw a text to the screen.
     *
     * @param font   the font used to render the text
     * @param text   the text that is rendered to the screen
     * @param color  the color of the drawn text
     * @param x      the x coordinate on the screen the text is rendered to
     * @param y      the y coordinate on the screen the text is rendered to
     * @param scaleX the scale applied to the width of the text
     * @param scaleY the scale applied to the height of the text
     */
    void drawText(@Nonnull Font font, @Nonnull CharSequence text, @Nonnull Color color, int x, int y, float scaleX,
                  float scaleY);

    /**
     * Draw a rectangle with a solid color.
     *
     * @param x      the x coordinate of the rectangle
     * @param y      the y coordinate of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param color  the color of the rectangle
     */
    void drawRectangle(int x, int y, int width, int height, @Nonnull Color color);

    /**
     * Draw a rectangle with a different color in each corner.
     *
     * @param x                the x coordinate of the rectangle
     * @param y                the y coordinate of the rectangle
     * @param width            the width of the rectangle
     * @param height           the height of the rectangle
     * @param topLeftColor     the color of the top left corner of the rectangle
     * @param topRightColor    the color of the top right corner of the rectangle
     * @param bottomLeftColor  the color of the bottom left corner of the rectangle
     * @param bottomRightColor the color of the bottom right corner of the rectangle
     */
    void drawRectangle(int x, int y, int width, int height, @Nonnull Color topLeftColor,
                       @Nonnull Color topRightColor, @Nonnull Color bottomLeftColor, @Nonnull Color bottomRightColor);

    /**
     * Draw a texture to the screen.
     *
     * @param texture the texture to draw
     * @param x       the x coordinate of the location of the texture
     * @param y       the y coordinate of the location of the texture
     * @param width   the width of the area to render the texture in
     * @param height  the height of the area to render the texture in
     * @param color   the color to be applied to the rendered texture
     */
    void drawTexture(@Nonnull Texture texture, int x, int y, int width, int height, @Nonnull Color color);

    /**
     * Draw a part of a texture to the screen.
     *
     * @param texture   the texture to draw
     * @param x         the x coordinate of the location of the texture
     * @param y         the y coordinate of the location of the texture
     * @param width     the width of the area to render the texture in
     * @param height    the height of the area to render the texture in
     * @param texX      the x coordinate on the texture that is mapped to the x coordinate of the render rectangle
     * @param texY      the y coordinate on the texture that is mapped to the y coordinate of the render rectangle
     * @param texWidth  the width of the area on the texture that is drawn into the rectangle
     * @param texHeight the height of the area on the texture that is drawn into the rectangle
     * @param color     the color to be applied to the rendered texture
     */
    void drawTexture(@Nonnull Texture texture, int x, int y, int width, int height, int texX, int texY, int texWidth,
                     int texHeight, @Nonnull Color color);

    /**
     * Set a clipping area. Outside of this area, render operations don't have any effect.
     * <p/>
     * Calling this function while another clipping area is already set clears the old clipping area and applies this
     * new one.
     *
     * @param x      the x coordinate of the area
     * @param y      the y coordinate of the area
     * @param width  the width of the area
     * @param height the height of the area
     */
    void setClippingArea(int x, int y, int width, int height);

    /**
     * Clear the clipping area and allow render operations to the entire screen again.
     * <p/>
     * Calling this function while no clipping area is set has not effect.
     */
    void unsetClippingArea();
}
