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

import org.illarion.engine.input.Input;

import javax.annotation.Nonnull;

/**
 * Create a mouse event that is marking the current mouse location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CurrentMouseLocationEvent extends AbstractMouseLocationEvent {
    private boolean highlightHandled;

    /**
     * Create and initialize such an event.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     */
    public CurrentMouseLocationEvent(int x, int y) {
        super(x, y);
    }

    /**
     * Create and initialize such an event.
     *
     * @param input the input handler supplying the data
     */
    public CurrentMouseLocationEvent(@Nonnull Input input) {
        this(input.getMouseX(), input.getMouseY());
    }

    public boolean isHighlightHandled() {
        return highlightHandled;
    }

    public void setHighlightHandled(boolean highlightHandled) {
        this.highlightHandled = highlightHandled;
    }
}
