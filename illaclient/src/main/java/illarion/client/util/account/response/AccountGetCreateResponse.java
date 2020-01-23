package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountGetCreateResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("route")
    private String route;
}
