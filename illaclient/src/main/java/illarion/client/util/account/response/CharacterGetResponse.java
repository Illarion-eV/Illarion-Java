package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterGetResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("race")
    private int race;

    @SerializedName("raceType")
    private int raceType;

    @SerializedName("attributes")
    private CharacterAttributesResponse attributes;

    @SerializedName("dateOfBirth")
    private int dateOfBirth;

    @SerializedName("bodyHeight")
    private int bodyHeight;

    @SerializedName("bodyWeight")
    private int bodyWeight;

    @SerializedName("paperDoll")
    private CharacterPaperDollResponse paperDoll;

    @SerializedName("items")
    private List<CharacterItemResponse> items;

    @SerializedName("error")
    private ErrorResponse error;
}
