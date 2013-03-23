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

import org.illarion.engine.backend.shared.AbstractFontManager;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This is the font manager implementation for Slick.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickFontManager extends AbstractFontManager {
    @Nonnull
    @Override
    protected Font buildFont(@Nonnull final String ttfRef, final float size, final int style, @Nonnull final String fntRef) throws IOException {
        try {
            return new SlickFont(fntRef, fntRef.replace(".fnt", "_0.png"));
        } catch (@Nonnull final SlickEngineException e) {
            throw new IOException(e);
        }
    }
}
