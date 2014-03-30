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
