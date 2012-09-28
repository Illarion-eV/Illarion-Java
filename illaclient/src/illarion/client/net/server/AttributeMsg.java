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
import illarion.client.net.server.events.AttributeUpdateReceivedEvent;
import illarion.client.world.characters.CharacterAttribute;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * Servermessage: Character attributes ({@link CommandList#MSG_ATTRIBUTE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class AttributeMsg extends AbstractReply {
    /**
     * The format string for the {@link #toString()}.
     */
    @SuppressWarnings("nls")
    private static final String TO_STRING_FORMAT = "%1$s = %2$d";

    /**
     * The ID of the character this attribute update is related to.
     */
    private CharacterId targetCharacter;

    /**
     * The name of the received attribute.
     */
    private String attribute;

    /**
     * The value of the received attribute.
     */
    private int value;

    /**
     * Default constructor for the attribute message.
     */
    public AttributeMsg() {
        super(CommandList.MSG_ATTRIBUTE);
    }

    /**
     * Create a new instance of the attribute message as recycle object.
     *
     * @return a new instance of this message object
     */
    @Override
    public AttributeMsg clone() {
        return new AttributeMsg();
    }

    /**
     * Decode the attribute data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        targetCharacter = new CharacterId(reader);
        attribute = reader.readString();
        value = reader.readUShort();
    }

    /**
     * Execute the attribute message and send the decoded data to the rest of
     * the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        for (final CharacterAttribute charAttribute : CharacterAttribute.values()) {
            if (charAttribute.getServerName().equals(attribute)) {
                EventBus.publish(new AttributeUpdateReceivedEvent(targetCharacter, charAttribute, value));
            }
        }
        return true;
    }

    /**
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        attribute = null;
        targetCharacter = null;
    }

    /**
     * Get the data of this attribute message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @Override
    public String toString() {
        return toString(String.format(TO_STRING_FORMAT, attribute, value));
    }
}
