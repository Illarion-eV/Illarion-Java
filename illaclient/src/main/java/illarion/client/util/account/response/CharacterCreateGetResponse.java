package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterCreateGetResponse {
    @SerializedName("races")
    private List<RaceResponse> races;

    @SerializedName("startPacks")
    private List<StartPackResponse> startPacks;

    @SerializedName("error")
    private ErrorResponse error;
}
