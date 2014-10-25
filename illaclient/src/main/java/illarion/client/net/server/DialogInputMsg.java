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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import javolution.text.TextBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Text Request ( {@link illarion.client.net.CommandList#MSG_DIALOG_INPUT}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_INPUT)
public final class DialogInputMsg extends AbstractGuiMsg {
    /**
     * The title that is supposed to be displayed in the dialog.
     */
    private String title;

    /**
     * The description text that is displayed in this dialog.
     */
    private String description;

    /**
     * The flag if the text input is supposed to be multi-lined or not.
     */
    private boolean multiLine;

    /**
     * The maximal amount of characters that are valid to be input.
     */
    private int maxCharacters;

    /**
     * The ID of this request.
     */
    private int requestId;

    /**
     * Decode the text request the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();
        description = reader.readString();
        multiLine = reader.readByte() != 0;
        maxCharacters = reader.readUShort();
        requestId = reader.readInt();
    }

    /**
     * Execute the text request message and send the decoded data to the rest of the client.
     */
    @Override
    public void executeUpdate() {
        World.getGameGui().getDialogInputGui().showInputDialog(requestId, title, description, maxCharacters, multiLine);
    }

    /**
     * Get the data of this text request message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        TextBuilder builder = new TextBuilder();
        builder.append("title: ").append(title);
        builder.append(" id: ").append(requestId);
        builder.append(" maximal characters: ").append(maxCharacters);
        builder.append(" support multiline: ").append(multiLine);
        return toString(builder.toString());
    }
}
