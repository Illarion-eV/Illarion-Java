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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.data.Skill;
import illarion.common.data.Skills;
import illarion.common.net.NetCommReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Update the character skills ({@link CommandList#MSG_SKILL}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_SKILL)
public final class SkillMsg extends AbstractGuiMsg {
    /**
     * The logger instance of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(SkillMsg.class);
    /**
     * The current minor skill points of that skill.
     */
    private int minor;

    /**
     * The ID of the skill that is used
     */
    private int skill;

    /**
     * The new value of the skill.
     */
    private int value;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        skill = reader.readUByte();
        value = reader.readUShort();
        minor = reader.readUShort();
    }

    @Override
    public void executeUpdate() {
        Skill skill = Skills.getInstance().getSkill(this.skill);
        if (skill == null) {
            log.warn("Unknown skill received! ID: {}", this.skill);
        } else {
            World.getGameGui().getSkillGui().updateSkill(skill, value, minor);
        }
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(skill + ": " + value + " - " + minor);
    }
}
