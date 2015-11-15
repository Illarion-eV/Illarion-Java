package illarion.client.util.account;

import javax.annotation.Nonnull;
import java.net.Authenticator;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AccountInfoRequest implements AuthenticatedRequest {
    @Nonnull
    private static final String ROUTE = "/account/account";
    @Nonnull
    private static final String METHOD = "GET";

    @Nonnull
    private final java.net.Authenticator authenticator;

    public AccountInfoRequest(@Nonnull Authenticator authenticator) {
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
        return ROUTE;
    }

    @Nonnull
    @Override
    public String getMethod() {
        return METHOD;
    }
}
