/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package org.illarion.engine.graphic;

import illarion.common.types.Rectangle;
import org.illarion.engine.graphic.effects.TextureEffect;

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
     * Draw a sprite to the screen.
     *
     * @param sprite the sprite
     * @param posX the x coordinate on the screen the sprite is supposed to appear at
     * @param posY the y coordinate on the screen the sprite is supposed to appear at
     * @param color the color that is supposed to be applied to the sprite
     * @param frame the frame of the sprite that should be rendered
     * @param scale the scaling that should be applied to the sprite
     * @param rotation the rotation that should be applied to the sprite
     * @param effects the effects that are supposed to be applied to this sprite while drawing
     */
    void drawSprite(
            @Nonnull Sprite sprite,
            int posX,
            int posY,
            @Nonnull Color color,
            int frame,
            double scale,
            double rotation,
            @Nonnull TextureEffect... effects);

    /**
     * This is a dedicated function to render tiles. It has additional abilities regarding the coloring to allow
     * rendering smooth light on the ground.
     *
     * @param sprite the sprite that is rendered
     * @param posX the x coordinate of the position
     * @param posY the y coordinate of the position
     * @param topColor the color at the top center of the tile
     * @param bottomColor the color at the bottom center of the tile
     * @param leftColor the color at the middle left of the tile
     * @param rightColor the color at the middle right of the tile
     * @param centerColor the color in the center of the tile
     * @param frame the frame of the sprite that is supposed to be rendered
     * @param effects the texture effects that should be applied
     */
    void drawTileSprite(@Nonnull Sprite sprite, int posX, int posY,
                        @Nonnull Color topColor, @Nonnull Color bottomColor,
                        @Nonnull Color leftColor, @Nonnull Color rightColor,
                        @Nonnull Color centerColor, int frame,
                        @Nonnull TextureEffect... effects);

    /**
     * Set the method used to blend the colors of overlapping areas.
     *
     * @param mode the blending mode
     */
    void setBlendingMode(@Nonnull BlendingMode mode);

    /**
     * Draw a text in its original size to the screen.
     *
     * @param font the font used to render the text
     * @param text the text that is rendered to the screen
     * @param color the color of the drawn text
     * @param x the x coordinate on the screen the text is rendered to
     * @param y the y coordinate on the screen the text is rendered to
     */
    void drawText(@Nonnull Font font, @Nonnull CharSequence text, @Nonnull Color color, int x, int y);

    /**
     * Draw a text to the screen.
     *
     * @param font the font used to render the text
     * @param text the text that is rendered to the screen
     * @param color the color of the drawn text
     * @param x the x coordinate on the screen the text is rendered to
     * @param y the y coordinate on the screen the text is rendered to
     * @param scaleX the scale applied to the width of the text
     * @param scaleY the scale applied to the height of the text
     */
    void drawText(
            @Nonnull Font font,
            @Nonnull CharSequence text,
            @Nonnull Color color,
            int x,
            int y,
            double scaleX,
            double scaleY);

    /**
     * Draw a rectangle with a solid color.
     *
     * @param x the x coordinate of the rectangle
     * @param y the y coordinate of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color of the rectangle
     */
    void drawRectangle(int x, int y, int width, int height, @Nonnull Color color);

    /**
     * Draw a rectangle with a solid color.
     *
     * @param rectangle the rectangle that is supposed to be rendered
     * @param color the color of the rectangle
     */
    void drawRectangle(@Nonnull Rectangle rectangle, @Nonnull Color color);

    /**
     * Draw a rectangle with a different color in each corner.
     *
     * @param x the x coordinate of the rectangle
     * @param y the y coordinate of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param topLeftColor the color of the top left corner of the rectangle
     * @param topRightColor the color of the top right corner of the rectangle
     * @param bottomLeftColor the color of the bottom left corner of the rectangle
     * @param bottomRightColor the color of the bottom right corner of the rectangle
     */
    void drawRectangle(
            int x,
            int y,
            int width,
            int height,
            @Nonnull Color topLeftColor,
            @Nonnull Color topRightColor,
            @Nonnull Color bottomLeftColor,
            @Nonnull Color bottomRightColor);

    /**
     * Draw a texture to the screen.
     *
     * @param texture the texture to draw
     * @param x the x coordinate of the location of the texture
     * @param y the y coordinate of the location of the texture
     * @param width the width of the area to render the texture in
     * @param height the height of the area to render the texture in
     * @param color the color to be applied to the rendered texture
     * @param effects the effects that are supposed to be applied to this texture while drawing
     */
    void drawTexture(
            @Nonnull Texture texture,
            int x,
            int y,
            int width,
            int height,
            @Nonnull Color color,
            @Nonnull TextureEffect... effects);

    /**
     * Draw a part of a texture to the screen.
     *
     * @param texture the texture to draw
     * @param x the x coordinate of the location of the texture
     * @param y the y coordinate of the location of the texture
     * @param width the width of the area to render the texture in
     * @param height the height of the area to render the texture in
     * @param texX the x coordinate on the texture that is mapped to the x coordinate of the render rectangle
     * @param texY the y coordinate on the texture that is mapped to the y coordinate of the render rectangle
     * @param texWidth the width of the area on the texture that is drawn into the rectangle
     * @param texHeight the height of the area on the texture that is drawn into the rectangle
     * @param color the color to be applied to the rendered texture
     * @param effects the effects that are supposed to be applied to this texture while drawing
     */
    void drawTexture(
            @Nonnull Texture texture,
            int x,
            int y,
            int width,
            int height,
            int texX,
            int texY,
            int texWidth,
            int texHeight,
            @Nonnull Color color,
            @Nonnull TextureEffect... effects);

    /**
     * Draw a part of a texture to the screen.
     *
     * @param texture the texture to draw
     * @param x the x coordinate of the location of the texture
     * @param y the y coordinate of the location of the texture
     * @param width the width of the area to render the texture in
     * @param height the height of the area to render the texture in
     * @param texX the x coordinate on the texture that is mapped to the x coordinate of the render rectangle
     * @param texY the y coordinate on the texture that is mapped to the y coordinate of the render rectangle
     * @param texWidth the width of the area on the texture that is drawn into the rectangle
     * @param texHeight the height of the area on the texture that is drawn into the rectangle
     * @param centerX the x coordinate of the texture center location
     * @param centerY the y coordinate of the texture center location
     * @param rotate the amount of degrees the texture is supposed to be rotated
     * @param color the color to be applied to the rendered texture
     * @param effects the effects that are supposed to be applied to this texture while drawing
     */
    void drawTexture(
            @Nonnull Texture texture,
            int x,
            int y,
            int width,
            int height,
            int texX,
            int texY,
            int texWidth,
            int texHeight,
            int centerX,
            int centerY,
            double rotate,
            @Nonnull Color color,
            @Nonnull TextureEffect... effects);

    /**
     * Set a clipping area. Outside of this area, render operations don't have any effect.
     * <p/>
     * Calling this function while another clipping area is already set clears the old clipping area and applies this
     * new one.
     *
     * @param x the x coordinate of the area
     * @param y the y coordinate of the area
     * @param width the width of the area
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
