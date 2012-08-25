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
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Send a text that was requested by the server and typed in by the player ( {@link
 * illarion.client.net.CommandList#CMD_CLOSE_DIALOG_INPUT}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CloseDialogSelectionCmd
        extends AbstractCommand {
    /**
     * The ID that was send by the server to initiate text input.
     */
    private int dialogID;

    /**
     * The text that is send to the server.
     */
    private String text;

    /**
     * The flag that stores if the input was confirmed or canceled.
     */
    private boolean success;

    /**
     * Default constructor for the text response command.
     */
    public CloseDialogSelectionCmd() {
        super(CommandList.CMD_CLOSE_DIALOG_INPUT);
    }

    /**
     * Create a duplicate of this text response command.
     *
     * @return new instance of this command
     */
    @Override
    public CloseDialogSelectionCmd clone() {
        return new CloseDialogSelectionCmd();
    }

    /**
     * Encode the data of this text response command and put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeInt(dialogID);
        if (success) {
            writer.writeUByte((byte) 0xFF);
        } else {
            writer.writeUByte((byte) 0x00);
        }
        writer.writeString(text);
    }

    /**
     * Clean up the command before put it back into the recycler for later reuse.
     */
    @Override
    public void reset() {
        text = null;
    }

    /**
     * Set the ID of the dialog that supplied the text.
     *
     * @param id the id of the dialog
     */
    public void setDialogId(final int id) {
        dialogID = id;
    }

    /**
     * Set the text that shall be send to the server.
     *
     * @param sayText the text that is send
     */
    public void setText(final String sayText) {
        text = sayText;
    }

    /**
     * Set if the input dialog was confirmed or canceled.
     *
     * @param value {@code true} in case the dialog was confirmed
     */
    public void setSuccess(final boolean value) {
        success = value;
    }

    /**
     * Get the data of this text response command as string.
     *
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Text Response: " + text);
    }
}
