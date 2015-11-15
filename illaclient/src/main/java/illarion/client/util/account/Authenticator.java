package illarion.client.util.account;

import javax.annotation.Nonnull;
import java.net.PasswordAuthentication;

/**
 * This is the authentication provider that can be used to interact with the API of the account system.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class Authenticator extends java.net.Authenticator {
    /**
     * The user name that is used to authenticate with the account system.
     */
    @Nonnull
    private final String userName;

    /**
     * The password that is used to authenticate.
     */
    @Nonnull
    private final String password;

    /**
     * Create a new instance of the authenticator that provides a password authentication based based on the provided
     * username and password.
     *
     * @param userName the user name
     * @param password the password
     */
    Authenticator(@Nonnull String userName, @Nonnull String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password.toCharArray());
    }
}
