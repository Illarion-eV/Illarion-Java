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

import illarion.common.annotation.NonNull;
import net.jcip.annotations.ThreadSafe;

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
    @NonNull
    private final String nameEnglish;

    /**
     * The german name of the skill.
     */
    @NonNull
    private final String nameGerman;

    /**
     * The list of skills that belong to this group.
     */
    @NonNull
    private final List<Skill> skills;

    /**
     * Create a new group of skills.
     *
     * @param german  the german name of the group
     * @param english the english name of the group
     */
    public SkillGroup(@NonNull final String german, @NonNull final String english) {
        nameEnglish = english;
        nameGerman = german;

        skills = new ArrayList<Skill>();
    }

    /**
     * Add a new skill to the group.
     *
     * @param skill the skill that is supposed to be added to the group
     */
    void addSkill(@NonNull final Skill skill) {
        skills.add(skill);
    }

    /**
     * Get the english name of the skill group.
     *
     * @return the english name
     */
    @NonNull
    public String getNameEnglish() {
        return nameEnglish;
    }

    /**
     * Get the german name of the skill group.
     *
     * @return the german name
     */
    @NonNull
    public String getNameGerman() {
        return nameGerman;
    }

    /**
     * Get a list of skills that belong to this group.
     *
     * @return the list of skills that is part of this group
     */
    @NonNull
    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    @NonNull
    @Override
    public String toString() {
        return "Skill Group: " + nameEnglish;
    }
}
