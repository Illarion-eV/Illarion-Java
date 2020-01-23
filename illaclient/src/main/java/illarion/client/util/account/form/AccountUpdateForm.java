package illarion.client.util.account.form;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountUpdateForm {
    @SerializedName("email")
    @Nullable
    private String eMail;

    @SerializedName("password")
    @Nullable
    private String password;

    @Nullable
    public String geteMail() {
        return eMail;
    }

    public void seteMail(@Nullable String eMail) {
        this.eMail = eMail;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }
}
