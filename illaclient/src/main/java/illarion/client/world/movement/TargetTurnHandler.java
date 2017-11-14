/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2017 - Illarion e.V.
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

import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;

/**
 * A movement handler that turns to the target instead of moving.
 *
 * @author Ilya Osadchiy
 */
public interface TargetTurnHandler extends MovementHandler {
    /**
     * Turn towards a location.
     *
     * @param target the target location
     */
    void turnTo(@Nonnull ServerCoordinate target);

}
