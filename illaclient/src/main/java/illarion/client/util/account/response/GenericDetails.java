package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class GenericDetails {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;
}
