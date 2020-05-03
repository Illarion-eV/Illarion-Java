/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import illarion.common.util.FastMath;
import org.illarion.engine.MouseCursor;
import org.illarion.engine.backend.shared.AbstractCursorManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the implementation of the cursor manager for libGDX.
 * <p/>
 * As libGDX does not directly support changing the cursors this is done by directly using LWJGL. This will not work
 * in case any other libGDX backend then LWJGL is used.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxLwjglCursorManager extends AbstractCursorManager {
    /**
     * The file system handler of libGDX that is supposed to be used by this manager.
     */
    @Nonnull
    private final Files files;

    /**
     * Create a new instance of this manager.
     *
     * @param files the files implementation that is supposed to be used to load the data
     */
    GdxLwjglCursorManager(@Nonnull Files files) {
        this.files = files;
    }

    @Nullable
    @Override
    protected MouseCursor loadCursor(@Nonnull String ref, int hotspotX, int hotspotY) {
        try {
            Pixmap cursorPixels = new Pixmap(files.internal(ref));

            int cursorHeight = cursorPixels.getHeight();
            int cursorWidth = cursorPixels.getWidth();

            cursorHeight = FastMath.clamp(cursorHeight, Cursor.getMinCursorSize(), Cursor.getMaxCursorSize());
            cursorWidth = FastMath.clamp(cursorWidth, Cursor.getMinCursorSize(), Cursor.getMaxCursorSize());

            cursorHeight = MathUtils.nextPowerOfTwo(cursorHeight);
            cursorWidth = MathUtils.nextPowerOfTwo(cursorWidth);

            if ((cursorHeight != cursorPixels.getHeight()) || (cursorWidth != cursorPixels.getWidth()) ||
                    (cursorPixels.getFormat() != Format.RGBA8888)) {

                Pixmap tempPixMap = new Pixmap(cursorWidth, cursorHeight, Format.RGBA8888);
                tempPixMap.drawPixmap(cursorPixels, 0, 0);
                cursorPixels.dispose();
                cursorPixels = tempPixMap;
            }
            Cursor lwjglCursor = new Cursor(cursorPixels.getWidth(), cursorPixels.getHeight(), hotspotX, hotspotY, 1,
                                            cursorPixels.getPixels().asIntBuffer(), null);
            return new GdxLwjglCursor(lwjglCursor);
        } catch (@Nonnull GdxRuntimeException e) {
            return null;
        } catch (@Nonnull LWJGLException e) {
            return null;
        }
    }
}
