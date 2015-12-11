package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class StartPackResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("skills")
    private List<IdNameResponse> skills;

    @SerializedName("items")
    private List<StartPackItemsResponse> items;
}
