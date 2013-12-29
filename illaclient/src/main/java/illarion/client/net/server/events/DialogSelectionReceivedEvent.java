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

import illarion.client.world.items.SelectionItem;
import illarion.common.util.ArrayEnumeration;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * This event is fired in case the client receives a selection dialog from the server that is supposed to be displayed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogSelectionReceivedEvent extends AbstractDialogReceivedEvent
        implements Iterable<SelectionItem>, ServerEvent {
    /**
     * The message displayed in the dialog.
     */
    private final SelectionItem[] options;

    /**
     * This string that is supposed to be displayed in the dialog.
     */
    private final String message;

    /**
     * Create a new instance of this event.
     *
     * @param dialogId the ID of this dialog
     * @param dialogTitle the title of the dialog
     * @param message
     * @param dialogItems the items to be displayed in this dialog
     */
    public DialogSelectionReceivedEvent(
            final int dialogId, final String dialogTitle, final String message, final SelectionItem... dialogItems) {
        super(dialogId, dialogTitle);
        this.message = message;
        options = dialogItems;
    }

    /**
     * Get the amount of options.
     *
     * @return the count of options
     */
    public int getOptionCount() {
        return options.length;
    }

    /**
     * Get the message that is supposed to be displayed in the selection dialog.
     *
     * @return the message to be displayed in the dialog
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the option specified with the index.
     *
     * @param index the index of the option
     * @return the option value
     * @throws IndexOutOfBoundsException in case index is less then 0 or larger or equal to {@link
     * #getOptionCount()}.
     */
    public SelectionItem getOption(final int index) {
        if ((index < 0) || (index >= options.length)) {
            throw new IndexOutOfBoundsException();
        }

        return options[index];
    }

    @Nonnull
    @Override
    public Iterator<SelectionItem> iterator() {
        return new ArrayEnumeration<>(options);
    }
}
