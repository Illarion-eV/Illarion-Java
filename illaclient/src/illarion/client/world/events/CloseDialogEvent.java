/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world.events;

/**
 * This event is used to close a specified dialog on the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CloseDialogEvent {
    /**
     * This enumerator is used to define the type of dialog this event is supposed to close.
     */
    public enum DialogType {
        /**
         * Ignore the type just close any dialog fitting the search parameters.
         */
        Any,

        /**
         * This event targets a message dialog that is supposed to be closed.
         */
        Message,

        /**
         * This event targets a input dialog that is supposed to be closed.
         */
        Input,

        /**
         * This event targets a merchant dialog that is supposed to be closed.
         */
        Merchant
    }

    /**
     * This constant is supposed to be used as dialog ID, in case all dialogs if the selected type are supposed to be
     * closed.
     */
    public static final int ALL_DIALOGS = -1;

    /**
     * The ID of the dialog that is supposed to be closed.
     */
    private final int dialogId;

    /**
     * The type of the dialog that is supposed to be closed.
     */
    private final CloseDialogEvent.DialogType dialogType;

    /**
     * The default constructor of this event.
     *
     * @param id   the ID of the dialog that is supposed to be closed or {@link #ALL_DIALOGS}
     * @param type the type of the dialog that is supposed to be closed
     */
    public CloseDialogEvent(final int id, final CloseDialogEvent.DialogType type) {
        dialogId = id;
        dialogType = type;
    }

    /**
     * Get the ID of the dialog that is supposed to be closed.
     *
     * @return the ID of the dialog that is supposed to be closed
     */
    public int getDialogId() {
        return dialogId;
    }

    /**
     * Get the type of the dialog that is supposed to be closed.
     *
     * @return the dialog that is supposed to be closed
     */
    public CloseDialogEvent.DialogType getDialogType() {
        return dialogType;
    }
}
