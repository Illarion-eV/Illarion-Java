/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG.elements;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * This class is the parent of both image implementations. It stores the basic
 * functions all image implementations share.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public abstract class AbstractImage extends Widget {
    /**
     * The default color used to render in case none is set.
     */
    private static final SpriteColor DEFAULT_COLOR;

    /**
     * The serialization UID of this image widget.
     */
    private static final long serialVersionUID = 1L;

    static {
        DEFAULT_COLOR = Graphics.getInstance().getSpriteColor();
        DEFAULT_COLOR.set(SpriteColor.COLOR_MAX);
        DEFAULT_COLOR.setAlpha(SpriteColor.COLOR_MAX);
    }

    /**
     * The color that is used to render the image.
     */
    private transient SpriteColor color;

    /**
     * The image that is drawn within this widget.
     */
    private transient Sprite image;

    /**
     * Set the color that is used to render the image. In case the color is set
     * to <code>null</code> the default color is used and the image appears
     * unchanged.
     * 
     * @param newColor the color that is used to render the image or
     *            <code>null</code>
     */
    public void setColor(final SpriteColor newColor) {
        color = newColor;
    }

    /**
     * Set a new image that is rendered within the widget.
     * 
     * @param newImage the image that is rendered from now on in the widget.
     */
    public void setImage(final Sprite newImage) {
        image = newImage;
    }

    /**
     * Get the color that is used to display this image.
     * 
     * @return the color that is used to display this image
     */
    protected final SpriteColor getColor() {
        if (color == null) {
            return DEFAULT_COLOR;
        }
        return color;
    }

    /**
     * Get the sprite that is displayed using this image.
     * 
     * @return the sprite of this image widget or <code>null</code> in case none
     *         is set
     */
    protected final Sprite getImage() {
        return image;
    }
}
