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
package illarion.client;

/**
 * The definitions of the existing servers. All data needed to connect and
 * identify a server is stored here.
 * 
 * @author Martin Karing
 * @since 1.22
 */
@SuppressWarnings("nls")
public enum Servers {
    /**
     * Local server, only for very special applications. It will connect to a
     * server running at localhost using the same connection parameters like the
     * Testserver of Illarion.
     */
    localserver("Local server", "localhost", 3012, 122),

    /**
     * The Gameserver of Illarion. Normal players should connect to this server.
     * If the selection of the server is disabled this server is chosen as
     * default server the client will show up as game client and not as
     * Testclient.
     */
    realserver("Game server", "illarion.org", 3008, 121),

    /**
     * The Testserver of Illarion. Testers and developers need a client that is
     * allowed to connect to this server.
     */
    testserver("Test server", realserver.serverAddr, 3012, 122);

    /**
     * The client version that needs to be transfered to the server so it
     * accepts the connection and the client shows that it is up to date.
     */
    private final int clientVers;

    /**
     * Storage of the server host address the client needs to connect to.
     */
    private final String serverAddr;

    /**
     * The name of the server.
     */
    private final String serverName;

    /**
     * The port of the server the clients needs to connect to.
     */
    private final int serverPort;

    /**
     * Default ENUM constructor for the enumeration entries. It creates a
     * definition of a server and stores it to the enumeration constants.
     * 
     * @param name the name of the server
     * @param addr the host address of the server
     * @param port the port the server is listening for connections
     * @param version the version that shall be transfered to the server to
     *            validate the correct client version
     */
    private Servers(final String name, final String addr, final int port,
        final int version) {
        serverName = name;
        serverAddr = addr;
        serverPort = port;
        clientVers = version;
    }

    /**
     * Get the version of the client that need to be transfered to connect to
     * this server.
     * 
     * @return the client version that need to be transfered
     */
    public int getClientVersion() {
        return clientVers;
    }

    /**
     * The the server host address of the server entry.
     * 
     * @return the host address of the server
     */
    public String getServerHost() {
        return serverAddr;
    }

    /**
     * Get the name of the server that is defined with this server entry.
     * 
     * @return the name of the server
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Get the port the server listens of the server entry.
     * 
     * @return the port that is listened by the server
     */
    public int getServerPort() {
        return serverPort;
    }
}
