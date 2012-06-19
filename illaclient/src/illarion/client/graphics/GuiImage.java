/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.client.resources.Resource;
import illarion.common.graphics.Sprite;

/**
 * This class is used to store the name and the sprite of a GUI image.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GuiImage implements Resource {
    /**
     * The name of this image.
     */
    private final String imageName;

    /**
     * The sprite that stores the actual graphic data.
     */
    private final Sprite graphic;

    /**
     * The constructor used to store the required informations for this image.
     *
     * @param name   the name of this image
     * @param sprite the sprite that is used to display this image
     */
    public GuiImage(final String name, final Sprite sprite) {
        imageName = name;
        graphic = sprite;
    }

    /**
     * The name of this image.
     *
     * @return the name of the image
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Get the graphic of this GUI image.
     *
     * @return the sprite that is used to draw this image
     */
    public Sprite getSprite() {
        return graphic;
    }
}
