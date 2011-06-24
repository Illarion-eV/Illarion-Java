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
import illarion.client.util.ChatHandler;
import illarion.client.world.Game;

import illarion.common.util.Location;

/**
 * Servermessage: Look at description of a tile (
 * {@link illarion.client.net.CommandList#MSG_LOOKAT_TILE}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class LookatTileMsg extends AbstractReply {
    /**
     * The location of the tile on the server map.
     */
    private transient Location loc;

    /**
     * The look at text for the tile.
     */
    private String text;

    /**
     * Default constructor for the tile look at text message.
     */
    public LookatTileMsg() {
        super(CommandList.MSG_LOOKAT_TILE);
    }

    /**
     * Create a new instance of the tile look at text message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public LookatTileMsg clone() {
        return new LookatTileMsg();
    }

    /**
     * Decode the tile look at text data the receiver got and prepare it for the
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
        text = reader.readString();
    }

    /**
     * Execute the tile look at text message and send the decoded data to the
     * rest of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        Game.getDisplay().lookAt(loc.getDcX(), loc.getDcY(), text);
        GUI.getInstance().getChatText()
            .showText(text, null, loc, ChatHandler.SpeechMode.normal);
        return true;
    }

    /**
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        text = null;
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this tile look at text message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Location: " + loc.toString() + " Message: " + text);
    }
}
