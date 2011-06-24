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

import illarion.client.net.NetCommWriter;

/**
 * Client Command: Send a simple command to the server (
 * {@link illarion.client.net.CommandList#CMD_KEEPALIVE},
 * {@link illarion.client.net.CommandList#CMD_LOGOFF},
 * {@link illarion.client.net.CommandList#CMD_TURN_N},
 * {@link illarion.client.net.CommandList#CMD_TURN_E},
 * {@link illarion.client.net.CommandList#CMD_TURN_S},
 * {@link illarion.client.net.CommandList#CMD_TURN_W},
 * {@link illarion.client.net.CommandList#CMD_INTRODUCE},
 * {@link illarion.client.net.CommandList#CMD_STAND_DOWN}). Such commands do not
 * contain any data. Just the ID is transfered.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class SimpleCmd extends AbstractCommand {
    /**
     * Default constructor of a simple command. Since this is a generic command
     * you have to set the ID of this command. The ID will be determine what
     * command it actually is.
     * 
     * @param id the ID of the command
     */
    public SimpleCmd(final int id) {
        super(id);
    }

    /**
     * Create a duplicate of this simple command.
     * 
     * @return new instance of this command
     */
    @Override
    public SimpleCmd clone() {
        return new SimpleCmd(getId());
    }

    /**
     * Encode the data of this simple command and put the values into the
     * buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        return;
    }

    /**
     * Get the data of this simple command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("[" + getId() + "]");
    }
}
