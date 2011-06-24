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

import illarion.client.guiNG.GUI;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.Game;

/**
 * Servermessage: Character attributes (
 * {@link illarion.client.net.CommandList#MSG_ATTRIBUTE}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class AttributeMsg extends AbstractReply {
    /**
     * The string constants for the food level attribute.
     */
    @SuppressWarnings("nls")
    private static final String FOODLEVEL = "foodlevel".intern();

    /**
     * The string constants for the hit points attribute.
     */
    @SuppressWarnings("nls")
    private static final String HITPOINTS = "hitpoints".intern();

    /**
     * The string constants for the mana points attribute.
     */
    @SuppressWarnings("nls")
    private static final String MANAPOINTS = "mana".intern();

    /**
     * The string constants for the perception attribute.
     */
    @SuppressWarnings("nls")
    private static final String PERCEPTION = "perception".intern();

    /**
     * The format string for the {@link #toString()}.
     */
    @SuppressWarnings("nls")
    private static final String TO_STRING_FORMAT = "%1$s = %2$d";

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
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
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
        if (attribute.equals(HITPOINTS)) {
            GUI.getInstance().getIndicators().getHealth().setValue(value);
        } else if (attribute.equals(MANAPOINTS)) {
            GUI.getInstance().getIndicators().getMana().setValue(value);
        } else if (attribute.equals(FOODLEVEL)) {
            GUI.getInstance().getIndicators().getFood().setValue(value);
        } else if (attribute.equals(PERCEPTION)) {
            Game.getPlayer().setPerception(value);
        }
        // TODO: handle the missing attributes that could be received.
        return true;
    }

    /**
     * Check if the GUI is already prepared before executing this message.
     */
    @Override
    public boolean processNow() {
        if (attribute.equals(HITPOINTS)) {
            return (GUI.getInstance().getIndicators().getHealth() != null);
        } else if (attribute.equals(FOODLEVEL)) {
            return (GUI.getInstance().getIndicators().getFood() != null);
        } else if (attribute.equals(MANAPOINTS)) {
            return (GUI.getInstance().getIndicators().getMana() != null);
        }
        return true;
    }

    /**
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        attribute = null;
    }

    /**
     * Get the data of this attribute message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @Override
    public String toString() {
        return toString(String.format(TO_STRING_FORMAT, attribute,
            Integer.valueOf(value)));
    }
}
