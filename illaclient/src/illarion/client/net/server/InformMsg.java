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

import illarion.client.net.NetCommReader;
import javolution.text.TextBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The inform message that is used to receive inform messages from the server.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class InformMsg extends AbstractReply {
    /**
     * The logger that is used for the log output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InformMsg.class);

    /**
     * The type of the inform.
     */
    private int informType;

    /**
     * The text of the inform.
     */
    private String informText;

    @Override
    public AbstractReply clone() {
        return new InformMsg();
    }

    @Override
    public void decode(final NetCommReader reader) throws IOException {
        informType = reader.readUByte();
        informText = reader.readString();
    }

    @Override
    public void reset() {
        informText = null;
    }

    @Override
    public boolean executeUpdate() {
        LOGGER.info(toString());
        return true;
    }

    @Override
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();
        try {
            builder.append("Type: ").append(informType);
            builder.append(" Text: ").append(informText);
            return toString(builder.toString());
        } finally {
            TextBuilder.recycle(builder);
        }
    }
}
