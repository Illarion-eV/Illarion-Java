package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterCreateResponse {
    @SerializedName("success")
    private SuccessResponse success;

    @SerializedName("error")
    private ErrorResponse error;
}
