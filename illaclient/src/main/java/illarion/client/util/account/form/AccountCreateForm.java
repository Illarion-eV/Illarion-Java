package illarion.client.util.account.form;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountCreateForm {
    @SerializedName("name")
    @Nonnull
    private String name;

    @SerializedName("email")
    @Nullable
    private String eMail;

    @SerializedName("password")
    @Nonnull
    private String password;

    public AccountCreateForm(@Nonnull String name, @Nonnull String password, @Nullable String eMail) {
        this.name = name;
        this.eMail = (eMail == null || eMail.isEmpty() ? null : eMail);
        this.password = password;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nullable
    public String geteMail() {
        return eMail;
    }

    public void seteMail(@Nullable String eMail) {
        this.eMail = eMail;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
    }
}
