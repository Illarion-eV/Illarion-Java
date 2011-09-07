/*
 * This file is part of the Illarion Nifty-GUI binding.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Nifty-GUI binding is free software: you can redistribute i
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * The Illarion Nifty-GUI binding is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Nifty-GUI binding. If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.renderer.render;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

import org.apache.log4j.Logger;

import de.lessvoid.nifty.tools.Color;

/**
 * This class is a image that can be displayed by the Nifty-GUI. It holds a
 * reference to a sprite that holds the actual image.
 * 
 * @author Martin Karing
 * @since 1.22/1.3
 * @version 1.22/1.3
 */
final class IllarionSpriteRenderImage implements IllarionRenderImage {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger
        .getLogger(IllarionSpriteRenderImage.class);

    /**
     * The sprite color that is used to transfer the nifty color data to the
     * illarion render environment.
     */
    private static final SpriteColor TEMP_COLOR;

    /**
     * This contains a sprite instance that is used for some render operations
     * temporarily.
     */
    private static final Sprite TEMP_SPRITE;

    static {
        TEMP_COLOR = Graphics.getInstance().getSpriteColor();
        TEMP_SPRITE = Graphics.getInstance().getSprite(1);
        TEMP_SPRITE.setAlign(Sprite.HAlign.center, Sprite.VAlign.middle);
    }

    /**
     * The sprite that contains the actual image.
     */
    private final Sprite internalSprite;

    /**
     * Create a copy of another render image.
     * 
     * @param org the image to copy
     */
    public IllarionSpriteRenderImage(final IllarionSpriteRenderImage org) {
        internalSprite = org.internalSprite;
    }

    /**
     * Create a render image that encapsulates a sprite.
     * 
     * @param sprite the sprite to encapsulate
     */
    public IllarionSpriteRenderImage(final Sprite sprite) {
        if (sprite == null) {
            throw new IllegalArgumentException();
        }
        internalSprite = sprite;
    }

    /**
     * Remove this image as its not used anymore.
     */
    @Override
    public void dispose() {
        /*
         * Disposing a image does nothing as the images are stored in the
         * internal storage for later usage.
         */
    }

    /**
     * Get the height of the image.
     * 
     * @return the height of the image
     */
    @Override
    public int getHeight() {
        return internalSprite.getHeight();
    }

    /**
     * Get the width of the image.
     * 
     * @return the width of the image
     */
    @Override
    public int getWidth() {
        return internalSprite.getWidth();
    }

    @Override
    public void renderImage(final int x, final int y, final int width,
        final int height, final Color color, final float imageScale) {
        TEMP_COLOR.set(color.getRed(), color.getGreen(), color.getBlue());
        TEMP_COLOR.setAlpha(color.getAlpha());
        internalSprite.draw(x, y, TEMP_COLOR, (int) (width * imageScale),
            (int) (height * imageScale));
    }

    @Override
    public void renderImage(final int x, final int y, final int w,
        final int h, final int srcX, final int srcY, final int srcW,
        final int srcH, final Color color, final float scale,
        final int centerX, final int centerY) {
        
        TEMP_COLOR.set(color.getRed(), color.getGreen(), color.getBlue());
        TEMP_COLOR.setAlpha(color.getAlpha());

        TEMP_SPRITE.addTexture(internalSprite.getTexture(0).getSubTexture(
            srcX, srcY, srcW, srcH));
        
        //TEMP_SPRITE.setOffset(x - centerX, y - centerY);

        TEMP_SPRITE.draw(x, y, (int) (w * scale),
            (int) (h * scale), TEMP_COLOR);

        TEMP_SPRITE.remove();
    }

}
