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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.util.Md5Crypto;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;

/**
 * Client Command: Send login information to the server ({@link CommandList#CMD_LOGIN}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class LoginCmd extends AbstractCommand {
    /**
     * The name of the character that shall log in.
     */
    @Nonnull
    private final String charName;

    /**
     * The account password that is used. This contains the plain text password.
     */
    @Nonnull
    private final String password;

    /**
     * The current client version that is used to validate the login and ensure
     * that the needed client is used.
     */
    private final short version;

    /**
     * Default constructor for the login command.
     *
     * @param charName the name of the character to login with
     * @param password the password used to login
     * @param version the version of the client to report to the server
     */
    public LoginCmd(@Nonnull String charName, @Nonnull String password, int version) {
        super(CommandList.CMD_LOGIN);
        this.charName = charName;

        Md5Crypto crypto = new Md5Crypto();
        this.password = crypto.crypt(password, "illarion");
        this.version = (short) version;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) throws IOException {
        writer.writeUByte(version);
        writer.writeString(charName);
        writer.writeString(password);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    @Contract(pure = true)
    public String toString() {
        return toString("Char: " + charName + " Client: " + version);
    }
}
