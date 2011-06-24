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
 * Sprite interface that represents a renderable object.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface Sprite {
    /**
     * This stores all possible values for the horizontal aligns that are
     * allowed to set at a sprite.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    enum HAlign {
        /**
         * Set the align of the graphic to center. Means the origin point of the
         * graphic will be at the center of the graphic.
         */
        center,

        /**
         * Set the align of the graphic to left. Means the origin point of the
         * graphic will be at the left side of the graphic.
         */
        left,

        /**
         * Set the align of the graphic to right. Means the origin point of the
         * graphic will be at the right side of the graphic.
         */
        right;
    }

    /**
     * This stores all possible values for the verticals align that are allowed
     * to set at a sprite.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    enum VAlign {
        /**
         * Set the align of the graphic to bottom. Means the origin point of the
         * graphic will be at the bottom border of the graphic.
         */
        bottom,

        /**
         * Set the align of the graphic to middle. Means the origin point of the
         * graphic will be at the middle of the graphic.
         */
        middle,

        /**
         * Set the align of the graphic to top. Means the origin point of the
         * graphic will be at the top border of the graphic.
         */
        top;
    }

    /**
     * Add a texture to this sprite. A sprite can contain as many textures as
     * wanted. The frame parameter of the drawing functions can be used to
     * select the frame that shall be drawn. A IllegalArgumentException will be
     * thrown in case the texture does not fit the size of the textures that
     * were added before.
     * 
     * @param newTexture the texture that shall be added to the sprite
     */
    void addTexture(Texture newTexture);

    /**
     * Simple drawing function that that draws a sprite at a special location
     * that is enlighten with the default light. The first frame is rendered at
     * all times and the frame is rendered with the same size as the textures so
     * no rescaling takes place.
     * 
     * @param x the x coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param y the y coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     */
    void draw(int x, int y);

    /**
     * Simple drawing function that renders a sprite at a special location that
     * is enlighten with the default light. The first frame is rendered always
     * and the resizing is performed so the size of the rendered texture fits to
     * the height and width value that is set as parameter.
     * 
     * @param x the x coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param y the y coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param w the width of the texture that is the target of the rendering.
     *            The image is resized in case it does not fit to this width
     *            value.
     * @param h the height of the texture that is the target of the rendering.
     *            The image is resized in case it does not fit to this width
     *            value.
     */
    void draw(int x, int y, int w, int h);

    /**
     * Simple drawing function that renders a sprite at a special location. The
     * first frame is rendered always and the resizing is performed so the size
     * of the rendered texture fits to the height and width value that is set as
     * parameter.
     * 
     * @param x the x coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param y the y coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param w the width of the texture that is the target of the rendering.
     *            The image is resized in case it does not fit to this width
     *            value.
     * @param h the height of the texture that is the target of the rendering.
     *            The image is resized in case it does not fit to this width
     *            value.
     * @param color the color that is used for rendering the texture of the
     *            sprite
     */
    void draw(int x, int y, int w, int h, SpriteColor color);

    /**
     * Simple drawing function that renders a sprite at a special location. The
     * resizing is performed so the size of the rendered texture fits to the
     * height and width value that is set as parameter.
     * 
     * @param x the x coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param y the y coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param w the width of the texture that is the target of the rendering.
     *            The image is resized in case it does not fit to this width
     *            value.
     * @param h the height of the texture that is the target of the rendering.
     *            The image is resized in case it does not fit to this width
     *            value.
     * @param color the color that is used for rendering the texture of the
     *            sprite
     * @param frame the frame that shall be drawn. In case its above the frame
     *            count or below 0 the first frame is rendered
     */
    void draw(int x, int y, int w, int h, SpriteColor color, int frame);

    /**
     * Simple drawing function that that draws a sprite at a special location.
     * The first frame is rendered at all times and the frame is rendered with
     * the same size as the textures so no rescaling takes place.
     * 
     * @param x the x coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param y the y coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param color the color that is used for rendering the texture of the
     *            sprite
     */
    void draw(int x, int y, SpriteColor color);

    /**
     * Simple drawing function that that draws a sprite at a special location.
     * The first frame is rendered at all times and the frame is rendered with
     * the same size as the textures so no rescaling takes place.
     * 
     * @param x the x coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param y the y coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param color the color that is used for rendering the texture of the
     *            sprite
     * @param frame the frame that shall be drawn. In case its above the frame
     *            count or below 0 the first frame is rendered
     */
    void draw(int x, int y, SpriteColor color, int frame);

    /**
     * Drawing function that draws a frame at a special location.
     * 
     * @param x the x coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param y the y coordinate where the picture shall be rendered. The origin
     *            of the sprite will be exactly at this location and the actual
     *            location of the rendered image is related to align and offset
     * @param color the color that is used for rendering the texture of the
     *            sprite
     * @param frame the frame that shall be drawn. In case its above the frame
     *            count or below 0 the first frame is rendered
     * @param scale the scaling value for this drawing operation. The original
     *            size of the texture is multiplicated with this scaling value
     */
    void draw(int x, int y, SpriteColor color, int frame, float scale);

    /**
     * The the instance of the default light that is used in this implementation
     * of the Sprite objects.
     * 
     * @return the default light object
     */
    SpriteColor getDefaultLight();

    /**
     * Get the frames of the sprite, means the amount of textures that were
     * added to this sprite.
     * 
     * @return the amount of textures of the sprite
     */
    int getFrames();

    /**
     * Get the height of the textures of the sprite.
     * 
     * @return the height of the sprite in pixels, or -1 in case there was not
     *         texture set yet.
     */
    int getHeight();

    /**
     * Get the offset in X direction that is added to the location the sprite is
     * rendered at.
     * 
     * @return the offset in x direction calculated
     */
    int getOffsetX();

    /**
     * Get the offset in Y direction that is added to the location the sprite is
     * rendered at.
     * 
     * @return the offset in y direction calculated
     */
    int getOffsetY();

    /**
     * Calculates the offset in X direction that is added to the location the
     * sprite is rendered at.
     * 
     * @param scale the scaling value that is used for rendering
     * @return the offset in x direction calculated
     */
    int getScaledOffsetX(float scale);

    /**
     * Calculates the offset in Y direction that is added to the location the
     * sprite is rendered at.
     * 
     * @param scale the scaling value that is used for rendering
     * @return the offset in y direction calculated
     */
    int getScaledOffsetY(float scale);

    /**
     * Get a texture at a specified index.
     * 
     * @param index the index of the texture
     * @return the texture at the index
     */
    Texture getTexture(int index);

    /**
     * Get the width of the textures of the sprite.
     * 
     * @return the width of the sprite in pixels, or -1 in case there was not
     *         texture set yet.
     */
    int getWidth();

    /**
     * This function should be called in case the sprite is not used anymore. It
     * will clean up all memory intensive parts of the sprite. After calling
     * this function the sprite is not usable anymore.
     */
    void remove();

    /**
     * Set the horizontal and the vertical align of the sprite pictures.
     * 
     * @param horzAlign the horizontal align of the picture
     * @param vertAlign the vertical align of the picture
     * @see illarion.graphics.Sprite.HAlign
     * @see illarion.graphics.Sprite.VAlign
     */
    void setAlign(final HAlign horzAlign, final VAlign vertAlign);

    /**
     * Set the value of the mirror flag. Setting this to true, results in a
     * horizontal mirrored display of all textures of this sprite
     * 
     * @param newMirror the new value for the mirror flag
     */
    void setMirror(boolean newMirror);

    /**
     * Set the offsets of this sprite. The offset determines how the origin
     * point is moved from the point that is handed over when rendering the
     * sprite at a special location.
     * 
     * @param xOffset the x coordinate of the offset
     * @param yOffset the y coordinate of the offset
     */
    void setOffset(int xOffset, int yOffset);

    /**
     * Set the rotation that is applied to the rendered texture. Default is 0
     * degree.
     * 
     * @param degree the rotation degree that is supposed to be applied to the
     *            texture
     */
    void setRotation(float degree);
}
