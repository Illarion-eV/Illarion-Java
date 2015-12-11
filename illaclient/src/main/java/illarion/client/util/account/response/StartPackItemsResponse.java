package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class StartPackItemsResponse {
    @SerializedName("itemId")
    private int itemId;

    @SerializedName("position")
    private int position;

    @SerializedName("number")
    private int number;

    @SerializedName("quality")
    private int quality;

    @SerializedName("name")
    private int name;

    @SerializedName("unitWorth")
    private int unitWorth;

    @SerializedName("unitWeight")
    private int unitWeight;
}
