package illarion.client.util.account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface Request<T> {
    @Nonnull
    String getRoute();

    @Nonnull
    String getMethod();

    @Nullable
    Object getData();

    @Nonnull
    Map<Integer, Class<T>> getResponseMap();
}
