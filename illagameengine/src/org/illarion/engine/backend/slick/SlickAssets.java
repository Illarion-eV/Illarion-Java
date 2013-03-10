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

import org.illarion.engine.assets.*;

import javax.annotation.Nonnull;

/**
 * The asset provider for the Slick2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickAssets implements Assets {
    /**
     * The instance of the texture manager used by this backend.
     */
    private final SlickTextureManager textureManager;

    /**
     * The instance of the cursor manager used by this backend.
     */
    private final SlickCursorManager cursorManager;

    /**
     * Constructor of this assets handler.
     */
    SlickAssets() {
        textureManager = new SlickTextureManager();
        cursorManager = new SlickCursorManager();
    }

    @Nonnull
    @Override
    public TextureManager getTextureManager() {
        return textureManager;
    }

    @Nonnull
    @Override
    public FontManager getFontManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    public CursorManager getCursorManager() {
        return cursorManager;
    }

    @Nonnull
    @Override
    public SoundsManager getSoundsManager() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
