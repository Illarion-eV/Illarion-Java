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
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import illarion.client.world.Char;
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;

/**
 * This interface defines the access to the GUI used to display input dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface DialogInputGui {
    /**
     * Show a input dialog on the GUI.
     *
     * @param dialogId the ID of the input dialog
     * @param title the title of the input dialog
     * @param message the message that is displayed in the dialog
     * @param maxLength the amount of characters that are maximal allowed in this dialog
     * @param multiLine {@code true} in case the input of multiple lines should be allowed
     */
    void showInputDialog(int dialogId, String title, @Nonnull String message, int maxLength, boolean multiLine);

    void showCharacterDialog(@Nonnull CharacterId charId, @Nonnull String lookAt);

}
