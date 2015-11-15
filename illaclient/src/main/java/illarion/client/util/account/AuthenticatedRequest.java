package illarion.client.util.account;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface AuthenticatedRequest extends Request {
    @Nonnull
    java.net.Authenticator getAuthenticator();
}
