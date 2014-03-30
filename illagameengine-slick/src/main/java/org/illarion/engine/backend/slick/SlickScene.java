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

import org.illarion.engine.GameContainer;
import org.illarion.engine.backend.shared.AbstractScene;
import org.illarion.engine.graphic.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.SGL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the Slick2D implementation of the game scene.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickScene extends AbstractScene<SlickSceneEffect> {
    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SlickScene.class);

    /**
     * The instance of the slick container this scene is rendered in.
     */
    @Nonnull
    private final org.newdawn.slick.GameContainer slickContainer;

    /**
     * The first of two images that can be used to buffer the scene to apply the post processing effects.
     */
    @Nullable
    private Image processImage0;

    /**
     * The second of two images that can be used to buffer the scene to apply the post processing effects.
     */
    @Nullable
    private Image processImage1;

    /**
     * The last image that was requested. This variable is required to flip-flop the textures.
     */
    private int lastImage = 1;

    SlickScene(@Nonnull final org.newdawn.slick.GameContainer container) {
        slickContainer = container;
    }

    /**
     * Get the next image that can be used as render target.
     *
     * @param width the width the image is supposed to have
     * @param height the height the image is supposed to have
     * @return the image that can be used now
     * @throws SlickException in case requesting the image fails
     */
    @Nullable
    private Image getNextProcessImage(final int width, final int height) throws SlickException {
        if (lastImage == 1) {
            processImage0 = validateImage(width, height, processImage0);
            lastImage = 0;
            return processImage0;
        }
        processImage1 = validateImage(width, height, processImage1);
        lastImage = 1;
        return processImage1;
    }

    /**
     * Check if a image is fitting the requirements.
     *
     * @param width the width the image needs to have
     * @param height the height the image needs to have
     * @param original the original image, if this is {@code null} a new image will be created
     * @return the image fitting the requirements
     * @throws SlickException in case creating the image fails
     */
    @Nullable
    private static Image validateImage(final int width, final int height, @Nullable final Image original)
            throws SlickException {
        if (original == null) {
            return Image.createOffscreenImage(width, height, SGL.GL_LINEAR);
        }
        if ((original.getHeight() == height) && (original.getWidth() == width)) {
            return original;
        }
        if ((original.getTexture().getTextureHeight() >= height) &&
                (original.getTexture().getTextureWidth() >= width)) {
            return original.getSubImage(0, 0, width, height);
        }
        original.destroy();
        return Image.createOffscreenImage(width, height, SGL.GL_LINEAR);
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        updateScene(container, delta);

        final int effectCount = getEffectCount();
        for (int i = 0; i < effectCount; i++) {
            getEffect(i).update(delta);
        }
    }

    @Override
    public void render(@Nonnull final Graphics graphics, final int offsetX, final int offsetY) {
        if (graphics instanceof SlickGraphics) {
            final SlickGraphics slickGraphics = (SlickGraphics) graphics;
            final org.newdawn.slick.Graphics slickGraphicsImpl = slickGraphics.getSlickGraphicsImpl();
            if (slickGraphicsImpl == null) {
                throw new IllegalStateException("Rendering outside the render loop is not allowed.");
            }

            final int effectCount = getEffectCount();
            if (effectCount == 0) {
                // No full screen effects. Just render it
                slickGraphicsImpl.pushTransform();
                slickGraphicsImpl.translate(-offsetX, -offsetY);
                slickGraphicsImpl.setBackground(Color.transparent);
                slickGraphicsImpl.clear();
                renderScene(graphics);
                slickGraphicsImpl.popTransform();
                slickGraphicsImpl.flush();
            } else {
                final int height = slickContainer.getHeight();
                final int width = slickContainer.getWidth();
                try {
                    final Image sceneTarget = getNextProcessImage(width, height);
                    final org.newdawn.slick.Graphics currentGraphics = sceneTarget.getGraphics();
                    org.newdawn.slick.Graphics.setCurrent(currentGraphics);
                    slickGraphics.setSlickGraphicsImpl(currentGraphics);
                    currentGraphics.setBackground(Color.black);
                    currentGraphics.clear();
                    currentGraphics.pushTransform();
                    currentGraphics.translate(-offsetX, -offsetY);
                    renderScene(graphics);
                    currentGraphics.popTransform();
                    currentGraphics.flush();

                    Image lastProcessedImage = sceneTarget;
                    for (int i = 0; i < effectCount; i++) {
                        final Image nextImage = getNextProcessImage(width, height);
                        final SlickSceneEffect effect = getEffect(i);
                        final org.newdawn.slick.Graphics nextGraphics = nextImage.getGraphics();
                        org.newdawn.slick.Graphics.setCurrent(nextGraphics);

                        nextGraphics.setBackground(Color.black);
                        nextGraphics.clear();
                        effect.activateEffect(width, height, nextImage.getTexture().getTextureWidth(),
                                              nextImage.getTexture().getTextureHeight());
                        nextGraphics.drawImage(lastProcessedImage, 0, 0);
                        effect.disableEffect();
                        nextGraphics.flush();

                        lastProcessedImage = nextImage;
                    }

                    org.newdawn.slick.Graphics.setCurrent(slickGraphicsImpl);
                    slickGraphics.setSlickGraphicsImpl(slickGraphicsImpl);
                    slickGraphicsImpl.setBackground(Color.black);
                    slickGraphicsImpl.clear();
                    slickGraphicsImpl.drawImage(lastProcessedImage, 0, 0);
                    slickGraphicsImpl.flush();
                } catch (@Nonnull final SlickException e) {
                    // postprocessing failed
                }
                slickGraphics.setSlickGraphicsImpl(slickGraphicsImpl);
            }
        }
    }
}
