package illarion.client.util.account.request;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CreateAccount {
    @SerializedName("name")
    @Nonnull
    private final String name;

    @SerializedName("email")
    @Nullable
    private final String eMail;

    @SerializedName("password")
    @Nonnull
    private final String password;

    public CreateAccount(@Nonnull String name, @Nonnull String password, @Nullable String eMail) {
        this.name = name;
        this.eMail = (eMail == null || eMail.isEmpty() ? null : eMail);
        this.password = password;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public String getEMail() {
        return eMail;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }
}
