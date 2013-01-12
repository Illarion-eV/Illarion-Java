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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.annotation.NonNull;
import illarion.common.net.NetCommWriter;
import net.jcip.annotations.Immutable;

/**
 * Client Command: This command returns the selection made in a selection dialog to the server
 * ({@link CommandList#CMD_CLOSE_DIALOG_SELECTION}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class CloseDialogSelectionCmd extends AbstractCommand {
    /**
     * The ID that was send by the server to initiate text input.
     */
    private final int dialogID;

    /**
     * The index that was selected.
     */
    private final short selectedIndex;

    /**
     * The flag that stores if the input was confirmed or canceled.
     */
    private final boolean success;

    /**
     * Default constructor for the text response command.
     *
     * @param dialogID      the ID of the dialog
     * @param selectedIndex the index that was selected
     * @param success       {@code true} in case the dialog got confirmed
     */
    public CloseDialogSelectionCmd(final int dialogID, final int selectedIndex, final boolean success) {
        super(CommandList.CMD_CLOSE_DIALOG_SELECTION);

        this.dialogID = dialogID;
        this.selectedIndex = (short) selectedIndex;
        this.success = success;
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        writer.writeInt(dialogID);
        if (success) {
            writer.writeUByte((byte) 0xFF);
        } else {
            writer.writeUByte((byte) 0x00);
        }
        writer.writeUByte(selectedIndex);
    }

    @NonNull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Dialog ID: " + dialogID + " - Index: " + selectedIndex + " successful: " + success);
    }
}
