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
package illarion.client.gui;

import illarion.common.types.Location;

import javax.annotation.Nonnull;

/**
 * This interface defines the access possibilities that are exposed to access the quest log.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface QuestGui {
    /**
     * Hide the quest log from the screen.
     */
    void hideQuestLog();

    /**
     * Check if the quest log is currently visible.
     *
     * @return {@code true} if the quest log is currently visible
     */
    boolean isQuestLogVisible();

    /**
     * Remove a quest from the log.
     *
     * @param questId the ID of the quest
     */
    void removeQuest(int questId);

    /**
     * Set the quest that is currently shown in the quest log.
     *
     * @param questId the ID of the quest that is shown in the log
     */
    void setDisplayedQuest(int questId);

    /**
     * Add or update a quest in the log.
     *
     * @param questId the ID of the quest
     * @param name the title of the quest
     * @param description the current description text of the quest
     * @param finished {@code true} in case this quest is finished already
     * @param locations a list of target locations for the text stage of the quest
     */
    void setQuest(
            int questId,
            @Nonnull String name,
            @Nonnull String description,
            boolean finished,
            @Nonnull Location... locations);

    /**
     * Toggle the visibility state of the quest log.
     */
    void toggleQuestLog();

    /**
     * Show the quest log on the screen.
     */
    void showQuestLog();
}
