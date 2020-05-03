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
package illarion.client.world.events;

import javax.annotation.Nonnull;

/**
 * @author Fredrik K
 */
public class ConnectionLostEvent {
    @Nonnull
    private final String message;
    private final boolean tryToReconnect;

    public ConnectionLostEvent(@Nonnull String message, boolean tryToReconnect) {
        this.message = message;
        this.tryToReconnect = tryToReconnect;
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    public boolean isTryToReconnect() {
        return tryToReconnect;
    }
}
