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

import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This is the parent class for messages that receive item look at messages from the server. It contains the decoder
 * for the look at structure.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractItemLookAtMsg extends AbstractReply {
    protected String name;
    protected short rareness;
    protected String description;
    protected String craftedBy;
    protected int weight;
    protected long worth;
    protected String qualityText;
    protected String durabilityText;
    protected short durabilityValue;
    protected short diamondLevel;
    protected short emeraldLevel;
    protected short rubyLevel;
    protected short sapphireLevel;
    protected short amethystLevel;
    protected short obsidianLevel;
    protected short topazLevel;
    protected short bonus;

    /**
     * Decode look at data from server receive buffer. And store the data for later execution.
     *
     * @param reader the receiver that stores the data that shall be decoded in this function
     * @throws IOException In case the function reads over the buffer of the receiver this exception is thrown
     */
    protected void decodeLookAt(@Nonnull final NetCommReader reader) throws IOException {
        name = reader.readString();
        rareness = reader.readUByte();
        description = reader.readString();
        craftedBy = reader.readString();
        weight = reader.readUShort();
        worth = reader.readUInt();
        qualityText = reader.readString();
        durabilityText = reader.readString();
        durabilityValue = reader.readUByte();
        diamondLevel = reader.readUByte();
        emeraldLevel = reader.readUByte();
        rubyLevel = reader.readUByte();
        sapphireLevel = reader.readUByte();
        amethystLevel = reader.readUByte();
        obsidianLevel = reader.readUByte();
        topazLevel = reader.readUByte();
        bonus = reader.readUByte();
    }
}
