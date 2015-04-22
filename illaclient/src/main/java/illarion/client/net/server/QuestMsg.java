/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This is the server message that contains the information regarding a single quest.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_QUEST)
public final class QuestMsg implements ServerReply {
    /**
     * The ID of the quest.
     */
    private int questId;

    /**
     * The title of the quest.
     */
    @Nullable
    private String title;

    /**
     * The description text of the quests current state.
     */
    @Nullable
    private String description;

    /**
     * The flag if this quest is already finished.
     */
    private boolean finished;

    /**
     * The target locations where the next steps of the quest will happen.
     */
    @Nullable
    private List<ServerCoordinate> targetLocations;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        questId = reader.readUShort();
        title = reader.readString();
        description = reader.readString();
        finished = reader.readByte() == 1;
        int count = reader.readUByte();
        targetLocations = Arrays.asList(new ServerCoordinate[count]);
        for (int i = 0; i < count; i++) {
            targetLocations.set(i, new ServerCoordinate(reader));
        }
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((title == null) || (description == null) || (targetLocations == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getGameGui().getQuestGui().setQuest(questId, title, description, finished, targetLocations);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(QuestMsg.class, "Quest ID: " + questId, title);
    }
}
