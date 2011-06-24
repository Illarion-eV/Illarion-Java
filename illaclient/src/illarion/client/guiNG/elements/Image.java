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

import illarion.graphics.Sprite;

/**
 * The image widget does nothing but showing some texture at a location. The
 * size of the texture is fit to the size of the texture. How ever there is the
 * possibility to set the size of the widget to the size of the texture
 * automatically.
 * <p>
 * There is always the first frame of the sprite displayed using this image. In
 * case there is the need to create a animation, this widget type does not work
 * out.
 * </p>
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Image extends AbstractImage {
    /**
     * The serialization UID of this image widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The offset that is applied to the drawing origin. Using this could be
     * handy from time to time in case the sprite is not aligned to the bottom
     * left edge. This variable store the x share of the offset.
     */
    private int drawOffsetX = 0;

    /**
     * The offset that is applied to the drawing origin. Using this could be
     * handy from time to time in case the sprite is not aligned to the bottom
     * left edge. This variable store the y share of the offset.
     */
    private int drawOffsetY = 0;

    /**
     * Draw the image.
     * 
     * @param delta the time since the render function was called last time
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        final Sprite targetImage = getImage();
        if (targetImage != null) {
            targetImage.draw(getRelX() + drawOffsetX, getRelY() + drawOffsetY,
                getWidth(), getHeight(), getColor());
        }
        super.draw(delta);
    }

    /**
     * Set the offset that is applied to the drawing origin. This is useful in
     * case the origin of the sprite is not located in the lower left edge.
     * 
     * @param offX the x share of the offset
     * @param offY the y share of the offset
     */
    public void setDrawingOffset(final int offX, final int offY) {
        drawOffsetX = offX;
        drawOffsetY = offY;
    }

    /**
     * Set the size of the widget to the size of the image. This function has no
     * effect in case the image is set to <code>null</code>.
     */
    public void setSizeToImage() {
        final Sprite targetImage = getImage();
        if (targetImage != null) {
            setWidth(targetImage.getWidth());
            setHeight(targetImage.getHeight());
        }
    }
}
