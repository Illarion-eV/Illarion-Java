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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the references to all skills.
 *
 * @author Martin Karing &lt;nitram@illarion.org%gt;
 */
@ThreadSafe
@Immutable
public final class Skills {
    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final Skills INSTANCE;

    static {
        INSTANCE = new Skills();
        SkillLoader.load();
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static Skills getInstance() {
        return INSTANCE;
    }

    /**
     * The map of skills that are known to this class.
     */
    @Nonnull
    private final Map<Integer, Skill> skillMap;

    /**
     * Private constructor that prepares the internal data structures and that is used to prevent that multiple
     * instances of this class are created.
     */
    private Skills() {
        skillMap = new HashMap<Integer, Skill>();
    }

    /**
     * Add a skill to this class.
     *
     * @param skill the skill to add
     */
    void addSkill(@Nonnull final Skill skill) {
        skillMap.put(skill.getId(), skill);
    }

    /**
     * Get the skill that is registered with a specified ID.
     *
     * @param id the ID of the skill
     * @return the skill
     */
    @Nullable
    public Skill getSkill(final int id) {
        return skillMap.get(id);
    }

    /**
     * This function tries to match the name given as parameter to one of the skills.
     *
     * @param name the name of the skill
     * @return the matched skill
     */
    @Nullable
    public Skill getSkill(@Nonnull final String name) {
        final String cleanName = name.trim().toLowerCase();

        for (final Skill skill : skillMap.values()) {
            if (cleanName.equalsIgnoreCase(skill.getName()) || cleanName.equalsIgnoreCase(skill.getNameGerman()) ||
                    cleanName.equalsIgnoreCase(skill.getNameEnglish())) {
                return skill;
            }
        }

        if ("lumberjacking".equalsIgnoreCase(cleanName)) {
            return getSkill("woodcutting");
        }

        if ("peasantry".equalsIgnoreCase(cleanName)) {
            return getSkill("farming");
        }

        if ((cleanName.contains("baking") || cleanName.contains("cooking")) &&
                !"cookingAndBaking".equalsIgnoreCase(cleanName)) {
            return getSkill("cookingAndBaking");
        }

        if (cleanName.contains(" ")) {
            return getSkill(cleanName.replace(" ", ""));
        }

        return null;
    }

    /**
     * Get a list of all skills that are known to this class.
     *
     * @return the list of all skills known to this class
     */
    @Nonnull
    public Collection<Skill> getSkills() {
        return Collections.unmodifiableCollection(skillMap.values());
    }
}
