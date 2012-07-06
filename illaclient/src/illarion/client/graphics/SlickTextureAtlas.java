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

import illarion.common.graphics.TextureAtlas;
import org.newdawn.slick.Image;
import org.newdawn.slick.XMLPackedSheet;

/**
 * This is the texture atlas implementation that is required for the texture loader of the client to store the texture
 * atlas definitions.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class SlickTextureAtlas implements TextureAtlas<Image> {
    /**
     * The XML packed sheet that is internally used.
     */
    private final XMLPackedSheet internalSheet;

    /**
     * Constructor of the slick texture atlas that defines the sheet that is supposed to be wrapped by this instance.
     *
     * @param sheet the texture atlas sheet to be wrapped by this instance
     */
    public SlickTextureAtlas(final XMLPackedSheet sheet) {
        internalSheet = sheet;
    }

    @Override
    public boolean containsTexture(final String texture) {
        return getTexture(texture) != null;
    }

    @Override
    public Image getTexture(final String texture) {
        return internalSheet.getSprite(texture);
    }
}
