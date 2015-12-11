package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class SuccessResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;
}
