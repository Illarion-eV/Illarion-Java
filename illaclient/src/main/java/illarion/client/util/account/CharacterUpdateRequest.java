package illarion.client.util.account;

import illarion.client.util.account.form.CharacterUpdateForm;
import illarion.client.util.account.response.CharacterGetResponse;
import illarion.client.util.account.response.CharacterUpdateResponse;
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterUpdateRequest implements AuthenticatedRequest<CharacterUpdateResponse> {
    @Nonnull
    private final Authenticator authenticator;
    @Nonnull
    private final String serverId;
    @Nonnull
    private final CharacterId charId;
    @Nonnull
    private final CharacterUpdateForm data;

    CharacterUpdateRequest(@Nonnull Authenticator authenticator,
                           @Nonnull String serverId,
                           @Nonnull CharacterId charId,
                           @Nonnull CharacterUpdateForm data) {
        this.authenticator = authenticator;
        this.serverId = serverId;
        this.charId = charId;
        this.data = data;
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
        return "PUT";
    }

    @Nullable
    @Override
    public Object getData() {
        return data;
    }

    @Nonnull
    @Override
    public Class<CharacterUpdateResponse> getResponseClass() {
        return CharacterUpdateResponse.class;
    }
}
