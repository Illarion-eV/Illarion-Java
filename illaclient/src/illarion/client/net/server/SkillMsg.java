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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.common.net.NetCommReader;

import java.io.IOException;

/**
 * Servermessage: Update the character skills ({@link CommandList#MSG_SKILL}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_SKILL)
public final class SkillMsg extends AbstractReply {
    /**
     * The ID of the group the skill is displayed in.
     */
    private short group;

    /**
     * The current minor skill points of that skill.
     */
    private int minor;

    /**
     * The skill that is updated.
     */
    private String skill;

    /**
     * The new value of the skill.
     */
    private int value;

    /**
     * Decode the skill data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        skill = reader.readString();
        group = reader.readUByte();
        value = reader.readUShort();
        minor = reader.readUShort();
    }

    /**
     * Execute the skill message and send the decoded data to the rest of the
     * client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        // Gui.getInstance().getSkills().update(group, skill, value, minor);

        return true;
    }

    /**
     * Get the data of this skill message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Group: ");
        builder.append(group);
        builder.append(' ');
        builder.append(skill);
        builder.append(": ");
        builder.append(value);
        builder.append(" - ");
        builder.append(minor);
        return toString(builder.toString());
    }
}
