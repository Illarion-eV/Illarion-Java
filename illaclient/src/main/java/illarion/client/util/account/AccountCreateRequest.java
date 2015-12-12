package illarion.client.util.account;

import illarion.client.util.account.form.AccountCreateForm;
import illarion.client.util.account.response.AccountCreateResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountCreateRequest implements Request<AccountCreateResponse> {
    @Nonnull
    private final AccountCreateForm data;

    AccountCreateRequest(@Nonnull AccountCreateForm data) {
        this.data = data;
    }

    @Nonnull
    @Override
    public String getRoute() {
        return "/account/account";
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
    public Class<AccountCreateResponse> getResponseClass() {
        return AccountCreateResponse.class;
    }
}
