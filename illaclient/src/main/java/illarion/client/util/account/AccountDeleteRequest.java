package illarion.client.util.account;

import illarion.client.util.account.response.AccountDeleteResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountDeleteRequest implements AuthenticatedRequest<AccountDeleteResponse> {
    @Nonnull
    private final Authenticator authenticator;

    AccountDeleteRequest(@Nonnull Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Nonnull
    @Override
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    @Nonnull
    @Override
    public String getRoute() {
        return "/account/account";
    }

    @Nonnull
    @Override
    public String getMethod() {
        return "DELETE";
    }

    @Nullable
    @Override
    public Object getData() {
        return null;
    }

    @Nonnull
    @Override
    public Class<AccountDeleteResponse> getResponseClass() {
        return AccountDeleteResponse.class;
    }
}
