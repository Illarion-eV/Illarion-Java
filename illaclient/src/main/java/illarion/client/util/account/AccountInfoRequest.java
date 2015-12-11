package illarion.client.util.account;

import illarion.client.util.account.response.AccountGetResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountInfoRequest implements AuthenticatedRequest<AccountGetResponse> {
    @Nonnull
    private final java.net.Authenticator authenticator;

    AccountInfoRequest(@Nonnull Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Nonnull
    @Override
    public java.net.Authenticator getAuthenticator() {
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
    public Map<Integer, Class<AccountGetResponse>> getResponseMap() {
        Map<Integer, Class<AccountGetResponse>> responses = new HashMap<>();
        responses.put(HttpURLConnection.HTTP_OK, AccountGetResponse.class);
        return responses;
    }
}
