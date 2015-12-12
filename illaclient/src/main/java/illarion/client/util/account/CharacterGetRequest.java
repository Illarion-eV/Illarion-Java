package illarion.client.util.account;

import illarion.client.util.account.response.CharacterCreateGetResponse;
import illarion.client.util.account.response.CharacterGetResponse;
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterGetRequest implements AuthenticatedRequest<CharacterGetResponse> {
    @Nonnull
    private final Authenticator authenticator;
    @Nonnull
    private final String serverId;
    @Nonnull
    private final CharacterId charId;

    CharacterGetRequest(@Nonnull Authenticator authenticator, @Nonnull String serverId, @Nonnull CharacterId charId) {
        this.authenticator = authenticator;
        this.serverId = serverId;
        this.charId = charId;
    }

    @Nonnull
    @Override
    public Authenticator getAuthenticator() {
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
