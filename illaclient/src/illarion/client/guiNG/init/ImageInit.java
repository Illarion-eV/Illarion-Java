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
package illarion.client.guiNG.init;

import illarion.client.graphics.MarkerFactory;
import illarion.client.guiNG.elements.AbstractImage;
import illarion.client.guiNG.elements.Widget;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * This initialization script takes in a generic way care for loading images to
 * a widget. It works <b>only</b> with
 * {@link illarion.client.guiNG.elements.Image} and
 * {@link illarion.client.guiNG.elements.ImageRepeated} widgets.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class ImageInit implements WidgetInit {
    /**
     * The serialization UID this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The values of the individual color in case its set.
     */
    private int color[];

    /**
     * The image ID used to fetch the needed image from the
     * {@link illarion.client.graphics.MarkerFactory}.
     */
    private int imageID = -1;

    /**
     * The protected constructor to avoid instances created uncontrolled.
     */
    protected ImageInit() {
        // private constructor to avoid instances created uncontrolled.
    }

    /**
     * Get a new instance of this initialization script. This either creates a
     * new instance of this class or returns always the same, depending on what
     * is needed for this script.
     * 
     * @return the instance of this initialization script that is to be used
     *         from now on
     */
    public static ImageInit getInstance() {
        return new ImageInit();
    }

    /**
     * Prepare the widget for the active work.
     * 
     * @param widget the widget that is prepared
     */
    @Override
    @SuppressWarnings("nls")
    public void initWidget(final Widget widget) {
        if (!(widget instanceof AbstractImage)) {
            throw new IllegalArgumentException(
                "Init Class requires a image widget");
        }
        if (imageID == -1) {
            return;
        }

        final AbstractImage image = (AbstractImage) widget;
        final Sprite sprite =
            MarkerFactory.getInstance().getPrototype(imageID).getSprite();
        sprite.setAlign(Sprite.HAlign.left, Sprite.VAlign.bottom);

        if (color != null) {
            final SpriteColor newColor =
                Graphics.getInstance().getSpriteColor();
            newColor.set(color[0], color[1], color[2]);
            newColor.setAlpha(color[3]);
            image.setColor(newColor);
        }

        image.setImage(sprite);
    }

    /**
     * Set the color the image is rendered with.
     * 
     * @param newColor the color of the image
     * @return the instance of this image initialization script
     */
    public ImageInit setColor(final SpriteColor newColor) {
        color = new int[4];
        color[0] = newColor.getRedi();
        color[1] = newColor.getBluei();
        color[2] = newColor.getGreeni();
        color[3] = newColor.getAlphai();
        return this;
    }

    /**
     * Set the needed image ID used to fetch the used image from the
     * {@link illarion.client.graphics.MarkerFactory}.
     * 
     * @param newID the image ID to fetch the image
     * @return the instance of this image initialization script
     */
    public ImageInit setImageID(final int newID) {
        imageID = newID;
        return this;
    }
}
