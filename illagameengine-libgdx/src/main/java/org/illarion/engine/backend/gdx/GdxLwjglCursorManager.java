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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.Pixmap;
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
    GdxLwjglCursorManager(@Nonnull final Files files) {
        this.files = files;
    }

    @Nullable
    @Override
    protected MouseCursor loadCursor(@Nonnull final String ref, final int hotspotX, final int hotspotY) {
        try {
            Pixmap cursorPixels = new Pixmap(files.internal(ref));

            int cursorHeight = cursorPixels.getHeight();
            int cursorWidth = cursorPixels.getWidth();

            cursorHeight = FastMath.clamp(cursorHeight, Cursor.getMinCursorSize(), Cursor.getMaxCursorSize());
            cursorWidth = FastMath.clamp(cursorWidth, Cursor.getMinCursorSize(), Cursor.getMaxCursorSize());

            cursorHeight = MathUtils.nextPowerOfTwo(cursorHeight);
            cursorWidth = MathUtils.nextPowerOfTwo(cursorWidth);

            if ((cursorHeight != cursorPixels.getHeight()) || (cursorWidth != cursorPixels.getWidth()) ||
                    (cursorPixels.getFormat() != Pixmap.Format.RGBA8888)) {

                final Pixmap tempPixMap = new Pixmap(cursorWidth, cursorHeight, Pixmap.Format.RGBA8888);
                tempPixMap.drawPixmap(cursorPixels, 0, 0);
                cursorPixels.dispose();
                cursorPixels = tempPixMap;
            }
            final Cursor lwjglCursor = new Cursor(cursorPixels.getWidth(), cursorPixels.getHeight(), hotspotX,
                    hotspotY, 1, cursorPixels.getPixels().asIntBuffer(), null);
            return new GdxLwjglCursor(lwjglCursor);
        } catch (@Nonnull final GdxRuntimeException e) {
            return null;
        } catch (@Nonnull final LWJGLException e) {
            return null;
        }
    }
}
