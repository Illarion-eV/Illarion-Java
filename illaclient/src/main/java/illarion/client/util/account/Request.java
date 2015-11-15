package illarion.client.util.account;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface Request {
    @Nonnull
    String getRoute();

    @Nonnull
    String getMethod();
}
