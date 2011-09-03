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

import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;

/**
 * This interface holds the common function definitions that are needed in
 * addition to the different RenderImage implementations that are required for
 * the Illarion interface.
 * 
 * @author Martin Karing
 * @since 1.22/1.3
 * @version 1.22/1.3
 */
public interface IllarionRenderImage extends RenderImage {
    /**
     * This function is used as a simple rendering function for images. It has
     * the ability to scale a image and change its color.
     * 
     * @param x the x coordinate of the position where the image is supposed to
     *            be placed on the screen
     * @param y the y coordinate of the position where the image is supposed to
     *            be place on the screen
     * @param width the width the image is supposed to be rendered with
     * @param height the height the image is supposed to be rendered with
     * @param color the color that is placed in the background of the image when
     *            it is rendered
     * @param imageScale the global scaling of the image that is applied after
     *            the height and width values are set
     */
    void renderImage(int x, int y, int width, int height, Color color,
        float imageScale);

    /**
     * This is the more advance rendering function for images. It is able to
     * render only parts of a image. Also scaling is possible.
     * 
     * @param x the x coordinate on the screen where the center of the image is
     *            located
     * @param y the y location on the screen where the center of the image is
     *            located
     * @param w the width of the image on the screen
     * @param h the height of the image on the screen
     * @param srcX the x coordinate on the image
     * @param srcY the y coordinate on the image
     * @param srcW the width of the area that is rendered from the image
     * @param srcH the height of the area that is rendered from the image
     * @param color the color that is applied to the vertex of the image
     * @param scale the scaling value that is applied at the end
     * @param centerX the x coordinate of the center location of the image,
     *            scaling is applied around that location
     * @param centerY the y coordinate of the center location of the image,
     *            scaling is applied around that location
     */
    void renderImage(int x, int y, int w, int h, int srcX, int srcY, int srcW,
        int srcH, Color color, float scale, int centerX, int centerY);
}
