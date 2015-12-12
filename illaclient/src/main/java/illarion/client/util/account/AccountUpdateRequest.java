package illarion.client.util.account;

import illarion.client.util.account.form.AccountUpdateForm;
import illarion.client.util.account.response.AccountUpdateResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountUpdateRequest implements AuthenticatedRequest<AccountUpdateResponse> {
    @Nonnull
    private final AccountUpdateForm data;
    @Nonnull
    private final Authenticator authenticator;

    AccountUpdateRequest(@Nonnull Authenticator authenticator, @Nonnull AccountUpdateForm data) {
        this.authenticator = authenticator;
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
        return "/account/account";
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
    public Class<AccountUpdateResponse> getResponseClass() {
        return AccountUpdateResponse.class;
    }
}
