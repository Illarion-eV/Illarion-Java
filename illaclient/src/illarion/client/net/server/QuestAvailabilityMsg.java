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
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This is the server message that handles the server messages about the available quests in range of the player.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_QUEST_AVAILABILITY)
public class QuestAvailabilityMsg extends AbstractReply {
    /**
     * This array contains the available quests in range of the character.
     */
    @Nullable
    private Location[] availableQuests;

    /**
     * This array contains the quests that become available to the player soon.
     */
    @Nullable
    private Location[] availableSoonQuests;

    @Override
    public void decode(final NetCommReader reader) throws IOException {
        final int availableQuestCount = reader.readUShort();
        if (availableQuestCount > 0) {
            availableQuests = new Location[availableQuestCount];
            for (int i = 0; i < availableQuestCount; i++) {
                availableQuests[i] = new Location(reader);
            }
        }

        final int soonQuestCount = reader.readUShort();
        if (soonQuestCount > 0) {
            availableSoonQuests = new Location[soonQuestCount];
            for (int i = 0; i < soonQuestCount; i++) {
                availableSoonQuests[i] = new Location(reader);
            }
        }
    }

    @Override
    public boolean executeUpdate() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return toString("Available quests: " + ((availableQuests == null) ? '0' : availableQuests.length) +
                " Available soon quests: " + ((availableSoonQuests == null) ? '0' : availableSoonQuests.length));
    }
}
