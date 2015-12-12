package illarion.client.util.account;

import illarion.client.util.account.form.CharacterCreateForm;
import illarion.client.util.account.response.CharacterCreateGetResponse;
import illarion.client.util.account.response.CharacterCreateResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class CharacterCreateRequest implements AuthenticatedRequest<CharacterCreateResponse> {
    @Nonnull
    private final Authenticator authenticator;
    @Nonnull
    private final String serverId;
    @Nonnull
    private final CharacterCreateForm data;

    CharacterCreateRequest(@Nonnull Authenticator authenticator,
                           @Nonnull String serverId,
                           @Nonnull CharacterCreateForm data) {
        this.authenticator = authenticator;
        this.serverId = serverId;
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
        return "/account/character/" + serverId;
    }

    @Nonnull
    @Override
    public String getMethod() {
        return "POST";
    }

    @Nullable
    @Override
    public Object getData() {
        return data;
    }

    @Nonnull
    @Override
    public Class<CharacterCreateResponse> getResponseClass() {
        return CharacterCreateResponse.class;
    }
}
