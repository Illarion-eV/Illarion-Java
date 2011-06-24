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

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Send login informations to the server (
 * {@link illarion.client.net.CommandList#CMD_LOGIN}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class LoginCmd extends AbstractCommand {
    /**
     * The name of the character that shall log in.
     */
    private String charname;

    /**
     * The account password that is used. This contains the plain text password.
     */
    private String password;

    /**
     * The current client version that is used to validate the login and ensure
     * that the needed client is used.
     */
    private byte version;

    /**
     * Default constructor for the login command.
     */
    public LoginCmd() {
        super(CommandList.CMD_LOGIN);
    }

    /**
     * Create a duplicate of this login command.
     * 
     * @return new instance of this command
     */
    @Override
    public LoginCmd clone() {
        return new LoginCmd();
    }

    /**
     * Encode the data of this login command and put the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(version);
        writer.writeString(charname);
        writer.writeString(password);
    }

    /**
     * Clean up the command before put it back into the recycler for later
     * reuse.
     */
    @Override
    public void reset() {
        charname = null;
        password = null;
    }

    /**
     * Set the login informations that are used.
     * 
     * @param loginname the character name that is used at the login
     * @param loginpw the plain text password that is used
     */
    public void setLogin(final String loginname, final String loginpw) {
        charname = loginname;
        password = loginpw;
    }

    /**
     * Set the version of the client that is transfered to the server. This is
     * needed to ensure that the user has the newest version of the client that
     * is needed to work correctly with the server.
     * 
     * @param clientVersion the client version that is transmitted to the server
     */
    public void setVersion(final int clientVersion) {
        version = (byte) clientVersion;
    }

    /**
     * Get the data of this login command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Char: " + charname + " Client: " + version);
    }
}
