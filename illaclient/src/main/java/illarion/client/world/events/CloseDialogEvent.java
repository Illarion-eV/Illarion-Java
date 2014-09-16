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

import illarion.client.gui.DialogType;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;

/**
 * This event is used to close a specified dialog on the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CloseDialogEvent {

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
    @Nonnull
    private final Collection<DialogType> dialogTypes;

    /**
     * The default constructor of this event.
     *
     * @param id the ID of the dialog that is supposed to be closed or {@link #ALL_DIALOGS}
     */
    public CloseDialogEvent(int id) {
        dialogId = id;
        dialogTypes = EnumSet.allOf(DialogType.class);
    }

    /**
     * The default constructor of this event.
     *
     * @param id the ID of the dialog that is supposed to be closed or {@link #ALL_DIALOGS}
     */
    public CloseDialogEvent(int id, @Nonnull DialogType firstType, @Nonnull DialogType... moreTypes) {
        dialogId = id;
        dialogTypes = EnumSet.of(firstType, moreTypes);
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
     * Check if this event is closing the specified dialog type.
     *
     * @param type the type to test
     * @return {@code true} in case the dialog is closing this kind of dialog
     */
    public boolean isClosingDialogType(@Nonnull DialogType type) {
        return dialogTypes.contains(type);
    }
}
