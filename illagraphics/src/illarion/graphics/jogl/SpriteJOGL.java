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
package illarion.graphics.jogl;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;
import illarion.graphics.Texture;
import illarion.graphics.jogl.render.AbstractTextureRender;

/**
 * JOGL implementation of the sprite interface that uses JOGL to render the
 * sprite on the screen.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class SpriteJOGL implements Sprite {
    /**
     * The default light that is used in case there is not light set for the
     * rendering.
     */
    protected static final SpriteColor DEFAULT_LIGHT;

    /**
     * The count of textures drawn.
     */
    private static int drawnTextures = 0;

    /**
     * The last fetched value of drawn objects.
     */
    private static int lastDrawnTex;

    static {
        DEFAULT_LIGHT = Graphics.getInstance().getSpriteColor();
        DEFAULT_LIGHT.set(SpriteColor.COLOR_MAX);
        DEFAULT_LIGHT.setAlpha(SpriteColor.COLOR_MAX);
    }

    /**
     * Get the count of drawn textures.
     * 
     * @return the count of drawn textures
     */
    public static int getDrawnObjects() {
        return lastDrawnTex;
    }

    /**
     * This function is supposed to be called, when ever a texture is drawn. Its
     * used to generate the statistics.
     */
    protected static void reportDrawTexture() {
        drawnTextures++;
    }

    /**
     * Reset the count of drawn objects and update the readable value.
     */
    public static void resetDrawCount() {
        lastDrawnTex = drawnTextures;
        drawnTextures = 0;
    }

    /**
     * The horizontal align that is used at rendering this sprite.
     */
    private HAlign hAlignUsed;

    /**
     * Flag for horizontal mirroring. Activating this flag results in a mirrored
     * display for this texture.
     */
    private boolean mirror;

    /**
     * The x offset of the sprite, so the amount of pixels the origin of the
     * sprite is moved horizontal.
     */
    private int offsetX;

    /**
     * The y offset of the sprite, so the amount of pixels the origin of the
     * sprite is moved vertical.
     */
    private int offsetY;

    /**
     * The rotation degree of the rendered sprite. Value in degree.
     */
    private float rotation;

    /**
     * The textures that are loaded to this Sprite and can be rendered.
     */
    private final TextureJOGL[] textures;

    /**
     * The amount of textures that are currently not set.
     */
    private int unsetTextures;

    /**
     * The vertical align that is used at rendering this sprite.
     */
    private VAlign vAlignUsed;

    /**
     * The default constructor of the LWJGL Sprite. This one does nothing on its
     * own.
     * 
     * @param frames the amount of frames stored in this sprite
     */
    public SpriteJOGL(final int frames) {
        hAlignUsed = HAlign.left;
        vAlignUsed = VAlign.top;
        textures = new TextureJOGL[frames];
        unsetTextures = frames;
        offsetX = 0;
        offsetY = 0;
        mirror = false;
        texRender = AbstractTextureRender.getInstance();
    }
    
    /**
     * Copy constructor creates a new sprite that is at the time of coping
     * identical to the original sprite. How ever its possible to change the
     * sprite later.
     * 
     * @param org the original sprite
     */
    public SpriteJOGL(final SpriteJOGL org) {
        hAlignUsed = org.hAlignUsed;
        vAlignUsed = org.vAlignUsed;
        textures = org.textures.clone();
        unsetTextures = org.unsetTextures;
        offsetX = org.offsetX;
        offsetY = org.offsetY;
        mirror = org.mirror;
        texRender = org.texRender;
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

        if (!(newTexture instanceof TextureJOGL)) {
            throw new IllegalArgumentException(
                "Added texture is not a correct " + "texture");
        }

        if ((textures.length - unsetTextures) > 0) {
            final TextureJOGL firstTex = textures[0];

            if ((firstTex.getImageHeight() != newTexture.getImageHeight())
                || (firstTex.getImageWidth() != newTexture.getImageWidth())) {
                throw new IllegalArgumentException(
                    "Texture size does not fit.");
            }
        }

        textures[textures.length - unsetTextures] =
            (TextureJOGL) newTexture;
        unsetTextures--;
    }

    /**
     * Get the additional offset that is caused by the align of the sprite. This
     * function returns the x share of the offset caused by the align.
     * 
     * @param width the width of the image that is drawn
     * @return the x share of the offset caused by the sprite align
     */
    protected final int getAlignOffsetX(final int width) {
        if (hAlignUsed == HAlign.center) {
            return -(width >> 1);
        } else if (hAlignUsed == HAlign.right) {
            return -width;
        }
        return 0;
    }

    /**
     * Get the additional offset that is caused by the align of the sprite. This
     * function returns the y share of the offset caused by the align.
     * 
     * @param height the height of the image that is drawn
     * @return the y share of the offset caused by the sprite align
     */
    protected final int getAlignOffsetY(final int height) {
        if (vAlignUsed == VAlign.middle) {
            return -(height >> 1);
        } else if (vAlignUsed == VAlign.top) {
            return -height;
        }
        return 0;
    }

    /**
     * The the default light instance of this sprite implementation.
     * 
     * @return the default light object
     */
    @Override
    public final SpriteColor getDefaultLight() {
        return DEFAULT_LIGHT;
    }

    /**
     * Get the amount of textures that are set to this sprite.
     * 
     * @return the amount of textures that are set to this sprite
     */
    @Override
    public final int getFrames() {
        return textures.length;
    }

    /**
     * Get the height of the sprite textures.
     * 
     * @return the height of the sprite textures and -1 in case there are no
     *         textures set yet
     */
    @Override
    public final int getHeight() {
        final TextureJOGL texture = textures[0];
        if (texture == null) {
            return -1;
        }
        return texture.getImageHeight();
    }

    /**
     * Get the offset in X direction of the sprite.
     * 
     * @return the offset in pixel that is added to the drawing position
     */
    @Override
    public final int getOffsetX() {
        if (hAlignUsed == HAlign.center) {
            return -(getWidth() >> 1) + offsetX;
        } else if (hAlignUsed == HAlign.right) {
            return -getWidth() + offsetX;
        }
        return offsetX;
    }

    /**
     * Get the offset in Y direction of the sprite.
     * 
     * @return the offset in pixel that is added to the drawing position
     */
    @Override
    public final int getOffsetY() {
        if (vAlignUsed == VAlign.middle) {
            return -(getHeight() >> 1) + offsetY;
        } else if (vAlignUsed == VAlign.top) {
            return -getHeight() + offsetY;
        }
        return offsetY;
    }

    /**
     * Get the uncorrected offset of this sprite.
     * 
     * @return the raw sprite offset
     */
    protected final int getRawOffsetX() {
        return offsetX;
    }

    /**
     * Get the uncorrected offset of this sprite.
     * 
     * @return the raw sprite offset
     */
    protected final int getRawOffsetY() {
        return offsetY;
    }

    /**
     * Get the rotation that is applied to this sprite.
     * 
     * @return the rotation applied to this sprite
     */
    protected final float getRotation() {
        return rotation;
    }

    /**
     * Get the offset in X direction of the sprite in case its rendered with a
     * set scaling value.
     * 
     * @param scale the scaling value that is assumed to be used
     * @return the offset in pixel that is added to the drawing position
     */
    @Override
    public final int getScaledOffsetX(final float scale) {
        int retOffset = (int) (offsetX * scale);

        if (hAlignUsed == HAlign.center) {
            retOffset += (-getWidth() * scale) / 2.f;
        } else if (hAlignUsed == HAlign.right) {
            retOffset += -getWidth() * scale;
        }
        return retOffset;
    }

    /**
     * Get the offset in Y direction of the sprite in case its rendered with a
     * set scaling value.
     * 
     * @param scale the scaling value that is assumed to be used
     * @return the offset in pixel that is added to the drawing position
     */
    @Override
    public final int getScaledOffsetY(final float scale) {
        int retOffset = (int) (offsetY * scale);

        if (vAlignUsed == VAlign.middle) {
            retOffset += (-getHeight() * scale) / 2;
        } else if (vAlignUsed == VAlign.top) {
            retOffset += -getHeight() * scale;
        }
        return retOffset;
    }

    /**
     * Get the texture at a specified index.
     * 
     * @param index the index of the texture
     * @return the texture at the index
     */
    @Override
    public final TextureJOGL getTexture(final int index) {
        return textures[index];
    }

    /**
     * Get the width of the sprite textures.
     * 
     * @return the width of the sprite textures and -1 in case there are no
     *         textures set yet
     */
    @Override
    public final int getWidth() {
        final TextureJOGL texture = textures[0];
        if (texture == null) {
            return -1;
        }
        return texture.getImageWidth();
    }

    /**
     * Get if the texture on the sprite is supposed to be displayed mirrored.
     * 
     * @return <code>true</code> in case the texture needs to be displayed
     *         mirrored
     */
    protected final boolean isMirrored() {
        return mirror;
    }

    /**
     * This function cleans up all textures of the sprite. So all textures get
     * removed from the sprite after this call. This should only be called in
     * case the sprite is not used anymore for sure.
     */
    @Override
    public final void remove() {
        for (final TextureJOGL texture : textures) {
            texture.remove();
            texture.cleanup();
            unsetTextures++;
        }
    }

    /**
     * Set the align that shall be used at rendering the sprite.
     * 
     * @param horzAlign the new horizontal align that shall be used for
     *            rendering the sprite
     * @param vertAlign the new vertical align that shall be used for rendering
     *            the sprite
     */
    @Override
    public final void setAlign(final HAlign horzAlign, final VAlign vertAlign) {
        hAlignUsed = horzAlign;
        vAlignUsed = vertAlign;
    }

    /**
     * Change the mirror flag. Activating this flag results in a mirrored
     * display for the texture.
     * 
     * @param newMirror new value for the mirror flag
     */
    @Override
    public final void setMirror(final boolean newMirror) {
        mirror = newMirror;
    }

    /**
     * Set the offset values for this sprite. The origin of the texture will be
     * moved and so the position relative to the location where the sprite shall
     * be drawn.
     * 
     * @param xOffset the new value of the horizontal offset
     * @param yOffset the new value of the vertical offset
     */
    @Override
    public final void setOffset(final int xOffset, final int yOffset) {
        offsetX = xOffset;
        offsetY = yOffset;
    }

    /**
     * Set the rotation that is applied to the rendered texture. Default is 0
     * degree.
     * 
     * @param degree the rotation degree that is supposed to be applied to the
     *            texture
     */
    @Override
    public final void setRotation(final float degree) {
        rotation = degree;
    }
    
    /**
     * The texture render that is used to draw the textures.
     */
    private final AbstractTextureRender texRender;

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

        final TextureJOGL texture = (TextureJOGL) getTexture(frame);
        if (texture == null) {
            throw new IllegalArgumentException("Failed to get proper texture.");
        }

        final int alignOffX = getAlignOffsetX(w);
        final int alignOffY = getAlignOffsetY(h);

        final float wScale = (float) w / (float) getWidth();
        final float hScale = (float) h / (float) getHeight();

        SpriteColor usedColor = color;
        if (color == null) {
            usedColor = DEFAULT_LIGHT;
        }

        texRender.drawTexture(x + (getRawOffsetX() * wScale) + alignOffX, y
            + (getRawOffsetY() * hScale) + alignOffY, 0, w, h, texture,
            usedColor, isMirrored(), getRotation());
        reportDrawTexture();
    }

    /**
     * Simple drawing function. Draw the first texture of the JOGL sprite to a
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
     * Simple drawing function. Draw the first texture of the JOGL sprite to a
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
        final TextureJOGL texture = (TextureJOGL) getTexture(frame);
        if (texture == null) {
            throw new IllegalArgumentException("Failed to get proper texture.");
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
        final TextureJOGL texture = (TextureJOGL) getTexture(frame);
        if (texture == null) {
            throw new IllegalArgumentException("Failed to get proper texture.");
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

    @Override
    public void drawNasty(int x, int y, int w, int h, int centerX,
        int centerY, float scale, SpriteColor color) {
        final GL2 gl = GLU.getCurrentGL().getGL2();
        
        final TextureJOGL tex = getTexture(0);
        
        DriverSettingsJOGL.getInstance().enableMode(gl,
            DriverSettingsJOGL.Modes.DRAWTEXTURE);
        
        tex.enable();
        
        gl.glPushMatrix();
        gl.glTranslatef(centerX, centerY, 0.0f);
        gl.glScalef(scale, scale, 1.0f);
        gl.glTranslatef(-(centerX), -(centerY), 0.0f);

        color.setActiveColor();
        gl.glBegin(GL2.GL_QUADS);
          gl.glTexCoord2f(tex.getRelX1(), tex.getRelY1()); gl.glVertex2i(x,     y);
          gl.glTexCoord2f(tex.getRelX2(), tex.getRelY1()); gl.glVertex2i(x + w, y);
          gl.glTexCoord2f(tex.getRelX2(), tex.getRelY2()); gl.glVertex2i(x + w, y + h);
          gl.glTexCoord2f(tex.getRelX1(), tex.getRelY2()); gl.glVertex2i(x,     y + h);
        gl.glEnd();

        gl.glPopMatrix();
    }
}
