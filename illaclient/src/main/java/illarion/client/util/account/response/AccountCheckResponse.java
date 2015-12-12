package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountCheckResponse {
    @SerializedName("error")
    private ErrorResponse error;

    @SerializedName("checks")
    private List<CheckResponse> checks;
}
