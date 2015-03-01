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
package illarion.client.gui;

import illarion.client.world.items.SelectionItem;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * This interface defines the access to the GUI used to display selection dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface DialogSelectionGui {
    /**
     * Show a message dialog on the GUI.
     *
     * @param dialogId the ID of the message dialog
     * @param title    the title of the message dialog
     * @param content  the description text displayed in the dialog
     * @param items    the entries that are supposed to be displayed
     */
    void showSelectionDialog(int dialogId, @Nonnull String title, @Nonnull String content,
                             @Nonnull Collection<SelectionItem> items);
}
