/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.engine.backend.slick;

import org.illarion.engine.MouseCursor;
import org.illarion.engine.backend.shared.AbstractCursorManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageDataFactory;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.util.ResourceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This is the mouse cursor manager that stores the cursors for Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickCursorManager extends AbstractCursorManager {
    @Nullable
    @Override
    protected MouseCursor loadCursor(@Nonnull final String ref, final int hotspotX, final int hotspotY) {
        final LoadableImageData data = ImageDataFactory.getImageDataFor(ref);
        try {
            data.loadImage(ResourceLoader.getResourceAsStream(ref), true, true, null);
            final Cursor lwjglCursor = CursorLoader.get().getCursor(data, hotspotX, hotspotY);
            return new SlickMouseCursor(lwjglCursor, hotspotX, hotspotY);
        } catch (@Nonnull final IOException ignored) {
            return null;
        } catch (@Nonnull final LWJGLException ignored) {
            return null;
        }
    }
}
