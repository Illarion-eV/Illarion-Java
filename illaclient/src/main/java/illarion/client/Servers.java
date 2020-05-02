/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.client;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * The definitions of the existing servers. All data needed to connect and
 * identify a server is stored here.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum Servers {
    /**
     * The Gameserver of Illarion. Normal players should connect to this server. If the selection of the server is
     * disabled this server is chosen as default server the client will show up as game client and not as Testclient.
     */
    Illarionserver(2, "illarionserver", "illarion.org", 3008, 20),

    /**
     * The Testserver of Illarion. Testers and developers need a client that is allowed to connect to this server.
     */
    Testserver(1, "testserver", Illarionserver.serverHostName, 3011, 20),

    /**
     * The development server of Illarion. Developers need a client that is allowed to connect to this server.
     */
    Devserver(0, "devserver", Illarionserver.serverHostName, 3012, 20),

    /**
     * Custom server, only for very special applications. It will connect to a server running at a user-specified host.
     */
    Customserver(3, "customserver", Illarionserver.serverHostName, 3012, 20);

    /**
     * The client version that needs to be transferred to the server so it accepts the connection and the client shows
     * that it is up to date.
     */
    private final int clientVersion;

    /**
     * Storage of the server host address the client needs to connect to.
     */
    @Nonnull
    private final String serverHostName;
    /**
     * The identifier key for this server.
     */
    private final int serverKey;
    /**
     * The name of the server.
     * Is in lowercase with no spaces.
     */
    private final String serverName;
    /**
     * The port of the server the clients needs to connect to.
     */
    private final int serverPort;

    /**
     * Default ENUM constructor for the enumeration entries. It creates a definition of a server and stores it to the
     * enumeration constants.
     *
     * @param name     the name of the server
     * @param hostName the host name of the server
     * @param port     the port the server is listening for connections
     * @param version  the version that shall be transferred to the server to
     *                 validate the correct client version
     */
    Servers(int key, @Nonnull String name, @Nonnull String hostName, int port, int version) {
        serverKey = key;
        serverName = name;
        serverHostName = hostName;
        serverPort = port;
        clientVersion = version;
    }

    /**
     * Get the version of the client that need to be transferred to connect to this server.
     *
     * @return the client version that need to be transferred
     */
    @Contract(pure = true)
    public int getClientVersion() {
        return clientVersion;
    }

    /**
     * The the server host address of the server entry.
     *
     * @return the host address of the server
     */
    @Nonnull
    @Contract(pure = true)
    public String getServerHost() {
        return serverHostName;
    }

    @Contract(pure = true)
    public int getServerKey() {
        return serverKey;
    }

    /**
     * Get the name of the server that is defined with this server entry.
     *
     * @return the name of the server
     */
    @Nonnull
    @Contract(pure = true)
    public String getServerName() {
        return serverName;
    }

    /**
     * Get the port the server listens of the server entry.
     *
     * @return the port that is listened by the server
     */
    @Contract(pure = true)
    public int getServerPort() {
        return serverPort;
    }
}
