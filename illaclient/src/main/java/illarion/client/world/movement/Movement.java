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
package illarion.client.world.movement;

import illarion.client.world.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This is the main controlling class for the movement. It maintains the references to the different handlers and
 * makes sure that the movement commands of the handlers are put in action.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Movement {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(Movement.class);

    /**
     * The instance of the player that is moved around by this class.
     */
    @Nonnull
    private final Player player;

    public Movement(@Nonnull Player player) {
        this.player = player;
    }
}
