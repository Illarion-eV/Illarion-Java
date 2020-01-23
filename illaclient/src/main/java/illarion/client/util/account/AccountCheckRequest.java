package illarion.client.util.account;

import illarion.client.util.account.form.AccountCheckForm;
import illarion.client.util.account.form.AccountCreateForm;
import illarion.client.util.account.response.AccountCheckResponse;
import illarion.client.util.account.response.AccountCreateResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountCheckRequest implements Request<AccountCheckResponse> {
    @Nonnull
    private final AccountCheckForm data;

    AccountCheckRequest(@Nonnull AccountCheckForm data) {
        this.data = data;
    }

    @Nonnull
    @Override
    public String getRoute() {
        return "/account/account/check";
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
    public Class<AccountCheckResponse> getResponseClass() {
        return AccountCheckResponse.class;
    }
}
