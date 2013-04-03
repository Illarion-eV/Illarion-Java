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
package org.illarion.engine.backend.slick;

import org.illarion.engine.graphic.*;
import org.illarion.engine.graphic.effects.TextureEffect;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.opengl.renderer.Renderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the graphics engine implementation for Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickGraphics implements Graphics {
    /**
     * This is a special implementation of the Slick rectangle that allows to render the four different colors of a
     * rectangle.
     */
    private static final class FourColorRect extends Rectangle implements ShapeFill {
        /**
         * This is the vector that is returned as offset at all times.
         */
        private static final Vector2f NULL_VECTOR = new Vector2f(0, 0);

        /**
         * The color on the bottom left corner of the shape.
         */
        private final org.newdawn.slick.Color bottomLeft;

        /**
         * The color on the bottom right corner of the shape.
         */
        private final org.newdawn.slick.Color bottomRight;

        /**
         * The color on the top left corner of the shape.
         */
        private final org.newdawn.slick.Color topLeft;

        /**
         * The color on the top right corner of the shape.
         */
        private final org.newdawn.slick.Color topRight;

        /**
         * The default constructor.
         */
        FourColorRect() {
            super(0, 0, 0, 0);
            bottomLeft = new org.newdawn.slick.Color(org.newdawn.slick.Color.white);
            bottomRight = new org.newdawn.slick.Color(org.newdawn.slick.Color.white);
            topLeft = new org.newdawn.slick.Color(org.newdawn.slick.Color.white);
            topRight = new org.newdawn.slick.Color(org.newdawn.slick.Color.white);
        }

        @Override
        public org.newdawn.slick.Color colorAt(final Shape shape, final float v, final float v2) {
            final boolean isMaxX = Math.abs(x - shape.getMaxX()) < 0.0f;
            final boolean isMaxY = Math.abs(y - shape.getMaxY()) < 0.0f;

            //noinspection IfMayBeConditional
            if (isMaxX) {
                return isMaxY ? topRight : topLeft;
            }
            return isMaxY ? bottomRight : bottomLeft;
        }

        @Override
        public Vector2f getOffsetAt(final Shape shape, final float v, final float v2) {
            return NULL_VECTOR;
        }

        /**
         * Set the colors that will be rendered using this rectangle.
         *
         * @param topLeft     the color in the top left corner
         * @param topRight    the color in the top right corner
         * @param bottomLeft  the color in the bottom left corner
         * @param bottomRight the color in the bottom right corner
         */
        public void setColors(@Nonnull final Color topLeft, @Nonnull final Color topRight,
                              @Nonnull final Color bottomLeft, @Nonnull final Color bottomRight) {
            transferColor(bottomLeft, this.bottomLeft);
            transferColor(bottomRight, this.bottomRight);
            transferColor(topLeft, this.topLeft);
            transferColor(topRight, this.topRight);
        }
    }

    /**
     * This is the instance of the four colored rectangle that is used in all cases.
     */
    private final FourColorRect fourColorRect = new FourColorRect();

    /**
     * This is a temporary color instance of slick that is used to transfer the color data from the engines color
     * objects to the Slick rendering functions.
     */
    private final org.newdawn.slick.Color tempSlickColor1 = new org.newdawn.slick.Color(org.newdawn.slick.Color.white);

    /**
     * The instance of the slick graphics class that is used for the rendering operations.
     */
    @Nullable
    private org.newdawn.slick.Graphics slickGraphicsImpl;

    /**
     * This function is used to transfer the color values from the engines color instances to a slick color instance.
     *
     * @param source the source color
     * @param target the target color
     */
    static void transferColor(@Nonnull final Color source, @Nonnull final org.newdawn.slick.Color target) {
        target.r = source.getRedf();
        target.g = source.getGreenf();
        target.b = source.getBluef();
        target.a = source.getAlphaf();
    }

    @Override
    public void clear() {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        slickGraphicsImpl.clear();
    }

    /**
     * Clear the graphics instance. This should be done after the rendering loop.
     */
    void clearSlickGraphicsImpl() {
        slickGraphicsImpl = null;
    }

    @Override
    public void drawRectangle(final int x, final int y, final int width, final int height, @Nonnull final Color color) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        transferColor(color, tempSlickColor1);
        slickGraphicsImpl.setColor(tempSlickColor1);
        slickGraphicsImpl.fillRect(x, y, width, height);
    }

    @Override
    public void drawRectangle(@Nonnull final illarion.common.types.Rectangle rectangle, @Nonnull final Color color) {
        drawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), color);
    }

    @Override
    public void drawRectangle(final int x, final int y, final int width, final int height, @Nonnull final Color topLeftColor, @Nonnull final Color topRightColor, @Nonnull final Color bottomLeftColor, @Nonnull final Color bottomRightColor) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        fourColorRect.setColors(topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
        fourColorRect.setLocation(x, y);
        fourColorRect.setSize(width, height);
        slickGraphicsImpl.fill(fourColorRect, fourColorRect);
    }

    /**
     * The rectangle used to exchange data with the drawn components. Its only used inside a single drawing function,
     * never beyond that.
     */
    @Nonnull
    private final illarion.common.types.Rectangle tempRect = new illarion.common.types.Rectangle();

    @Override
    public void drawSprite(@Nonnull final Sprite sprite, final int posX, final int posY, @Nonnull final Color color,
                           final int frame, final float scale, final float rotation,
                           @Nonnull final TextureEffect... effects) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        if (sprite instanceof SlickSprite) {
            final SlickSprite slickSprite = (SlickSprite) sprite;

            slickGraphicsImpl.pushTransform();
            slickGraphicsImpl.translate(posX, posY);

            if (slickSprite.isMirrored()) {
                slickGraphicsImpl.scale(-scale, scale);
            } else {
                slickGraphicsImpl.scale(scale, scale);
            }
            final float centerTransX = slickSprite.getWidth() * slickSprite.getCenterX();
            final float centerTransY = slickSprite.getHeight() * slickSprite.getCenterY();
            transferColor(color, tempSlickColor1);
            slickSprite.getDisplayArea(0, 0, 1.f, 0.f, tempRect);

            @Nullable SlickTextureEffect usedEffect = null;
            if ((effects.length > 0) && (effects[0] instanceof SlickTextureEffect)) {
                usedEffect = (SlickTextureEffect) effects[0];
            }

            if (usedEffect != null) {
                usedEffect.activateEffect(slickGraphicsImpl);
            }

            final Image slickImage = slickSprite.getFrame(frame).getBackingImage();
            slickImage.setCenterOfRotation(centerTransX, centerTransY);
            slickImage.setRotation(rotation);
            slickGraphicsImpl.drawImage(slickImage, tempRect.getX(), tempRect.getY(), tempSlickColor1);

            if (usedEffect != null) {
                usedEffect.disableEffect(slickGraphicsImpl);
            }
            slickGraphicsImpl.popTransform();
        }
    }

    @Override
    public void drawText(@Nonnull final Font font, @Nonnull final CharSequence text, @Nonnull final Color color, final int x, final int y) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        if (font instanceof SlickFont) {
            final AngelCodeFont internalFont = ((SlickFont) font).getInternalFont();
            org.newdawn.slick.Graphics.setCurrent(slickGraphicsImpl);
            transferColor(color, tempSlickColor1);
            internalFont.drawString(x, y, text, tempSlickColor1);
        }
    }

    @Override
    public void drawText(@Nonnull final Font font, @Nonnull final CharSequence text, @Nonnull final Color color,
                         final int x, final int y, final float scaleX, final float scaleY) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        if (font instanceof SlickFont) {
            final AngelCodeFont internalFont = ((SlickFont) font).getInternalFont();
            org.newdawn.slick.Graphics.setCurrent(slickGraphicsImpl);
            slickGraphicsImpl.pushTransform();
            slickGraphicsImpl.translate(x, y);
            slickGraphicsImpl.scale(scaleX, scaleY);
            transferColor(color, tempSlickColor1);
            internalFont.drawString(0, 0, text, tempSlickColor1);
            slickGraphicsImpl.popTransform();
        }
    }

    @Override
    public void drawTexture(@Nonnull final Texture texture, final int x, final int y, final int width,
                            final int height, @Nonnull final Color color, @Nonnull final TextureEffect... effects) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        final SlickTexture slickTexture = (SlickTexture) texture;
        transferColor(color, tempSlickColor1);

        @Nullable SlickTextureEffect usedEffect = null;
        if ((effects.length > 0) && (effects[0] instanceof SlickTextureEffect)) {
            usedEffect = (SlickTextureEffect) effects[0];
        }

        if (usedEffect != null) {
            usedEffect.activateEffect(slickGraphicsImpl);
        }
        slickGraphicsImpl.drawImage(slickTexture.getBackingImage(), x, y, x + width, y + height, 0, 0,
                slickTexture.getWidth(), slickTexture.getHeight(), tempSlickColor1);

        if (usedEffect != null) {
            usedEffect.disableEffect(slickGraphicsImpl);
        }
    }

    @Override
    public void drawTexture(@Nonnull final Texture texture, final int x, final int y, final int width,
                            final int height, final int texX, final int texY, final int texWidth,
                            final int texHeight, @Nonnull final Color color,
                            @Nonnull final TextureEffect... effects) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        final SlickTexture slickTexture = (SlickTexture) texture;

        transferColor(color, tempSlickColor1);
        @Nullable SlickTextureEffect usedEffect = null;
        if ((effects.length > 0) && (effects[0] instanceof SlickTextureEffect)) {
            usedEffect = (SlickTextureEffect) effects[0];
        }

        if (usedEffect != null) {
            usedEffect.activateEffect(slickGraphicsImpl);
        }
        slickGraphicsImpl.drawImage(slickTexture.getBackingImage(), x, y, x + width, y + height, texX, texY,
                texX + texWidth, texY + texHeight, tempSlickColor1);

        if (usedEffect != null) {
            usedEffect.disableEffect(slickGraphicsImpl);
        }
    }

    @Override
    public void drawTexture(@Nonnull final Texture texture, final int x, final int y, final int width,
                            final int height, final int texX, final int texY, final int texWidth,
                            final int texHeight, final int centerX, final int centerY, final float rotate,
                            @Nonnull final Color color, @Nonnull final TextureEffect... effects) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        final SlickTexture slickTexture = (SlickTexture) texture;

        transferColor(color, tempSlickColor1);
        @Nullable SlickTextureEffect usedEffect = null;
        if ((effects.length > 0) && (effects[0] instanceof SlickTextureEffect)) {
            usedEffect = (SlickTextureEffect) effects[0];
        }

        slickGraphicsImpl.pushTransform();
        if (usedEffect != null) {
            usedEffect.activateEffect(slickGraphicsImpl);
        }
        slickGraphicsImpl.translate(centerX, centerY);
        slickGraphicsImpl.rotate(0, 0, rotate);
        slickGraphicsImpl.translate(-centerX, -centerY);
        slickGraphicsImpl.drawImage(slickTexture.getBackingImage(), x, y, x + width, y + height, texX, texY,
                texX + texWidth, texY + texHeight, tempSlickColor1);

        if (usedEffect != null) {
            usedEffect.disableEffect(slickGraphicsImpl);
        }
        slickGraphicsImpl.popTransform();
    }

    /**
     * Get the slick graphics engine.
     *
     * @return the slick graphics engine or {@code null} in case there currently is not active rendering operation
     */
    @Nullable
    org.newdawn.slick.Graphics getSlickGraphicsImpl() {
        return slickGraphicsImpl;
    }

    /**
     * Set the instance of the graphics that is supposed to be used to render graphics.
     *
     * @param graphics the slick graphics instance
     */
    @SuppressWarnings("NullableProblems")
    void setSlickGraphicsImpl(@Nonnull final org.newdawn.slick.Graphics graphics) {
        slickGraphicsImpl = graphics;
        setBlendingMode(BlendingMode.AlphaBlend);
    }

    @Override
    public void setBlendingMode(@Nonnull final BlendingMode mode) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        switch (mode) {
            case AlphaBlend:
                slickGraphicsImpl.setDrawMode(org.newdawn.slick.Graphics.MODE_NORMAL);
                Renderer.get().glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case Multiply:
                slickGraphicsImpl.setDrawMode(org.newdawn.slick.Graphics.MODE_NORMAL);
                Renderer.get().glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
                break;
        }
    }

    @Override
    public void setClippingArea(final int x, final int y, final int width, final int height) {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        slickGraphicsImpl.setWorldClip(x, y, width, height);
    }

    @Override
    public void unsetClippingArea() {
        if (slickGraphicsImpl == null) {
            throw new IllegalStateException("Using graphics outside of the render loop is not allowed.");
        }
        slickGraphicsImpl.clearWorldClip();
    }
}
