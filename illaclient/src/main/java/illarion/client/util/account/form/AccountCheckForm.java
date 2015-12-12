package illarion.client.util.account.form;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountCheckForm {
    @SerializedName("name")
    @Nullable
    private String name;

    @SerializedName("email")
    @Nullable
    private String eMail;
}
