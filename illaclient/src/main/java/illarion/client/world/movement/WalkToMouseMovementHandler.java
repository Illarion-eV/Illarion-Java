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

import illarion.client.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class WalkToMouseMovementHandler extends WalkToMovementHandler {
    private static final Logger log = LoggerFactory.getLogger(WalkToMouseMovementHandler.class);

    WalkToMouseMovementHandler(@Nonnull Movement movement) {
        super(movement);
    }

    @Override
    public void disengage(boolean transferAllowed) {
        boolean targetWasSet = isTargetSet() && isActive();
        super.disengage(transferAllowed);
        if (transferAllowed && targetWasSet) {
            TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
            log.debug("Transferring movement control from {} to {}", this, handler);
            handler.walkTo(getTargetLocation(), 0);
            handler.assumeControl();
        }
    }

    @Override
    public String toString() {
        return "Walk to mouse pointer movement handler";
    }
}
