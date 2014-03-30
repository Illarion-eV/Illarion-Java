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
 * This is the partial definition of a event that is generated in case a dialog was received from the server and should
 * be displayed. In this class all components shared between the dialogs are implemented.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public abstract class AbstractDialogReceivedEvent {
    /**
     * The ID of the dialog that has to be send to notify the server that the dialog was closed.
     */
    private final int id;

    /**
     * The title of the dialog.
     */
    private final String title;

    /**
     * Create a new instance of this event.
     *
     * @param dialogId the ID of this dialog
     * @param dialogTitle the title of the dialog
     */
    protected AbstractDialogReceivedEvent(final int dialogId, final String dialogTitle) {
        title = dialogTitle;
        id = dialogId;
    }

    /**
     * Get the ID of this dialog.
     *
     * @return the ID of the dialog
     */
    public final int getId() {
        return id;
    }

    /**
     * Get the title of the dialog.
     *
     * @return the title of the message dialog
     */
    public final String getTitle() {
        return title;
    }
}
