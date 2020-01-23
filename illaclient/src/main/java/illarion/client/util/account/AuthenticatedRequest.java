package illarion.client.util.account;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface AuthenticatedRequest<T> extends Request<T> {
    @Nonnull
    java.net.Authenticator getAuthenticator();
}
