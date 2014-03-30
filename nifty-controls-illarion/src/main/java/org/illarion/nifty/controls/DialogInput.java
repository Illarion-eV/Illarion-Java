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

import de.lessvoid.nifty.controls.Window;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This is the common interface for a input dialog that provides all required functions for the control of a dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface DialogInput extends Window {
    /**
     * This enumerator contains the possible values for the buttons displayed in this dialog.
     */
    enum DialogButton {
        /**
         * The constant for the left button.
         */
        LeftButton,

        /**
         * The constant for the right button.
         */
        RightButton
    }

    /**
     * Set the text of one button in this dialog.
     *
     * @param button the button to change
     * @param label the new label of this button
     */
    void setButtonLabel(@Nonnull DialogButton button, @Nonnull String label);

    /**
     * Set the maximal amount of characters allowed to be typed into this dialog.
     *
     * @param length the maximal amount of characters
     */
    void setMaximalLength(int length);

    /**
     * Set the description text.
     *
     * @param text the text shown as description
     */
    void setDescription(@Nonnull String text);
}
