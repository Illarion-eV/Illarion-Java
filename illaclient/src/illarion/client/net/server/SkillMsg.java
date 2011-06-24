/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import java.io.IOException;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;

/**
 * Servermessage: Update the character skills (
 * {@link illarion.client.net.CommandList#MSG_SKILL}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
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
     * Default constructor for the skill message.
     */
    public SkillMsg() {
        super(CommandList.MSG_SKILL);
    }

    /**
     * Create a new instance of the skill message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public SkillMsg clone() {
        return new SkillMsg();
    }

    /**
     * Decode the skill data the receiver got and prepare it for the execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
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
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        skill = null;
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
        builder.append(" ");
        builder.append(skill);
        builder.append(": ");
        builder.append(value);
        builder.append(" - ");
        builder.append(minor);
        return toString(builder.toString());
    }
}
