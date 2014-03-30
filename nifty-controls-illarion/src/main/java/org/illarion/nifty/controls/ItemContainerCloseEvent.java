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
package org.illarion.nifty.controls;

import de.lessvoid.nifty.NiftyEvent;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This event is fired in case a item container is closed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
@Immutable
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
