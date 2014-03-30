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
package illarion.client.net.server.events;

/**
 * This event is send once the server sends a aborted event for a crafting dialog
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DialogCraftingUpdateAbortedReceivedEvent {
    /**
     * The ID of the dialog.
     */
    private final int requestId;

    /**
     * Create a new instance of this event.
     *
     * @param requestId the ID of the dialog that is updated
     */
    public DialogCraftingUpdateAbortedReceivedEvent(final int requestId) {
        this.requestId = requestId;
    }

    /**
     * Get the ID of this dialog.
     *
     * @return the ID of the dialog
     */
    public int getDialogId() {
        return requestId;
    }
}
