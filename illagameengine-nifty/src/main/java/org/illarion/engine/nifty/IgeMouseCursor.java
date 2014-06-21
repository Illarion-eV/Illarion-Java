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
package org.illarion.engine.nifty;

import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.spi.render.RenderDevice;

import javax.annotation.Nonnull;

/**
 * This is the mouse cursor implementation that uses the mouse cursors of the game engine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class IgeMouseCursor implements MouseCursor {
    /**
     * This is the cursor that is wrapped by this mouse cursor implementation.
     */
    @Nonnull
    private final org.illarion.engine.MouseCursor cursor;

    @Nonnull
    private final RenderDevice device;

    /**
     * Create a new mouse cursor that wraps the mouse cursor of the engine.
     *
     * @param cursor the wrapped cursor
     */
    IgeMouseCursor(@Nonnull RenderDevice device, @Nonnull org.illarion.engine.MouseCursor cursor) {
        this.device = device;
        this.cursor = cursor;
    }

    /**
     * Get the mouse cursor that is wrapped in this class.
     *
     * @return the wrapped mouse cursor
     */
    @Nonnull
    public org.illarion.engine.MouseCursor getCursor() {
        return cursor;
    }

    @Override
    public void enable() {
        device.enableMouseCursor(this);
    }

    @Override
    public void disable() {
        device.disableMouseCursor();
    }

    @Override
    public void dispose() {
        cursor.dispose();
    }
}
