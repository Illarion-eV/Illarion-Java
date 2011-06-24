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
import illarion.client.world.Game;
import illarion.client.world.MapTile;

import illarion.common.util.Location;

/**
 * Servermessage: Change item on map (
 * {@link illarion.client.net.CommandList#MSG_CHANGE_ITEM}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class ChangeItemMsg extends AbstractReply {
    /**
     * The new count value of the item.
     */
    private int count;

    /**
     * The location on the map this update is performed on.
     */
    private transient Location loc;

    /**
     * The ID of the item after the change.
     */
    private int newItem;

    /**
     * The ID of the item before the change.
     */
    private int oldItem;

    /**
     * Default constructor for the change item message.
     */
    public ChangeItemMsg() {
        super(CommandList.MSG_CHANGE_ITEM);
    }

    /**
     * Create a new instance of the change item message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public ChangeItemMsg clone() {
        return new ChangeItemMsg();
    }

    /**
     * Decode the change item data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        oldItem = reader.readUShort();
        newItem = reader.readUShort();
        count = reader.readUByte();
    }

    /**
     * Execute the change item message and send the decoded data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        final MapTile tile = Game.getMap().getMapAt(loc);
        if (tile != null) {
            tile.changeTopItem(oldItem, newItem, count);
        }

        return true;
    }

    /**
     * Cleanup the object and release all unneeded references.
     */
    @Override
    public void reset() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this change item message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(oldItem);
        builder.append(" to ");
        builder.append(newItem);
        builder.append(" - count: ");
        builder.append(count);
        builder.append(" at ");
        builder.append(loc.toString());
        return toString(builder.toString());
    }
}
