package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CheckResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("checkedValue")
    public String checkedValue;

    @SerializedName("description")
    public String description;
}
