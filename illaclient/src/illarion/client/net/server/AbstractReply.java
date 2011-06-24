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

import javolution.context.PoolContext;

import illarion.client.net.NetCommReader;
import illarion.client.net.ReplyFactory;

import illarion.common.util.Location;
import illarion.common.util.RecycleObject;

/**
 * Default class of a server message. This is the superclass of every server
 * message class.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public abstract class AbstractReply implements RecycleObject {
    /**
     * The string that is used to format the text at the
     * {@link #toString(String)} function.
     */
    @SuppressWarnings("nls")
    private static final String TO_STRING_FORMAT = "%1$s(%2$s)";

    /**
     * The ID of this server message.
     */
    private int id;

    /**
     * Default constructor for a server message.
     */
    protected AbstractReply() {
        // constructor does not need to do anything. A empty reply with a
        // undefined ID
    }

    /**
     * Constructor for a server message that also set the ID of this server
     * message. Child classes should call this function in order to set up the
     * server message class correctly.
     * 
     * @param newId the ID of the server message this reply instance represents.
     */
    protected AbstractReply(final int newId) {
        id = newId;
    }

    /**
     * Decode the following 6 bytes as location.
     * 
     * @param reader the receiver that delivers the data that shall be used as
     *            location data
     * @return the location that stores the position informations now
     * @throws IOException in case there are not enough bytes in the buffer to
     *             decode a location, this exception is thrown
     */
    protected static final Location decodeLocation(final NetCommReader reader)
        throws IOException {
        final Location loc = Location.getInstance();
        loc.setSC(reader.readShort(), reader.readShort(), reader.readShort());
        return loc;
    }

    /**
     * Activate the object with a different ID. To change the ID of this server
     * reply and set up all needed new data, this function can be used.
     * 
     * @param newId the new ID this object gets
     */
    @Override
    public final void activate(final int newId) {
        id = newId;
    }

    /**
     * Create a new instance of the abstract reply.
     */
    @Override
    public abstract AbstractReply clone();

    /**
     * Decode data from server receive buffer. And store the data for later
     * execution.
     * 
     * @param reader the receiver that stores the data that shall be decoded in
     *            this function
     * @throws IOException In case the function reads over the buffer of the
     *             receiver this exception is thrown
     */
    public abstract void decode(final NetCommReader reader) throws IOException;

    /**
     * Execute the update and send the decoded data to the rest of the client.
     * 
     * @return true in case the update is done, false in case this function has
     *         to be triggered again later.
     */
    public abstract boolean executeUpdate();

    /**
     * Get the ID of this reply.
     * 
     * @return the new object ID
     * @see illarion.common.util.RecycleObject#getId()
     */
    @Override
    public final int getId() {
        return id;
    }

    /**
     * Check if the message can be executed right now. The update is not
     * executed now in case this function returns false.
     * 
     * @return true if the message can be executed now, false if not.
     */
    public boolean processNow() {
        return true;
    }

    /**
     * Recycle the object, so put it back into the recycle factory for later
     * reuse.
     * 
     * @see illarion.common.util.RecycleObject#recycle()
     */
    @Override
    public final void recycle() {
        PoolContext.enter();
        try {
            ReplyFactory.getInstance().recycle(this);
        } finally {
            PoolContext.exit();
        }
    }

    /**
     * Clean up the server reply class and prepare it for reuse. This is called
     * just before the function is put back into the recycle factory.
     */
    @Override
    public void reset() {
        // nothing to clear by default
    }

    /**
     * Get the string representation of this reply object.
     * 
     * @return String that contains the simple class name of this reply class
     *         instance
     */
    @Override
    public abstract String toString();

    /**
     * Get the string representation of this reply object, with some added
     * parameter informations.
     * 
     * @param param the parameters that shall be added to the simple class name
     *            that is returned
     * @return the simple class name of this reply class instance along with the
     *         content of parameters
     */
    protected final String toString(final String param) {
        return String.format(TO_STRING_FORMAT, getClass().getSimpleName(),
            param);
    }
}
