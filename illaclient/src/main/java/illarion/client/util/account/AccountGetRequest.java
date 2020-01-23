package illarion.client.util.account;

import illarion.client.util.account.response.AccountGetResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.Authenticator;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountGetRequest implements AuthenticatedRequest<AccountGetResponse> {
    @Nonnull
    private final Authenticator authenticator;

    AccountGetRequest(@Nonnull Authenticator authenticator) {
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
        return "GET";
    }

    @Nullable
    @Override
    public Object getData() {
        return null;
    }

    @Nonnull
    @Override
    public Class<AccountGetResponse> getResponseClass() {
        return AccountGetResponse.class;
    }
}
