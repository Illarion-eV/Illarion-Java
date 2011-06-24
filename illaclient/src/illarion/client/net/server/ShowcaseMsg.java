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

/**
 * Servermessage: Content of a container (
 * {@link illarion.client.net.CommandList#MSG_SHOWCASE}).
 * 
 * @author Blay09
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class ShowcaseMsg extends AbstractReply {
    /**
     * Default size of the arrays that store the items this message contains.
     * The size of the arrays is automatically increased in case its needed.
     */
    private static final int DEFAULT_SIZE = 70;

    /**
     * List of the count values of the items inside the container.
     */
    private short[] count = new short[DEFAULT_SIZE];

    /**
     * List of the IDs of the Items inside the container.
     */
    private int[] itemId = new int[DEFAULT_SIZE];

    /**
     * List of the Y positions of the items inside the container.
     */
    private int[] itemX = new int[DEFAULT_SIZE];

    /**
     * List of the X positions of the Items inside the container.
     */
    private int[] itemY = new int[DEFAULT_SIZE];

    /**
     * ID of the container that shall be shown.
     */
    private byte sc;

    /**
     * Count of items inside the container.
     */
    private short size;

    /**
     * Default constructor for the container message.
     */
    public ShowcaseMsg() {
        super(CommandList.MSG_SHOWCASE);
    }

    /**
     * Create a new instance of the container message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public ShowcaseMsg clone() {
        return new ShowcaseMsg();
    }

    /**
     * Decode the container data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        sc = (byte) reader.readUByte();
        size = reader.readUByte();

        if (size > itemId.length) {
            itemId = new int[size];
            count = new short[size];
            itemX = new int[size];
            itemY = new int[size];
        }

        for (int i = 0; i < size; i++) {
            itemId[i] = reader.readUShort();
            count[i] = reader.readUByte();
            itemX[i] = reader.readInt();
            itemY[i] = reader.readInt();
        }
    }

    /**
     * Execute the container message and send the decoded data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        GUI.getInstance().addContainer(sc, itemId, count, itemX, itemY);
        return true;
    }

    /**
     * Get the data of this container message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("SC: " + sc + " size: " + size);
    }
}
