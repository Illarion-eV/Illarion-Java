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
package illarion.client.input;

import illarion.client.world.MapTile;
import org.illarion.engine.input.Button;

import javax.annotation.Nonnull;

/**
 * This even is send to the map display to handle dragging event on items. It uses callback functions to inform the
 * GUI on what is happening.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class PrimaryKeyMapDrag extends DragOnMapEvent {
    public interface PrimaryKeyMapDragCallback {
        boolean startDraggingItemFromTile(PrimaryKeyMapDrag event, MapTile tile);
    }

    private final PrimaryKeyMapDragCallback callback;


    /**
     * Create and initialize such an event.
     *
     * @param startX   the X coordinate where the dragging starts
     * @param startY   the Y coordinate where the dragging starts
     * @param stopX    the X coordinate where the dragging is currently
     * @param stopY    the Y coordinate where the dragging is currently
     * @param callback the callback instance for this class
     */
    public PrimaryKeyMapDrag(final int startX, final int startY, final int stopX, final int stopY,
                             final PrimaryKeyMapDragCallback callback) {
        super(startX, startY, stopX, stopY, Button.Left);
        this.callback = callback;
    }

    public PrimaryKeyMapDrag(@Nonnull final DragOnMapEvent org,
                             final PrimaryKeyMapDragCallback callback) {
        super(org);
        this.callback = callback;
    }

    public boolean startDraggingItemFromTile(final MapTile tile) {
        return callback.startDraggingItemFromTile(this, tile);
    }
}
