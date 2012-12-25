/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.client.net.server.events;

import illarion.common.data.Skill;

/**
 * This event is published in case a new skill value is received.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class SkillReceivedEvent implements ServerEvent {
    /**
     * The skill that is updated with this event.
     */
    private final Skill skill;

    /**
     * The new value of the skill.
     */
    private final int value;

    /**
     * The minor value of the skill.
     */
    private final int minor;

    /**
     * Constructor of the show book event.
     *
     * @param skill the skill that is received
     * @param value the value of the skill
     * @param minor the minor value of the skill
     */
    public SkillReceivedEvent(final Skill skill, final int value, final int minor) {
        this.skill = skill;
        this.value = value;
        this.minor = minor;
    }

    /**
     * Get the minor value of the skill.
     *
     * @return the new minor value of the skill
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Get the skill that is updated with this event.
     *
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * Get the new value of this skill.
     *
     * @return the new value
     */
    public int getValue() {
        return value;
    }
}
