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
import illarion.common.types.Location;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * This is the server message that handles the server messages about the available quests in range of the player.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_QUEST_AVAILABILITY)
public final class QuestAvailabilityMsg implements ServerReply {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(QuestAvailabilityMsg.class);

    /**
     * This array contains the available quests in range of the character.
     */
    @Nullable
    private List<Location> availableQuests;

    /**
     * This array contains the quests that become available to the player soon.
     */
    @Nullable
    private List<Location> availableSoonQuests;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        int availableQuestCount = reader.readUShort();
        if (availableQuestCount > 0) {
            availableQuests = Arrays.asList(new Location[availableQuestCount]);
            for (int i = 0; i < availableQuestCount; i++) {
                availableQuests.set(i, new Location(reader));
            }
        } else {
            availableQuests = Collections.emptyList();
        }

        int soonQuestCount = reader.readUShort();
        if (soonQuestCount > 0) {
            availableSoonQuests = Arrays.asList(new Location[soonQuestCount]);
            for (int i = 0; i < soonQuestCount; i++) {
                availableSoonQuests.set(i, new Location(reader));
            }
        } else {
            availableSoonQuests = Collections.emptyList();
        }
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((availableQuests == null) || (availableSoonQuests == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        boolean needToRebuildList = false;
        for (Location location : availableSoonQuests) {
            if (availableSoonQuests.contains(location)) {
                needToRebuildList = true;
                break;
            }
        }

        if (needToRebuildList) {
            log.warn("Server send at least one location for a quest marker in both lists. Need to rebuild.");
            Collection<Location> newList = new HashSet<>(availableSoonQuests);
            newList.removeAll(availableQuests);
            World.getMap().applyQuestStartLocations(availableQuests, newList);
        } else {
            World.getMap().applyQuestStartLocations(availableQuests, availableSoonQuests);
        }
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(QuestAvailabilityMsg.class, availableQuests, availableSoonQuests);
    }
}
