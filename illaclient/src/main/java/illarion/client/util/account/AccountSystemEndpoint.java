package illarion.client.util.account;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is a small utility class to identify the endpoint of the account system.
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountSystemEndpoint {
    @Nonnull
    private final String url;
    @Nonnull
    private final String name;
    @Nonnull
    private final String configRoot;
    @Nullable
    private final String defaultUserName;
    @Nullable
    private final String defaultPassword;
    private final boolean useConfigParameters;

    AccountSystemEndpoint(@Nonnull String url, @Nonnull String name, @Nonnull String configRoot) {
        this(url, name, configRoot, false, null, null);
    }

    AccountSystemEndpoint(@Nonnull String url,
                          @Nonnull String name,
                          @Nonnull String configRoot,
                          boolean useConfigParameters,
                          @Nullable String defaultUserName,
                          @Nullable String defaultPassword) {
        this.url = url;
        this.name = name;
        this.configRoot = configRoot;
        this.useConfigParameters = useConfigParameters;
        this.defaultUserName = defaultUserName;
        this.defaultPassword = defaultPassword;
    }

    @Nonnull
    public String getUrl() {
        return url;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public String getConfigRoot() {
        return configRoot;
    }

    @Nonnull
    public String getConfigSubKey(@Nonnull String subKey) {
        return getConfigRoot() + '.' + subKey;
    }

    @Nullable
    public String getDefaultUserName() {
        return defaultUserName;
    }

    @Nullable
    public String getDefaultPassword() {
        return defaultPassword;
    }

    public boolean isUseConfigParameters() {
        return useConfigParameters;
    }
}
