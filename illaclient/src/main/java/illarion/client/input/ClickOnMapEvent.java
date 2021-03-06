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
package illarion.client.input;

import illarion.client.world.World;
import org.illarion.engine.input.Button;

import javax.annotation.Nonnull;

/**
 * This event is published when a click operation on the map was noted.
 *
 * @author Vilarion &lt;vilarion@illarion.org&gt;
 */
public final class ClickOnMapEvent extends AbstractMouseOnMapEvent {
    /**
     * Create and initialize such an event.
     *
     * @param key the mouse key that was clicked
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    ClickOnMapEvent(@Nonnull Button key, int x, int y) {
        super(key, x, y);
    }

    /**
     * Create and initialize such an event.
     */
    public ClickOnMapEvent() {
        super();
    }

    /**
     * Initialize such an event.
     *
     * @param key the mouse key that was clicked
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    public void set (@Nonnull Button key, int x, int y) {
        super.set(key, x, y);
    }

    @Override
    public void notHandled() {
        if (getKey() == Button.Right) {
            World.getPlayer().getCombatHandler().standDown();
        }
    }
}
