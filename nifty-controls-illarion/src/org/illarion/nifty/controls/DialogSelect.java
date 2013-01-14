/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.Window;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interface is used to interact with a select dialog that is displayed inside the GUI.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@NotThreadSafe
public interface DialogSelect extends Window {
    /**
     * Get the amount of entries on the list.
     *
     * @return the entry count
     */
    int getEntryCount();

    /**
     * Get the item that was selected.
     *
     * @return the selected item or {@code null} in case no item is selected
     */
    @Nullable
    SelectListEntry getSelectedItem();

    /**
     * Get the selected index.
     *
     * @return the index that was selected or {@code -1} in case no item is selected
     */
    int getSelectedIndex();

    /**
     * Add a item to the list of items.
     *
     * @param entry the item to add
     */
    void addItem(@Nonnull SelectListEntry entry);
}
