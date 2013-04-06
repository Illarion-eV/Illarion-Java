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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import illarion.common.types.Rectangle;
import org.illarion.engine.graphic.*;
import org.illarion.engine.graphic.effects.TextureEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the graphics engine implementation that uses libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxGraphics implements Graphics {
    /**
     * The libGDX graphics instance that is used to display the graphics.
     */
    @Nonnull
    private final com.badlogic.gdx.Graphics gdxGraphics;

    /**
     * The sprite batch used to perform the batch rendering.
     */
    @Nonnull
    private final SpriteBatch spriteBatch;

    /**
     * This is a temporary color that is used only to transfer data to the libGDX functions.
     */
    @Nonnull
    private final com.badlogic.gdx.graphics.Color tempColor1;

    /**
     * This is a temporary color that is used only to transfer data to the libGDX functions.
     */
    @Nonnull
    private final com.badlogic.gdx.graphics.Color tempColor2;

    /**
     * This is a temporary color that is used only to transfer data to the libGDX functions.
     */
    @Nonnull
    private final com.badlogic.gdx.graphics.Color tempColor3;

    /**
     * This is a temporary color that is used only to transfer data to the libGDX functions.
     */
    @Nonnull
    private final com.badlogic.gdx.graphics.Color tempColor4;

    /**
     * The shape renderer used to draw primitive shapes.
     */
    @Nonnull
    private final ShapeRenderer shapeRenderer;

    /**
     * This is a temporary texture region instance that is used for some calculations.
     */
    @Nonnull
    private final TextureRegion tempRegion;

    /**
     * A temporary rectangle used for some calculations.
     */
    @Nonnull
    private final com.badlogic.gdx.math.Rectangle tempGdxRectangle;

    /**
     * A temporary rectangle used for some calculations.
     */
    @Nonnull
    private final Rectangle tempEngineRectangle;

    /**
     * The transformation matrix that is applied to all render operations.
     */
    @Nonnull
    private final Matrix4 transformationMatrix;

    /**
     * Create a new instance of the graphics engine that is using libGDX to render.
     *
     * @param gdxGraphics the libGDX graphics instance that is used
     */
    GdxGraphics(@Nonnull final com.badlogic.gdx.Graphics gdxGraphics) {
        this.gdxGraphics = gdxGraphics;
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        tempColor1 = new com.badlogic.gdx.graphics.Color();
        tempColor2 = new com.badlogic.gdx.graphics.Color();
        tempColor3 = new com.badlogic.gdx.graphics.Color();
        tempColor4 = new com.badlogic.gdx.graphics.Color();
        tempRegion = new TextureRegion();
        tempGdxRectangle = new com.badlogic.gdx.math.Rectangle();
        tempEngineRectangle = new Rectangle();
        transformationMatrix = new Matrix4();

        shapeRenderer.setTransformMatrix(transformationMatrix);
        spriteBatch.setTransformMatrix(transformationMatrix);
    }

    /**
     * This function needs to be called before all rendering operations of a frame. It will setup the render system.
     */
    void beginFrame() {
        clear();
        spriteBatch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
    }

    /**
     * This function needs to be called after rendering a frame.
     */
    void endFrame() {
        spriteBatch.end();
        shapeRenderer.end();
    }


    @Override
    public void clear() {
        gdxGraphics.getGLCommon().glClearColor(0.f, 0.f, 0.f, 0.f);
        gdxGraphics.getGLCommon().glClear(GL10.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void drawSprite(@Nonnull final Sprite sprite, final int posX, final int posY, @Nonnull final Color color,
                           final int frame, final float scale, final float rotation,
                           @Nonnull final TextureEffect... effects) {
        if (sprite instanceof GdxSprite) {
            final GdxSprite gdxSprite = (GdxSprite) sprite;

            gdxSprite.getDisplayArea(posX, posY, scale, rotation, tempEngineRectangle);
            final int centerTransX = Math.round(gdxSprite.getWidth() * gdxSprite.getCenterX());
            final int centerTransY = Math.round(gdxSprite.getHeight() * gdxSprite.getCenterY());

            drawTexture(gdxSprite.getFrame(frame), tempEngineRectangle.getX(), tempEngineRectangle.getY(),
                    tempEngineRectangle.getWidth(), tempEngineRectangle.getHeight(), 0, 0, gdxSprite.getWidth(),
                    gdxSprite.getHeight(), centerTransX, centerTransY, rotation, color, effects);
        }
    }

    @Override
    public void setBlendingMode(@Nonnull final BlendingMode mode) {
        switch (mode) {
            case AlphaBlend:
                spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case Multiply:
                spriteBatch.setBlendFunction(GL10.GL_DST_COLOR, GL10.GL_ZERO);
                break;
        }
    }

    @Override
    public void drawText(@Nonnull final Font font, @Nonnull final CharSequence text, @Nonnull final Color color,
                         final int x, final int y) {
        if (font instanceof GdxFont) {
            shapeRenderer.flush();
            transferColor(color, tempColor1);
            spriteBatch.setColor(tempColor1);
            final BitmapFont bitmapFont = ((GdxFont) font).getBitmapFont();
            bitmapFont.setScale(1.f);
            bitmapFont.draw(spriteBatch, text, x, fixY(y));
        }
    }

    /**
     * Transfer the color values from a game engine color instance to a libGDX color instance.
     *
     * @param source the engine color instance that is the source of the color data
     * @param target the libGDX color instance that is the target of the color data
     */
    static void transferColor(@Nonnull final Color source, @Nonnull final com.badlogic.gdx.graphics.Color target) {
        target.set(source.getRedf(), source.getGreenf(), source.getBluef(), source.getAlphaf());
    }

    @Override
    public void drawText(@Nonnull final Font font, @Nonnull final CharSequence text, @Nonnull final Color color,
                         final int x, final int y, final float scaleX, final float scaleY) {
        if (font instanceof GdxFont) {
            shapeRenderer.flush();
            transferColor(color, tempColor1);
            spriteBatch.setColor(tempColor1);
            final BitmapFont bitmapFont = ((GdxFont) font).getBitmapFont();
            bitmapFont.setScale(scaleX, scaleY);
            bitmapFont.draw(spriteBatch, text, x, fixY(y));
        }
    }

    @Override
    public void drawRectangle(final int x, final int y, final int width, final int height, @Nonnull final Color color) {
        spriteBatch.flush();
        transferColor(color, tempColor1);
        shapeRenderer.setColor(tempColor1);
        shapeRenderer.filledRect(x, fixY(y), width, height);
    }

    @Override
    public void drawRectangle(@Nonnull final Rectangle rectangle, @Nonnull final Color color) {
        drawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), color);
    }

    @Override
    public void drawRectangle(final int x, final int y, final int width, final int height,
                              @Nonnull final Color topLeftColor, @Nonnull final Color topRightColor,
                              @Nonnull final Color bottomLeftColor, @Nonnull final Color bottomRightColor) {
        spriteBatch.flush();
        transferColor(topLeftColor, tempColor1);
        transferColor(topRightColor, tempColor2);
        transferColor(bottomLeftColor, tempColor3);
        transferColor(bottomRightColor, tempColor4);
        shapeRenderer.filledRect(x, fixY(y), width, height, tempColor3, tempColor4, tempColor2, tempColor1);
    }

    @Override
    public void drawTexture(@Nonnull final Texture texture, final int x, final int y, final int width,
                            final int height, @Nonnull final Color color, @Nonnull final TextureEffect... effects) {
        if (texture instanceof GdxTexture) {
            shapeRenderer.flush();
            transferColor(color, tempColor1);

            @Nullable final GdxTextureEffect usedEffect;
            if ((effects.length > 0) && (effects[0] instanceof GdxTextureEffect)) {
                usedEffect = (GdxTextureEffect) effects[0];
            } else {
                usedEffect = null;
            }
            if (usedEffect != null) {
                usedEffect.activateEffect(spriteBatch);
            }
            spriteBatch.setColor(tempColor1);
            spriteBatch.draw(((GdxTexture) texture).getTextureRegion(), x, fixY(y), width, height);

            if (usedEffect != null) {
                usedEffect.disableEffect(spriteBatch);
            }
        }
    }

    @Override
    public void drawTexture(@Nonnull final Texture texture, final int x, final int y, final int width,
                            final int height, final int texX, final int texY, final int texWidth,
                            final int texHeight, @Nonnull final Color color, @Nonnull final TextureEffect... effects) {
        if (texture instanceof GdxTexture) {
            shapeRenderer.flush();
            transferColor(color, tempColor1);

            @Nullable final GdxTextureEffect usedEffect;
            if ((effects.length > 0) && (effects[0] instanceof GdxTextureEffect)) {
                usedEffect = (GdxTextureEffect) effects[0];
            } else {
                usedEffect = null;
            }
            if (usedEffect != null) {
                usedEffect.activateEffect(spriteBatch);
            }
            spriteBatch.setColor(tempColor1);
            tempRegion.setRegion(((GdxTexture) texture).getTextureRegion(), texX, texY, texWidth, texHeight);
            spriteBatch.draw(tempRegion, x, fixY(y), width, height);

            if (usedEffect != null) {
                usedEffect.disableEffect(spriteBatch);
            }
        }
    }

    @Override
    public void drawTexture(@Nonnull final Texture texture, final int x, final int y, final int width,
                            final int height, final int texX, final int texY, final int texWidth,
                            final int texHeight, final int centerX, final int centerY, final float rotate,
                            @Nonnull final Color color, @Nonnull final TextureEffect... effects) {
        if (texture instanceof GdxTexture) {
            shapeRenderer.flush();
            transferColor(color, tempColor1);

            @Nullable final GdxTextureEffect usedEffect;
            if ((effects.length > 0) && (effects[0] instanceof GdxTextureEffect)) {
                usedEffect = (GdxTextureEffect) effects[0];
            } else {
                usedEffect = null;
            }
            if (usedEffect != null) {
                usedEffect.activateEffect(spriteBatch);
            }

            spriteBatch.setColor(tempColor1);
            tempRegion.setRegion(((GdxTexture) texture).getTextureRegion(), texX, texY, texWidth, texHeight);
            spriteBatch.draw(tempRegion, x, fixY(y), centerX, centerY, width, height, 1.f, 1.f, rotate, true);

            if (usedEffect != null) {
                usedEffect.disableEffect(spriteBatch);
            }
        }
    }

    private int fixY(final int y) {
        return gdxGraphics.getHeight() - y;
    }

    @Override
    public void setClippingArea(final int x, final int y, final int width, final int height) {
        gdxGraphics.getGLCommon().glEnable(GL10.GL_SCISSOR_TEST);
        gdxGraphics.getGLCommon().glScissor(x, y, width, height);
    }

    @Override
    public void unsetClippingArea() {
        gdxGraphics.getGLCommon().glDisable(GL10.GL_SCISSOR_TEST);
    }

    /**
     * Apply a global offset to all following render operations.
     *
     * @param offsetX the x component of the offset
     * @param offsetY the y component of the offset
     */
    void applyOffset(final int offsetX, final int offsetY) {
        transformationMatrix.translate(offsetX, offsetY, 0.f);
        spriteBatch.setTransformMatrix(transformationMatrix);
        shapeRenderer.setTransformMatrix(transformationMatrix);
    }

    /**
     * Reset any global offset applied.
     */
    void resetOffset() {
        transformationMatrix.idt();
        spriteBatch.setTransformMatrix(transformationMatrix);
        shapeRenderer.setTransformMatrix(transformationMatrix);
    }
}
