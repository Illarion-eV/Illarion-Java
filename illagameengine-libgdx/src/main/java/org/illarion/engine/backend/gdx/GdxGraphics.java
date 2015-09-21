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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Pools;
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
    @Nonnull
    private static final float[] FLT_BUFFER = new float[20];
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
    private final Rectangle tempEngineRectangle;
    /**
     * The camera that views the scene.
     */
    @Nonnull
    private final OrthographicCamera camera;
    /**
     * The engine implementation used for the rendering.
     */
    @Nonnull
    private final GdxEngine engine;
    /**
     * The blank background texture used to render rectangles.
     */
    @Nullable
    private Texture blankBackground;
    /**
     * This flag is set {@code true} in case the sprite batch rendering is currently activated.
     */
    private boolean spriteBatchActive;
    /**
     * The blending mode that was applied last.
     */
    @Nullable
    private BlendingMode lastBlendingMode;
    /**
     * This is set {@code true} in case the clipping is activated.
     */
    private boolean activeClipping;

    /**
     * Create a new instance of the graphics engine that is using libGDX to render.
     *
     * @param gdxGraphics the libGDX graphics instance that is used
     */
    GdxGraphics(@Nonnull GdxEngine engine, @Nonnull com.badlogic.gdx.Graphics gdxGraphics) {
        this.gdxGraphics = gdxGraphics;
        this.engine = engine;
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        tempColor1 = new com.badlogic.gdx.graphics.Color();
        tempColor2 = new com.badlogic.gdx.graphics.Color();
        tempColor3 = new com.badlogic.gdx.graphics.Color();
        tempColor4 = new com.badlogic.gdx.graphics.Color();
        tempRegion = new TextureRegion();
        tempEngineRectangle = new Rectangle();

        camera = new OrthographicCamera();
        camera.zoom = 1.f;
        camera.setToOrtho(true);
    }

    @Nonnull
    SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    /**
     * This function needs to be called before all rendering operations of a frame. It will setup the render system.
     */
    void beginFrame() {
        camera.setToOrtho(true, gdxGraphics.getWidth(), gdxGraphics.getHeight());
        resetOffset();
        clear();

        lastBlendingMode = null;
        setBlendingMode(BlendingMode.AlphaBlend);

        if (blankBackground == null) {
            blankBackground = engine.getAssets().getTextureManager().getTexture("gui/", "blank.png");
        }
    }

    /**
     * Reset any global offset applied.
     */
    void resetOffset() {
        applyOffset(0, 0);
    }

    @Override
    public void clear() {
        GL20 gl20 = gdxGraphics.getGL20();
        gl20.glClearColor(0.f, 0.f, 0.f, 1.f);
        gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void drawSprite(
            @Nonnull Sprite sprite,
            int posX,
            int posY,
            @Nonnull Color color,
            int frame,
            double scale,
            double rotation,
            @Nonnull TextureEffect... effects) {
        if (sprite instanceof GdxSprite) {
            GdxSprite gdxSprite = (GdxSprite) sprite;

            gdxSprite.getDisplayArea(posX, posY, scale, rotation, tempEngineRectangle);
            double centerTransX = (gdxSprite.getWidth() * gdxSprite.getCenterX()) + (gdxSprite.getOffsetX() * scale);
            double centerTransY = (gdxSprite.getHeight() * gdxSprite.getCenterY()) + (gdxSprite.getOffsetY() * scale);

            activateSpriteBatch();
            transferColor(color, tempColor1);

            @Nullable GdxTextureEffect usedEffect;
            if ((effects.length > 0) && (effects[0] instanceof GdxTextureEffect)) {
                usedEffect = (GdxTextureEffect) effects[0];
            } else {
                usedEffect = null;
            }
            spriteBatch.setColor(tempColor1);
            tempRegion.setRegion(gdxSprite.getFrame(frame).getTextureRegion());
            tempRegion.flip(gdxSprite.isMirrored(), true);
            if (usedEffect != null) {
                float u, u2;
                if (tempRegion.isFlipX()) {
                    u = tempRegion.getU();
                    u2 = tempRegion.getU2();
                } else {
                    u2 = tempRegion.getU();
                    u = tempRegion.getU2();
                }
                float v, v2;
                if (tempRegion.isFlipY()) {
                    v = tempRegion.getV();
                    v2 = tempRegion.getV2();
                } else {
                    v2 = tempRegion.getV();
                    v = tempRegion.getV2();
                }
                usedEffect.setTopLeftCoordinate(u2, v2);
                usedEffect.setBottomRightCoordinate(u, v);
                usedEffect.activateEffect(spriteBatch);
            }
            spriteBatch.draw(tempRegion, tempEngineRectangle.getX(), tempEngineRectangle.getY(), (float) centerTransX,
                    (float) centerTransY, tempEngineRectangle.getWidth(), tempEngineRectangle.getHeight(), 1.f,
                    1.f, (float) rotation);

            if (usedEffect != null) {
                usedEffect.disableEffect(spriteBatch);
            }
        }
    }

    private void activateSpriteBatch() {
        if (spriteBatchActive) {
            return;
        }

        if (shapeRenderer.getCurrentType() != null) {
            shapeRenderer.end();
        }
        spriteBatch.begin();
        spriteBatchActive = true;
    }

    /**
     * Transfer the color values from a game engine color instance to a libGDX color instance.
     *
     * @param source the engine color instance that is the source of the color data
     * @param target the libGDX color instance that is the target of the color data
     */
    static void transferColor(@Nonnull Color source, @Nonnull com.badlogic.gdx.graphics.Color target) {
        target.set(source.getRedf(), source.getGreenf(), source.getBluef(), source.getAlphaf());
        target.clamp();
    }

    @Override
    public void drawTileSprite(@Nonnull Sprite sprite, int posX, int posY, @Nonnull Color topColor,
                               @Nonnull Color bottomColor, @Nonnull Color leftColor, @Nonnull Color rightColor,
                               @Nonnull Color centerColor, int frame, @Nonnull TextureEffect... effects) {
        if (!(sprite instanceof GdxSprite)) {
            throw new IllegalArgumentException("The sprite is expected to be a sprite provided by this engine.");
        }

        /*
        This is something where libGDX provides no function for. So in this case we need to build the data array for
        OpenGL by hand.
         */

        GdxSprite gdxSprite = (GdxSprite) sprite;
        activateSpriteBatch();

        float bottomColorF = getFloatColor(bottomColor, tempColor1);
        float topColorF = getFloatColor(topColor, tempColor1);
        float leftColorF = getFloatColor(leftColor, tempColor1);
        float rightColorF = getFloatColor(rightColor, tempColor1);
        float centerColorF = getFloatColor(centerColor, tempColor1);

        TextureRegion textureRegion = gdxSprite.getFrame(frame).getTextureRegion();

        int width = sprite.getWidth() + 1;
        int height = sprite.getHeight() + 1;

        gdxSprite.getDisplayArea(posX, posY, 1.f, 0.f, tempEngineRectangle);
        float centerTransX = (width * gdxSprite.getCenterX()) + gdxSprite.getOffsetX();
        float centerTransY = (height * gdxSprite.getCenterY()) + gdxSprite.getOffsetY();

        float originX = posX - centerTransX;
        float originY = posY + centerTransY;

        float topX = originX + (width / 2.f);
        float topY = originY - height;
        float topU = textureRegion.getU() + ((textureRegion.getU2() - textureRegion.getU()) / 2.f);
        float topV = textureRegion.getV();

        float leftX = originX;
        float leftY = originY - (height / 2.f);
        float leftU = textureRegion.getU();
        float leftV = textureRegion.getV() + ((textureRegion.getV2() - textureRegion.getV()) / 2.f);

        float rightX = originX + width;
        float rightY = originY - (height / 2.f);
        float rightU = textureRegion.getU2();
        float rightV = textureRegion.getV() + ((textureRegion.getV2() - textureRegion.getV()) / 2.f);

        float bottomX = originX + (width / 2.f);
        float bottomY = originY;
        float bottomU = textureRegion.getU() + ((textureRegion.getU2() - textureRegion.getU()) / 2.f);
        float bottomV = textureRegion.getV2();


        float[] vertices = FLT_BUFFER;
        vertices[0] = topX;
        vertices[1] = topY;
        vertices[2] = topColorF;
        vertices[3] = topU;
        vertices[4] = topV;

        vertices[5] = leftX;
        vertices[6] = leftY;
        vertices[7] = leftColorF;
        vertices[8] = leftU;
        vertices[9] = leftV;

        vertices[10] = bottomX;
        vertices[11] = bottomY;
        vertices[12] = bottomColorF;
        vertices[13] = bottomU;
        vertices[14] = bottomV;

        vertices[15] = rightX;
        vertices[16] = rightY;
        vertices[17] = rightColorF;
        vertices[18] = rightU;
        vertices[19] = rightV;

        @Nullable GdxTextureEffect usedEffect;
        if ((effects.length > 0) && (effects[0] instanceof GdxTextureEffect)) {
            usedEffect = (GdxTextureEffect) effects[0];
        } else {
            usedEffect = null;
        }
        spriteBatch.setColor(tempColor1);
        tempRegion.setRegion(gdxSprite.getFrame(frame).getTextureRegion());
        tempRegion.flip(gdxSprite.isMirrored(), true);
        if (usedEffect != null) {
            float u, u2;
            if (tempRegion.isFlipX()) {
                u = tempRegion.getU();
                u2 = tempRegion.getU2();
            } else {
                u2 = tempRegion.getU();
                u = tempRegion.getU2();
            }
            float v, v2;
            if (tempRegion.isFlipY()) {
                v = tempRegion.getV();
                v2 = tempRegion.getV2();
            } else {
                v2 = tempRegion.getV();
                v = tempRegion.getV2();
            }
            usedEffect.setTopLeftCoordinate(u2, v2);
            usedEffect.setBottomRightCoordinate(u, v);
            usedEffect.activateEffect(spriteBatch);
        }
        spriteBatch.draw(textureRegion.getTexture(), FLT_BUFFER, 0, 20);

        if (usedEffect != null) {
            usedEffect.disableEffect(spriteBatch);
        }
    }

    @Override
    public void setBlendingMode(@Nonnull BlendingMode mode) {
        if (lastBlendingMode == mode) {
            return;
        }
        switch (mode) {
            case AlphaBlend:
                spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case Multiply:
                spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
                break;
        }
        spriteBatch.enableBlending();
        lastBlendingMode = mode;
    }

    @Override
    public void drawText(
            @Nonnull Font font, @Nonnull CharSequence text, @Nonnull Color color, int x, int y) {
        drawText(font, text, color, x, y, 1.f, 1.f);
    }

    @Override
    public void drawText(
            @Nonnull Font font,
            @Nonnull CharSequence text,
            @Nonnull Color color,
            int x,
            int y,
            double scaleX,
            double scaleY) {
        if (font instanceof GdxFont) {
            activateSpriteBatch();
            GdxFont gdxFont = (GdxFont) font;

            BitmapFont bitmapFont = gdxFont.getBitmapFont();
            bitmapFont.getData().setScale((float) scaleX, (float) scaleY);
            transferColor(color, tempColor1);
            bitmapFont.setColor(tempColor1);
            GlyphLayout layout = Pools.obtain(GlyphLayout.class);
            layout.setText(bitmapFont, text);

            BitmapFont outlineFont = gdxFont.getOutlineBitmapFont();
            if (outlineFont != null) {
                transferColor(Color.BLACK, tempColor1);
                tempColor1.a = color.getAlphaf();
                outlineFont.getData().setScale((float) scaleX, (float) scaleY);
                outlineFont.setColor(tempColor1);

                GlyphLayout outlineLayout = Pools.obtain(GlyphLayout.class);
                outlineLayout.setText(outlineFont, text);

                float widthOffset = (layout.width - outlineLayout.width) / 2.f;

                outlineFont.draw(spriteBatch, outlineLayout, x + widthOffset, y - outlineFont.getAscent());
                Pools.free(outlineLayout);
            }

            bitmapFont.draw(spriteBatch, layout, x, y - bitmapFont.getAscent());
            Pools.free(layout);
        }
    }

    @Override
    public void drawRectangle(int x, int y, int width, int height, @Nonnull Color color) {
        activateSpriteBatch();
        if (blankBackground == null) {
            return;
        }

        drawTexture(blankBackground, x, y, width, height, color);
    }

    @Override
    public void drawRectangle(@Nonnull Rectangle rectangle, @Nonnull Color color) {
        drawRectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), color);
    }

    @Override
    public void drawRectangle(
            int x,
            int y,
            int width,
            int height,
            @Nonnull Color topLeftColor,
            @Nonnull Color topRightColor,
            @Nonnull Color bottomLeftColor,
            @Nonnull Color bottomRightColor) {
        activateShapeRenderer();
        transferColor(topLeftColor, tempColor1);
        transferColor(topRightColor, tempColor2);
        transferColor(bottomLeftColor, tempColor3);
        transferColor(bottomRightColor, tempColor4);
        shapeRenderer.rect(x, y, width, height, tempColor3, tempColor4, tempColor2, tempColor1);
        shapeRenderer.end();
        gdxGraphics.getGL20().glDisable(GL20.GL_BLEND);
    }

    private void activateShapeRenderer() {
        if (shapeRenderer.getCurrentType() != null) {
            return;
        }
        if (spriteBatchActive) {
            spriteBatch.end();
            spriteBatchActive = false;
        }

        GL20 gl20 = gdxGraphics.getGL20();
        gl20.glEnable(GL20.GL_BLEND);
        assert lastBlendingMode != null;
        switch (lastBlendingMode) {
            case AlphaBlend:
                gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                break;
            case Multiply:
                gl20.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
                break;
        }
        shapeRenderer.begin(ShapeType.Filled);
    }

    @Override
    public void drawTexture(
            @Nonnull Texture texture,
            int x,
            int y,
            int width,
            int height,
            @Nonnull Color color,
            @Nonnull TextureEffect... effects) {
        if ((width == 0) || (height == 0)) {
            return;
        }
        if (texture instanceof GdxTexture) {
            activateSpriteBatch();
            transferColor(color, tempColor1);

            @Nullable GdxTextureEffect usedEffect;
            if ((effects.length > 0) && (effects[0] instanceof GdxTextureEffect)) {
                usedEffect = (GdxTextureEffect) effects[0];
            } else {
                usedEffect = null;
            }
            if (usedEffect != null) {
                usedEffect.activateEffect(spriteBatch);
            }
            spriteBatch.setColor(tempColor1);
            tempRegion.setRegion(((GdxTexture) texture).getTextureRegion());
            if (!tempRegion.isFlipY()) {
                tempRegion.flip(false, true);
            }
            spriteBatch.draw(tempRegion, x, y, width, height);

            if (usedEffect != null) {
                usedEffect.disableEffect(spriteBatch);
            }
        }
    }

    @Override
    public void drawTexture(
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
            @Nonnull TextureEffect... effects) {
        if ((width == 0) || (height == 0)) {
            return;
        }
        if (color.getAlpha() == 0 && effects.length == 0) {
            return;
        }
        if (texture instanceof GdxTexture) {
            activateSpriteBatch();
            transferColor(color, tempColor1);

            @Nullable GdxTextureEffect usedEffect;
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
            if (!tempRegion.isFlipY()) {
                tempRegion.flip(false, true);
            }
            spriteBatch.draw(tempRegion, x, y, width, height);

            if (usedEffect != null) {
                usedEffect.disableEffect(spriteBatch);
            }
        }
    }

    @Override
    public void drawTexture(
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
            @Nonnull TextureEffect... effects) {
        if ((width == 0) || (height == 0)) {
            return;
        }
        if (texture instanceof GdxTexture) {
            activateSpriteBatch();
            transferColor(color, tempColor1);

            @Nullable GdxTextureEffect usedEffect;
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
            if (!tempRegion.isFlipY()) {
                tempRegion.flip(false, true);
            }
            spriteBatch.draw(tempRegion, x, y, centerX, centerY, width, height, 1.f, 1.f, (float) rotate);

            if (usedEffect != null) {
                usedEffect.disableEffect(spriteBatch);
            }
        }
    }

    @Override
    public void setClippingArea(int x, int y, int width, int height) {
        if (activeClipping) {
            unsetClippingArea();
        }
        if ((x == 0) && (y == 0) && (width == gdxGraphics.getWidth()) && (height == gdxGraphics.getHeight())) {
            return;
        }
        flushAll();
        com.badlogic.gdx.math.Rectangle clippingRect = Pools.obtain(com.badlogic.gdx.math.Rectangle.class);
        clippingRect.set(x, y, width, height);

        com.badlogic.gdx.math.Rectangle scissor = Pools.obtain(com.badlogic.gdx.math.Rectangle.class);
        ScissorStack.calculateScissors(camera, 0, 0, gdxGraphics.getWidth(), gdxGraphics.getHeight(),
                spriteBatch.getTransformMatrix(), clippingRect, scissor);
        Pools.free(clippingRect);

        if (ScissorStack.pushScissors(scissor)) {
            activeClipping = true;
        } else {
            Pools.free(scissor);
        }
    }

    @Override
    public void unsetClippingArea() {
        if (activeClipping) {
            flushAll();
            Pools.free(ScissorStack.popScissors());
            activeClipping = false;
        }
    }

    private float getFloatColor(@Nonnull Color source, @Nonnull com.badlogic.gdx.graphics.Color workingInstance) {
        transferColor(source, workingInstance);
        return workingInstance.toFloatBits();
    }

    /**
     * Apply a global offset to all following render operations.
     *
     * @param offsetX the x component of the offset
     * @param offsetY the y component of the offset
     */
    void applyOffset(int offsetX, int offsetY) {
        camera.position.set((camera.viewportWidth / 2.f) + offsetX, (camera.viewportHeight / 2.f) + offsetY, 0.f);
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    /**
     * This function needs to be called after rendering a frame.
     */
    void endFrame() {
        flushAll();
        unsetClippingArea();
    }

    /**
     * Stops the render operation of both the shape renderer and the sprite batch renderer to ensure that the
     * buffered data is flushed to the screen.
     */
    public void flushAll() {
        if (shapeRenderer.getCurrentType() != null) {
            shapeRenderer.end();
        }
        if (spriteBatchActive) {
            spriteBatch.end();
            spriteBatchActive = false;
        }
    }
}
