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
package illarion.client.input;

import illarion.client.world.MapTile;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * This even is send to the map display to handle dragging event on items. It uses callback functions to inform the
 * GUI on what is happening.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class PrimaryKeyMapDrag extends DragOnMapEvent {
    public interface PrimaryKeyMapDragCallback {
        boolean startDraggingItemFromTile(@Nonnull PrimaryKeyMapDrag event, @Nonnull MapTile tile);

        void notHandled();
    }

    @Nonnull
    private final PrimaryKeyMapDragCallback callback;

    public PrimaryKeyMapDrag(@Nonnull DragOnMapEvent org, @Nonnull PrimaryKeyMapDragCallback callback) {
        super(org);
        this.callback = callback;
    }

    @Contract(pure = false)
    public boolean startDraggingItemFromTile(@Nonnull MapTile tile) {
        return callback.startDraggingItemFromTile(this, tile);
    }

    @Override
    public void notHandled() {
        callback.notHandled();
    }
}
