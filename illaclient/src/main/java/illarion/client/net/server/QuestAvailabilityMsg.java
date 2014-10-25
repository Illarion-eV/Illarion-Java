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
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * This is the server message that handles the server messages about the available quests in range of the player.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_QUEST_AVAILABILITY)
public class QuestAvailabilityMsg extends AbstractGuiMsg {
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
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        int availableQuestCount = reader.readUShort();
        if (availableQuestCount > 0) {
            availableQuests = new Location[availableQuestCount];
            for (int i = 0; i < availableQuestCount; i++) {
                availableQuests[i] = new Location(reader);
            }
        }

        int soonQuestCount = reader.readUShort();
        if (soonQuestCount > 0) {
            availableSoonQuests = new Location[soonQuestCount];
            for (int i = 0; i < soonQuestCount; i++) {
                availableSoonQuests[i] = new Location(reader);
            }
        }
    }

    @Override
    public void executeUpdate() {
        Collection<Location> available;
        if (availableQuests == null) {
            available = Collections.emptyList();
        } else {
            available = new HashSet<>(Arrays.asList(availableQuests));
        }

        Collection<Location> availableSoon;
        if (availableSoonQuests == null) {
            availableSoon = Collections.emptyList();
        } else {
            availableSoon = new HashSet<>(Arrays.asList(availableSoonQuests));
        }
        availableSoon.removeAll(available);

        World.getMap().applyQuestStartLocations(available, availableSoon);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("Available quests: " + ((availableQuests == null) ? '0' : availableQuests.length) +
                                " Available soon quests: " +
                                ((availableSoonQuests == null) ? '0' : availableSoonQuests.length));
    }
}
