/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import illarion.client.util.account.response.CharacterGetResponse;
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterGetRequest implements AuthenticatedRequest<CharacterGetResponse> {
    @Nonnull
    private final IllarionAuthenticator authenticator;
    @Nonnull
    private final String serverId;
    @Nonnull
    private final CharacterId charId;

    CharacterGetRequest(@Nonnull IllarionAuthenticator authenticator, @Nonnull String serverId, @Nonnull CharacterId charId) {
        this.authenticator = authenticator;
        this.serverId = serverId;
        this.charId = charId;
    }

    @Nonnull
    @Override
    public IllarionAuthenticator getAuthenticator() {
        return authenticator;
    }

    @Nonnull
    @Override
    public String getRoute() {
        return "/account/character/" + serverId + '/' + charId.getValue();
    }

    @Nonnull
    @Override
    public String getMethod() {
        return "GET";
    }

    @Nullable
    @Override
    public Object getData() {
        return null;
    }

    @Nonnull
    @Override
    public Class<CharacterGetResponse> getResponseClass() {
        return CharacterGetResponse.class;
    }
}
