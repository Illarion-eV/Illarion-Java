/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls;

import de.lessvoid.nifty.NiftyEvent;

/**
 * This event is fired in case a item container is closed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemContainerCloseEvent implements NiftyEvent {
    /**
     * The ID of the container to close.
     */
    private final int id;

    /**
     * Create a new instance of this event and set the ID of the container that was closed.
     *
     * @param containerId the ID of the container
     */
    public ItemContainerCloseEvent(final int containerId) {
        id = containerId;
    }

    /**
     * Get the ID of the container that is closed.
     *
     * @return the ID of the container
     */
    public int getContainerId() {
        return id;
    }
}
