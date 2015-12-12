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
package illarion.client.util.account;

import illarion.client.util.account.response.AccountGetResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountSystem {
    /**
     * The root URL for the official account system.
     */
    @Nonnull
    public static final String OFFICIAL = "https://illarion.org/app.php";

    /**
     * The root URL for the local server account system.
     */
    @Nonnull
    public static final String LOCAL = "http://localhost/app.php";

    @Nonnull
    private final String endpoint;

    /**
     * The authenticator used to handle authenticated requests.
     */
    @Nullable
    private Authenticator authenticator;

    public AccountSystem(@Nonnull String endpoint) {
        this.endpoint = endpoint;
    }

    public AccountSystem(@Nonnull String endpoint, @Nonnull String userName, @Nonnull String password) {
        this(endpoint);

        setAuthentication(userName, password);
    }

    /**
     * Set the authentication that is used for the interaction with the account system.
     *
     * @param userName the user name
     * @param password the password
     * @throws IllegalStateException in case the authentication is already set
     */
    public void setAuthentication(@Nonnull String userName, @Nonnull String password) {
        if (authenticator != null) {
            throw new IllegalStateException("Setting the authentication credentials is now allowed once they are set.");
        }
        authenticator = new Authenticator(userName, password);
    }

    @Nullable
    public AccountGetResponse getAccountInformation() {
        if (authenticator == null) {
            throw new IllegalStateException("This function requires the account system to be authenticated.");
        }

        AccountGetRequest request = new AccountGetRequest(authenticator);
        RequestHandler handler = new RequestHandler(endpoint);

        try {
            return handler.sendRequest(request);
        } catch (IOException e) {
            return null;
        }
    }
}
