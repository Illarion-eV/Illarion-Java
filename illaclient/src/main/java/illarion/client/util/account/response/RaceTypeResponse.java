package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class RaceTypeResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("hairs")
    private List<IdNameResponse> hairs;

    @SerializedName("beards")
    private List<IdNameResponse> beards;

    @SerializedName("hairColour")
    private List<ColourResponse> hairColours;

    @SerializedName("skinColour")
    private List<ColourResponse> skinColours;
}
