package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class RaceResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("attributes")
    private AttributesCreationResponse attributes;

    @SerializedName("types")
    private List<RaceTypeResponse> types;
}
