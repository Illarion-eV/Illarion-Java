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

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This is the server message that causes a quest to be deleted from the quest log.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_QUEST_DELETE)
public final class QuestDeleteMsg extends AbstractReply {
    /**
     * The ID of the quest.
     */
    private int questId;

    @Override
    @Nonnull
    public String toString() {
        return toString("ID: " + questId);
    }

    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        questId = reader.readUShort();
    }

    @Override
    public boolean executeUpdate() {
        World.getGameGui().getQuestGui().removeQuest(questId);
        return true;
    }

    @Override
    public boolean processNow() {
        return World.getGameGui().isReady();
    }
}
