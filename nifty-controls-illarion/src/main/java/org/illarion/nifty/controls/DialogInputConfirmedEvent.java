/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import org.illarion.nifty.controls.DialogInput.DialogButton;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This event is fired in case a input dialog is confirmed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
@Immutable
public final class DialogInputConfirmedEvent extends DialogEvent {
    /**
     * The dialog button that was pressed to trigger this event.
     */
    @Nonnull
    private final DialogButton button;

    /**
     * The text that was typed into the input area.
     */
    @Nonnull
    private final String text;

    /**
     * Create a new instance of this event and set the ID of the dialog that was closed when this event was fired.
     *
     * @param id the ID of the event
     * @param pressedButton the button that was pressed to trigger this event
     * @param inputText the text that was typed into the text input area of the dialog
     */
    public DialogInputConfirmedEvent(
            int id, @Nonnull DialogButton pressedButton, @Nonnull String inputText) {
        super(id);
        button = pressedButton;
        text = inputText;
    }

    /**
     * Get the button that was used to trigger this event.
     *
     * @return the constant that defines what button was used
     */
    @Nonnull
    public DialogButton getPressedButton() {
        return button;
    }

    /**
     * Get the text that was written into the dialog.
     *
     * @return the text the user typed into the dialog
     */
    @Nonnull
    public String getText() {
        return text;
    }
}
