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

import illarion.graphics.Sprite;

import de.lessvoid.nifty.spi.render.RenderImage;

/**
 * This class is a image that can be displayed by the Nifty-GUI. It holds a
 * reference to a sprite that holds the actual image.
 * 
 * @author Martin Karing
 * @since 1.22/1.3
 * @version 1.22/1.3
 */
final class IllarionSpriteRenderImage implements RenderImage {
    /**
     * The sprite that contains the actual image.
     */
    private final Sprite internalSprite;
    
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
     * Create a copy of another render image.
     * 
     * @param org the image to copy
     */
    public IllarionSpriteRenderImage(final IllarionSpriteRenderImage org) {
        internalSprite = org.internalSprite;
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
     * Remove this image as its not used anymore.
     */
    @Override
    public void dispose() {
        /*
         * Disposing a image does nothing as the images are stored in the
         * internal storage for later usage.
         */
    }

}
