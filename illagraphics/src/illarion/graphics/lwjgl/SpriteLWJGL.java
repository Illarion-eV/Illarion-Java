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
package illarion.graphics.lwjgl;

import illarion.graphics.SpriteColor;
import illarion.graphics.Texture;
import illarion.graphics.generic.AbstractSprite;
import illarion.graphics.lwjgl.render.AbstractTextureRender;

/**
 * LWJGL implementation of the sprite interface that uses LWJGL to render the
 * sprite on the screen.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class SpriteLWJGL extends AbstractSprite {
    /**
     * The texture render that is used to draw the textures.
     */
    private final AbstractTextureRender texRender;

    /**
     * The default constructor of the LWJGL Sprite. This one does nothing on its
     * own.
     * 
     * @param frames the amount of frames this sprite will display
     */
    public SpriteLWJGL(final int frames) {
        super(frames);
        texRender = AbstractTextureRender.getInstance();
    }

    /**
     * Add a texture to the sprite. This texture needs to by a LWJGL
     * implementation of a texture.
     * 
     * @param newTexture the instance of the texture that is added to the
     *            texture storage of this sprite.
     */
    @Override
    @SuppressWarnings("nls")
    public void addTexture(final Texture newTexture) {
        if (newTexture == null) {
            throw new IllegalArgumentException("Added NULL Texture");
        }

        if (!(newTexture instanceof TextureLWJGL)) {
            throw new IllegalArgumentException(
                "Added texture is not a correct " + "texture");
        }

        super.addTexture(newTexture);
    }

    /**
     * Simple drawing function. Draw the first texture of the LWJGL sprite to a
     * location.
     * 
     * @param x the x coordinate of the location the texture shall been drawn at
     * @param y the y coordinate of the location the texture shall been drawn at
     */
    @Override
    public void draw(final int x, final int y) {
        draw(x, y, DEFAULT_LIGHT, 0, 1.f);
    }

    /**
     * Simple drawing function. Draw a frame texture of the LWJGL sprite to a
     * location that is enlighten with the default color. The texture is scaled
     * to the width and height set in this function. The first texture is
     * rendered.
     * 
     * @param x the x coordinate of the location the texture shall been drawn at
     * @param y the y coordinate of the location the texture shall been drawn at
     * @param w the width the width of the sprite shall be scaled to
     * @param h the height the height of the sprite shall be scaled to
     */
    @Override
    public void draw(final int x, final int y, final int w, final int h) {
        draw(x, y, w, h, DEFAULT_LIGHT, 0);
    }

    /**
     * Simple drawing function. Draw a frame texture of the LWJGL sprite to a
     * location that is enlighten with the color that is set. The texture is
     * scaled to the width and height set in this function. The first texture is
     * rendered.
     * 
     * @param x the x coordinate of the location the texture shall been drawn at
     * @param y the y coordinate of the location the texture shall been drawn at
     * @param w the width the width of the sprite shall be scaled to
     * @param h the height the height of the sprite shall be scaled to
     * @param color the color that is used to render the sprite
     */
    @Override
    public void draw(final int x, final int y, final int w, final int h,
        final SpriteColor color) {
        draw(x, y, w, h, color, 0);
    }

    /**
     * Drawing function. Draw a frame texture of the LWJGL sprite to a location
     * that is enlighten with the color that is set. The texture is scaled to
     * the width and height set in this function.
     * 
     * @param x the x coordinate of the location the texture shall been drawn at
     * @param y the y coordinate of the location the texture shall been drawn at
     * @param w the width the width of the sprite shall be scaled to
     * @param h the height the height of the sprite shall be scaled to
     * @param color the color that is used to render the sprite
     * @param frame the frame that shall be rendered
     */
    @Override
    @SuppressWarnings("nls")
    public void draw(final int x, final int y, final int w, final int h,
        final SpriteColor color, final int frame) {

        final TextureLWJGL texture = (TextureLWJGL) getTexture(frame);
        if (texture == null) {
            throw new IllegalArgumentException("Can't render a frame that is "
                + "not set.");
        }

        final int alignOffX = getAlignOffsetX(w);
        final int alignOffY = getAlignOffsetY(h);

        final float wScale = (float) w / (float) getWidth();
        final float hScale = (float) h / (float) getHeight();

        SpriteColor usedColor = color;
        if (color == null) {
            usedColor = DEFAULT_LIGHT;
        }

        texRender.drawTexture(x + ((getRawOffsetX()) * wScale) + alignOffX, y
            + ((getRawOffsetY()) * hScale) + alignOffY, 0, w, h, texture,
            usedColor, isMirrored(), getRotation());
        reportDrawTexture();
    }

    /**
     * Simple drawing function. Draw the first texture of the LWJGL sprite to a
     * location that is enlighten with the color that is set. Also the alpha
     * value of this color is taken into account for transparency effects.
     * 
     * @param x the x coordinate of the location the texture shall been drawn at
     * @param y the y coordinate of the location the texture shall been drawn at
     * @param color the color the texture of the sprite is rendered with
     */
    @Override
    public void draw(final int x, final int y, final SpriteColor color) {
        draw(x, y, color, 0);
    }

    /**
     * Simple drawing function. Draw the first texture of the LWJGL sprite to a
     * location that is enlighten with the color that is set. Also the alpha
     * value of this color is taken into account for transparency effects.
     * 
     * @param x the x coordinate of the location the texture shall been drawn at
     * @param y the y coordinate of the location the texture shall been drawn at
     * @param color the color the texture of the sprite is rendered with
     * @param frame the frame that is rendered
     */
    @SuppressWarnings("nls")
    @Override
    public void draw(final int x, final int y, final SpriteColor color,
        final int frame) {
        final TextureLWJGL texture = (TextureLWJGL) getTexture(frame);
        if (texture == null) {
            throw new IllegalArgumentException("Can't render a frame that is "
                + "not set.");
        }

        final int texWidth = texture.getImageWidth();
        final int texHeight = texture.getImageHeight();

        SpriteColor usedColor = color;
        if (color == null) {
            usedColor = DEFAULT_LIGHT;
        }

        texRender.drawTexture(x + getOffsetX(), y + getOffsetY(), 0, texWidth,
            texHeight, texture, usedColor, isMirrored(), getRotation());
        reportDrawTexture();
    }

    /**
     * Drawing function. Draw a frame texture of the LWJGL sprite to a location
     * that is enlighten with the color that is set. Also the alpha value of
     * this color is taken into account for transparency effects. The texture is
     * scaled by the value set.
     * 
     * @param x the x coordinate of the location the texture shall been drawn at
     * @param y the y coordinate of the location the texture shall been drawn at
     * @param color the color the texture of the sprite is rendered with
     * @param frame the frame that is rendered
     * @param scale the scaling value the height and the width is reduced with
     */
    @Override
    @SuppressWarnings("nls")
    public void draw(final int x, final int y, final SpriteColor color,
        final int frame, final float scale) {
        final TextureLWJGL texture = (TextureLWJGL) getTexture(frame);
        if (texture == null) {
            throw new IllegalArgumentException("Can't render a frame that is "
                + "not set.");
        }

        final int texWidth = texture.getImageWidth();
        final int texHeight = texture.getImageHeight();

        SpriteColor usedColor = color;
        if (color == null) {
            usedColor = DEFAULT_LIGHT;
        }

        texRender.drawTexture(x + (getOffsetX() * scale), y
            + (getOffsetY() * scale), 0, texWidth * scale, texHeight * scale,
            texture, usedColor, isMirrored(), getRotation());
        reportDrawTexture();
    }
}
