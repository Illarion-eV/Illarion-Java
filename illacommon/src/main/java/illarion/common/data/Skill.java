/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.data;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This class defines a single skill.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public final class Skill {
    /**
     * The name of the skill.
     */
    @Nonnull
    private final String name;

    /**
     * The name of the skill in english.
     */
    @Nonnull
    private final String nameEnglish;

    /**
     * The name of the skill in german.
     */
    @Nonnull
    private final String nameGerman;

    /**
     * The ID of the skill.
     */
    private final int id;

    /**
     * The group this skill belongs to.
     */
    @Nonnull
    private final SkillGroup group;

    /**
     * Create a new instance of the skill class.
     *
     * @param skillId    the ID of the skill
     * @param skillName  the name of the skill
     * @param german     the german name of the skill
     * @param english    the english name of the skill
     * @param skillGroup the group this skill belong to
     */
    Skill(final int skillId, @Nonnull final String skillName, @Nonnull final String german,
          @Nonnull final String english, @Nonnull final SkillGroup skillGroup) {
        id = skillId;
        name = skillName;
        nameEnglish = english;
        nameGerman = german;
        group = skillGroup;
        group.addSkill(this);
    }

    /**
     * Get the ID of the skill.
     *
     * @return the ID of the skill
     */
    public int getId() {
        return id;
    }

    /**
     * Get the group this skill belongs to.
     *
     * @return the group this skill belong to
     */
    @Nonnull
    public SkillGroup getGroup() {
        return group;
    }

    /**
     * Get the name of the skill. This name is mainly required for the scripts.
     *
     * @return the name of the skills
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Get the german name of the skill.
     *
     * @return the german name of the skill
     */
    @Nonnull
    public String getNameGerman() {
        return nameGerman;
    }

    /**
     * Get the english name of the skill.
     *
     * @return the english name of the skill
     */
    @Nonnull
    public String getNameEnglish() {
        return nameEnglish;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Skill " + Integer.toString(id) + ": " + nameEnglish;
    }
}
