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

import de.lessvoid.nifty.NiftyEvent;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This event is the parent of other events that refer to a specified dialog. This class provides a unified way to
 * refer to the dialog ID.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
@Immutable
public class DialogEvent implements NiftyEvent {
    /**
     * The ID of the dialog.
     */
    private final int dialogId;

    /**
     * Create a new instance of this event and set the ID of the dialog that is referred.
     *
     * @param id the ID of the dialog
     */
    public DialogEvent(int id) {
        dialogId = id;
    }

    /**
     * Get the ID of the dialog this event refers to when this event was fired.
     *
     * @return the dialog ID
     */
    public int getDialogId() {
        return dialogId;
    }
}
