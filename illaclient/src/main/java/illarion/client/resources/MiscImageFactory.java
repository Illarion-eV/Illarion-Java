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
package illarion.client.resources;

import illarion.client.resources.data.MiscImageTemplate;

import javax.annotation.Nonnull;

/**
 * This class is used to load and store the graphics that are needed for displaying the GUI of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MiscImageFactory extends AbstractTemplateFactory<MiscImageTemplate> {
    /**
     * The ID of the attack marker image.
     */
    public static final int ATTACK_MARKER = 0;

    /**
     * The ID of the arrow that is displayed on the mini map to show the direction of the next quest.
     */
    public static final int MINI_MAP_ARROW = 1;

    /**
     * The ID of the point that is displayed on the mini map in case the target is on the mini map area.
     */
    public static final int MINI_MAP_POINT = 2;

    /**
     * The ID of te exclamation mark that is displayed on the mini map for the starting points of quests.
     */
    public static final int MINI_MAP_EXCLAMATION = 6;

    /**
     * The ID of the quest marker graphic that is rendered on the game map to mark where the player finds the next
     * target of a quest.
     */
    public static final int QUEST_MARKER_QUESTION_MARK = 3;

    /**
     * The ID of the quest marker graphic that is rendered on the game map to mark where the player is able to find a
     * new quest.
     */
    public static final int QUEST_MARKER_EXCLAMATION_MARK = 4;

    /**
     * The singleton instance.
     */
    private static final MiscImageFactory INSTANCE = new MiscImageFactory();

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance
     */
    @Nonnull
    public static MiscImageFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor.
     */
    private MiscImageFactory() {
    }
}
