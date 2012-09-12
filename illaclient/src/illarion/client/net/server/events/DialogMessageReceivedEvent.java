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
package illarion.client.net.server.events;

/**
 * This event is fired in case the client receives a message dialog from the server that is supposed to be displayed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogMessageReceivedEvent extends AbstractDialogReceivedEvent implements ServerEvent {
    /**
     * The message displayed in the dialog.
     */
    private final String message;

    /**
     * Create a new instance of this event.
     *
     * @param dialogId      the ID of this dialog
     * @param dialogTitle   the title of the dialog
     * @param dialogMessage the message of the dialog
     */
    public DialogMessageReceivedEvent(final int dialogId, final String dialogTitle, final String dialogMessage) {
        super(dialogId, dialogTitle);
        message = dialogMessage;
    }

    /**
     * Get the message that is supposed to be displayed in the dialog.
     *
     * @return the message of the dialog
     */
    public String getMessage() {
        return message;
    }
}
