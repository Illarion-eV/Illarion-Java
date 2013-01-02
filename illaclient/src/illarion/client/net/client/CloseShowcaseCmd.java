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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;

/**
 * Client Command: Close a opened container
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CloseShowcaseCmd extends AbstractCommand {
    /**
     * The ID of the container.
     */
    private int showcaseId;

    /**
     * Default constructor for the open bag command.
     */
    public CloseShowcaseCmd() {
        super(CommandList.CMD_CLOSE_SHOWCASE);
    }

    /**
     * Create a duplicate of this open bag command.
     *
     * @return new instance of this command
     */
    @Override
    public CloseShowcaseCmd clone() {
        return new CloseShowcaseCmd();
    }

    /**
     * Set the ID of the showcase.
     *
     * @param id the showcase id
     */
    public void setShowcaseId(final int id) {
        showcaseId = id;
    }

    /**
     * Encode the data of this open bag command and put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte((byte) showcaseId);
    }

    /**
     * Get the data of this open bag command as string.
     *
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Showcase: " + showcaseId);
    }
}
