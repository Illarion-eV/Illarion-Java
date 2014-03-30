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
        Merchant,

        /**
         * The event targets a crafting dialog that is supposed to be closed.
         */
        Crafting,

        /**
         * This event targets a selection dialog that is supposed to be closed.
         */
        Selection
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
    private final DialogType dialogType;

    /**
     * The default constructor of this event.
     *
     * @param id the ID of the dialog that is supposed to be closed or {@link #ALL_DIALOGS}
     * @param type the type of the dialog that is supposed to be closed
     */
    public CloseDialogEvent(final int id, final DialogType type) {
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
    public DialogType getDialogType() {
        return dialogType;
    }

    /**
     * Check if this event is closing the specified dialog type.
     *
     * @param type the type to test
     * @return {@code true} in case the dialog is closing this kind of dialog
     */
    public boolean isClosingDialogType(final DialogType type) {
        if (dialogType == DialogType.Any) {
            return true;
        }
        return dialogType == type;
    }
}
