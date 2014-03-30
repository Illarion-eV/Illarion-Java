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

import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;

/**
 * This event is fired upon a change of the visibility of a character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharVisibilityEvent extends AbstractCharEvent {
    /**
     * The name of this event type.
     */
    public static final String EVENT = "CharVisibilityEvent";

    /**
     * The visibility level the character is set to.
     */
    private final int visibility;

    /**
     * Create a instance of this event and set the ID of the character that changed its visibility and also the new
     * visibility value.
     *
     * @param id the ID of the character
     * @param charVisibility the visibility of the character
     */
    public CharVisibilityEvent(final CharacterId id, final int charVisibility) {
        super(id);
        visibility = charVisibility;
    }

    /**
     * The name that identifies this event.
     *
     * @return the string literal that identifies this event
     */
    @Nonnull
    @Override
    public String getEvent() {
        return EVENT;
    }

    /**
     * Get the visibility value the character that triggered this event was changed to.
     *
     * @return the new visibility value
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * Check if the character is visible due the change that triggered this event.
     *
     * @return {@code true} in case he character is visible
     */
    public boolean isVisible() {
        return visibility > 0;
    }
}
