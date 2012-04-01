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
 * This event is fired in case the client receives a input dialog from the server that is supposed to be displayed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DialogInputReceivedEvent {
    /**
     * The title of the dialog.
     */
    private final String title;

    /**
     * The maximal length that is allowed in this input.
     */
    private final int length;

    /**
     * The ID of the dialog that has to be send to notify the server that the dialog was closed.
     */
    private final int id;

    /**
     * The multiple lines flag marks if more then one line is allowed to be typed in.
     */
    private final boolean multiple;

    /**
     * Create a new instance of this event.
     *
     * @param dialogId      the ID of this dialog
     * @param dialogTitle   the title of the dialog
     * @param maxLength     the maximal mount of characters to type in
     * @param multipleLines the multiple line flag of this dialog
     */
    public DialogInputReceivedEvent(final int dialogId, final String dialogTitle, final int maxLength,
                                    final boolean multipleLines) {
        title = dialogTitle;
        length = maxLength;
        id = dialogId;
        multiple = multipleLines;
    }

    /**
     * Get the ID of this dialog.
     *
     * @return the ID of the dialog
     */
    public int getId() {
        return id;
    }

    /**
     * Get the maximal amount of characters allowed to type in into the input dialog.
     *
     * @return the maximal length of the input dialog
     */
    public int getMaxLength() {
        return length;
    }

    /**
     * Get the title of the dialog.
     *
     * @return the title of the message dialog
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the multiple line flag of the requested input dialog.
     *
     * @return {@code true} in case the input dialog is supposed to display multiple lines.
     */
    public boolean hasMultipleLines() {
        return multiple;
    }
}
