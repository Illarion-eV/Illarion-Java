/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;

import java.io.IOException;

/**
 * This is the server message that contains the information regarding a single quest.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_QUEST)
public final class QuestMsg extends AbstractReply {
    /**
     * The ID of the quest.
     */
    private int questId;

    /**
     * The title of the quest.
     */
    private String title;

    /**
     * The description text of the quests current state.
     */
    private String description;

    /**
     * The flag if this quest is already finished.
     */
    private boolean finished;

    /**
     * The target locations where the next steps of the quest will happen.
     */
    private Location[] targetLocations;

    /**
     * Decode data from server receive buffer. And store the data for later execution.
     *
     * @param reader the receiver that stores the data that shall be decoded in this function
     * @throws java.io.IOException In case the function reads over the buffer of the receiver this exception is thrown
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        questId = reader.readUShort();
        title = reader.readString();
        description = reader.readString();
        finished = reader.readByte() == 1;
        targetLocations = new Location[reader.readUByte()];
        for (int i = 0; i < targetLocations.length; i++) {
            targetLocations[i] = decodeLocation(reader);
        }
    }

    @Override
    public boolean executeUpdate() {
        return World.getGameGui().isReady();
    }

    /**
     * Get the string representation of this reply object.
     *
     * @return String that contains the simple class name of this reply class instance
     */
    @Override
    public String toString() {
        return toString("ID: " + questId + " Title: \"" + title + '"' + (finished ? " (finished)" : ""));
    }
}
