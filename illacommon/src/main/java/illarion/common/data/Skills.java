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
    private static final Skills INSTANCE = new Skills();

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
        skillMap = new HashMap<>();
    }

    /**
     * Add a skill to this class.
     *
     * @param skill the skill to add
     */
    void addSkill(@Nonnull Skill skill) {
        skillMap.put(skill.getId(), skill);
    }

    /**
     * Get the skill that is registered with a specified ID.
     *
     * @param id the ID of the skill
     * @return the skill
     */
    @Nullable
    public Skill getSkill(int id) {
        return skillMap.get(id);
    }

    /**
     * This function tries to match the name given as parameter to one of the skills.
     *
     * @param name the name of the skill
     * @return the matched skill
     */
    @Nullable
    public Skill getSkill(@Nonnull String name) {
        String cleanName = name.trim().toLowerCase();

        for (Skill skill : skillMap.values()) {
            if (cleanName.equalsIgnoreCase(skill.getName())) {
                return skill;
            }
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
