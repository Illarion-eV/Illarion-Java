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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.types.CharacterId;

/**
 * Client Command: Attacking a character ({@link CommandList#CMD_ATTACK}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AttackCmd extends AbstractCommand {
    /**
     * The ID of the character that shall be attacked.
     */
    private CharacterId charId;

    /**
     * Default constructor for the attacking character command.
     */
    public AttackCmd() {
        super(CommandList.CMD_ATTACK);
    }

    /**
     * Create a duplicate of this attacking character command.
     *
     * @return new instance of this command
     */
    @Override
    public AttackCmd clone() {
        return new AttackCmd();
    }

    /**
     * Encode the data of this attacking character command and put the values
     * into the buffer.
     *
     * @param writer the interface that allows writing data to the network
     *               communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        charId.encode(writer);
    }

    /**
     * Set the target for this attacking command.
     *
     * @param attCharId the ID of the character that shall be attacked
     */
    public void setTarget(final CharacterId attCharId) {
        charId = attCharId;
    }

    /**
     * Get the data of this attacking command as string.
     *
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(charId.toString());
    }
}
