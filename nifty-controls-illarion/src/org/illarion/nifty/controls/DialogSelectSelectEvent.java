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

/**
 * This event is fired in case the player selected a entry of the select dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogSelectSelectEvent extends DialogEvent {
    /**
     * The item that was bought.
     */
    private final SelectListEntry item;

    /**
     * The index of the item that is bought.
     */
    private final int itemIndex;

    /**
     * Create a new instance of this event and set the ID of the dialog anything was selected from.
     *
     * @param id       the ID of the dialog
     * @param buyItem  the item that was selected
     * @param buyIndex the index of the item that was selected
     */
    public DialogSelectSelectEvent(final int id, final SelectListEntry buyItem, final int buyIndex) {
        super(id);
        item = buyItem;
        itemIndex = buyIndex;
    }

    /**
     * Get the item the player selected
     *
     * @return the item that is selected
     */
    public SelectListEntry getItem() {
        return item;
    }

    /**
     * Get the index of the item the player selected
     *
     * @return the index of the item
     */
    public int getItemIndex() {
        return itemIndex;
    }
}
