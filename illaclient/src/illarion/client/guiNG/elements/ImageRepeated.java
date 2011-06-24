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

import illarion.client.ClientWindow;

import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * This is a image widget that does not stretch the image in the way to
 * {@link illarion.client.guiNG.elements.Image} implementation does, it repeats
 * the image as often is needed to fill the area of the widget.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class ImageRepeated extends AbstractImage {
    /**
     * The serialization UID for this repeated image widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Draw the image. In case its needed the sprite is drawn multiple times
     * until the entire area if this widget is filled.
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
            final SpriteColor usedColor = getColor();
            ClientWindow.getInstance().getRenderDisplay()
                .setAreaLimit(getAbsX(), getAbsY(), getWidth(), getHeight());
            final int width = getWidth();
            final int height = getHeight();
            final int imageWidth = targetImage.getWidth();
            final int imageHeight = targetImage.getHeight();
            for (int x = 0; x < width; x += imageWidth) {
                for (int y = 0; y < height; y += imageHeight) {
                    targetImage.draw(x, y, usedColor);
                }
            }
            ClientWindow.getInstance().getRenderDisplay().unsetAreaLimit();
        }
        super.draw(delta);
    }
}
