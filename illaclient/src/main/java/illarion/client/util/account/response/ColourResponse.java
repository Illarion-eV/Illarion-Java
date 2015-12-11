package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ColourResponse {
    @SerializedName("red")
    private int red;

    @SerializedName("green")
    private int green;

    @SerializedName("blue")
    private int blue;

    @SerializedName("alpha")
    private int alpha;
}
