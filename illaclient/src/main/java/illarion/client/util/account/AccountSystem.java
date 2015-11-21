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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

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
        authenticator = new Authenticator(userName, password);
    }

    @Nullable
    public AccountInfo getAccountInformation() {
        if (authenticator == null) {
            throw new IllegalStateException("This function requires the account system to be authenticated.");
        }

        Request request = new AccountInfoRequest(authenticator);
        RequestHandler handler = new RequestHandler(endpoint);

        Map<Integer, Class<AccountInfo>> responses = new HashMap<>();
        responses.put(HttpURLConnection.HTTP_OK, AccountInfo.class);

        try {
            return handler.sendRequest(request, responses);
        } catch (IOException e) {
            return null;
        }
    }
}
