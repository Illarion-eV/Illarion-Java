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
import illarion.common.net.NetCommWriter;

/**
 * This command is used to inform the server that a message dialog was closed.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class CloseDialogMessageCmd
        extends AbstractCommand {
    /**
     * The ID of the dialog to close. This ID is send by the server once the dialog is opened.
     */
    private int dialogId;

    /**
     * Default constructor for the close message dialog command.
     */
    public CloseDialogMessageCmd() {
        super(CommandList.CMD_CLOSE_DIALOG_MSG);
    }

    /**
     * Set the ID of the dialog that was closed.
     *
     * @param newId the ID of the dialog
     */
    public void setDialogId(final int newId) {
        dialogId = newId;
    }

    @Override
    public CloseDialogMessageCmd clone() {
        return new CloseDialogMessageCmd();
    }

    @Override
    public void encode(NetCommWriter writer) {
        writer.writeInt(dialogId);
    }

    @Override
    public String toString() {
        return toString("dialog ID: " + dialogId);
    }
}
