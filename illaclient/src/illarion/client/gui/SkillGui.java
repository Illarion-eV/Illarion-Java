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
package illarion.client.gui;

import illarion.common.data.Skill;

import javax.annotation.Nonnull;

/**
 * This class is used to control the display of the skill messages in the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface SkillGui {
    /**
     * Hide the skill window.
     */
    void hideSkillWindow();

    /**
     * Show the skill window.
     */
    void showSkillWindow();

    /**
     * Toggle the visibility state of the skill window.
     */
    void toggleSkillWindow();

    /**
     * Update the value of the skill.
     *
     * @param skill the skill that is updated
     * @param value the new value of the skill
     * @param minor the minor value of the skill
     */
    void updateSkill(@Nonnull Skill skill, int value, int minor);
}
