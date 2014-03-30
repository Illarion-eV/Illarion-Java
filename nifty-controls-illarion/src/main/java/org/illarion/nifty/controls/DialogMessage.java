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
 * This is the common interface for a dialog message that provides all required functions for the control of a dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface DialogMessage extends Window {
    /**
     * Set the text that is supposed to be displayed in the dialog message.
     *
     * @param text the text to be displayed
     */
    void setText(@Nonnull String text);

    /**
     * Set the text that is supposed to be displayed in the dialog.
     *
     * @param button the button to display
     */
    void setButton(@Nonnull String button);
}
