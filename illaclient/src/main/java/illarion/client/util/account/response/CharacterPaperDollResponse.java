package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterPaperDollResponse {
    @SerializedName("hairId")
    private int hairId;

    @SerializedName("beardId")
    private int beardId;

    @SerializedName("skinColour")
    private ColourResponse skinColour;

    @SerializedName("hairColour")
    private ColourResponse hairColour;
}
