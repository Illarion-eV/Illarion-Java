package illarion.client.util.account;

import illarion.client.util.account.response.CharacterCreateGetResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterCreateGetRequest implements AuthenticatedRequest<CharacterCreateGetResponse> {
    @Nonnull
    private final Authenticator authenticator;
    @Nonnull
    private final String serverId;

    CharacterCreateGetRequest(@Nonnull Authenticator authenticator, @Nonnull String serverId) {
        this.authenticator = authenticator;
        this.serverId = serverId;
    }

    @Nonnull
    @Override
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    @Nonnull
    @Override
    public String getRoute() {
        return "/account/character/" + serverId;
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
    public Class<CharacterCreateGetResponse> getResponseClass() {
        return CharacterCreateGetResponse.class;
    }
}
