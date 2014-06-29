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
package illarion.common.data;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class defines a group of skills.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
public final class SkillGroup {
    /**
     * The english name of the skill.
     */
    @Nonnull
    private final String nameEnglish;

    /**
     * The german name of the skill.
     */
    @Nonnull
    private final String nameGerman;

    /**
     * The ID of the group.
     */
    private final int groupId;

    /**
     * The list of skills that belong to this group.
     */
    @Nonnull
    private final List<Skill> skills;

    /**
     * Create a new group of skills.
     *
     * @param german the german name of the group
     * @param english the english name of the group
     */
    public SkillGroup(int id, @Nonnull String german, @Nonnull String english) {
        groupId = id;
        nameEnglish = english;
        nameGerman = german;

        skills = new ArrayList<>();
    }

    /**
     * Add a new skill to the group.
     *
     * @param skill the skill that is supposed to be added to the group
     */
    void addSkill(@Nonnull Skill skill) {
        skills.add(skill);
    }

    /**
     * Get the english name of the skill group.
     *
     * @return the english name
     */
    @Nonnull
    public String getNameEnglish() {
        return nameEnglish;
    }

    /**
     * Get the german name of the skill group.
     *
     * @return the german name
     */
    @Nonnull
    public String getNameGerman() {
        return nameGerman;
    }

    /**
     * Get a list of skills that belong to this group.
     *
     * @return the list of skills that is part of this group
     */
    @Nonnull
    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Skill Group: " + nameEnglish;
    }

    /**
     * Get the ID of the group of this skill.
     *
     * @return the group of the skill
     */
    public int getGroupId() {
        return groupId;
    }
}
