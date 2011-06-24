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
package illarion.client.net.client;

import java.io.UnsupportedEncodingException;

import javolution.context.PoolContext;
import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

import illarion.client.net.CommandFactory;
import illarion.client.net.NetCommWriter;
import illarion.client.world.Game;

import illarion.common.util.RecycleObject;

/**
 * Default super class for all commands that get send to a server. This command
 * objects are created by the recycle factory for commands.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public abstract class AbstractCommand implements RecycleObject {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger
        .getLogger(AbstractCommand.class);

    /**
     * The ID of the command.
     */
    private int id;

    /**
     * The constructor of a command. This is used to set the ID of the command.
     * 
     * @param commId the ID of the command
     */
    protected AbstractCommand(final int commId) {
        id = commId;
    }

    /**
     * Encode a string and put it into the buffer.
     * 
     * @param buffer the buffer that contains the the already encoded data
     * @param pos the first free position in the buffer
     * @param txt the string that shall be encoded
     * @return the new first free position in the buffer
     */
    @SuppressWarnings("nls")
    protected static int encodeString(final byte[] buffer, final int pos,
        final String txt) {
        int newPos = pos;
        final int len = txt.length();
        buffer[newPos++] = (byte) len;
        byte[] convString;
        try {
            convString = txt.getBytes("ISO-8859-1");
        } catch (final UnsupportedEncodingException e) {
            LOGGER.error("Encoding problem ISO-8859-1 not found");
            convString = txt.getBytes();
        }
        for (final byte value : convString) {
            buffer[newPos++] = value;
        }
        return newPos;
    }

    /**
     * Activate the client command after taking it out of the recycle factory.
     * The ID is set again with this command.
     * 
     * @param commId the ID of this command
     */
    @Override
    public final void activate(final int commId) {
        id = commId;
    }

    /**
     * Create a clone of the command.
     */
    @Override
    public abstract AbstractCommand clone();

    /**
     * Encode data for transfer to server. Only for send commands.
     * 
     * @param writer the byte buffer the values are added to from index 0 on
     */
    public abstract void encode(final NetCommWriter writer);

    /**
     * Get the ID of this client command.
     * 
     * @return the ID of the client command that is currently set.
     */
    @Override
    public final int getId() {
        return id;
    }

    /**
     * Recycle the object and put is back into the command factory.
     */
    @Override
    public final void recycle() {
        PoolContext.enter();
        try {
            CommandFactory.getInstance().recycle(this);
        } finally {
            PoolContext.exit();
        }
    }

    /**
     * Clearing the command up before storing it in the recycle factory. Mainly
     * used to clean up references to objects that are not longer in use so the
     * garbage collection can clean them up.
     */
    @Override
    public void reset() {
        // nothing to clear by default
    }

    /**
     * Send this command to the using the net interface.
     */
    public final void send() {
        Game.getNet().sendCommand(this);
    }

    /**
     * Get the informations about this object as a string.
     * 
     * @return the data of the command as string
     */
    @Override
    public abstract String toString();

    /**
     * Get the simple name of this class along with the parameters of the
     * command.
     * 
     * @param param the parameters of the command that shall be displayed along
     *            with the class name of the command
     * @return the string that contains the simple class name and the parameters
     */
    protected final String toString(final String param) {
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append(getClass().getSimpleName());
        builder.append('(');
        builder.append(param);
        builder.append(')');

        final String retString = builder.toString();
        TextBuilder.recycle(builder);
        return retString;
    }
}
