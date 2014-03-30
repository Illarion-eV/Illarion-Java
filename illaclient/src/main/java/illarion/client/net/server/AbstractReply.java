/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.common.net.NetCommReader;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Default class of a server message. This is the superclass of every server message class.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public abstract class AbstractReply {
    /**
     * Default constructor for a server message.
     */
    protected AbstractReply() {
    }

    /**
     * Decode the following 6 bytes as location.
     *
     * @param reader the receiver that delivers the data that shall be used as location data
     * @return the location that stores the position informations now
     * @throws IOException in case there are not enough bytes in the buffer to decode a location, this exception is
     * thrown
     */
    @Nonnull
    protected static Location decodeLocation(@Nonnull final NetCommReader reader) throws IOException {
        final Location loc = new Location();
        loc.setSC(reader.readShort(), reader.readShort(), reader.readShort());
        return loc;
    }

    /**
     * Decode data from server receive buffer. And store the data for later execution.
     *
     * @param reader the receiver that stores the data that shall be decoded in this function
     * @throws IOException In case the function reads over the buffer of the receiver this exception is thrown
     */
    public abstract void decode(final NetCommReader reader) throws IOException;

    /**
     * Execute the update and send the decoded data to the rest of the client.
     *
     * @return true in case the update is done, false in case this function has to be triggered again later.
     */
    public abstract boolean executeUpdate();

    /**
     * Check if the message can be executed right now. The update is not executed now in case this function returns
     * false.
     *
     * @return true if the message can be executed now, false if not.
     */
    public boolean processNow() {
        return true;
    }

    /**
     * Get the string representation of this reply object.
     *
     * @return String that contains the simple class name of this reply class instance
     */
    @Nonnull
    @Override
    public abstract String toString();

    /**
     * Get the string representation of this reply object, with some added parameter information.
     *
     * @param param the parameters that shall be added to the simple class name that is returned
     * @return the simple class name of this reply class instance along with the content of parameters
     */
    @Nonnull
    protected final String toString(final CharSequence param) {
        return getClass().getSimpleName() + '(' + param + ')';
    }
}
