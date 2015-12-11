package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MinMaxResponse {
    @SerializedName("min")
    private int min;

    @SerializedName("max")
    private int max;
}
