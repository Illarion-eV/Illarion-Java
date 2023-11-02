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

import illarion.common.memory.Poolable;
import org.illarion.engine.input.Button;
import illarion.client.IllaClient;

import javax.annotation.Nonnull;

/**
 * This event is in general triggered in case the user performs any action with the mouse on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractMouseOnMapEvent extends AbstractMouseLocationEvent {
    /**
     * The mouse key that was clicked.
     */
    private Button key;

    /**
     * Create and initialize such an event.
     *
     * @param key the mouse key that was clicked
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    protected AbstractMouseOnMapEvent(@Nonnull Button key, int x, int y) {
        super(x, y);
        IllaClient.lastInputTime = System.currentTimeMillis() / 1000L; // Registers that the user is active client-side when clicking the mouse, for RP alert purposes
        this.key = key;
    }

    /**
     * Create and initialize such an event.
     */
    protected AbstractMouseOnMapEvent() {
        super(0, 0);
    }

    /**
     * The copy constructor.
     *
     * @param org the original object to copy
     */
    protected AbstractMouseOnMapEvent(@Nonnull AbstractMouseOnMapEvent org) {
        super(org);
        key = org.key;
    }

    /**
     * Create and initialize such an event.
     *
     * @param key the mouse key that was clicked
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    public void set (@Nonnull Button key, int x, int y) {
        super.set(x, y);
        this.key = key;
    }

    /**
     * Get the key that was clicked on the mouse.
     *
     * @return the key that was clicked
     */
    public Button getKey() {
        return key;
    }

    @Override
    public void reset () {
        super.reset();
        this.key = null;
    }

}
