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

import org.illarion.engine.MouseCursor;
import org.illarion.engine.assets.CursorManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.util.ResourceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the mouse cursor manager that stores the cursors for Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickCursorManager implements CursorManager {
    /**
     * This map stores the cursors that were already load.
     */
    private final Map<String, SlickMouseCursor> loadedCursors;

    /**
     * Create a new instance of this manager.
     */
    SlickCursorManager() {
        loadedCursors = new HashMap<String, SlickMouseCursor>();
    }

    @Nullable
    @Override
    public MouseCursor getCursor(@Nonnull final String ref, final int hotspotX, final int hotspotY) {
        @Nullable final SlickMouseCursor loadedCursor = loadedCursors.get(ref);
        if (loadedCursor == null) {
            final LoadableImageData data = ImageDataFactory.getImageDataFor(ref);
            try {
                data.loadImage(ResourceLoader.getResourceAsStream(ref), true, true, null);
                final Cursor lwjglCursor = CursorLoader.get().getCursor(data, hotspotX, hotspotY);
                final SlickMouseCursor slickCursor = new SlickMouseCursor(lwjglCursor);
                loadedCursors.put(ref, slickCursor);
                return slickCursor;
            } catch (@Nonnull final IOException ignored) {
                return null;
            } catch (@Nonnull final LWJGLException ignored) {
                return null;
            }
        }
        return loadedCursor;
    }
}
