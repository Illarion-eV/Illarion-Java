package illarion.client.util.account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountSystem {
    /**
     * The root URL for the official account system.
     */
    @Nonnull
    public static final String OFFICIAL = "https://illarion.org/api.php";

    /**
     * The root URL for the local server account system.
     */
    @Nonnull
    public static final String LOCAL = "http://localhost/api.php";

    @Nonnull
    private final String endpoint;

    /**
     * The authenticator used to handle authenticated requests.
     */
    @Nullable
    private Authenticator authenticator;

    public AccountSystem(@Nonnull String endpoint) {
        this.endpoint = endpoint;
    }

    public AccountSystem(@Nonnull String endpoint, @Nonnull String userName, @Nonnull String password) {
        this(endpoint);
        authenticator = new Authenticator(userName, password);
    }

    public AccountInfo getAccountInformation() {
        if (authenticator == null) {
            throw new IllegalStateException("This function requires the account system to be authenticated.");
        }

        AuthenticatedRequest request = new AccountInfoRequest(authenticator);
        RequestHandler handler = new RequestHandler(endpoint);

        Map<Integer, Class<AccountInfo>> responses = new HashMap<>();
        responses.put(HttpURLConnection.HTTP_OK, AccountInfo.class);

        try {
            return handler.sendRequest(request, responses);
        } catch (IOException e) {
            return null;
        }
    }
}
